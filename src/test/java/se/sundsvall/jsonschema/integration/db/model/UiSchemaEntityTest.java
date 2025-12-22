package se.sundsvall.jsonschema.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToStringExcluding;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UiSchemaEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(UiSchemaEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToStringExcluding("jsonSchema")));
	}

	@Test
	void testBuilderMethods() {

		final var created = now().minusDays(1);
		final var description = "description";
		final var id = "id";
		final var jsonSchema = JsonSchemaEntity.create().withId("xxx");
		final var value = "value";

		final var bean = UiSchemaEntity.create()
			.withCreated(created)
			.withDescription(description)
			.withJsonSchema(jsonSchema)
			.withId(id)
			.withValue(value);

		assertThat(bean).isNotNull().hasNoNullFieldsOrProperties();
		assertThat(bean.getCreated()).isEqualTo(created);
		assertThat(bean.getDescription()).isEqualTo(description);
		assertThat(bean.getId()).isEqualTo(id);
		assertThat(bean.getJsonSchema()).isEqualTo(jsonSchema);
		assertThat(bean.getValue()).isEqualTo(value);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(UiSchemaEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new UiSchemaEntity()).hasAllNullFieldsOrProperties();
	}

	@Test
	void testPrePersist() {
		final var bean = UiSchemaEntity.create();

		bean.prePersist();

		assertThat(bean.getCreated()).isCloseTo(now(), within(2, SECONDS));
	}
}
