package se.sundsvall.jsonschema.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Schema;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.test.annotation.resource.Load;
import se.sundsvall.dept44.test.extension.ResourceLoaderExtension;
import se.sundsvall.jsonschema.service.JsonSchemaValidationService;

@ExtendWith({
	MockitoExtension.class, ResourceLoaderExtension.class
})
class ValidJsonSchemaConstraintValidatorTest {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String VALID_SCHEMA = "files/jsonschema/valid_schema.json";
	private static final String INVALID_SCHEMA_WRONG_TYPE1 = "files/jsonschema/invalid_schema_wrong_type1.json";
	private static final String INVALID_SCHEMA_WRONG_TYPE2 = "files/jsonschema/invalid_schema_wrong_type2.json";
	private static final String INVALID_SCHEMA_WRONG_TYPE3 = "files/jsonschema/invalid_schema_wrong_type3.json";
	private static final String INVALID_SCHEMA_WRONG_SPECIFICATION = "files/jsonschema/invalid_schema_wrong_schema_specification.json";

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@Mock
	private ConstraintViolationBuilder constraintViolationBuilderMock;

	@Mock(answer = CALLS_REAL_METHODS)
	private JsonSchemaValidationService jsonSchemaValidationServiceMock;

	@InjectMocks
	private ValidJsonSchemaConstraintValidator validator;

	@Test
	void validateValidJsonSchema(@Load(VALID_SCHEMA) final String schemaString) throws Exception {

		// Arrange
		final var schemaJsonNode = OBJECT_MAPPER.readTree(schemaString);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isTrue();
		verify(jsonSchemaValidationServiceMock).validate(eq(schemaJsonNode.toString()), any(Schema.class));
		verifyNoInteractions(constraintValidatorContextMock, constraintViolationBuilderMock);
	}

	@Test
	void validateInvalidJsonSchemaWhenWrongType1(@Load(INVALID_SCHEMA_WRONG_TYPE1) final String schemaString) throws Exception {

		// Arrange
		final var schemaJsonNode = OBJECT_MAPPER.readTree(schemaString);

		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isFalse();
		verify(jsonSchemaValidationServiceMock).validate(eq(schemaJsonNode.toString()), any(Schema.class));
		verify(constraintValidatorContextMock, times(2)).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("/type: does not have a value in the enumeration [\"array\", \"boolean\", \"integer\", \"null\", \"number\", \"object\", \"string\"]");
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("/type: string found, array expected");
		verify(constraintViolationBuilderMock, times(2)).addConstraintViolation();
	}

	@Test
	void validateInvalidJsonSchemaWhenWrongType2(@Load(INVALID_SCHEMA_WRONG_TYPE2) final String schemaString) throws Exception {

		// Arrange
		final var schemaJsonNode = OBJECT_MAPPER.readTree(schemaString);

		// Arrange
		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isFalse();
		verify(jsonSchemaValidationServiceMock).validate(eq(schemaJsonNode.toString()), any(Schema.class));
		verify(constraintValidatorContextMock, times(8)).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock, times(8)).buildConstraintViolationWithTemplate("/additionalProperties: string found, [object, boolean] expected");
		verify(constraintViolationBuilderMock, times(8)).addConstraintViolation();
	}

	@Test
	void validateInvalidJsonSchemaWhenWrongType3(@Load(INVALID_SCHEMA_WRONG_TYPE3) final String schemaString) throws Exception {

		// Arrange
		final var schemaJsonNode = OBJECT_MAPPER.readTree(schemaString);

		// Arrange
		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isFalse();
		verify(jsonSchemaValidationServiceMock).validate(eq(schemaJsonNode.toString()), any(Schema.class));
		verify(constraintValidatorContextMock, times(2)).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("/type: does not have a value in the enumeration [\"array\", \"boolean\", \"integer\", \"null\", \"number\", \"object\", \"string\"]");
		verify(constraintViolationBuilderMock, times(2)).addConstraintViolation();
	}

	@Test
	void validateInvalidJsonSchemaWhenWrongSchemaSpecification(@Load(INVALID_SCHEMA_WRONG_SPECIFICATION) final String schemaString) throws Exception {

		// Arrange
		final var schemaJsonNode = OBJECT_MAPPER.readTree(schemaString);

		// Arrange
		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isFalse();
		verify(jsonSchemaValidationServiceMock, never()).validate(any(), anyString());
		verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("Wrong value in $schema-node. Expected: 'https://json-schema.org/draft/2020-12/schema' Found: 'https://json-schema.org/draft/invalid/schema'");
		verify(constraintViolationBuilderMock).addConstraintViolation();
	}

	@Test
	void validateBlankJsonSchemaValues() throws Exception {

		// Arrange
		final var schemaJsonNode = OBJECT_MAPPER.readTree("");

		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isFalse();
		verify(jsonSchemaValidationServiceMock, never()).validate(any(), anyString());
		verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("must be valid JSON, but was empty");
		verify(constraintViolationBuilderMock).addConstraintViolation();
	}

	void validateNullJsonSchemaValues() {

		// Arrange
		final var schemaJsonNode = (JsonNode) null;

		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		// Act
		final var result = validator.isValid(schemaJsonNode, constraintValidatorContextMock);

		// Assert
		assertThat(result).isFalse();
		verify(jsonSchemaValidationServiceMock, never()).validate(any(), anyString());
		verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("must be valid JSON, but was blank");
		verify(constraintViolationBuilderMock).addConstraintViolation();
	}
}
