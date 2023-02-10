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
package org.xwiki.contrib.youtrack.config;

/**
 * Represents all data related to a YouTrack server: url and credentials to access it.
 *
 * @version $Id$
 * @since 8.2
 */
public class YouTrackServer
{
    private String url;

    private String username;

    private String password;

    /**
     * Public-access YouTrack server.
     *
     * @param url see {@link #getURL()}
     */
    public YouTrackServer(String url)
    {
        this.url = url;
    }

    /**
     * Credential-protected YouTrack server.
     *
     * @param url see {@link #getURL()}
     * @param username see {@link #getUsername()}
     * @param password see {@link #getPassword()}
     */
    public YouTrackServer(String url, String username, String password)
    {
        this(url);
        this.username = username;
        this.password = password;
    }

    /**
     * @return the YouTrack server's url prefix (e.g. {@code http://youtrack.xwiki.org})
     */
    public String getURL()
    {
        return this.url;
    }

    /**
     * @return the username to log in on that YouTrack server
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @return the password to log in on that YouTrack server
     */
    public String getPassword()
    {
        return this.password;
    }
}
