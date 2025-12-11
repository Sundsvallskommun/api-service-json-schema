package se.sundsvall.jsonschema.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.jsonschema.TestFactory;

class JsonSchemaMapperTest {

	@Test
	void toJsonSchema() throws Exception {

		// Arrange
		final var entity = TestFactory.getJsonSchemaEntity();

		// Act
		final var result = JsonSchemaMapper.toJsonSchema(entity);

		// Assert
		assertThat(result.getId()).isEqualTo(entity.getId());
		assertThat(result.getName()).isEqualTo(entity.getName());
		assertThat(result.getVersion()).isEqualTo(entity.getVersion());
		assertThat(result.getDescription()).isEqualTo(entity.getDescription());
		assertThat(result.getCreated()).isEqualTo(entity.getCreated());
		assertThat(result.getValue()).isEqualTo(new ObjectMapper().readTree(entity.getValue()));
	}

	@Test
	void toJsonSchemaWhenInputIsNull() {

		// Act
		final var result = JsonSchemaMapper.toJsonSchema(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toJsonSchemaList() throws Exception {

		// Arrange
		final var entity = TestFactory.getJsonSchemaEntity();
		final var entityList = List.of(entity);

		// Act
		final var result = JsonSchemaMapper.toJsonSchemaList(entityList);

		// Assert
		assertThat(result).hasSize(1);

		final var schema = result.getFirst();
		assertThat(schema.getId()).isEqualTo(entity.getId());
		assertThat(schema.getName()).isEqualTo(entity.getName());
		assertThat(schema.getVersion()).isEqualTo(entity.getVersion());
		assertThat(schema.getDescription()).isEqualTo(entity.getDescription());
		assertThat(schema.getCreated()).isEqualTo(entity.getCreated());
		assertThat(schema.getValue()).isEqualTo(new ObjectMapper().readTree(entity.getValue()));
	}

	@Test
	void toJsonSchemaListWhenInputIsNull() {

		// Act
		final var result = JsonSchemaMapper.toJsonSchemaList(null);

		// Assert
		assertThat(result).isEmpty();
	}

	@Test
	void toJsonSchemaEntity() {

		// Arrange
		final var municipalityId = "2281";
		final var jsonSchemaCreateRequest = TestFactory.getJsonSchemaCreateRequest();

		// Act
		final var result = JsonSchemaMapper.toJsonSchemaEntity(municipalityId, jsonSchemaCreateRequest);

		// Assert
		assertThat(result.getCreated()).isNull();
		assertThat(result.getDescription()).isEqualTo(jsonSchemaCreateRequest.getDescription());
		assertThat(result.getId())
			.isEqualToIgnoringCase("%s_%s_%s".formatted(municipalityId, jsonSchemaCreateRequest.getName(), jsonSchemaCreateRequest.getVersion()))
			.isLowerCase();
		assertThat(result.getName())
			.isEqualToIgnoringCase(jsonSchemaCreateRequest.getName())
			.isLowerCase();
		assertThat(result.getValue()).isEqualTo(jsonSchemaCreateRequest.getValue().toString());
		assertThat(result.getVersion()).isEqualTo(jsonSchemaCreateRequest.getVersion());
	}
}
