package se.sundsvall.jsonschema.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonNode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.jsonschema.Application;
import se.sundsvall.jsonschema.api.model.UiSchema;
import se.sundsvall.jsonschema.api.model.UiSchemaRequest;
import se.sundsvall.jsonschema.service.UiSchemaStorageService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class UiSchemaStorageResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private UiSchemaStorageService uiSchemaStorageServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getUiSchema() {

		// Arrange
		final var schemaId = "some-schema-id";
		final var uiSchema = UiSchema.create().withValue(toJsonNode("{}"));

		when(uiSchemaStorageServiceMock.getSchema(MUNICIPALITY_ID, schemaId)).thenReturn(uiSchema);

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/schemas/{id}/ui-schema", MUNICIPALITY_ID, schemaId)
			.exchange()
			.expectStatus()
			.isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(UiSchema.class).returnResult().getResponseBody();

		// Assert
		assertThat(response).isEqualTo(uiSchema);
		verify(uiSchemaStorageServiceMock).getSchema(MUNICIPALITY_ID, schemaId);
	}

	@Test
	void createOrReplaceUiSchema() {

		// Arrange
		final var schemaId = "some-schema-id";
		final var requestBody = UiSchemaRequest.create()
			.withDescription("description")
			.withValue(toJsonNode("{}"));

		// Act
		webTestClient.put()
			.uri("/{municipalityId}/schemas/{schemaId}/ui-schema", MUNICIPALITY_ID, schemaId)
			.bodyValue(requestBody)
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(uiSchemaStorageServiceMock).createOrReplace(MUNICIPALITY_ID, schemaId, requestBody);
	}

	@Test
	void deleteUiSchema() {

		// Arrange
		final var schemaId = "some-schema-id";

		// Act
		webTestClient.delete()
			.uri("/{municipalityId}/schemas/{schemaId}/ui-schema", MUNICIPALITY_ID, schemaId)
			.exchange()
			.expectStatus().isNoContent();

		// Assert
		verify(uiSchemaStorageServiceMock).delete(MUNICIPALITY_ID, schemaId);
	}
}
