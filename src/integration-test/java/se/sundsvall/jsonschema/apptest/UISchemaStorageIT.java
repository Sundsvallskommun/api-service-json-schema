package se.sundsvall.jsonschema.apptest;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.jsonschema.Application;

/**
 * UISchemaStorageIT integration tests.
 *
 * @see src/test/resources/db/scripts/jsonSchemaIT.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/uiSchemaStorageIT/", classes = Application.class)
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/jsonSchemaIT.sql"
})
class UISchemaStorageIT extends AbstractAppTest {

	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String MUNICIPALITY_ID = "2281";
	private static final String JSON_SCHEMA_ID_WITH_UI_SCHEMA = "2281_schema_with_uischema_1.0.0";
	private static final String JSON_SCHEMA_ID_WITHOUT_UI_SCHEMA = "2281_schema_1.0.0";

	@Test
	void test01_replaceUiSchema() {
		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITH_UI_SCHEMA))
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITH_UI_SCHEMA))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_createUiSchema() {
		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITHOUT_UI_SCHEMA))
			.withHttpMethod(PUT)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITHOUT_UI_SCHEMA))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_getSchema() {
		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITH_UI_SCHEMA))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test04_deleteSchema() {
		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITH_UI_SCHEMA))
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();

		setupCall()
			.withServicePath("/%s/schemas/%s/ui-schema".formatted(MUNICIPALITY_ID, JSON_SCHEMA_ID_WITH_UI_SCHEMA))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.sendRequestAndVerifyResponse();
	}
}
