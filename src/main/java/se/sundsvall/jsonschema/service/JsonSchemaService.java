package se.sundsvall.jsonschema.service;

import static java.util.Comparator.comparing;
import static org.springframework.data.domain.Pageable.unpaged;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.jsonschema.service.Constants.JSON_SCHEMA_ALREADY_EXISTS;
import static se.sundsvall.jsonschema.service.Constants.JSON_SCHEMA_WITH_GREATER_VERSION_EXISTS;
import static se.sundsvall.jsonschema.service.Constants.MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID;
import static se.sundsvall.jsonschema.service.Constants.MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_NAME;
import static se.sundsvall.jsonschema.service.mapper.JsonSchemaMapper.toJsonSchema;
import static se.sundsvall.jsonschema.service.mapper.JsonSchemaMapper.toJsonSchemaEntity;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.integration.db.JsonSchemaRepository;
import se.sundsvall.jsonschema.service.mapper.JsonSchemaMapper;

@Service
public class JsonSchemaService {

	private final JsonSchemaRepository jsonSchemaRepository;

	public JsonSchemaService(JsonSchemaRepository jsonSchemaRepository) {
		this.jsonSchemaRepository = jsonSchemaRepository;
	}

	/**
	 * Get all schemas by municipality ID, enriched with number of references.
	 *
	 * @param  municipalityId the municipality ID
	 * @param  pageable       pagination data
	 * @return                a list of {@link JsonSchema}
	 */
	@Transactional(readOnly = true)
	public Page<JsonSchema> getSchemas(String municipalityId, final Pageable pageable) {
		return jsonSchemaRepository.findAllByMunicipalityId(municipalityId, pageable)
			.map(JsonSchemaMapper::toJsonSchema);
	}

	/**
	 * Get schema by municipality ID and schema ID, enriched with number of references.
	 *
	 * @param  municipalityId                       the municipality ID
	 * @param  id                                   the schema ID
	 * @return                                      a {@link JsonSchema}
	 * @throws org.zalando.problem.ThrowableProblem if not found
	 */
	@Transactional(readOnly = true)
	public JsonSchema getSchema(String municipalityId, String id) {
		return jsonSchemaRepository.findByMunicipalityIdAndId(municipalityId, id)
			.map(JsonSchemaMapper::toJsonSchema)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID.formatted(id)));
	}

	/**
	 * Get latest schema by municipality ID and schema name, enriched with number of references.
	 *
	 * @param  municipalityId                       the municipality ID
	 * @param  name                                 the schema name
	 * @return                                      a {@link JsonSchema}
	 * @throws org.zalando.problem.ThrowableProblem if not found
	 */
	@Transactional(readOnly = true)
	public JsonSchema getLatestSchemaByName(final String municipalityId, final String name) {
		return jsonSchemaRepository.findAllByMunicipalityIdAndName(municipalityId, name.toLowerCase(), unpaged()).stream()
			.max(comparing(obj -> new ComparableVersion(obj.getVersion())))
			.map(JsonSchemaMapper::toJsonSchema)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_NAME.formatted(name)));
	}

	/**
	 * Create new schema or a new version of an existing schema.
	 *
	 * @param  municipalityId                       the municipality ID
	 * @param  request                              the schema request
	 * @return                                      the created {@link JsonSchema}
	 * @throws org.zalando.problem.ThrowableProblem if a conflicting schema already exists
	 */
	@Transactional
	public JsonSchema create(String municipalityId, JsonSchemaCreateRequest request) {
		final var schemaEntity = toJsonSchemaEntity(municipalityId, request);

		validateSchemaDoesNotAlreadyExist(schemaEntity.getId());
		validateNoGreaterVersionExists(municipalityId, request);

		// All good! Create schema.
		return toJsonSchema(jsonSchemaRepository.save(schemaEntity));
	}

	/**
	 * Delete an existing schema.
	 *
	 * @param  municipalityId                       the municipality ID
	 * @param  id                                   the schema ID
	 * @throws org.zalando.problem.ThrowableProblem if not found or referenced
	 */
	@Transactional
	public void delete(String municipalityId, String id) {
		jsonSchemaRepository.findByMunicipalityIdAndId(municipalityId, id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID.formatted(id)));

		jsonSchemaRepository.deleteById(id);
	}

	// ---- Private helpers ------------------------------------------------------

	private void validateSchemaDoesNotAlreadyExist(String id) {
		if (jsonSchemaRepository.existsById(id)) {
			throw Problem.valueOf(CONFLICT, JSON_SCHEMA_ALREADY_EXISTS.formatted(id));
		}
	}

	private void validateNoGreaterVersionExists(String municipalityId, JsonSchemaCreateRequest request) {
		final var newVersion = new ComparableVersion(request.getVersion());

		jsonSchemaRepository.findAllByMunicipalityIdAndName(municipalityId, request.getName().toLowerCase(), unpaged()).stream()
			.filter(existing -> new ComparableVersion(existing.getVersion()).compareTo(newVersion) > 0)
			.findAny()
			.ifPresent(existing -> {
				throw Problem.valueOf(CONFLICT, JSON_SCHEMA_WITH_GREATER_VERSION_EXISTS.formatted(existing.getId()));
			});
	}
}
