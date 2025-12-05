package se.sundsvall.jsonschema.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.jsonschema.Application;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class JsonSchemaResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSchemas() {

		// Arrange
		final var jsonSchemas = List.of(JsonSchema.create());

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/jsonschemas", MUNICIPALITY_ID)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<List<JsonSchema>>() {}).returnResult().getResponseBody();

		// Assert
		assertThat(response).isEqualTo(jsonSchemas);

		// TODO: Verifications
	}

	void getSchema() {

		// Arrange
		final var id = "some-schema-id";
		final var jsonSchema = JsonSchema.create();

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/jsonschemas/{id}", MUNICIPALITY_ID, id)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(JsonSchema.class).returnResult().getResponseBody();

		// Assert
		assertThat(response).isEqualTo(jsonSchema);

		// TODO: Verifications
	}

	@Test
	void getLatestSchemaByName() {

		// Arrange
		final var name = "some-schema-name";
		final var jsonSchema = JsonSchema.create();

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/jsonschemas/{name}/latest", MUNICIPALITY_ID, name)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(JsonSchema.class).returnResult().getResponseBody();

		// Assert
		assertThat(response).isEqualTo(jsonSchema);

		// TODO: Verifications
	}

	@Test
	void createSchema() {

		// Arrange
		final var id = "some-schema-id";
		final var body = JsonSchemaCreateRequest.create()
			.withDescription("description")
			.withName("name")
			.withValue("{\"$schema\": \"https://json-schema.org/draft/2020-12/schema\"}")
			.withVersion("1.0");

		// Act
		webTestClient.post()
			.uri("/{municipalityId}/jsonschemas", MUNICIPALITY_ID)
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location("/" + MUNICIPALITY_ID + "/jsonschemas/" + id);

		// TODO: Verifications
	}

	@Test
	void deleteSchema() {

		// Arrange
		final var id = "some-schema-id";

		// Act
		webTestClient.delete()
			.uri("/{municipalityId}/jsonschemas/{id}", MUNICIPALITY_ID, id)
			.exchange()
			.expectStatus().isNoContent();

		// TODO: Verifications
	}
}
