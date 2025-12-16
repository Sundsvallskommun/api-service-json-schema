package se.sundsvall.jsonschema.service;

import static com.networknt.schema.InputFormat.JSON;
import static java.util.Locale.ENGLISH;
import static java.util.Optional.ofNullable;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.jsonschema.service.Constants.MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID;

import com.networknt.schema.Error;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.Schema;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.jsonschema.integration.db.JsonSchemaRepository;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

@Service
public class JsonSchemaValidationService {

	private static final Locale LOCALE = ENGLISH;

	private final JsonSchemaRepository jsonSchemaRepository;
	private final JsonSchemaCache jsonSchemaCache;

	public JsonSchemaValidationService(final JsonSchemaRepository jsonSchemaRepository, final JsonSchemaCache jsonSchemaCache) {
		this.jsonSchemaRepository = jsonSchemaRepository;
		this.jsonSchemaCache = jsonSchemaCache;
	}

	/**
	 * Validates input JSON against a schema by ID.
	 *
	 * @param  input    JSON input
	 * @param  schemaId schema ID
	 * @return          validation messages (empty if valid)
	 */
	public List<Error> validate(String input, String schemaId) {
		return validate(input, resolveSchema(schemaId));
	}

	/**
	 * Validates input JSON against a schema.
	 *
	 * @param  input  JSON input
	 * @param  schema JsonSchema
	 * @return        validation messages (empty if valid)
	 */
	public List<Error> validate(String input, Schema schema) {
		return ofNullable(schema.validate(input, JSON, JsonSchemaValidationService::configureExecutionContext))
			.orElseGet(Collections::emptyList);
	}

	/**
	 * Validates input JSON against a schema by ID and throws on errors.
	 *
	 * @param  input                      JSON input
	 * @param  schemaId                   schema ID
	 * @throws ConstraintViolationProblem BAD_REQUEST if input is invalid
	 */
	public void validateAndThrow(String input, String schemaId) {
		validateAndThrow(input, resolveSchema(schemaId));
	}

	/**
	 * Validates input JSON against a schema and throws on errors.
	 *
	 * @param  input                      JSON input
	 * @param  schema                     JsonSchema
	 * @throws ConstraintViolationProblem BAD_REQUEST if input is invalid
	 */
	public void validateAndThrow(String input, Schema schema) {
		final var violations = validate(input, schema).stream()
			.map(error -> new Violation(Optional.ofNullable(error.getInstanceLocation()).map(Object::toString).orElse(""), error.getMessage()))
			.toList();

		if (!violations.isEmpty()) {
			throw new ConstraintViolationProblem(BAD_REQUEST, violations);
		}
	}

	// ---- Private helpers ------------------------------------------------------

	private Schema resolveSchema(String schemaId) {
		final var entity = jsonSchemaRepository.findById(schemaId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, MESSAGE_JSON_SCHEMA_NOT_FOUND_BY_ID.formatted(schemaId)));

		registerValidationAttempt(entity);

		return jsonSchemaCache.getSchema(entity);
	}

	private void registerValidationAttempt(final JsonSchemaEntity entity) {
		jsonSchemaRepository.save(entity
			.withLastUsedForValidation(OffsetDateTime.now())
			.withValidationUsageCount(entity.getValidationUsageCount() + 1));
	}

	private static void configureExecutionContext(ExecutionContext executionContext) {
		executionContext.executionConfig(config -> config
			.annotationCollectionEnabled(true)
			.annotationCollectionFilter(_ -> true)
			.locale(LOCALE)
			.formatAssertionsEnabled(true));
	}
}
