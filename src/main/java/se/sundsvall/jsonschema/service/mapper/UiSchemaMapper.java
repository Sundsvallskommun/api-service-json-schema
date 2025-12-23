package se.sundsvall.jsonschema.service.mapper;

import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonNode;
import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonString;

import java.util.Optional;
import se.sundsvall.jsonschema.api.model.UiSchema;
import se.sundsvall.jsonschema.api.model.UiSchemaRequest;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;
import se.sundsvall.jsonschema.integration.db.model.UiSchemaEntity;

public final class UiSchemaMapper {

	private UiSchemaMapper() {}

	public static UiSchema toUiSchema(UiSchemaEntity entity) {
		return Optional.ofNullable(entity)
			.map(e -> UiSchema.create()
				.withCreated(e.getCreated())
				.withDescription(e.getDescription())
				.withId(e.getId())
				.withValue(toJsonNode(e.getValue())))
			.orElse(null);
	}

	public static UiSchemaEntity toUiSchemaEntity(JsonSchemaEntity jsonSchemaEntity, UiSchemaRequest request) {
		return UiSchemaEntity.create()
			.withJsonSchema(jsonSchemaEntity)
			.withDescription(request.getDescription())
			.withValue(toJsonString(request.getValue()));
	}
}
