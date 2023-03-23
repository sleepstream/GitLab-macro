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
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFieldDisplayer;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.CustomFields;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ValueObject;

import java.util.stream.Collectors;

/**
 * Helper to extract field values from YouTrack XML.
 *
 * @version $Id$
 * @since 8.3
 */
public abstract class AbstractYouTrackFieldDisplayer implements YouTrackFieldDisplayer
{
    /**
     * Get the field value from the passed XML represented as an {@link Element} and look in custom fields when not
     * found in the default youtrack fields.
     *
     * @param field the field for which to get the value
     * @param issue the XML representation of the YouTrack issue from which to extract the field's value
     * @return the string value of the field from the passed issue or null if not found
     */
    protected String getValue(YouTrackField field, ItemObject issue)
    {
        if(field.equals(YouTrackField.SUMMARY) || field.getId().equalsIgnoreCase(YouTrackField.SUMMARY.getId())) {
            return issue.getSummary();
        }

        CustomFields customField = issue.getCustomField(field.getId());
        if(customField.getValues() != null) {
            return customField.getValues().stream().map(ValueObject::getName).collect(Collectors.joining(", "));
        }
        return customField.getValue().getName();
    }
}
