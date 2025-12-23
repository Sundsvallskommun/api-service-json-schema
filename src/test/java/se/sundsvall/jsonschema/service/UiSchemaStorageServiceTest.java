package se.sundsvall.jsonschema.service;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.jsonschema.api.model.UiSchemaRequest;
import se.sundsvall.jsonschema.integration.db.JsonSchemaRepository;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;
import se.sundsvall.jsonschema.integration.db.model.UiSchemaEntity;

@ExtendWith(MockitoExtension.class)
class UiSchemaStorageServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String JSON_SCHEMA_ID = "2281_person_1.0";
	private static final String UI_SCHEMA_ID = randomUUID().toString();

	@Mock
	private JsonSchemaRepository jsonSchemaRepositoryMock;

	@InjectMocks
	private UiSchemaStorageService service;

	@Captor
	private ArgumentCaptor<JsonSchemaEntity> jsonSchemaEntityCaptor;

	private JsonSchemaEntity jsonSchemaEntity;
	private UiSchemaEntity uiSchemaEntity;

	@BeforeEach
	void setup() {

		uiSchemaEntity = UiSchemaEntity.create()
			.withId(UI_SCHEMA_ID);

		jsonSchemaEntity = JsonSchemaEntity.create()
			.withId(JSON_SCHEMA_ID)
			.withMunicipalityId(MUNICIPALITY_ID)
			.withUiSchema(uiSchemaEntity);
	}

	// -------------------------------------------------------------------------
	// GetSchema
	// -------------------------------------------------------------------------

	@Test
	void getSchema() {

		// Arrange
		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		final var result = service.getSchema(MUNICIPALITY_ID, JSON_SCHEMA_ID);

		// Assert
		assertThat(result).isNotNull();

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
	}

	@Test
	void getSchemaShouldThrowNotFoundWhenUiSchemaMissing() {

		// Arrange
		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.of(jsonSchemaEntity.withUiSchema(null)));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.getSchema(MUNICIPALITY_ID, JSON_SCHEMA_ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: No UiSchema on JsonSchema with ID '2281_person_1.0' was found!");

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
	}

	@Test
	void getSchemaShouldThrowNotFoundWhenJsonSchemaMissing() {

		// Arrange
		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.getSchema(MUNICIPALITY_ID, JSON_SCHEMA_ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: No JsonSchema with ID '2281_person_1.0' was found!");

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
	}

	// -------------------------------------------------------------------------
	// CreateOrReplace
	// -------------------------------------------------------------------------

	@Test
	void createOrReplaceShouldPersistUpdatedEntity() {

		// Arrange
		final var request = UiSchemaRequest.create();

		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		service.createOrReplace(MUNICIPALITY_ID, JSON_SCHEMA_ID, request);

		// Assert
		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
		verify(jsonSchemaRepositoryMock).save(jsonSchemaEntityCaptor.capture());

		final var capturedJsonSchemaEntity = jsonSchemaEntityCaptor.getValue();
		assertThat(capturedJsonSchemaEntity).isNotNull();
		assertThat(capturedJsonSchemaEntity.getUiSchema()).isNotNull();
	}

	@Test
	void createOrReplaceShouldThrowNotFoundWhenJsonSchemaMissing() {

		// Arrange
		final var uiSchemaRequest = new UiSchemaRequest();

		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.createOrReplace(MUNICIPALITY_ID, JSON_SCHEMA_ID, uiSchemaRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: No JsonSchema with ID '2281_person_1.0' was found!");

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
		verify(jsonSchemaRepositoryMock, never()).save(any());
	}

	// -------------------------------------------------------------------------
	// Delete
	// -------------------------------------------------------------------------

	@Test
	void deleteShouldRemoveUiSchema() {

		// Arrange
		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.of(jsonSchemaEntity));

		// Act
		service.delete(MUNICIPALITY_ID, JSON_SCHEMA_ID);

		// Assert
		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
		verify(jsonSchemaRepositoryMock).save(jsonSchemaEntityCaptor.capture());

		assertThat(jsonSchemaEntityCaptor.getValue().getUiSchema()).isNull();
	}

	@Test
	void deleteShouldThrowNotFoundWhenUiSchemaMissing() {

		// Arrange
		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID))
			.thenReturn(Optional.of(jsonSchemaEntity.withUiSchema(null)));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(MUNICIPALITY_ID, JSON_SCHEMA_ID));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: No UiSchema on JsonSchema with ID '2281_person_1.0' was found!");

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, JSON_SCHEMA_ID);
		verify(jsonSchemaRepositoryMock, never()).save(any());
	}
}
