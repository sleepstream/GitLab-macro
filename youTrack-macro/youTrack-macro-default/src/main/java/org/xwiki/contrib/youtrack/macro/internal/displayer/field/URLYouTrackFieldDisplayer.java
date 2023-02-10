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

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.VerbatimBlock;
import org.xwiki.rendering.listener.reference.ResourceReference;
import org.xwiki.rendering.listener.reference.ResourceType;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Generic displayer for URL-type fields.
 *
 * @version $Id$
 * @since 8.3
 */
@Component
@Named("type/url")
@Singleton
public class URLYouTrackFieldDisplayer extends AbstractYouTrackFieldDisplayer
{
    @Override
    public List<Block> displayField(YouTrackField field, Element issue, YouTrackMacroParameters parameters)
    {
        List<Block> result = Collections.emptyList();

        String value = getValue(field, issue);
        if (value != null) {
            String label = parameters.getParameters().getProperty("field.url.label");
            if (StringUtils.isBlank(label)) {
                label = value;
            }
            List<Block> labelBlocks = Arrays.<Block>asList(new VerbatimBlock(label, true));
            ResourceReference reference = new ResourceReference(value, ResourceType.URL);
            result = Arrays.<Block>asList(new LinkBlock(labelBlocks, reference, true));
        }

        return result;
    }
}
