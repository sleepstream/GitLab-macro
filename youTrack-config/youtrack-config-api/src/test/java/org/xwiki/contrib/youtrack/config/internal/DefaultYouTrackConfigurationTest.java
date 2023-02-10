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

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DefaultYouTrackConfiguration}.
 *
 * @version $Id$
 * @since 8.2
 */
public class DefaultYouTrackConfigurationTest
{
    @Rule
    public MockitoComponentMockingRule<DefaultYouTrackConfiguration> mocker =
        new MockitoComponentMockingRule<>(DefaultYouTrackConfiguration.class);

    private class CustomYouTrackConfiguration implements YouTrackConfiguration
    {
        @Override
        public Map<String, YouTrackServer> getYouTrackServers()
        {
            return null;
        }
    }

    @Test
    public void getterAndSetters() throws Exception
    {
        DefaultYouTrackConfiguration configuration = this.mocker.getComponentUnderTest();

        assertTrue(configuration.getYouTrackServers().isEmpty());
        configuration.setYouTrackServers(Collections.singletonMap("key", new YouTrackServer("url", "username", "password")));
        assertEquals(1, configuration.getYouTrackServers().size());
        assertEquals("url", configuration.getYouTrackServers().get("key").getURL());
        assertEquals("username", configuration.getYouTrackServers().get("key").getUsername());
        assertEquals("password", configuration.getYouTrackServers().get("key").getPassword());

        assertFalse(configuration.isAsync());
        configuration.setAsync(true);
        assertTrue(configuration.isAsync());

        // Verify backward compatibility and that async is false by default
        assertFalse(new CustomYouTrackConfiguration().isAsync());
    }
}
