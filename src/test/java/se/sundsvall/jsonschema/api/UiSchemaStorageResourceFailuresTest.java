package se.sundsvall.jsonschema.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import se.sundsvall.jsonschema.Application;
import se.sundsvall.jsonschema.api.model.UiSchemaRequest;
import se.sundsvall.jsonschema.service.UiSchemaStorageService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class UiSchemaStorageResourceFailuresTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String SCHEMA_ID = "2281_product_1.0";

	@MockitoBean
	private UiSchemaStorageService uiSchemaStorageServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getUiSchemaInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/schemas/{schemaId}/ui-schema", "invalid", SCHEMA_ID)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("getUiSchemaById.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(uiSchemaStorageServiceMock);
	}

	@Test
	void createOrReplaceUiSchemaInvalidMunicipalityId() throws Exception {

		// Arrange
		final var uiSchemaRequest = UiSchemaRequest.create()
			.withDescription("description")
			.withValue(new ObjectMapper().readTree("{}"));

		// Act
		final var response = webTestClient.put()
			.uri("/{municipalityId}/schemas/{schemaId}/ui-schema", "invalid", SCHEMA_ID)
			.bodyValue(uiSchemaRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("createOrReplaceUiSchema.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(uiSchemaStorageServiceMock);
	}

	@Test
	void createOrReplaceUiSchemaEmptyRequestBody() {

		// Arrange
		final var uiSchemaRequest = UiSchemaRequest.create();

		// Act
		final var response = webTestClient.put()
			.uri("/{municipalityId}/schemas/{schemaId}/ui-schema", MUNICIPALITY_ID, SCHEMA_ID)
			.bodyValue(uiSchemaRequest)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(
				tuple("value", "must not be null"));

		verifyNoInteractions(uiSchemaStorageServiceMock);
	}

	@Test
	void deleteUiSchemaInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.delete()
			.uri("/{municipalityId}/schemas/{schemaId}/ui-schema", "invalid", SCHEMA_ID)
			.exchange()
			.expectStatus().isBadRequest()
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations())
			.extracting(Violation::getField, Violation::getMessage)
			.containsExactly(tuple("deleteUiSchema.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(uiSchemaStorageServiceMock);
	}
}
