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
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.macro.YouTrackField;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Displayer for the "status" YouTrack field (displayed as an image).
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("status")
@Singleton
public class StatusYouTrackFieldDisplayer extends AbstractImageYouTrackFieldDisplayer
{
    @Override
    protected Element getElement(Element issue)
    {
        return issue.getChild(YouTrackField.STATUS.getId());
    }

    @Override
    protected String getURL(Element issue)
    {
        return getElement(issue).getAttributeValue("iconUrl");
    }
}
