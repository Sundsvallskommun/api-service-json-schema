package se.sundsvall.jsonschema.service.mapper;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Optional;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

public final class JsonSchemaMapper {

	private static final String ID_PATTERN = "%s_%s_%s"; // [municipality_id]_[schema_name]_[schema_version]

	private JsonSchemaMapper() {}

	public static JsonSchema toJsonSchema(JsonSchemaEntity jsonSchemaEntity) {
		return Optional.ofNullable(jsonSchemaEntity)
			.map(entity -> JsonSchema.create()
				.withCreated(entity.getCreated())
				.withDescription(entity.getDescription())
				.withId(entity.getId())
				.withName(entity.getName())
				.withValue(entity.getValue())
				.withVersion(entity.getVersion()))
			.orElse(null);
	}

	public static List<JsonSchema> toJsonSchemaList(List<JsonSchemaEntity> jsonSchemaEntityList) {
		return Optional.ofNullable(jsonSchemaEntityList).orElse(emptyList()).stream()
			.map(JsonSchemaMapper::toJsonSchema)
			.toList();
	}

	public static JsonSchemaEntity toJsonSchemaEntity(String municipalityId, JsonSchemaCreateRequest jsonSchemaCreateRequest) {
		return JsonSchemaEntity.create()
			.withDescription(jsonSchemaCreateRequest.getDescription())
			.withId(ID_PATTERN.formatted(municipalityId, jsonSchemaCreateRequest.getName(), jsonSchemaCreateRequest.getVersion()).toLowerCase())
			.withMunicipalityId(municipalityId)
			.withName(jsonSchemaCreateRequest.getName().toLowerCase())
			.withValue(jsonSchemaCreateRequest.getValue())
			.withVersion(jsonSchemaCreateRequest.getVersion());
	}
}
