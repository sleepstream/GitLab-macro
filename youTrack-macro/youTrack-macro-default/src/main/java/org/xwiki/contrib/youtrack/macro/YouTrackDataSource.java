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

import org.xwiki.component.annotation.Role;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.rendering.macro.MacroExecutionException;

import java.util.List;

/**
 * Source to fetch data from a YouTrack instance.
 *
 * @version $Id$
 * @since 4.2M1
 */
@Role
public interface YouTrackDataSource
{
    /**
     * @param macroContent the macro content which contains the source definition
     * @param parameters the macro parameters which can contain source-specific configuration information
     * @return the list of matching YouTrack issues
     * @throws MacroExecutionException in case of an error while getting the YouTrack data
     */
    List<ItemObject> getData(String macroContent, YouTrackMacroParameters parameters)
        throws MacroExecutionException;
}
