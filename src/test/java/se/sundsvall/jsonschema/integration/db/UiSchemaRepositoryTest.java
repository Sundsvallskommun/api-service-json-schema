package se.sundsvall.jsonschema.integration.db;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;
import se.sundsvall.jsonschema.integration.db.model.UiSchemaEntity;

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
class UiSchemaRepositoryTest {

	private static final String ID = "5acd163f-f959-4ba3-ab81-9c705753eaf3";
	private static final String ID_OF_JSON_SCHEMA_WITH_UI_SCHEMA = "2281_schema_with_uischema_1.0.0";

	@Autowired
	private UiSchemaRepository repository;

	@Test
	void testCreate() {

		// Arrange
		final var entity = UiSchemaEntity.create()
			.withDescription("description")
			.withJsonSchema(JsonSchemaEntity.create().withId("2281_schema_1.0.0"))
			.withValue("{}");

		// Act
		final var persistedEntity = repository.save(entity);

		// Assert
		assertThat(repository.findById(persistedEntity.getId())).isPresent();
		assertThat(persistedEntity).usingRecursiveComparison().ignoringFields("created").isEqualTo(entity);
		assertThat(persistedEntity.getCreated()).isCloseTo(now(), within(2, SECONDS));
	}

	@Test
	void findById() {

		// Act
		final var persistedEntity = repository.findById(ID);

		// Assert
		assertThat(persistedEntity).isPresent();
	}

	@Test
	void findByJsonSchemaId() {

		// Act
		final var persistedEntity = repository.findByJsonSchemaId(ID_OF_JSON_SCHEMA_WITH_UI_SCHEMA);

		// Assert
		assertThat(persistedEntity).isPresent();
	}

	@Test
	void findByJsonSchemaIdNotFound() {

		// Act
		final var result = repository.findByJsonSchemaId("unknown-id");

		// Assert
		assertThat(result).isNotPresent();
	}

	@Test
	void update() {

		// Arrange
		final var persistedEntity = repository.findById(ID).orElseThrow();
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
		assertThat(repository.findById(ID)).isPresent();

		// Act
		repository.deleteById(ID);

		// Assert
		assertThat(repository.findById(ID)).isEmpty();
	}
}
