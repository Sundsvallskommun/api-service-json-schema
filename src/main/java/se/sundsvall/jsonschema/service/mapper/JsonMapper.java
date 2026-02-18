package se.sundsvall.jsonschema.service.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

import static org.jooq.lambda.Unchecked.function;

public final class JsonMapper {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private JsonMapper() {}

	/**
	 * Converts a JSON string into a JsonNode, throwing an IllegalArgumentException on error.
	 */
	public static JsonNode toJsonNode(String json) {
		return Optional.ofNullable(json)
			.map(function(OBJECT_MAPPER::readTree))
			.orElse(null);
	}

	/**
	 * Converts a JsonNode into a String, handling nulls.
	 */
	public static String toJsonString(JsonNode node) {
		return Optional.ofNullable(node)
			.map(function(OBJECT_MAPPER::writeValueAsString))
			.orElse(null);
	}
}
