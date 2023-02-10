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
package org.xwiki.contrib.youtrack.macro.internal.displayer;

import org.jdom2.Element;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFields;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.SpaceBlock;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Displays YouTrack issues next to each other (like an enumeration) in inline mode.
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("enum")
@Singleton
public class EnumYouTrackDisplayer extends AbstractYouTrackDisplayer
{
    /**
     * Default list of YouTrack fields to display.
     */
    private static final YouTrackFields FIELDS = new YouTrackFields(Arrays.asList(YouTrackField.STATUS,
            YouTrackField.KEY));

    @Override
    public List<Block> display(Collection<Element> issues, YouTrackMacroParameters parameters)
    {
        List<Block> blocks = new ArrayList<>();

        YouTrackFields fields = normalizeFields(parameters);
        Iterator<Element> issueIt = issues.iterator();
        while (issueIt.hasNext()) {
            Element issue = issueIt.next();
            Iterator<YouTrackField> it = fields.iterator();
            while (it.hasNext()) {
                YouTrackField field = it.next();
                // Use the displayer for the field
                blocks.addAll(getFieldDisplayer(field).displayField(field, issue, parameters));
                // Add space to separate fields, unless we're on the last field
                if (it.hasNext()) {
                    blocks.add(new SpaceBlock());
                }
            }
            // Add space to separate issues, unless we're on the last field
            if (issueIt.hasNext()) {
                blocks.add(new SpaceBlock());
            }
        }
        return blocks;
    }

    @Override
    protected YouTrackFields getDefaultFields()
    {
        return FIELDS;
    }
}
