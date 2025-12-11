package se.sundsvall.jsonschema.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.networknt.schema.Error;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.jsonschema.integration.db.JsonSchemaRepository;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

@SpringBootTest(classes = {
	JsonSchemaCache.class,
	JsonSchemaValidationService.class
})
@ActiveProfiles(value = "junit")
@ExtendWith(ResourceLoaderExtension.class)
class JsonSchemaValidationServiceTest {

	private static final String VALID_SCHEMA = "files/jsonschema/valid_schema.json";
	private static final String VALID_JSON = "files/jsonschema/valid_json.json";
	private static final String INVALID_JSON_MISSING_ALL_PROPERTIES = "files/jsonschema/invalid_json_missing_all_properties.json";
	private static final String INVALID_JSON_BAD_DATATYPE_ON_PROPERTY = "files/jsonschema/invalid_json_bad_datatype_on_property.json";
	private static final String INVALID_JSON_NON_UNIQUE_TAGS = "files/jsonschema/invalid_json_non_unique_tags.json";
	private static final String INVALID_JSON_MISC_ERRORS = "files/jsonschema/invalid_json_misc_errors.json";

	@MockitoBean
	private JsonSchemaRepository jsonSchemaRepositoryMock;

	@Autowired
	private JsonSchemaValidationService jsonSchemaValidationService;

	@Test
	void validateWithValidJson(@Load(VALID_SCHEMA) final String schema, @Load(VALID_JSON) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages).isEmpty();
	}

	@Test
	void validateWithAllMissingProperties(@Load(VALID_SCHEMA) final String schema, @Load(INVALID_JSON_MISSING_ALL_PROPERTIES) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages)
			.isNotEmpty()
			.extracting(e -> Optional.ofNullable(e.getInstanceLocation())
				.map(Object::toString)
				.orElse(null),
				Error::getMessage)
			.containsExactly(
				tuple("", "required property 'productId' not found"),
				tuple("", "required property 'productName' not found"),
				tuple("", "required property 'price' not found"));
	}

	@Test
	void validateWithBadDatatypeOnProperty(@Load(VALID_SCHEMA) final String schema, @Load(INVALID_JSON_BAD_DATATYPE_ON_PROPERTY) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages)
			.isNotEmpty()
			.extracting(e -> Optional.ofNullable(e.getInstanceLocation())
				.map(Object::toString)
				.orElse(null),
				Error::getMessage)
			.containsExactly(
				tuple("/productId", "string found, integer expected"));
	}

	@Test
	void validateWithNonUniqueTags(@Load(VALID_SCHEMA) final String schema, @Load(INVALID_JSON_NON_UNIQUE_TAGS) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages)
			.isNotEmpty()
			.extracting(e -> Optional.ofNullable(e.getInstanceLocation())
				.map(Object::toString)
				.orElse(null),
				Error::getMessage)
			.containsExactly(tuple("/tags", "must have only unique items in the array"));
	}

	@Test
	void validateWithMiscErrors(@Load(VALID_SCHEMA) final String schema, @Load(INVALID_JSON_MISC_ERRORS) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages)
			.isNotEmpty()
			.extracting(e -> Optional.ofNullable(e.getInstanceLocation())
				.map(Object::toString)
				.orElse(null),
				Error::getMessage)
			.containsExactly(
				tuple("/price", "must have an exclusive minimum value of 0"),
				tuple("/tags/5", "integer found, string expected"),
				tuple("/tags", "must have only unique items in the array"),
				tuple("", "required property 'productName' not found"));
	}

	@Test
	void validateAndThrowWithMiscErrors(@Load(VALID_SCHEMA) final String schema, @Load(INVALID_JSON_MISC_ERRORS) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var exception = assertThrows(ConstraintViolationProblem.class, () -> jsonSchemaValidationService.validateAndThrow(json, schemaId));

		// Assert
		assertThat(exception)
			.isNotNull()
			.hasMessage("Constraint Violation");

		// Assert
		assertThat(exception.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(
				tuple("/price", "must have an exclusive minimum value of 0"),
				tuple("/tags/5", "integer found, string expected"),
				tuple("/tags", "must have only unique items in the array"),
				tuple("", "required property 'productName' not found"));
	}

	@Test
	void validateAndThrowWithValidJson(@Load(VALID_SCHEMA) final String schema, @Load(VALID_JSON) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		assertDoesNotThrow(() -> jsonSchemaValidationService.validateAndThrow(json, schemaId));
	}

	@Test
	void validateBySchemaIdWithValidJson(@Load(VALID_SCHEMA) final String schema, @Load(VALID_JSON) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema)));

		// Act
		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages).isEmpty();
		verify(jsonSchemaRepositoryMock).findById(schemaId);
	}

	@Test
	void validateBySchemaIdWithMiscErrors(@Load(VALID_SCHEMA) final String schema, @Load(INVALID_JSON_MISC_ERRORS) final String json) {

		// Arrange
		final var schemaId = "schemaId";
		final var jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(schemaId)
			.withValue(schema);

		when(jsonSchemaRepositoryMock.findById(schemaId)).thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var validationMessages = jsonSchemaValidationService.validate(json, schemaId);

		// Assert
		assertThat(validationMessages)
			.isNotEmpty()
			.extracting(e -> Optional.ofNullable(e.getInstanceLocation())
				.map(Object::toString)
				.orElse(null),
				Error::getMessage)
			.containsExactly(
				tuple("/price", "must have an exclusive minimum value of 0"),
				tuple("/tags/5", "integer found, string expected"),
				tuple("/tags", "must have only unique items in the array"),
				tuple("", "required property 'productName' not found"));

		verify(jsonSchemaRepositoryMock).findById(schemaId);
	}
}
