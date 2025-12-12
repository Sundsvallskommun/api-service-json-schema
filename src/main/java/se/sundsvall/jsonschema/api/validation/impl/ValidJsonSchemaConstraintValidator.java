package se.sundsvall.jsonschema.api.validation.impl;

import static com.networknt.schema.SpecificationVersion.DRAFT_2020_12;
import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.hasText;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.dialect.DialectId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import org.springframework.util.StringUtils;
import se.sundsvall.jsonschema.api.validation.ValidJsonSchema;
import se.sundsvall.jsonschema.service.JsonSchemaValidationService;

public class ValidJsonSchemaConstraintValidator implements ConstraintValidator<ValidJsonSchema, JsonNode> {

	private static final String SUPPORTED_SCHEMA_SPECIFICATION = DialectId.DRAFT_2020_12;
	private static final SchemaRegistry REGISTRY = SchemaRegistry.withDefaultDialect(DRAFT_2020_12);

	private final JsonSchemaValidationService jsonSchemaValidationService;
	private boolean nullable;

	public ValidJsonSchemaConstraintValidator(JsonSchemaValidationService jsonSchemaValidationService) {
		this.jsonSchemaValidationService = jsonSchemaValidationService;
	}

	@Override
	public void initialize(final ValidJsonSchema constraintAnnotation) {
		this.nullable = constraintAnnotation.nullable();
	}

	@Override
	public boolean isValid(final JsonNode inputJsonSchema, final ConstraintValidatorContext context) {
		if (isNull(inputJsonSchema) && this.nullable) {
			return true;
		}

		// Assert valid JSON
		if (!isValidJson(inputJsonSchema, context)) {
			return false;
		}

		// Assert valid specification version. Defined in SUPPORTED_SCHEMA_SPECIFICATION.
		if (!hasSupportedSpecification(inputJsonSchema, context)) {
			return false;
		}

		// Assert JSON-schema against meta schema.
		final var metaSchema = REGISTRY.getSchema(SchemaLocation.of(SUPPORTED_SCHEMA_SPECIFICATION));
		final var validationMessages = jsonSchemaValidationService.validate(inputJsonSchema.toString(), metaSchema);

		validationMessages.forEach(message -> addViolation(Optional.ofNullable(message.getInstanceLocation()).map(Object::toString).filter(StringUtils::hasText).map(value -> value + ": ").orElse("") + message.getMessage(), context));

		return validationMessages.isEmpty();
	}

	// ---- Private helpers ------------------------------------------------------

	private void addViolation(String value, ConstraintValidatorContext constraintContext) {
		constraintContext.disableDefaultConstraintViolation();
		constraintContext.buildConstraintViolationWithTemplate(value).addConstraintViolation();
	}

	private boolean isValidJson(JsonNode inputJsonSchema, ConstraintValidatorContext constraintContext) {
		if (isNull(inputJsonSchema)) {
			addViolation("must be valid JSON, but was null", constraintContext);
			return false;
		}

		if (!hasText(inputJsonSchema.toString())) {
			addViolation("must be valid JSON, but was empty", constraintContext);
			return false;
		}

		return true;
	}

	private boolean hasSupportedSpecification(JsonNode inputJsonSchema, ConstraintValidatorContext constraintContext) {
		try {
			final var schemaNodeValue = Optional.ofNullable(inputJsonSchema.get("$schema"))
				.map(JsonNode::asText)
				.orElse(null);

			if (!SUPPORTED_SCHEMA_SPECIFICATION.equals(schemaNodeValue)) {
				addViolation("Wrong value in $schema-node. Expected: '%s' Found: '%s'".formatted(SUPPORTED_SCHEMA_SPECIFICATION, schemaNodeValue), constraintContext);
				return false;
			}
		} catch (Exception e) {
			addViolation("Could not determine schema specification: [%s]".formatted(e.getMessage()), constraintContext);
			return false;
		}
		return true;
	}
}
