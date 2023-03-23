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

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.macro.MacroExecutionException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


/**
 * Takes a discrete list of YouTrack issues from the Macro content and return their field values.
 *
 * The format is one issue id per line, ignoring any data after the pipe symbol (used to add some notes).
 * <p/>
 * Example:
 * <code><pre>
 *   ISSUE-1
 *   ISSUE-2|Whatever here
 *   ISSUE-3
 * </pre></code>
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("list")
@Singleton
public class ListYouTrackDataSource extends AbstractYouTrackDataSource
{
    /**
     * The symbol used to separate the issue id from a user-specified note.
     */
    private static final String PIPE = "|";

    @Override
    public List<ItemObject> getData(String macroContent, YouTrackMacroParameters parameters)
        throws MacroExecutionException
    {
        YouTrackServer youTrackServer = getYouTrackServer(parameters);

        List<Pair<String, String>> ids = parseIds(macroContent);
        if (ids.isEmpty()) {
            throw new MacroExecutionException("Empty list of YouTrack ids!");
        }

        return buildIssues(ids, youTrackServer, parameters);
    }

    /**

     * @param issueIds the list of YouTrack issue ids specified by the user
     * @return the list of YouTrack issues (returned as XML elements), in the same order as the
     * YouTrack issue id list specified
     *         by the user
     */
    public List<ItemObject> buildIssues(List<Pair<String, String>> issueIds,
                                        YouTrackServer youTrackServer, YouTrackMacroParameters parameters)
            throws MacroExecutionException
    {
        // Note: YouTrack doesn't return items in the order specified in the JQL query, thus we need to manually order
        // them in the same order as passed in the issueIds parameter.

        List<ItemObject> issues = new ArrayList<>();
        for (Pair<String, String> id : issueIds) {
            JsonObject document = getJsonDocument(youTrackServer, id.getLeft(), parameters.getMaxCount());
            issues.add(buildIssues(document, youTrackServer));
        }

        return issues;
    }

    /**
     * @param macroContent the macro content listing YouTrack issue ids and optional notes
     * @return the list of issue ids and optional notes specified in the macro content
     */
    public List<Pair<String, String>> parseIds(String macroContent)
    {
        List<Pair<String, String>> ids = new ArrayList<Pair<String, String>>();
        if (macroContent != null) {
            for (String issueLine : macroContent.split("\\r?\\n")) {
                // Split on pipe symbol
                String issue = StringUtils.substringBefore(issueLine, PIPE).trim();
                String note = StringUtils.substringAfter(issueLine, PIPE).trim();
                // Only add if the issue is not empty
                if (StringUtils.isNotBlank(issue)) {
                    ids.add(new ImmutablePair<String, String>(issue, note));
                }
            }
        }
        return ids;
    }
}
