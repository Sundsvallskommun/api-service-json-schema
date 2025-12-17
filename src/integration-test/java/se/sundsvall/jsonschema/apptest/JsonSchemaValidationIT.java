package se.sundsvall.jsonschema.apptest;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.jsonschema.Application;

/**
 * JsonSchemaValidationIT integration tests.
 *
 * @see src/test/resources/db/scripts/jsonSchemaIT.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/jsonSchemaValidationIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/jsonSchemaIT.sql"
})
class JsonSchemaValidationIT extends AbstractAppTest {

	private static final String PATH = "/%s/jsonschemas/%s/validations";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String SCHEMA_ID = "2281_product_1.0.0";

	@Test
	void test01_validateValidJson() {
		setupCall()
			.withServicePath(PATH.formatted(MUNICIPALITY_ID, SCHEMA_ID))
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_validateInvalidJsonMiscErrors() {
		setupCall()
			.withServicePath(PATH.formatted(MUNICIPALITY_ID, SCHEMA_ID))
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_validateInvalidJsonMissingAllProperties() {
		setupCall()
			.withServicePath(PATH.formatted(MUNICIPALITY_ID, SCHEMA_ID))
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_validateInvalidJsonMissingSchemaId() {
		setupCall()
			.withServicePath(PATH.formatted(MUNICIPALITY_ID, "non-existing-id"))
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponse(RESPONSE_FILE)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}
}
