package se.sundsvall.jsonschema.integration.db.model;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;
import static org.hibernate.type.SqlTypes.LONG32VARCHAR;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TimeZoneStorage;

@Entity
@Table(name = "json_schema",
	uniqueConstraints = {
		@UniqueConstraint(name = "uc_json_schema_municipality_id_name_version", columnNames = {
			"municipality_id",
			"name",
			"version"
		})
	},
	indexes = {
		@Index(name = "idx_municipality_id", columnList = "municipality_id"),
		@Index(name = "idx_municipality_id_name", columnList = "municipality_id, name"),
		@Index(name = "idx_municipality_id_name_version", columnList = "municipality_id, name, version")
	})
public class JsonSchemaEntity {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "municipality_id", length = 8)
	private String municipalityId;

	@Column(name = "name", length = 64)
	private String name;

	@Column(name = "version", length = 32)
	private String version;

	@JdbcTypeCode(LONG32VARCHAR)
	@Column(name = "value")
	private String value;

	@JdbcTypeCode(LONG32VARCHAR)
	@Column(name = "description")
	private String description;

	@TimeZoneStorage(NORMALIZE)
	@Column(name = "created")
	private OffsetDateTime created;

	/*
	 * =======================
	 * Validation usage stats
	 * =======================
	 */

	@Column(name = "validation_usage_count", nullable = false)
	private long validationUsageCount;

	@TimeZoneStorage(NORMALIZE)
	@Column(name = "last_used_for_validation")
	private OffsetDateTime lastUsedForValidation;

	public static JsonSchemaEntity create() {
		return new JsonSchemaEntity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JsonSchemaEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public JsonSchemaEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JsonSchemaEntity withName(String name) {
		this.name = name;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public JsonSchemaEntity withVersion(String version) {
		this.version = version;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public JsonSchemaEntity withValue(String value) {
		this.value = value;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JsonSchemaEntity withDescription(String description) {
		this.description = description;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public JsonSchemaEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public long getValidationUsageCount() {
		return validationUsageCount;
	}

	public void setValidationUsageCount(long validationUsageCount) {
		this.validationUsageCount = validationUsageCount;
	}

	public JsonSchemaEntity withValidationUsageCount(long validationUsageCount) {
		this.validationUsageCount = validationUsageCount;
		return this;
	}

	public OffsetDateTime getLastUsedForValidation() {
		return lastUsedForValidation;
	}

	public void setLastUsedForValidation(OffsetDateTime lastUsedForValidation) {
		this.lastUsedForValidation = lastUsedForValidation;
	}

	public JsonSchemaEntity withLastUsedForValidation(OffsetDateTime lastUsedForValidation) {
		this.lastUsedForValidation = lastUsedForValidation;
		return this;
	}

	@PrePersist
	void prePersist() {
		created = now(systemDefault()).truncatedTo(MILLIS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, description, id, lastUsedForValidation, municipalityId, name, validationUsageCount, value, version);
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
		JsonSchemaEntity other = (JsonSchemaEntity) obj;
		return Objects.equals(created, other.created) && Objects.equals(description, other.description) && Objects.equals(id, other.id) && Objects.equals(lastUsedForValidation, other.lastUsedForValidation) && Objects.equals(municipalityId,
			other.municipalityId) && Objects.equals(name, other.name) && validationUsageCount == other.validationUsageCount && Objects.equals(value, other.value) && Objects.equals(version, other.version);
	}

	@Override
	public String toString() {
		return "JsonSchemaEntity [id=" + id + ", municipalityId=" + municipalityId + ", name=" + name + ", version=" + version + ", value=" + value + ", description=" + description + ", created=" + created + ", validationUsageCount=" + validationUsageCount
			+ ", lastUsedForValidation=" + lastUsedForValidation + "]";
	}
}
