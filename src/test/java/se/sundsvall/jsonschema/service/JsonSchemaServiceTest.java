package se.sundsvall.jsonschema.service;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Pageable.unpaged;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.zalando.problem.ThrowableProblem;
import se.sundsvall.jsonschema.TestFactory;
import se.sundsvall.jsonschema.integration.db.JsonSchemaRepository;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

@ExtendWith(MockitoExtension.class)
class JsonSchemaServiceTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private JsonSchemaRepository jsonSchemaRepositoryMock;

	@Captor
	private ArgumentCaptor<JsonSchemaEntity> entityCaptor;

	@InjectMocks
	private JsonSchemaService service;

	@Test
	void getSchemas() {

		// Arrange
		final var pageable = PageRequest.of(0, 10);
		final var entity = TestFactory.getJsonSchemaEntity();
		when(jsonSchemaRepositoryMock.findAllByMunicipalityId(MUNICIPALITY_ID, pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

		// Act
		final var result = service.getSchemas(MUNICIPALITY_ID, pageable);

		// Assert
		assertThat(result)
			.hasSize(1)
			.first()
			.usingRecursiveComparison()
			.isEqualTo(entity);

		verify(jsonSchemaRepositoryMock).findAllByMunicipalityId(MUNICIPALITY_ID, pageable);
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}

	@Test
	void getSchema() {

		// Arrange
		final var entity = TestFactory.getJsonSchemaEntity();
		final var id = entity.getId();
		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(MUNICIPALITY_ID, id)).thenReturn(Optional.of(entity));

		// Act
		final var result = service.getSchema(MUNICIPALITY_ID, id);

		// Assert
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(entity);

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, id);
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}

	@Test
	void getLatestSchemaByName() {

		// Arrange
		final var pageable = PageRequest.of(0, 10);
		final var name = "schema-name";
		when(jsonSchemaRepositoryMock.findAllByMunicipalityIdAndName(MUNICIPALITY_ID, name, unpaged())).thenReturn(
			new PageImpl<>(List.of(
				JsonSchemaEntity.create().withId("id1").withName(name).withVersion("2.0"),
				JsonSchemaEntity.create().withId("id2").withName(name).withVersion("1.2.0"),
				JsonSchemaEntity.create().withId("id3").withName(name).withVersion("1.1"),
				JsonSchemaEntity.create().withId("id4").withName(name).withVersion("5.7"), // The greatest version
				JsonSchemaEntity.create().withId("id5").withName(name).withVersion("4.1.1")), pageable, 1));

		// Act
		final var result = service.getLatestSchemaByName(MUNICIPALITY_ID, name);

		// Assert
		assertThat(result.getId()).isEqualTo("id4");
		assertThat(result.getVersion()).isEqualTo("5.7");

		verify(jsonSchemaRepositoryMock).findAllByMunicipalityIdAndName(MUNICIPALITY_ID, name, unpaged());
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}

	@Test
	void createSchema() {

		// Arrange
		final var pageable = unpaged();
		final var jsonSchemaCreateRequest = TestFactory.getJsonSchemaCreateRequest();
		final var entity = TestFactory.getJsonSchemaEntity();

		when(jsonSchemaRepositoryMock.existsById(any())).thenReturn(false);
		when(jsonSchemaRepositoryMock.findAllByMunicipalityIdAndName(any(), any(), any())).thenReturn(new PageImpl<>(emptyList(), pageable, 1));
		when(jsonSchemaRepositoryMock.save(any())).thenReturn(entity);

		// Act
		final var result = service.create(MUNICIPALITY_ID, jsonSchemaCreateRequest);

		// Assert
		assertThat(result)
			.usingRecursiveComparison()
			.isEqualTo(entity);

		verify(jsonSchemaRepositoryMock).findAllByMunicipalityIdAndName(MUNICIPALITY_ID, jsonSchemaCreateRequest.getName().toLowerCase(), pageable);
		verify(jsonSchemaRepositoryMock).existsById("%s_%s_%s".formatted(MUNICIPALITY_ID, jsonSchemaCreateRequest.getName(), jsonSchemaCreateRequest.getVersion()).toLowerCase());
		verify(jsonSchemaRepositoryMock).save(entityCaptor.capture());
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);

		final var capturedValue = entityCaptor.getValue();
		assertThat(capturedValue.getCreated()).isNull();
		assertThat(capturedValue.getDescription()).isEqualTo(jsonSchemaCreateRequest.getDescription());
		assertThat(capturedValue.getId())
			.isEqualToIgnoringCase("%s_%s_%s".formatted(MUNICIPALITY_ID, jsonSchemaCreateRequest.getName(), jsonSchemaCreateRequest.getVersion()))
			.isLowerCase();
		assertThat(capturedValue.getMunicipalityId()).isEqualTo(MUNICIPALITY_ID);
		assertThat(capturedValue.getName())
			.isEqualToIgnoringCase(jsonSchemaCreateRequest.getName())
			.isLowerCase();
		assertThat(capturedValue.getValue()).isEqualTo(jsonSchemaCreateRequest.getValue());
		assertThat(capturedValue.getVersion()).isEqualTo(jsonSchemaCreateRequest.getVersion());
	}

	@Test
	void createSchemaWhenVersionAlreadyExists() {

		// Arrange
		final var jsonSchemaCreateRequest = TestFactory.getJsonSchemaCreateRequest();

		when(jsonSchemaRepositoryMock.existsById(any())).thenReturn(true);

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(MUNICIPALITY_ID, jsonSchemaCreateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getMessage()).isEqualTo("Conflict: A JsonSchema already exists with ID '2281_person_schema_1.0'!");

		verify(jsonSchemaRepositoryMock).existsById("%s_%s_%s".formatted(MUNICIPALITY_ID, jsonSchemaCreateRequest.getName(), jsonSchemaCreateRequest.getVersion()).toLowerCase());
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}

	@Test
	void createSchemaWhenGreaterVersionAlreadyExists() {

		// Arrange
		final var pageable = PageRequest.of(0, 10);
		final var jsonSchemaCreateRequest = TestFactory.getJsonSchemaCreateRequest();
		assertThat(jsonSchemaCreateRequest.getVersion()).isEqualTo("1.0");

		when(jsonSchemaRepositoryMock.existsById(any())).thenReturn(false);
		when(jsonSchemaRepositoryMock.findAllByMunicipalityIdAndName(any(), any(), any())).thenReturn(
			new PageImpl<>(List.of(
				JsonSchemaEntity.create().withId("id-1").withVersion("0.4"),
				JsonSchemaEntity.create().withId("id-2").withVersion("0.123456789"),
				JsonSchemaEntity.create().withId("id-3").withVersion("1.4"), // greater version
				JsonSchemaEntity.create().withId("id-4").withVersion("0.9876.54321")), pageable, 1));

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.create(MUNICIPALITY_ID, jsonSchemaCreateRequest));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(CONFLICT);
		assertThat(exception.getMessage()).isEqualTo("Conflict: A JsonSchema with a greater version already exists! (see schema with ID: 'id-3')");

		verify(jsonSchemaRepositoryMock).existsById("%s_%s_%s".formatted(MUNICIPALITY_ID, jsonSchemaCreateRequest.getName(), jsonSchemaCreateRequest.getVersion()).toLowerCase());
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}

	@Test
	void delete() {

		// Arrange
		final var id = "some-id";
		final var entityToDelete = JsonSchemaEntity.create().withId(id);

		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(any(), any())).thenReturn(Optional.of(entityToDelete));

		// Act
		service.delete(MUNICIPALITY_ID, id);

		// Assert
		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, id);
		verify(jsonSchemaRepositoryMock).deleteById(id);
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}

	@Test
	void deleteWhenNotFound() {

		// Arrange
		final var id = "some-id";

		when(jsonSchemaRepositoryMock.findByMunicipalityIdAndId(any(), any())).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(ThrowableProblem.class, () -> service.delete(MUNICIPALITY_ID, id));

		// Assert
		assertThat(exception.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(exception.getMessage()).isEqualTo("Not Found: No JsonSchema with ID 'some-id' was found!");

		verify(jsonSchemaRepositoryMock).findByMunicipalityIdAndId(MUNICIPALITY_ID, id);
		verifyNoMoreInteractions(jsonSchemaRepositoryMock);
	}
}
