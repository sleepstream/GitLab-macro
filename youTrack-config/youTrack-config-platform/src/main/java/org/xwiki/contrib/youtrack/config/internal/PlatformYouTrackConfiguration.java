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
package org.xwiki.contrib.youtrack.config.internal;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.config.YouTrackServer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.Map;

/**
 * Platform implementation of the YouTrack configuration.
 *
 * @version $Id$
 */
@Component
@Singleton
public class PlatformYouTrackConfiguration implements YouTrackConfiguration
{
    @Inject
    @Named("YouTrack")
    private ConfigurationSource youTrackConfigurationSource;

    @Inject
    @Named("xwikiproperties")
    private ConfigurationSource xwikiPropertiesConfigurationSource;

    @Override
    public Map<String, YouTrackServer> getYouTrackServers()
    {
        Map<String, YouTrackServer> youTrackServers = this.youTrackConfigurationSource.getProperty("serverMappings");
        // The returned value can be null if no xobject has been defined on the wiki config page.
        if (youTrackServers == null) {
            youTrackServers = Collections.emptyMap();
        }
        return youTrackServers;
    }

    @Override
    public boolean isAsync()
    {
        return this.xwikiPropertiesConfigurationSource.getProperty("YouTrack.async", Boolean.TRUE);
    }
}
