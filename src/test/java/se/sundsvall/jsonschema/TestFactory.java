package se.sundsvall.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

public final class TestFactory {

	private TestFactory() {}

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

		JsonNode value = null;
		try {
			value = new ObjectMapper().readTree("{\"$schema\": \"https://json-schema.org/draft/2020-12/schema\"}");
		} catch (Exception _) {
			// Should not happen
		}

		return JsonSchemaCreateRequest.create()
			.withDescription("description")
			.withName("Person_Schema")
			.withValue(value)
			.withVersion("1.0");
	}
}
