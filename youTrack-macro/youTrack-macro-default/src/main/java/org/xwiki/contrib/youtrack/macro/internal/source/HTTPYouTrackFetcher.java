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
package org.xwiki.contrib.youtrack.macro.internal.source;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.config.YouTrackServer;

import javax.inject.Singleton;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Fetches remotely the XML content at the passed URL.
 *
 * @version $Id$
 * @since 8.5
 */
@Component(roles = { HTTPYouTrackFetcher.class })
@Singleton
public class HTTPYouTrackFetcher
{
    private static final ErrorMessageExtractor EXTRACTOR = new ErrorMessageExtractor();

    /**
     * @param urlString the full YouTrack URL to call
     * @param youTrackServer the youtrack server data containing optional credentials (used to setup preemptive basic
     * authentication
     * @return the {@link Document} object containing the XML data
     * @throws Exception if an error happened during the fetch or if the passed URL is malformed
     */
    public Document fetch(String urlString, YouTrackServer youTrackServer) throws Exception
    {
        HttpGet httpGet = new HttpGet(urlString);
        CloseableHttpClient httpClient = createHttpClientBuilder(youTrackServer).build();

        HttpHost targetHost = createHttpHost(youTrackServer);
        HttpClientContext context = HttpClientContext.create();
        setPreemptiveBasicAuthentication(context, youTrackServer, targetHost);

        return retrieveRemoteDocument(httpClient, httpGet, targetHost, context);
    }

    private void setPreemptiveBasicAuthentication(HttpClientContext context, YouTrackServer youTrackServer,
                                                  HttpHost targetHost)
    {
        // Connect to YouTrack using basic authentication if username and password are defined
        // Note: Set up preemptive basic authentication since YouTrack can accept both
        // unauthenticated and authenticated
        // requests. See https://developer.atlassian.com/server/youtrack/platform/basic-authentication/
        if (StringUtils.isNotBlank(youTrackServer.getUsername()) && StringUtils.isNotBlank(youTrackServer
                .getPassword())) {
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials(youTrackServer.getUsername(), youTrackServer.getPassword()));
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            // Add AuthCache to the execution context
            context.setCredentialsProvider(provider);
            context.setAuthCache(authCache);
        }
    }

    private HttpHost createHttpHost(YouTrackServer server) throws MalformedURLException
    {
        URL youtrackURL = new URL(server.getURL());
        return new HttpHost(youtrackURL.getHost(), youtrackURL.getPort(), youtrackURL.getProtocol());
    }

    protected Document retrieveRemoteDocument(CloseableHttpClient httpClient, HttpGet httpGet, HttpHost targetHost,
        HttpClientContext context) throws Exception
    {
        try (CloseableHttpResponse response = httpClient.execute(targetHost, httpGet, context)) {
            // Only parse the content if there was no error.
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300) {
                HttpEntity entity = response.getEntity();
                return createSAXBuilder().build(entity.getContent());
            } else {
                // The error message is in the HTML. We extract it to perform some good error-reporting, by extracting
                // it from the <h1> tag.
                throw new Exception(String.format("Error = [%s]. URL = [%s]",
                    EXTRACTOR.extract(response.getEntity().getContent()), httpGet.getURI().toString()));
            }
        }
    }

    protected HttpClientBuilder createHttpClientBuilder(YouTrackServer youTrackServer)
    {
        // Allows system properties to override our default config (by calling useSystemProperties() first).
        HttpClientBuilder builder = HttpClientBuilder.create().useSystemProperties();
        return builder.setUserAgent("XWikiYouTrackMacro");
    }

    /**
     * @return the SAXBuilder instance to use to retrieve the data
     */
    private SAXBuilder createSAXBuilder()
    {
        // Note: SAXBuilder is not thread-safe which is why we're instantiating a new one every time.
        SAXBuilder builder = new SAXBuilder();
        // Note: Prevent XXE attacks by disabling completely DTDs. This is possible since YouTrack
        // returns an XML content
        // that doesn't use a DTD.
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return builder;
    }
}
