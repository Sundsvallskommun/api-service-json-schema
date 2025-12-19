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
import se.sundsvall.jsonschema.api.model.JsonSchemaRequest;
import se.sundsvall.jsonschema.service.JsonSchemaStorageService;

@ActiveProfiles("junit")
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
class JsonSchemaResourceFailuresTest {

	private static final String MUNICIPALITY_ID = "2281";

	@MockitoBean
	private JsonSchemaStorageService jsonSchemaStorageServiceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getSchemasInvalidMunicipalityId() {

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/schemas", "invalid")
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
			.containsExactly(tuple("getSchemas.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void getSchemaInvalidMunicipalityId() {

		// Arrange
		final var id = "some_schema";

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/schemas/{id}", "invalid", id)
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
			.containsExactly(tuple("getSchemaById.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void getLatestSchemaByNameInvalidMunicipalityId() {

		// Arrange
		final var name = "schemaName";

		// Act
		final var response = webTestClient.get()
			.uri("/{municipalityId}/schemas/{name}/versions/latest", "invalid", name)
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
			.containsExactly(tuple("getLatestSchemaByName.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void createSchemaInvalidMunicipalityId() throws Exception {

		// Arrange
		final var schemaRequest = JsonSchemaRequest.create()
			.withDescription("description")
			.withName("name")
			.withValue(new ObjectMapper().readTree("{\"$schema\": \"https://json-schema.org/draft/2020-12/schema\"}"))
			.withVersion("1.0");

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/schemas", "invalid")
			.bodyValue(schemaRequest)
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
			.containsExactly(tuple("createSchema.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void createSchemaEmptyRequestBody() {

		// Arrange
		final var schemaRequest = JsonSchemaRequest.create();

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/schemas", MUNICIPALITY_ID)
			.bodyValue(schemaRequest)
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
				tuple("name", "must not be blank"),
				tuple("value", "must be valid JSON, but was null"),
				tuple("version", "must not be blank"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void createSchemaInvalidVersion() throws Exception {

		// Arrange
		final var schemaRequest = JsonSchemaRequest.create()
			.withDescription("description")
			.withName("name")
			.withValue(new ObjectMapper().readTree("{\"$schema\": \"https://json-schema.org/draft/2020-12/schema\"}"))
			.withVersion("invalid-version");

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/schemas", MUNICIPALITY_ID)
			.bodyValue(schemaRequest)
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
			.containsExactly(tuple("version", "must match \"^(\\d+\\.)?(\\d+)$\""));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void createSchemaInvalidSpecificationVersion() throws Exception {

		// Arrange
		final var schemaRequest = JsonSchemaRequest.create()
			.withDescription("description")
			.withName("name")
			.withValue(new ObjectMapper().readTree("{\"$schema\": \"https://json-schema.org/draft/2019-09/schema\"}")) // Should be 2020-12
			.withVersion("1.0");

		// Act
		final var response = webTestClient.post()
			.uri("/{municipalityId}/schemas", MUNICIPALITY_ID)
			.bodyValue(schemaRequest)
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
			.containsExactly(tuple("value", "Wrong value in $schema-node. Expected: 'https://json-schema.org/draft/2020-12/schema' Found: 'https://json-schema.org/draft/2019-09/schema'"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}

	@Test
	void deleteSchemaInvalidMunicipalityId() {

		// Arrange
		final var id = "some_schema";

		// Act
		final var response = webTestClient.delete()
			.uri("/{municipalityId}/schemas/{id}", "invalid", id)
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
			.containsExactly(tuple("deleteSchema.municipalityId", "not a valid municipality ID"));

		verifyNoInteractions(jsonSchemaStorageServiceMock);
	}
}
