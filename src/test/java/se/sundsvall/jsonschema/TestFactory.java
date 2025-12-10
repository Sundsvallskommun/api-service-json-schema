package se.sundsvall.jsonschema;

import java.time.OffsetDateTime;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

public final class TestFactory {

	public static JsonSchemaEntity getJsonSchemaEntity() {
		return JsonSchemaEntity.create()
			.withCreated(OffsetDateTime.now())
			.withDescription("description")
			.withId("2281_person_schema_1.0.0")
			.withMunicipalityId("2281")
			.withName("person_schema")
			.withValue("{}")
			.withVersion("1.0");
	}

	public static JsonSchemaCreateRequest getJsonSchemaCreateRequest() {
		return JsonSchemaCreateRequest.create()
			.withDescription("description")
			.withName("Person_Schema")
			.withValue("{}")
			.withVersion("1.0");
	}
}
