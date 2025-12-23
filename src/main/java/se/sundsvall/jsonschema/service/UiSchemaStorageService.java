package se.sundsvall.jsonschema.service;

import static java.util.Objects.isNull;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.jsonschema.service.Constants.MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID;
import static se.sundsvall.jsonschema.service.Constants.MESSAGE_UI_SCHEMA_NOT_FOUND_BY_JSON_SCHEMA_ID;
import static se.sundsvall.jsonschema.service.mapper.UiSchemaMapper.toUiSchemaEntity;

import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.jsonschema.api.model.UiSchema;
import se.sundsvall.jsonschema.api.model.UiSchemaRequest;
import se.sundsvall.jsonschema.integration.db.JsonSchemaRepository;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;
import se.sundsvall.jsonschema.service.mapper.UiSchemaMapper;

@Service
public class UiSchemaStorageService {

	private final JsonSchemaRepository jsonSchemaRepository;

	public UiSchemaStorageService(final JsonSchemaRepository jsonSchemaRepository) {
		this.jsonSchemaRepository = jsonSchemaRepository;
	}

	/**
	 * Get UI schema by JSON schema ID.
	 *
	 * @param  jsonSchemaId     the JSON schema ID
	 * @return                  a {@link UiSchema}
	 * @throws ThrowableProblem if the JSON schema or UI schema does not exist
	 */
	@Transactional(readOnly = true)
	public UiSchema getSchema(final String municipalityId, final String jsonSchemaId) {

		final var jsonSchemaEntity = fetchJsonSchemaEntity(municipalityId, jsonSchemaId);

		return Optional.ofNullable(jsonSchemaEntity.getUiSchema())
			.map(UiSchemaMapper::toUiSchema)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, MESSAGE_UI_SCHEMA_NOT_FOUND_BY_JSON_SCHEMA_ID.formatted(jsonSchemaId)));
	}

	/**
	 * Create new UI schema or replace an existing one.
	 *
	 * @param municipalityId the municipality ID
	 * @param jsonSchemaId   the JSON schema ID
	 * @param request        the UI schema request
	 */
	@Transactional
	public void createOrReplace(final String municipalityId, final String jsonSchemaId, final UiSchemaRequest request) {

		final var jsonSchemaEntity = fetchJsonSchemaEntity(municipalityId, jsonSchemaId);
		final var uiSchemaEntity = toUiSchemaEntity(jsonSchemaEntity, request);

		jsonSchemaRepository.save(jsonSchemaEntity.withUiSchema(uiSchemaEntity));
	}

	/**
	 * Delete an existing UI schema.
	 *
	 * @param  municipalityId                       the municipality ID
	 * @param  jsonSchemaId                         the JSON schema ID
	 * @throws org.zalando.problem.ThrowableProblem if not found or referenced
	 */
	@Transactional
	public void delete(final String municipalityId, final String jsonSchemaId) {
		final var jsonSchemaEntity = fetchJsonSchemaEntity(municipalityId, jsonSchemaId);

		if (isNull(jsonSchemaEntity.getUiSchema())) {
			throw Problem.valueOf(NOT_FOUND, MESSAGE_UI_SCHEMA_NOT_FOUND_BY_JSON_SCHEMA_ID.formatted(jsonSchemaId));
		}

		jsonSchemaRepository.save(jsonSchemaEntity.withUiSchema(null));
	}

	// ---- Private helpers ------------------------------------------------------

	private JsonSchemaEntity fetchJsonSchemaEntity(final String municipalityId, final String jsonSchemaId) {
		return jsonSchemaRepository.findByMunicipalityIdAndId(municipalityId, jsonSchemaId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID.formatted(jsonSchemaId)));
	}
}
