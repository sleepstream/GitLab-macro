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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFieldDisplayer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.CustomFields;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.FormatBlock;
import org.xwiki.rendering.block.ImageBlock;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.VerbatimBlock;
import org.xwiki.rendering.listener.Format;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.listener.reference.ResourceType;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displayer for the "Type" YouTrack field (displayed as an image).
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("type")
@Singleton
public class TypeYouTrackFieldDisplayer implements YouTrackFieldDisplayer {
    @Override
    public List<Block> displayField(YouTrackField field, ItemObject issue, YouTrackMacroParameters parameters)
    {
        List<Block> result;
        String type = issue.getCustomField(YouTrackField.TYPE.getId()).getValue().getName();
        VerbatimBlock verbatimBlock = new VerbatimBlock(type, true);

        Map<String, String> resourceParameters = new HashMap<>();
        resourceParameters.put("alt", type);
        resourceParameters.put("title", type);
        ImageBlock imageBlock = null;
        if ("Bug".equals(type)) {
            imageBlock = new ImageBlock(
                    new ResourceReference("/resources/icons/silk/bug.png",
                            ResourceType.URL),
                    false, resourceParameters);
        } else if ("Task".equals(type)) {
            imageBlock = new ImageBlock(
                    new ResourceReference("/resources/icons/silk/cog.png",
                            ResourceType.URL),
                    false, resourceParameters);
        }

        if(imageBlock != null) {
            result = Arrays.asList(
                    imageBlock, verbatimBlock);
        } else {
            result = Collections.singletonList(verbatimBlock);
        }
        return result;
    }

}
