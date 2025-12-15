package se.sundsvall.jsonschema.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.jsonschema.Application;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.service.JsonSchemaStorageService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class JsonSchemaStorageResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private JsonSchemaStorageService jsonSchemaStorageServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSchemas() {

		// Arrange
		final var pageable = PageRequest.of(0, 20);
		final var matches = new PageImpl<>(List.of(JsonSchema.create().withId("schema_1.0")), pageable, 20);

		when(jsonSchemaStorageServiceMock.getSchemas(MUNICIPALITY_ID, pageable)).thenReturn(matches);

		// Act
		webTestClient.get()
			.uri("/{municipalityId}/jsonschemas", MUNICIPALITY_ID)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.content.length()").isEqualTo(1)
			.jsonPath("$.totalElements").isEqualTo(20)
			.jsonPath("$.pageable.pageNumber").isEqualTo(0)
			.jsonPath("$.size").isEqualTo(20);

		// Assert
		verify(jsonSchemaStorageServiceMock).getSchemas(MUNICIPALITY_ID, pageable);
	}

	@Test
	void getSchema() {

		// Arrange
		final var id = "some-schema-id";
		final var jsonSchema = JsonSchema.create().withId("schema_1.0");

		when(jsonSchemaStorageServiceMock.getSchema(MUNICIPALITY_ID, id)).thenReturn(jsonSchema);

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
		verify(jsonSchemaStorageServiceMock).getSchema(MUNICIPALITY_ID, id);
	}

	@Test
	void getLatestSchemaByName() {

		// Arrange
		final var name = "some-schema-name";
		final var jsonSchema = JsonSchema.create().withId("schema_1.0");

		when(jsonSchemaStorageServiceMock.getLatestSchemaByName(MUNICIPALITY_ID, name)).thenReturn(jsonSchema);

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/jsonschemas/{name}/versions/latest", MUNICIPALITY_ID, name)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(JsonSchema.class).returnResult().getResponseBody();

		// Assert
		assertThat(response).isEqualTo(jsonSchema);

		verify(jsonSchemaStorageServiceMock).getLatestSchemaByName(MUNICIPALITY_ID, name);
	}

	@Test
	void createSchema() throws Exception {

		// Arrange
		final var id = "schema_1.0";
		final var name = "schema";
		final var jsonSchema = JsonSchema.create().withId(id);
		final var body = JsonSchemaCreateRequest.create()
			.withDescription("description")
			.withName(name)
			.withValue(new ObjectMapper().readTree("{\"$schema\": \"https://json-schema.org/draft/2020-12/schema\"}"))
			.withVersion("1.0");

		when(jsonSchemaStorageServiceMock.create(MUNICIPALITY_ID, body)).thenReturn(jsonSchema);

		// Act
		webTestClient.post()
			.uri("/{municipalityId}/jsonschemas", MUNICIPALITY_ID)
			.bodyValue(body)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().location("/" + MUNICIPALITY_ID + "/jsonschemas/" + id);

		// Assert
		verify(jsonSchemaStorageServiceMock).create(MUNICIPALITY_ID, body);
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

		// Assert
		verify(jsonSchemaStorageServiceMock).delete(MUNICIPALITY_ID, id);
	}
}
