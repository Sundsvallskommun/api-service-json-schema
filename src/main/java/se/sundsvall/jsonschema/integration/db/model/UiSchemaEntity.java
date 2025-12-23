package se.sundsvall.jsonschema.integration.db.model;

import static jakarta.persistence.FetchType.LAZY;
import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;
import static org.hibernate.type.SqlTypes.LONG32VARCHAR;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(
	name = "ui_schema",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_ui_schema_json_schema", columnNames = "json_schema_id")
	})
public class UiSchemaEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@OneToOne(optional = false, fetch = LAZY)
	@JoinColumn(
		name = "json_schema_id",
		nullable = false,
		updatable = false,
		foreignKey = @ForeignKey(name = "fk_ui_schema_json_schema"))
	private JsonSchemaEntity jsonSchema;

	@JdbcTypeCode(LONG32VARCHAR)
	@Column(name = "value")
	private String value;

	@JdbcTypeCode(LONG32VARCHAR)
	@Column(name = "description")
	private String description;

	@TimeZoneStorage(NORMALIZE)
	@Column(name = "created")
	private OffsetDateTime created;

	public static UiSchemaEntity create() {
		return new UiSchemaEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UiSchemaEntity withId(String id) {
		this.id = id;
		return this;
	}

	public JsonSchemaEntity getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(JsonSchemaEntity jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	public UiSchemaEntity withJsonSchema(JsonSchemaEntity jsonSchema) {
		this.jsonSchema = jsonSchema;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public UiSchemaEntity withValue(String value) {
		this.value = value;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UiSchemaEntity withDescription(String description) {
		this.description = description;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public UiSchemaEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@PrePersist
	void prePersist() {
		created = now(systemDefault()).truncatedTo(MILLIS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, description, id, jsonSchema, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UiSchemaEntity other = (UiSchemaEntity) obj;
		return Objects.equals(created, other.created) && Objects.equals(description, other.description) && Objects.equals(id, other.id) && Objects.equals(jsonSchema, other.jsonSchema) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "UiSchemaEntity [id=" + id + ", jsonSchema=" + (jsonSchema != null ? jsonSchema.getId() : null) + ", value=" + value + ", description=" + description + ", created=" + created + "]";
	}
}
