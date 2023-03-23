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

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFields;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.BulletedListBlock;
import org.xwiki.rendering.block.ListItemBlock;
import org.xwiki.rendering.block.NumberedListBlock;
import org.xwiki.rendering.block.SpaceBlock;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Displays YouTrack issues as a list (ie one under another).
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("list")
@Singleton
public class ListYouTrackDisplayer extends AbstractYouTrackDisplayer
{
    /**
     * Default list of YouTrack fields to display.
     */
    private static final YouTrackFields FIELDS = new YouTrackFields(
        Arrays.asList(YouTrackField.STATE, YouTrackField.KEY, YouTrackField.SUMMARY));

    @Override
    public List<Block> display(Collection<ItemObject> issues, YouTrackMacroParameters parameters)
    {
        List<Block> listItemBlocks = new ArrayList<>();

        YouTrackFields fields = normalizeFields(parameters);
        for (ItemObject issue : issues) {
            List<Block> itemBlocks = new ArrayList<>();
            Iterator<YouTrackField> it = fields.iterator();
            while (it.hasNext()) {
                YouTrackField field = it.next();
                // Use the displayer for the field
                itemBlocks.addAll(getFieldDisplayer(field).displayField(field, issue, parameters));
                // Add space to separate fields, unless we're on the last field
                if (it.hasNext()) {
                    itemBlocks.add(new SpaceBlock());
                }
            }
            listItemBlocks.add(new ListItemBlock(itemBlocks));
        }
        return Arrays.asList(new NumberedListBlock(listItemBlocks));
    }

    @Override
    protected YouTrackFields getDefaultFields()
    {
        return FIELDS;
    }
}
