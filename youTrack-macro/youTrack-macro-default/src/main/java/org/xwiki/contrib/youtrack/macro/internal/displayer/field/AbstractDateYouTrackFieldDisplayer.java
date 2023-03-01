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
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.VerbatimBlock;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Common Field Displayer for Dates. Parses field in the YouTrack date format and generates date the format
 * {@code dd-MMM-yyyy}.
 *
 * @version $Id$
 * @since 4.2M1
 */
public abstract class AbstractDateYouTrackFieldDisplayer extends AbstractYouTrackFieldDisplayer
{
    /**
     * YouTrack Date format.
     */
    private DateFormat youtrackDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    /**
     * Date format for displaying.
     */
    private DateFormat displayDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

    @Override
    public List<Block> displayField(YouTrackField field, ItemObject issue, YouTrackMacroParameters parameters)
    {
        List<Block> result;
        String date = getValue(field, issue);
        if (date != null) {
            try {
                Date parsedDate = this.youtrackDateFormat.parse(date);
                result = Arrays.<Block>asList(new VerbatimBlock(this.displayDateFormat.format(parsedDate), true));
            } catch (ParseException e) {
                result = Arrays.<Block>asList(new VerbatimBlock(date, true));
            }
        } else {
            result = Collections.emptyList();
        }
        return result;
    }
}
