package se.sundsvall.jsonschema.api;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.util.UriComponentsBuilder.fromPath;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.jsonschema.api.model.JsonSchema;
import se.sundsvall.jsonschema.api.model.JsonSchemaCreateRequest;
import se.sundsvall.jsonschema.service.JsonSchemaStorageService;

@RestController
@Validated
@RequestMapping(value = "/{municipalityId}/jsonschemas")
@Tag(name = "JSON-schemas")
@ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
	Problem.class, ConstraintViolationProblem.class
})))
@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
class JsonSchemaStorageResource {

	private final JsonSchemaStorageService jsonSchemaStorageService;

	public JsonSchemaStorageResource(JsonSchemaStorageService jsonSchemaService) {
		this.jsonSchemaStorageService = jsonSchemaService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get JSON schemas", responses = @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true))
	ResponseEntity<Page<JsonSchema>> getSchemas(
		@Parameter(name = "municipalityId", description = "Municipality id", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@ParameterObject final Pageable pageable) {

		return ok(jsonSchemaStorageService.getSchemas(municipalityId, pageable));
	}

	@GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get a JSON schema", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<JsonSchema> getSchemaById(
		@Parameter(name = "municipalityId", description = "MunicipalityID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Schema ID", example = "2281_person_1.0") @NotBlank @PathVariable final String id) {

		return ok(jsonSchemaStorageService.getSchema(municipalityId, id));
	}

	@GetMapping(path = "{name}/versions/latest", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get latest version of a schema identified by schema name", responses = {
		@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<JsonSchema> getLatestSchemaByName(
		@Parameter(name = "municipalityId", description = "MunicipalityID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "name", description = "Schema name", example = "person") @NotBlank @PathVariable final String name) {

		return ok(jsonSchemaStorageService.getLatestSchemaByName(municipalityId, name));
	}

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = ALL_VALUE)
	@Operation(summary = "Create a JSON schema",
		responses = @ApiResponse(responseCode = "201", description = "Created - Successful operation", headers = @Header(name = LOCATION, description = "Location of the created resource."), useReturnTypeSchema = true))
	ResponseEntity<Void> createSchema(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Valid @NotNull @RequestBody final JsonSchemaCreateRequest body) {

		final var createdJsonSchema = jsonSchemaStorageService.create(municipalityId, body);

		return created(fromPath("/{municipalityId}/jsonschemas/{id}").buildAndExpand(municipalityId, createdJsonSchema.getId()).toUri())
			.header(CONTENT_TYPE, ALL_VALUE)
			.build();
	}

	@DeleteMapping(path = "{id}", produces = ALL_VALUE)
	@Operation(summary = "Delete a JSON schema", responses = {
		@ApiResponse(responseCode = "204", description = "No content - Successful operation", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	})
	ResponseEntity<Void> deleteSchema(
		@Parameter(name = "municipalityId", description = "Municipality ID", example = "2281") @ValidMunicipalityId @PathVariable final String municipalityId,
		@Parameter(name = "id", description = "Schema ID", example = "2281_person_1.0") @PathVariable @NotBlank final String id) {

		jsonSchemaStorageService.delete(municipalityId, id);

		return noContent().build();
	}
}
