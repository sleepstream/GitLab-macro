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

import com.xpn.xwiki.internal.context.XWikiContextContextStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.YouTrackMacro;
import org.xwiki.rendering.async.internal.AsyncRendererConfiguration;
import org.xwiki.rendering.async.internal.block.BlockAsyncRendererExecutor;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.CompositeBlock;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.transformation.MacroTransformationContext;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Overrides the {@link YouTrackMacro} to make it work asynchronously. This is done so that the YouTrack macro can be used
 * in XWiki Rendering (the non-async version) or in XWiki Platform (the async version).
 *
 * @since 8.6
 * @version $Id$
 */
@Component
@Named("YouTrack")
@Singleton
public class AsyncYouTrackMacro extends YouTrackMacro
{
    @Inject
    @Named("context")
    private ComponentManager contextComponentManager;

    @Inject
    private BlockAsyncRendererExecutor executor;

    @Inject
    private YouTrackConfiguration youTrackConfiguration;

    @Override
    public List<Block> execute(YouTrackMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        YouTrackBlockAsyncRenderer renderer;
        try {
            renderer = this.contextComponentManager.getInstance(YouTrackBlockAsyncRenderer.class);
        } catch (ComponentLookupException e) {
            throw new MacroExecutionException("Failed to create YouTrack async renderer", e);
        }
        renderer.initialize(this, parameters, content, this.youTrackConfiguration.isAsync(), context);

        AsyncRendererConfiguration configuration = new AsyncRendererConfiguration();

        // Take into account the current wiki since the YouTrack config document
        // can be located in each wiki (without this
        // all config document will be taken from the main wiki).
        configuration.setContextEntries(Collections.singleton(XWikiContextContextStore.PROP_WIKI));

        // Execute the renderer
        Block result;
        try {
            result = this.executor.execute(renderer, configuration);
        } catch (Exception e) {
            throw new MacroExecutionException("Failed to execute YouTrack macro", e);
        }

        return result instanceof CompositeBlock ? result.getChildren() : Arrays.asList(result);
    }

    List<Block> executeCodeMacro(YouTrackMacroParameters parameters, String content,
        MacroTransformationContext context) throws MacroExecutionException
    {
        return super.execute(parameters, content, context);
    }
}
