/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.youtrack.macro;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.rendering.test.integration.RenderingTestSuite;
import org.xwiki.test.annotation.AllComponents;
import org.xwiki.test.mockito.MockitoComponentManager;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.mockito.Mockito.when;

/**
 * Run all tests found in {@code *.test} files located in the classpath. These {@code *.test} files must follow the
 * conventions described in {@link org.xwiki.rendering.test.integration.TestDataParser}.
 *
 * @version $Id$
 * @since 4.2M1
 */
@RunWith(RenderingTestSuite.class)
@AllComponents
public class IntegrationTests
{
    private static WireMockServer server;

    @BeforeClass
    public static void setUp()
    {
        // Simulate a fake YouTrack instance using WireMock
        server = new WireMockServer(8889);
        server.start();

        // Default answer for testingrequesting a non-existing YouTrack issue
        server.stubFor(get(urlMatching(
            "\\/sr\\/youtrack.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*NOTEXISTING.*"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "text/html")
                .withBodyFile("notfound.html")));

        // Default answer when no authentication is required
        server.stubFor(get(urlMatching(
            "\\/sr\\/youtrack.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*XWIKI-1000.*"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBodyFile("input.xml")));

        // Default answer when authentication is required
        server.stubFor(get(urlMatching(
            "\\/auth\\/sr\\/youtrack.issueviews:searchrequest-xml\\/temp\\/SearchRequest\\.xml\\?jqlQuery=.*"))
            .withBasicAuth("username", "password")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/xml")
                .withBodyFile("input.xml")));
    }

    @RenderingTestSuite.Initialized
    public void initialize(MockitoComponentManager componentManager) throws Exception
    {
        // Register various YouTrackServer configurations:
        // - one with authentication
        // - one without authentication
        YouTrackConfiguration configuration = componentManager.registerMockComponent(YouTrackConfiguration.class);
        Map<String, YouTrackServer> servers = new HashMap<>();
        YouTrackServer server1 = new YouTrackServer("http://localhost:8889");
        servers.put("youtrack-noauth", server1);
        YouTrackServer server2 = new YouTrackServer("http://localhost:8889/auth", "username", "password");
        servers.put("youtrack-auth", server2);
        when(configuration.getYouTrackServers()).thenReturn(servers);
    }

    @AfterClass
    public static void tearDown()
    {
        server.stop();
    }
}
