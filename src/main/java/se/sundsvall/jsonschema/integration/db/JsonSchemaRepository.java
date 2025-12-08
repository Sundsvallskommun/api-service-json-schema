package se.sundsvall.jsonschema.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.jsonschema.integration.db.model.JsonSchemaEntity;

@CircuitBreaker(name = "jsonSchemaRepository")
public interface JsonSchemaRepository extends JpaRepository<JsonSchemaEntity, String> {

	Optional<JsonSchemaEntity> findByMunicipalityIdAndId(String municipalityId, String id);

	Page<JsonSchemaEntity> findAllByMunicipalityId(String municipalityId, Pageable pageable);

	Page<JsonSchemaEntity> findAllByMunicipalityIdAndName(String municipalityId, String name, Pageable pageable);
}
