package se.sundsvall.jsonschema.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

/**
 * JsonSchema repository tests.
 *
 * @see src/test/resources/db/scripts/jsonSchemaRepositoryTest.sql for data setup.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/jsonSchemaRepositoryTest.sql"
})
@Transactional(propagation = NOT_SUPPORTED)
class JsonSchemaRepositoryTest {

	private static final String ID_OF_JSON_SCHEMA = "2281_schema_1.0.0";
	private static final String ID_OF_JSON_SCHEMA_WITH_UI_SCHEMA = "2281_schema_with_uischema_1.0.0";

	@Autowired
	private JsonSchemaRepository repository;

	@Test
	void testCreate() {

		// Arrange
		final var entity = JsonSchemaEntity.create()
			.withDescription("description")
			.withId(randomUUID().toString())
			.withMunicipalityId("2281")
			.withName("name")
			.withValue("{}")
			.withVersion("1.0");

		// Act
		final var persistedEntity = repository.save(entity);

		// Assert
		assertThat(repository.findById(entity.getId())).isPresent();
		assertThat(persistedEntity).isNotSameAs(entity);
		assertThat(persistedEntity).usingRecursiveComparison().ignoringFields("created").isEqualTo(entity);
		assertThat(persistedEntity.getCreated()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void findById() {

		// Act
		final var persistedEntity = repository.findById(ID_OF_JSON_SCHEMA);

		// Assert
		assertThat(persistedEntity).isPresent();
	}

	@Test
	void update() {

		// Arrange
		final var persistedEntity = repository.findById(ID_OF_JSON_SCHEMA).orElseThrow();
		persistedEntity.withDescription("new desription");

		// Act
		final var updatedEntity = repository.save(persistedEntity);

		// Assert
		assertThat(updatedEntity).isNotSameAs(persistedEntity);
		assertThat(updatedEntity).usingRecursiveComparison().isEqualTo(persistedEntity);
	}

	@Test
	void deleteById() {

		// Arrange
		assertThat(repository.findById(ID_OF_JSON_SCHEMA)).isPresent();

		// Act
		repository.deleteById(ID_OF_JSON_SCHEMA);

		// Assert
		assertThat(repository.findById(ID_OF_JSON_SCHEMA)).isEmpty();
	}

	@Test
	void findByMunicipalityIdAndId() {

		// Act
		final var result = repository.findByMunicipalityIdAndId("2281", ID_OF_JSON_SCHEMA);

		// Assert
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(ID_OF_JSON_SCHEMA);
		assertThat(result.get().getMunicipalityId()).isEqualTo("2281");
	}

	@Test
	void findByMunicipalityIdAndIdNotFound() {

		// Act
		final var result = repository.findByMunicipalityIdAndId("9999", "unknown-id");

		// Assert
		assertThat(result).isNotPresent();
	}

	@Test
	void findAllByMunicipalityIdPaged() {

		// Arrange
		final var pageable = Pageable.ofSize(10);

		// Act
		final var page = repository.findAllByMunicipalityId("2281", pageable);

		// Assert
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent())
			.allMatch(e -> "2281".equals(e.getMunicipalityId()));
	}

	@Test
	void findAllByMunicipalityIdEmptyResult() {

		// Arrange
		final var pageable = Pageable.ofSize(10);

		// Act
		final var page = repository.findAllByMunicipalityId("9999", pageable);

		// Assert
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isEmpty();
	}

	@Test
	void findAllByMunicipalityIdAndNamePaged() {

		// Arrange
		final var pageable = Pageable.ofSize(10);

		// Act
		final var page = repository.findAllByMunicipalityIdAndName("2281", "schema", pageable);

		// Assert
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotEmpty();
		assertThat(page.getContent())
			.allSatisfy(entity -> {
				assertThat(entity.getMunicipalityId()).isEqualTo("2281");
				assertThat(entity.getName()).isEqualTo("schema");
			});
	}

	@Test
	void findAllByMunicipalityIdAndNameEmptyResult() {

		// Arrange
		final var pageable = Pageable.ofSize(10);

		// Act
		final var page = repository.findAllByMunicipalityIdAndName("2281", "does-not-exist", pageable);

		// Assert
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isEmpty();
	}

	@Test
	void findByIdAndVerifyExistingUiSchema() {

		// Act
		final var result = repository.findById(ID_OF_JSON_SCHEMA_WITH_UI_SCHEMA);

		// Assert
		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(ID_OF_JSON_SCHEMA_WITH_UI_SCHEMA);
		assertThat(result.get().getUiSchema()).isNotNull();
		assertThat(result.get().getUiSchema().getDescription()).isEqualTo("UI schema for rendering the person form");
	}
}
