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

import com.google.gson.JsonElement;
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
@Named("jql")
@Singleton
public class SearchYouTrackDataSource extends AbstractYouTrackDataSource
{
    @Override
    public List<ItemObject> getData(String macroContent, YouTrackMacroParameters parameters)
        throws MacroExecutionException
    {
        YouTrackServer youTrackServer = getYouTrackServer(parameters);


        if (StringUtils.isBlank(macroContent)) {
            throw new MacroExecutionException("Missing JQL query!");
        }

        return buildIssues(macroContent, youTrackServer, parameters);
    }

    /**

     * @param macroContent the jql request YouTrack specified by the user
     * @return the list of YouTrack issues (returned as XML elements), in the same order as the
     * YouTrack issue id list specified
     *         by the user
     */
    public List<ItemObject> buildIssues(String macroContent,
                                        YouTrackServer youTrackServer, YouTrackMacroParameters parameters)
            throws MacroExecutionException
    {
        // Note: YouTrack doesn't return items in the order specified in the JQL query, thus we need to manually order
        // them in the same order as passed in the issueIds parameter.

        List<ItemObject> issues = new ArrayList<>();
        List<JsonObject> documentsList = getJsonDocumentByJQL(youTrackServer, macroContent, parameters.getMaxCount());
        for (JsonObject issue : documentsList) {
            issues.add(buildIssues(issue, youTrackServer));
        }

        return issues;
    }
}
