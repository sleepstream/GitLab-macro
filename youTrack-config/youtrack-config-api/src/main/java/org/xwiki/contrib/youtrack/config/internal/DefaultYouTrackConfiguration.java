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

import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.config.YouTrackServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Default in-memory implementation for the YouTrack Configuration.
 *
 * @version $Id$
 * @since 8.2
 */
public class DefaultYouTrackConfiguration implements YouTrackConfiguration
{
    private Map<String, YouTrackServer> youTrackServers = Collections.EMPTY_MAP;

    private boolean isAsync;

    @Override
    public Map<String, YouTrackServer> getYouTrackServers()
    {
        return this.youTrackServers;
    }

    /**
     * @param youTrackServers see {@link #getYouTrackServers()}
     */
    public void setYouTrackServers(Map<String, YouTrackServer> youTrackServers)
    {
        this.youTrackServers = new HashMap<>(youTrackServers);
    }

    @Override
    public boolean isAsync()
    {
        return this.isAsync;
    }

    /**
      * @param async see {@link #isAsync()}
     */
    public void setAsync(boolean async)
    {
        this.isAsync = async;
    }
}
