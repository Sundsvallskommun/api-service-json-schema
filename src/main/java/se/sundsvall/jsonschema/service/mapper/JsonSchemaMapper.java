package se.sundsvall.jsonschema.service.mapper;

import static java.util.Collections.emptyList;
import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonNode;
import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonString;

import java.util.List;
import java.util.Optional;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaRequest;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

public final class JsonSchemaMapper {

	private static final String ID_PATTERN = "%s_%s_%s"; // [municipality_id]_[schema_name]_[schema_version]

	private JsonSchemaMapper() {}

	public static JsonSchema toJsonSchema(JsonSchemaEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> JsonSchema.create()
				.withCreated(e.getCreated())
				.withDescription(e.getDescription())
				.withId(e.getId())
				.withLastUsedForValidation(e.getLastUsedForValidation())
				.withName(e.getName())
				.withValidationUsageCount(e.getValidationUsageCount())
				.withValue(toJsonNode(e.getValue()))
				.withVersion(e.getVersion()))
			.orElse(null);
	}

	public static List<JsonSchema> toJsonSchemaList(List<JsonSchemaEntity> entityList) {
		return Optional.ofNullable(entityList).orElse(emptyList()).stream()
			.map(JsonSchemaMapper::toJsonSchema)
			.toList();
	}

	public static JsonSchemaEntity toJsonSchemaEntity(String municipalityId, JsonSchemaRequest request) {
		final var id = ID_PATTERN.formatted(municipalityId, request.getName(), request.getVersion()).toLowerCase();
		return JsonSchemaEntity.create()
			.withDescription(request.getDescription())
			.withId(id)
			.withMunicipalityId(municipalityId)
			.withName(request.getName().toLowerCase())
			.withValue(toJsonString(request.getValue()))
			.withVersion(request.getVersion());
	}
}
