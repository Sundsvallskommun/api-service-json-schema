package se.sundsvall.jsonschema.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.jsonschema.Application;
import se.sundsvall.jsonschema.service.JsonSchemaValidationService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class JsonSchemaValidationResourceFailuresTest {

	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private JsonSchemaValidationService validationServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void validateJsonInvalidMunicipalityId() {

		// Arrange
		final var id = "schema_1.0";
		final var json = "{\"a\":1}";

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/jsonschemas/{id}/validations", "invalid", id)
			.contentType(APPLICATION_JSON)
			.bodyValue(json)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("validateJson.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(validationServiceMock);
	}

	@Test
	void validateJsonBlankId() {

		// Arrange
		final var json = "{\"a\":1}";

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/jsonschemas/{id}/validations", MUNICIPALITY_ID, " ")
			.contentType(APPLICATION_JSON)
			.bodyValue(json)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("validateJson.id", "must not be blank"));

		verifyNoInteractions(validationServiceMock);
	}

	@Test
	void validateJsonNullBody() {

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/jsonschemas/{id}/validations", MUNICIPALITY_ID, "schema_1.0")
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);

		assertThat(response.getTitle()).isEqualTo(BAD_REQUEST.getReasonPhrase());
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(
			"Required request body is missing: public org.springframework.http.ResponseEntity<java.lang.Void> se.sundsvall.jsonschema.api.JsonSchemaValidationResource.validateJson(java.lang.String,java.lang.String,java.lang.String)");

		verifyNoInteractions(validationServiceMock);
	}
}
