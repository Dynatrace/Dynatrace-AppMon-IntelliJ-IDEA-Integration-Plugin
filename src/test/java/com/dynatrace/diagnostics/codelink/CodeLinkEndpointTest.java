/*
 *  Dynatrace IntelliJ IDEA Integration Plugin
 *  Copyright (c) 2008-2016, DYNATRACE LLC
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  * Neither the name of the dynaTrace software nor the names of its contributors
 *  may be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *  ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 *
 */

package com.dynatrace.diagnostics.codelink;

import com.dynatrace.diagnostics.codelink.exceptions.CodeLinkConnectionException;
import com.dynatrace.integration.idea.plugin.settings.CodeLinkSettings;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.UnknownHostException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CodeLinkEndpointTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private CodeLinkSettings dummySettings = new CodeLinkSettings();
    private IIDEDescriptor ide = new DummyIDEDescriptor();
    private IProjectDescriptor project = new DummyProjectDescriptor();
    private CodeLinkEndpoint endpoint = new CodeLinkEndpoint(this.project, this.ide, this.dummySettings);

    @Before
    public void setup() {
        this.dummySettings.setPort(8080);
        this.dummySettings.setSSL(false);
    }

    @Test
    public void connectGivenInvalidHostname() throws Exception {
        this.dummySettings.setHost("__INVALID__");
        try {
            this.endpoint.connect(-1);
            fail("Exception not thrown when expected to do");
        } catch (CodeLinkConnectionException e) {
            //exception must be thrown
            assert (e.getCause() instanceof UnknownHostException);
        }
    }

    @Test
    public void connectGivenInvalidPort() throws Exception {
        //http://en.wikipedia.org/wiki/Ephemeral_port
        this.dummySettings.setPort(49153);
        try {
            this.endpoint.connect(-1);
            fail("Exception not thrown when expected to do");
        } catch (CodeLinkConnectionException e) {
            assertTrue(e.getCause() instanceof HttpHostConnectException);
            assertTrue(e.getCause().getMessage().endsWith("Connection refused: connect"));
        }
    }

    @Test
    public void connectGivenInvalidStatusCode() throws Exception {
        stubFor(post(urlPathEqualTo("/rest/management/codelink/connect")).willReturn(aResponse().withStatus(401)));
        try {
            this.endpoint.connect(-1);
            fail("Exception not thrown when expected to do");
        } catch (CodeLinkConnectionException e) {
            assertTrue(e.getMessage().equals("Unauthorized"));
        }
    }

    @Test
    public void connectGivenValidResponseWithTimeout() throws Exception {
        stubFor(post(urlPathEqualTo("/rest/management/codelink/connect")).willReturn(aResponse().withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><codeLinkLookup sessionId=\"1468832125420\" timedOut=\"true\" versionMatched=\"true\"/>")));
        CodeLinkLookupResponse response = this.endpoint.connect(-1);
        assertTrue(response.timedOut);
        assertTrue(response.sessionId == 1468832125420L);
        assertTrue(response.versionMatched);
    }


    @Test
    public void connectGivenValidResponseWithLookupRequest() throws Exception {
        stubFor(post(urlPathEqualTo("/rest/management/codelink/connect")).willReturn(aResponse().withBody("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><codeLinkLookup className=\"com.dynatrace.Clazz\" methodName=\"testAll\" sessionId=\"1468832125420\" timedOut=\"false\" versionMatched=\"true\"><attributes/></codeLinkLookup>")));
        CodeLinkLookupResponse response = this.endpoint.connect(-1);
        assertTrue(response.className.equals("com.dynatrace.Clazz"));
        assertTrue(response.methodName.equals("testAll"));
        assertTrue(response.sessionId == 1468832125420L);
        assertTrue(!response.timedOut);
        assertTrue(response.versionMatched);
        verify(postRequestedFor(urlPathEqualTo("/rest/management/codelink/connect"))
                .withRequestBody(containing("sessionid=-1"))
                .withRequestBody(containing("ideid="+this.ide.getId()))
                .withRequestBody(containing("ideversion="+this.ide.getVersion())));
    }

    @Test
    public void respondGivenInvalidStatusCode() {
        stubFor(post(urlPathEqualTo("/rest/management/codelink/response")).willReturn(aResponse().withStatus(401)));
        try {
            this.endpoint.respond(CodeLinkEndpoint.ResponseStatus.NOT_FOUND ,-1);
            fail("Exception not thrown when expected to do");
        } catch (CodeLinkConnectionException e) {
            assertTrue(e.getMessage().equals("Unauthorized"));
        }
    }

    @Test
    public void respondGivenValidResponse() throws Exception {
        stubFor(post(urlPathEqualTo("/rest/management/codelink/response")).willReturn(aResponse().withStatus(200)));
        this.endpoint.respond(CodeLinkEndpoint.ResponseStatus.FOUND, -1);
        verify(postRequestedFor(urlPathEqualTo("/rest/management/codelink/response"))
            .withRequestBody(containing("responsecode="+CodeLinkEndpoint.ResponseStatus.FOUND.getCode()))
            .withRequestBody(containing("sessionid=-1")));
    }
}
