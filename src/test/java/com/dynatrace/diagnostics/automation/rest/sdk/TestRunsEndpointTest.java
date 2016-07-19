package com.dynatrace.diagnostics.automation.rest.sdk;

import com.dynatrace.diagnostics.automation.rest.sdk.entity.*;
import com.dynatrace.diagnostics.automation.rest.sdk.exceptions.TestRunsConnectionException;
import com.dynatrace.integration.idea.plugin.settings.ServerSettings;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.UnknownHostException;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestRunsEndpointTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private ServerSettings settings = new ServerSettings();
    private TestRunsEndpoint endpoint = new TestRunsEndpoint(settings);

    @Before
    public void setup() {
        this.settings.setPort(8080);
        this.settings.setSSL(false);
    }

    @Test
    public void getTestRunGivenInvalidStatusCode() throws Exception {
        stubFor(get(urlPathEqualTo("/rest/management/profiles/INVALID/testruns/INVALID.xml")).willReturn(aResponse().withStatus(401)));
        try {
            this.endpoint.getTestRun("INVALID", "INVALID");
            fail("Exception not thrown when expected to do");
        } catch (TestRunsConnectionException e) {
            assertTrue(e.getMessage().equals("Unauthorized"));
        }
    }

    @Test
    public void getTestRunGivenInvalidHostname() throws Exception {
        this.settings.setHost("__INVALID__");
        try {
            this.endpoint.getTestRun("INVALID", "INVALID");
            fail("Exception not thrown when expected to do");
        } catch (TestRunsConnectionException e) {
            //exception must be thrown
            assert (e.getCause() instanceof UnknownHostException);
        }
    }

    @Test
    public void getTestRunGivenInvalidPort() throws Exception {
        //http://en.wikipedia.org/wiki/Ephemeral_port
        this.settings.setPort(49153);
        try {
            this.endpoint.getTestRun("INVALID", "INVALID");
            fail("Exception not thrown when expected to do");
        } catch (TestRunsConnectionException e) {
            assertTrue(e.getCause() instanceof HttpHostConnectException);
            assertTrue(e.getCause().getMessage().endsWith("Connection refused: connect"));
        }
    }

    @Test
    public void getTestRunGivenValidResponse() throws Exception {
        stubFor(get(urlPathEqualTo("/rest/management/profiles/INVALID/testruns/INVALID.xml")).willReturn(aResponse().withStatus(201).withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testRun category=\"unit\" versionBuild=\"09:55:06\" versionMajor=\"2016\" versionMinor=\"7\" versionRevision=\"3\" platform=\"Windows 10 10.0.10586 x64\" startTime=\"1468914906596\" id=\"1130196b-1c61-408e-ab51-80d0283fab8e\" numPassed=\"1\" numFailed=\"0\" numVolatile=\"0\" numImproved=\"0\" numDegraded=\"0\" numInvalidated=\"0\" systemProfile=\"IntelliJ\" creationMode=\"MANUAL\"><testResult name=\"ITDaoTest.testGetDestination\" status=\"passed\" exectime=\"1468914908874\" package=\"com.compuware.apm.samples.simplewebapp\" platform=\"Windows 10 10.0.10586 x64\"><measure name=\"DB Count\" metricGroup=\"Database\" value=\"1.0\" unit=\"num\" expectedMin=\"1.0\" expectedMax=\"1.0\" numFailingOrInvalidatedRuns=\"0\" numValidRuns=\"13\" numImprovedRuns=\"0\" numDegradedRuns=\"0\" violationPercentage=\"0.0\"/><measure name=\"Failed Transaction Count\" metricGroup=\"Error Detection\" value=\"0.0\" unit=\"num\" expectedMin=\"0.0\" expectedMax=\"0.0\" numFailingOrInvalidatedRuns=\"0\" numValidRuns=\"13\" numImprovedRuns=\"0\" numDegradedRuns=\"0\" violationPercentage=\"0.0\"/></testResult></testRun>")));
        TestRun tr = this.endpoint.getTestRun("INVALID", "INVALID");
        assertTrue(tr.getTestResults().size() == 1);
        assertTrue(tr.getCategory() == TestCategory.UNIT);
        assertTrue(tr.getId().equals("1130196b-1c61-408e-ab51-80d0283fab8e"));
        assertTrue(tr.getStatus() == TestStatus.PASSED);
        TestResult result = tr.getTestResults().get(0);
        assertTrue(result.getStatus() == TestStatus.PASSED);
        assertTrue(result.getTestName().equals("ITDaoTest.testGetDestination"));
        assertTrue(result.getPackageName().equals("com.compuware.apm.samples.simplewebapp"));
        Set<TestMeasure> measures = result.getTestMeasures();
        assertTrue(measures.size() == 2);
    }
}
