package se.sundsvall.jsonschema.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UncheckedIOException;
import org.junit.jupiter.api.Test;

class JsonMapperTest {

	@Test
	void toJsonNode() {

		// Arrange
		String json = """
			{
			  "firstName": "Joe",
			  "age": 42
			}
			""";

		// Act
		final var result = JsonMapper.toJsonNode(json);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.get("firstName").asText()).isEqualTo("Joe");
		assertThat(result.get("age").asInt()).isEqualTo(42);
	}

	@Test
	void toJsonNodeShouldReturnNullWhenInputIsNull() {

		// Act
		final var result = JsonMapper.toJsonNode(null);

		// Assert
		assertThat(result).isNull();
	}

	@Test
	void toJsonNodeShouldThrowIllegalArgumentExceptionWhenJsonIsInvalid() {

		// Arrange
		final var invalidJson = """
			{ "firstName":
			""";

		// Act + Assert
		assertThatThrownBy(() -> JsonMapper.toJsonNode(invalidJson))
			.isInstanceOf(UncheckedIOException.class);
	}

	@Test
	void toJsonString() {

		// Arrange
		final var node = JsonMapper.toJsonNode("""
			{
			  "city": "Sundsvall"
			}
			""");

		// Act
		String result = JsonMapper.toJsonString(node);

		// Assert
		assertThat(result)
			.isNotNull()
			.contains("\"city\":\"Sundsvall\"");
	}

	@Test
	void toJsonStringShouldReturnNullWhenNodeIsNull() {

		// Act
		String result = JsonMapper.toJsonString(null);

		// Assert
		assertThat(result).isNull();
	}
}
