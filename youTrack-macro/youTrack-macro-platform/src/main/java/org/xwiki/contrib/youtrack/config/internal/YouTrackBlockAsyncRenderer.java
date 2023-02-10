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

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.YouTrackServerResolver;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rendering.async.AsyncContext;
import org.xwiki.rendering.async.internal.block.AbstractBlockAsyncRenderer;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.CompositeBlock;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.MetaDataBlock;
import org.xwiki.rendering.block.MacroMarkerBlock;
import org.xwiki.rendering.block.match.MetadataBlockMatcher;
import org.xwiki.rendering.listener.MetaData;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.rendering.util.ErrorBlockGenerator;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Render the YouTrack macro content asynchronously.
 *
 * @since 8.6
 * @version $Id$
 */
@Component(roles = YouTrackBlockAsyncRenderer.class)
public class YouTrackBlockAsyncRenderer extends AbstractBlockAsyncRenderer
{
    private static final String ESCAPE_CHAR_SLASH = "_";

    private static final String ESCAPE_CHAR_BACKSLASH = "-";

    @Inject
    private DocumentReferenceResolver<String> resolver;

    @Inject
    private AsyncContext asyncContext;

    @Inject
    private ErrorBlockGenerator errorBlockGenerator;

    @Inject
    private YouTrackServerResolver youTrackServerResolver;

    private List<String> id;

    private boolean inline;

    private Syntax targetSyntax;

    private AsyncYouTrackMacro macro;

    private YouTrackMacroParameters parameters;

    private String content;

    private MacroTransformationContext context;

    private DocumentReference sourceReference;

    private boolean isAsync;

    void initialize(AsyncYouTrackMacro macro, YouTrackMacroParameters parameters, String content, boolean isAsync,
                    MacroTransformationContext context)
    {
        this.macro = macro;
        this.parameters = parameters;
        this.content = content;
        this.isAsync = isAsync;
        this.context = context;

        this.inline = context.isInline();
        this.targetSyntax = context.getTransformationContext().getTargetSyntax();

        String source = getCurrentSource(context);
        if (source != null) {
            this.sourceReference = this.resolver.resolve(source);
        }

        this.id = createId(source, context);
    }

    @Override
    protected Block execute(boolean async, boolean cached)
    {
        List<Block> resultBlocks;

        if (this.sourceReference != null) {
            // Invalidate the cache when the document containing the macro call is modified
            this.asyncContext.useEntity(this.sourceReference);
        }
        try {
            resultBlocks = this.macro.executeCodeMacro(this.parameters, this.content, this.context);
        } catch (MacroExecutionException e) {
            // Display the error in the result
            resultBlocks = this.errorBlockGenerator.generateErrorBlocks("Failed to execute the YouTrack macro", e,
                this.inline);
        }

        resultBlocks = Arrays.asList(wrapInMacroMarker(this.context.getCurrentMacroBlock(), resultBlocks));

        return new CompositeBlock(resultBlocks);
    }

    @Override
    public boolean isInline()
    {
        return this.inline;
    }

    @Override
    public Syntax getTargetSyntax()
    {
        return this.targetSyntax;
    }

    @Override
    public List<String> getId()
    {
        return this.id;
    }

    @Override
    public boolean isAsyncAllowed()
    {
        return this.isAsync;
    }

    @Override
    public boolean isCacheAllowed()
    {
        return false;
    }

    private List<String> createId(String source, MacroTransformationContext context)
    {
        // Find index of the macro in the XDOM to create a unique id.
        long index = context.getXDOM().indexOf(context.getCurrentMacroBlock());

        // Make sure we don't have a / or \ in the source so that it works on Tomcat by default even if Tomcat is not
        // configured to support \ and / in URLs.
        // Make "_" and "-" escape characters and thus:
        // - replace "_" by "__"
        // - replace "-" by "--"
        // - replace "\" by "_"
        // - replace "/" by "-"
        // This keeps the unicity of the source reference.
        // TODO: Remove when the parent pom is upgraded to 13.0+ (i.e where
        //  https://YouTrack.xwiki.org/browse/XWIKI-17515
        //  has been fixed).
        String escapedSource = StringUtils.replaceEach(source,
            new String[] { ESCAPE_CHAR_SLASH, ESCAPE_CHAR_BACKSLASH },
            new String[] { ESCAPE_CHAR_SLASH + ESCAPE_CHAR_SLASH, ESCAPE_CHAR_BACKSLASH + ESCAPE_CHAR_BACKSLASH });
        escapedSource = StringUtils.replaceEach(escapedSource, new String[] { "\\", "/" },
            new String[] { ESCAPE_CHAR_SLASH, ESCAPE_CHAR_BACKSLASH });

        // Note: make sure we don't cache if a different YouTrack url is used or if a different user
        // is used for the same
        // YouTrack url since different users can have different permissions on YouTrack.
        String youTrackURL;
        String username = null;
        try {
            YouTrackServer youTrackServer = this.youTrackServerResolver.resolve(this.parameters);
            youTrackURL = youTrackServer.getURL();
            username = youTrackServer.getUsername();
        } catch (MacroExecutionException e) {
            // The YouTrack url is not set nor an id set (or the id points to a not-defined YouTrack URL),
            // so we cannot get the
            // value. The macro will fail to display. We cache the result based on the id if it's defined. Otherwise,
            // we don't use the YouTrack url.
            youTrackURL = this.parameters.getId();
        }

        return createId("rendering", "macro", "YouTrack", escapedSource, index, youTrackURL, username);
    }

    private String getCurrentSource(MacroTransformationContext context)
    {
        String currentSource = null;

        if (context != null) {
            currentSource =
                context.getTransformationContext() != null ? context.getTransformationContext().getId() : null;

            MacroBlock currentMacroBlock = context.getCurrentMacroBlock();

            if (currentMacroBlock != null) {
                MetaDataBlock metaDataBlock =
                    currentMacroBlock.getFirstBlock(new MetadataBlockMatcher(MetaData.SOURCE),
                        Block.Axes.ANCESTOR_OR_SELF);

                if (metaDataBlock != null) {
                    currentSource = (String) metaDataBlock.getMetaData().getMetaData(MetaData.SOURCE);
                }
            }
        }

        return currentSource;
    }

    private Block wrapInMacroMarker(MacroBlock macroBlockToWrap, List<Block> newBlocks)
    {
        return new MacroMarkerBlock(macroBlockToWrap.getId(), macroBlockToWrap.getParameters(),
            macroBlockToWrap.getContent(), newBlocks, macroBlockToWrap.isInline());
    }
}
