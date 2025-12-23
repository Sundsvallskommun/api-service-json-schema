package se.sundsvall.jsonschema.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import se.sundsvall.jsonschema.TestFactory;

class UiSchemaMapperTest {

	@Test
	void toUiSchema() throws Exception {

		// Arrange
		final var entity = TestFactory.getUiSchemaEntity();

		// Act
		final var result = UiSchemaMapper.toUiSchema(entity);

		// Assert
		assertThat(result.getId()).isEqualTo(entity.getId());
		assertThat(result.getDescription()).isEqualTo(entity.getDescription());
		assertThat(result.getCreated()).isEqualTo(entity.getCreated());
		assertThat(result.getValue()).isEqualTo(new ObjectMapper().readTree(entity.getValue()));
	}

	@Test
	void toUiSchemaWhenInputIsNull() {

		// Act
		final var result = UiSchemaMapper.toUiSchema(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toUiSchemaEntity() {

		// Arrange
		final var uiSchemaCreateRequest = TestFactory.getUiSchemaCreateRequest();
		final var jsonSchemaEntity = TestFactory.getJsonSchemaEntity();

		// Act
		final var result = UiSchemaMapper.toUiSchemaEntity(jsonSchemaEntity, uiSchemaCreateRequest);

		// Assert
		assertThat(result.getCreated()).isNull();
		assertThat(result.getDescription()).isEqualTo(uiSchemaCreateRequest.getDescription());
		assertThat(result.getValue()).isEqualTo(uiSchemaCreateRequest.getValue().toString());
		assertThat(result.getJsonSchema()).isEqualTo(jsonSchemaEntity);
	}
}
