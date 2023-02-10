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
package org.xwiki.contrib.youtrack.macro.internal;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;
import org.xwiki.contrib.youtrack.macro.YouTrackFields;
import org.xwiki.properties.converter.AbstractConverter;

import javax.inject.Singleton;
import java.lang.reflect.Type;

/**
 * Converts a String in the format {@code id:label!type,...} into a {@link YouTrackFields}.
 *
 * @version $Id$
 */
@Component
@Singleton
public class FieldConverter extends AbstractConverter<YouTrackFields>
{
    private static final char NAME_SEPARATOR = ':';

    private static final char TYPE_SEPARATOR = '!';

    private class FieldParser extends AbstractTokenListParser
    {
        public YouTrackFields parse(String content)
        {
            YouTrackFields fields = new YouTrackFields();
            for (String[] tokens : parseGeneric(content)) {
                YouTrackField field = new YouTrackField(tokens[0]);
                field.setLabel(tokens[1]);
                field.setType(tokens[2]);
                fields.add(field);
            }
            return fields;
        }

        @Override
        protected int getTokenCount()
        {
            return 3;
        }

        @Override
        protected boolean isSeparator(char currentChar)
        {
            return (currentChar == NAME_SEPARATOR || currentChar == TYPE_SEPARATOR);
        }

        @Override
        protected int getNextPosition(char currentChar)
        {
            int nextPosition;
            if (currentChar == NAME_SEPARATOR) {
                nextPosition = 1;
            } else {
                nextPosition = 2;
            }
            return nextPosition;
        }
    }

    private FieldParser parser = new FieldParser();

    @Override
    protected YouTrackFields convertToType(Type targetType, Object value)
    {
        return this.parser.parse(value.toString());
    }
}
