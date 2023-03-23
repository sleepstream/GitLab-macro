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

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFieldDisplayer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.DefaultYouTrackServerResolver;
import org.xwiki.contrib.youtrack.macro.internal.source.YouTrackServerResolver;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.FormatBlock;
import org.xwiki.rendering.block.ImageBlock;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.RawBlock;
import org.xwiki.rendering.block.VerbatimBlock;
import org.xwiki.rendering.listener.Format;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.listener.reference.ResourceType;
import org.xwiki.rendering.macro.MacroExecutionException;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Ref;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.xwiki.rendering.syntax.Syntax.HTML_5_0;
import static org.xwiki.rendering.syntax.Syntax.XWIKI_2_0;
import static org.xwiki.rendering.syntax.Syntax.XWIKI_2_1;

/**
 * Displayer for the "key" YouTrack field (displayed with an external link to the YouTrack issue).
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("idReadable")
@Singleton
public class KeyYouTrackFieldDisplayer implements YouTrackFieldDisplayer {
    @Override
    public List<Block> displayField(YouTrackField field, ItemObject issue, YouTrackMacroParameters parameters)
    {
        List<Block> result = Collections.emptyList();
        String key = issue.getId();
        if (key != null) {
            List<Block> labelBlocks = Arrays.<Block>asList(new VerbatimBlock(key, true));
            ResourceReference reference = null;

            // If the Issue is closed then display it striked-out
            ImageBlock imageBlock = null;
            Map<String, String> resourceParameters = new HashMap<String, String>();
            String status = "No State";
            if(issue.getCustomField(YouTrackField.STATE.getId()).getValue() != null) {
                status = issue.getCustomField(YouTrackField.STATE.getId()).getValue().getName();
                resourceParameters.put("alt", status);
                resourceParameters.put("title", status);
                if ("Done".equals(status)) {
                    // The issue is resolved
                    labelBlocks = Arrays.<Block>asList(new FormatBlock(labelBlocks, Format.STRIKEDOUT));
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/accept.png",
                                    ResourceType.URL),
                            false, resourceParameters);
                } else if ("In Review".equals(status)) {
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/eye.png", ResourceType.URL),
                            false, resourceParameters);
                } else if ("In Progress".equals(status)) {
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/wrench.png", ResourceType.URL),
                            false, resourceParameters);
                } else if ("To Do".equals(status)) {
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/clock.png", ResourceType.URL),
                            false, resourceParameters);
                } else if ("In Testing".equals(status)) {
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/bomb.png", ResourceType.URL),
                            false, resourceParameters);
                } else if ("Open".equals(status)) {
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/page.png", ResourceType.URL),
                            false, resourceParameters);
                } else {
                    imageBlock = new ImageBlock(
                            new ResourceReference("/resources/icons/silk/arrow_out.png", ResourceType.URL),
                            true, resourceParameters);
                }
            } else {
                resourceParameters.put("alt", status);
                resourceParameters.put("title", status);
                imageBlock = new ImageBlock(
                        new ResourceReference("/resources/icons/silk/exclamation.png", ResourceType.URL),
                        true, resourceParameters);
            }

            reference = new ResourceReference(issue.getLink(), ResourceType.URL);
            result = Arrays.<Block>asList(
                    imageBlock,
                    new LinkBlock(labelBlocks, reference, true));
        }
        return result;
    }
}
