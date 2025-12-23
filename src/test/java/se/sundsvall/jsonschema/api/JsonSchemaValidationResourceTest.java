package se.sundsvall.jsonschema.api;

import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonNode;
import static se.sundsvall.jsonschema.service.mapper.JsonMapper.toJsonString;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.jsonschema.Application;
import se.sundsvall.jsonschema.service.JsonSchemaValidationService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class JsonSchemaValidationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private JsonSchemaValidationService validationServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void validateJson() {

		// Arrange
		final var id = "schema_1.0";
		final var json = """
			{
				"productId": 1,
				"productName": "Ice sculpture",
				"price": 12.5
			}
			""";
		final var jsonNode = toJsonNode(json);
		final var jsonString = toJsonString(jsonNode);

		// Act
		webTestClient.post()
			.uri("/{municipalityId}/schemas/{id}/validation", MUNICIPALITY_ID, id)
			.contentType(APPLICATION_JSON)
			.bodyValue(jsonNode)
			.exchange()
			.expectStatus()
			.isNoContent();

		// Assert
		verify(validationServiceMock).validateAndThrow(jsonString, id);
	}
}
