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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ListYouTrackDataSource} and
 * {@link AbstractYouTrackDataSource}.
 *
 * @version $Id$
 * @since 4.2M1
 */
@ComponentList({
    DefaultYouTrackServerResolver.class
})
public class ListYouTrackDataSourceTest
{
    @Rule
    public MockitoComponentMockingRule<ListYouTrackDataSource> mocker =
        new MockitoComponentMockingRule<>(ListYouTrackDataSource.class, Arrays.asList(YouTrackServerResolver.class));

    @Test
    public void parseIdsWhenNull() throws Exception
    {
        assertEquals(Collections.emptyList(), this.mocker.getComponentUnderTest().parseIds(null));
    }

    @Test
    public void parseIds() throws Exception
    {
        List<Pair<String, String>> expected = Arrays.asList(
            new ImmutablePair<>("ISSUE-1", ""),
            new ImmutablePair<>("ISSUE-2", "Whatever"),
            new ImmutablePair<>("ISSUE-3", ""));
        assertEquals(expected,
            this.mocker.getComponentUnderTest().parseIds("\nISSUE-1\nISSUE-2 |Whatever \n ISSUE-3\n"));
    }

//    /**
//     * Verify several things:
//     * <ul>
//     *     <li>Issue order is preserved even though YouTrack returns them in no specific order</li>
//     *     <li>List fields are supported (for example the "version" field)</li>
//     *     <li>Notes are taken into account</li>
//     * </ul>
//     */
//    @Test
//    public void buildIssues() throws Exception
//    {
//        List<Pair<String, String>> ids = Arrays.asList(
//            new ImmutablePair<>("XWIKI-1000", ""),
//            new ImmutablePair<>("XWIKI-1001", "Note"));
//
//        List<Element> issues = this.mocker.getComponentUnderTest().buildIssues(document, ids);
//
//        assertEquals(2, issues.size());
//        Element issue1 = issues.get(0);
//        assertEquals("XWIKI-1000", issue1.getChildTextTrim(KEY.getId()));
//        assertEquals("Improve PDF Output", issue1.getChildTextTrim(SUMMARY.getId()));
//        Element issue2 = issues.get(1);
//        assertEquals("XWIKI-1001", issue2.getChildTextTrim(KEY.getId()));
//        assertEquals("On jetty, non-default skins are not usable", issue2.getChildTextTrim(SUMMARY.getId()));
//        assertEquals("Note", issue2.getChildTextTrim(NOTE));
//    }

    @Test
    public void getYouTrackServerWhenNoneDefined() throws Exception
    {
        try {
            this.mocker.getComponentUnderTest().getYouTrackServer(new YouTrackMacroParameters());
            fail("should have thrown an exception");
        } catch (MacroExecutionException expected) {
            assertEquals("No YouTrack Server found. You must specify a YouTrack server, using the \"url\" macro parameter or "
                + "using the \"id\" macro parameter to reference a server defined in the YouTrack Macro configuration.",
                expected.getMessage());
        }
    }

    @Test
    public void getYouTrackServerWhenIdUsedButNoneDefined() throws Exception
    {
        YouTrackMacroParameters parameters = new YouTrackMacroParameters();
        parameters.setId("unknownid");
        try {
            this.mocker.getComponentUnderTest().getYouTrackServer(parameters);
            fail("should have thrown an exception");
        } catch (MacroExecutionException expected) {
            assertEquals("The YouTrack Server id [unknownid] is not defined in the macro's configuration. Please fix the "
                + "id or add a new server in the YouTrack Macro configuration.", expected.getMessage());
        }
    }

    @Test
    public void getYouTrackServerWhenIdUsedAndDefined() throws Exception
    {
        YouTrackConfiguration configuration = this.mocker.getInstance(YouTrackConfiguration.class);
        when(configuration.getYouTrackServers()).thenReturn(Collections.singletonMap("someid",
            new YouTrackServer("http://localhost")));

        YouTrackMacroParameters parameters = new YouTrackMacroParameters();
        parameters.setId("someid");
        assertEquals("http://localhost", this.mocker.getComponentUnderTest().getYouTrackServer(parameters).getURL());
    }

    @Test
    public void computeFullURL() throws Exception
    {
        // No credentials passed
        YouTrackServer youTrackServer = new YouTrackServer("http://localhost/youtrack");
        assertEquals("http://localhost/youtrack/sr/youtrack.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query",
            this.mocker.getComponentUnderTest().computeFullURL(youTrackServer, "query", -1));

        // Just username defined but no password (or empty password)
        youTrackServer = new YouTrackServer("http://localhost/youtrack", "username", "");
        assertEquals("http://localhost/youtrack/sr/youtrack.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=query",
            this.mocker.getComponentUnderTest().computeFullURL(youTrackServer, "query", -1));

        // With Max Count and no credentials
        youTrackServer = new YouTrackServer("http://localhost/youtrack");
        assertEquals("http://localhost/youtrack/sr/youtrack.issueviews:searchrequest-xml/temp/SearchRequest.xml?"
                + "jqlQuery=query&tempMax=5",
            this.mocker.getComponentUnderTest().computeFullURL(youTrackServer, "query", 5));
    }
}
