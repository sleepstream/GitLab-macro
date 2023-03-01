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
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.macro.MacroExecutionException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Takes a JQL query from the Macro content and return matching YouTrack issues.
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("jql")
@Singleton
public class JQLYouTrackDataSource extends AbstractYouTrackDataSource
{
    @Override
    public List<ItemObject> getData(String macroContent, YouTrackMacroParameters parameters)
        throws MacroExecutionException
    {
        YouTrackServer youTrackServer = getYouTrackServer(parameters);

        if (StringUtils.isBlank(macroContent)) {
            throw new MacroExecutionException("Missing JQL query!");
        }

        JsonObject document = getJsonDocument(youTrackServer, macroContent, parameters.getMaxCount());
        return buildIssues(document, youTrackServer);
    }
}
