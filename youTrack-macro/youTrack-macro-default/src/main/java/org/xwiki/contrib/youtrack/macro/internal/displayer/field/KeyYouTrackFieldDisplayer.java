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

import org.jdom2.Element;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFieldDisplayer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.FormatBlock;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.VerbatimBlock;
import org.xwiki.rendering.listener.Format;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.listener.reference.ResourceType;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Displayer for the "key" YouTrack field (displayed with an external link to the YouTrack issue).
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("key")
@Singleton
public class KeyYouTrackFieldDisplayer implements YouTrackFieldDisplayer
{
    @Override
    public List<Block> displayField(YouTrackField field, Element issue, YouTrackMacroParameters parameters)
    {
        List<Block> result = Collections.emptyList();
        String key = issue.getChildText(YouTrackField.KEY.getId());
        if (key != null) {
            String link = issue.getChildText(YouTrackField.LINK.getId());
            List<Block> labelBlocks = Arrays.<Block>asList(new VerbatimBlock(key, true));

            // If the Issue is closed then display it striked-out
            String resolutionId = issue.getChild(YouTrackField.RESOLUTION.getId()).getAttributeValue("id");
            if (!"-1".equals(resolutionId)) {
                // The issue is resolved
                labelBlocks = Arrays.<Block>asList(new FormatBlock(labelBlocks, Format.STRIKEDOUT));
            }

            if (link != null) {
                ResourceReference reference = new ResourceReference(link, ResourceType.URL);
                result = Arrays.<Block>asList(new LinkBlock(labelBlocks, reference, true));
            } else {
                result = labelBlocks;
            }
        }
        return result;
    }
}
