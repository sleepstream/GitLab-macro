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
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.youtrack.macro.YouTrackDataSource;
import org.xwiki.contrib.youtrack.macro.YouTrackDisplayer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.macro.descriptor.DefaultContentDescriptor;
import org.xwiki.rendering.transformation.MacroTransformationContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

/**
 * Fetches information from a YouTrack server and displays them as a table, list or enumeration.
 *
 * @version $Id$
 * @since 4.2M1
 */
@Component
@Named("YouTrack")
@Singleton
public class YouTrackMacro extends AbstractMacro<YouTrackMacroParameters>
{
    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION =
            "Fetches information from a YouTrack server and displays them as a table, list or enumeration.";

    /**
     * The description of the macro content.
     */
    private static final String CONTENT_DESCRIPTION = "The YouTrack issues to retrieve";

    /**
     * Used to get YouTrack Data Source and YouTrack Displayer matching what the user has asked for.
     */
    @Inject
    private ComponentManager componentManager;

    /**
     * Create and initialize the descriptor of the macro.
     */
    public YouTrackMacro()
    {
        super("YouTrack", DESCRIPTION, new DefaultContentDescriptor(CONTENT_DESCRIPTION),
                YouTrackMacroParameters.class);
        setDefaultCategory(DEFAULT_CATEGORY_CONTENT);
    }

    @Override
    public boolean supportsInlineMode()
    {
        return true;
    }

    @Override
    public List<Block> execute(YouTrackMacroParameters parameters, String content, MacroTransformationContext context)
            throws MacroExecutionException
    {
        return getDisplayer(parameters).display(getDataSource(parameters).getData(content, parameters), parameters);
    }

    /**
     * @param parameters the macro parameters specified by the user
     * @return the data source component asked by the user (defaults to the List Data Source if not specified)
     * @throws MacroExecutionException if the data source component doesn't exist
     */
    private YouTrackDataSource getDataSource(YouTrackMacroParameters parameters) throws MacroExecutionException
    {
        YouTrackDataSource source;
        try {
            source = this.componentManager.getInstance(YouTrackDataSource.class, parameters.getSource());
        } catch (ComponentLookupException e) {
            throw new MacroExecutionException(String.format("Unknown YouTrack source [%s]", parameters.getSource()), e);
        }

        return source;
    }

    /**
     * @param parameters the macro parameters specified by the user
     * @return the displayer component sked by the user (defaults to the Table Displayer if not specified)
     * @throws MacroExecutionException if the displayer component doesn't exist
     */
    private YouTrackDisplayer getDisplayer(YouTrackMacroParameters parameters) throws MacroExecutionException
    {
        YouTrackDisplayer displayer;
        try {
            displayer = this.componentManager.getInstance(YouTrackDisplayer.class, parameters.getStyle());
        } catch (ComponentLookupException e) {
            throw new MacroExecutionException(String.format("Unknown YouTrack style [%s]", parameters.getStyle()), e);
        }

        return displayer;
    }
}
