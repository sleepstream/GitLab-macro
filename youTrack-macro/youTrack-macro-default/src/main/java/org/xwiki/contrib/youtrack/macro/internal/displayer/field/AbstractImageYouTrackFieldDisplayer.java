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
package org.xwiki.contrib.youtrack.macro.internal.displayer.field;

import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFieldDisplayer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.CustomFields;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.ImageBlock;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.listener.reference.ResourceType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common Field Displayer for fields that need to display an image (eg for State or Type fields).
 *
 * @version $Id$
 * @since 4.2M1
 */
public abstract class AbstractImageYouTrackFieldDisplayer implements YouTrackFieldDisplayer
{
    @Override
    public List<Block> displayField(YouTrackField field, ItemObject issue, YouTrackMacroParameters parameters)
    {
        List<Block> result;
        String key = issue.getId();
        ResourceReference reference = new ResourceReference(issue.getLink(), ResourceType.URL);
        Map<String, String> resourceParameters = new HashMap<String, String>();
        resourceParameters.put("alt", issue.getSummary());
        resourceParameters.put("title", issue.getType());
        result = Arrays.<Block>asList(new ImageBlock(reference, false, resourceParameters));
        return result;
    }

    /**
     * @param issue the XML Element representing a YouTrack issue
     * @return the XML Element that contains the text to display as an image
     */
    protected abstract CustomFields getElement(ItemObject issue);
}
