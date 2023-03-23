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

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFieldDisplayer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.FormatBlock;
import org.xwiki.rendering.block.ImageBlock;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.VerbatimBlock;
import org.xwiki.rendering.block.WordBlock;
import org.xwiki.rendering.listener.Format;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.listener.reference.ResourceType;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displayer for the "reporter" YouTrack field
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("reporter")
@Singleton
public class ReporterYouTrackFieldDisplayer implements YouTrackFieldDisplayer {

    @Override
    public List<Block> displayField(YouTrackField field, ItemObject issue, YouTrackMacroParameters parameters)
    {
        List<Block> blockList = Collections.emptyList();
        Map<String, String> resourceParameters = new HashMap<String, String>();
        if(issue.getReporter() != null) {
            if(issue.getReporter().getFullName() != null && issue.getReporter().getAvatarUrl() != null) {
                resourceParameters.put("alt", issue.getReporter().getFullName());
                resourceParameters.put("title", issue.getReporter().getFullName());
                resourceParameters.put("width", "25");
                resourceParameters.put("height", "25");
                ImageBlock imageBlock = new ImageBlock(
                        new ResourceReference(issue.getReporter().getAvatarUrl(),
                                ResourceType.URL),
                        false, resourceParameters);
                blockList = Arrays.<Block>asList(imageBlock, new WordBlock(issue.getReporter().getFullName()));
            }
        }
        return blockList;
    }

}
