package se.sundsvall.jsonschema.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

public final class JsonSchemaMapper {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String ID_PATTERN = "%s_%s_%s"; // [municipality_id]_[schema_name]_[schema_version]

	private JsonSchemaMapper() {}

	public static JsonSchema toJsonSchema(JsonSchemaEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> JsonSchema.create()
				.withCreated(e.getCreated())
				.withDescription(e.getDescription())
				.withId(e.getId())
				.withName(e.getName())
				.withValue(parseJsonNode(e.getValue()))
				.withVersion(e.getVersion()))
			.orElse(null);
	}

	public static List<JsonSchema> toJsonSchemaList(List<JsonSchemaEntity> entityList) {
		return Optional.ofNullable(entityList).orElse(emptyList()).stream()
			.map(JsonSchemaMapper::toJsonSchema)
			.toList();
	}

	public static JsonSchemaEntity toJsonSchemaEntity(String municipalityId, JsonSchemaCreateRequest request) {

		final var id = ID_PATTERN.formatted(municipalityId, request.getName(), request.getVersion()).toLowerCase();

		return JsonSchemaEntity.create()
			.withDescription(request.getDescription())
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withName(request.getName().toLowerCase())
			.withValue(writeJsonNode(request.getValue()))
			.withVersion(request.getVersion());
	}

	// ---- Private helpers ------------------------------------------------------

	/**
	 * Converts a JSON string into a JsonNode, throwing an IllegalArgumentException on error.
	 */
	private static JsonNode parseJsonNode(String json) {
		if (isNull(json)) {
			return null;
		}
		try {
			return OBJECT_MAPPER.readTree(json);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to parse JSON schema value", e);
		}
	}

	/**
	 * Converts a JsonNode into a String, handling nulls.
	 */
	private static String writeJsonNode(JsonNode node) {
		if (isNull(node)) {
			return null;
		}
		try {
			return OBJECT_MAPPER.writeValueAsString(node);
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to write JSON schema value", e);
		}
	}
}
