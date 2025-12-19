package se.sundsvall.jsonschema.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.jsonschema.integration.db.model.UiSchemaEntity;

@CircuitBreaker(name = "uiSchemaRepository")
public interface UiSchemaRepository extends JpaRepository<UiSchemaEntity, String> {

	Optional<UiSchemaEntity> findByJsonSchemaId(String jsonSchemaId);
}
