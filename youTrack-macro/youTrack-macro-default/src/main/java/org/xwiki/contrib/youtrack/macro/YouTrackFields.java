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
package org.xwiki.contrib.youtrack.macro;

import java.util.ArrayList;
import java.util.Collection;

/**
 * List of {@link YouTrackField}. Note that this class is required only because of a limitation, see
 * <a href="http://youtrack.xwiki.org/browse/XCOMMONS-994">XCOMMONS-994</a>.
 *
 * @version $Id$
 * @since 8.3
 */
public class YouTrackFields extends ArrayList<YouTrackField>
{
    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param  initialCapacity  the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is negative
     */
    public YouTrackFields(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public YouTrackFields()
    {
        super();
    }

    /**
     * Constructs a list containing the elements of the specified collection, in the order they are returned by the
     * collection's iterator.
     *
     * @param collection the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public YouTrackFields(Collection<? extends YouTrackField> collection)
    {
        super(collection);
    }
}
