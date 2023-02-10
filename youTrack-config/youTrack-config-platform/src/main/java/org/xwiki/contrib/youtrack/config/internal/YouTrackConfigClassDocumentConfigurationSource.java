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

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;
import com.xpn.xwiki.objects.StringProperty;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.internal.AbstractDocumentConfigurationSource;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides configuration from the {@code YouTrack.YouTrackConfig} document in the current wiki,
 * extracting the data from the
 * {@code YouTrack.YouTrackConfigClass} xobjects.
 *
 * @version $Id$
 * @since 8.2
 */
@Component
@Named("YouTrack")
@Singleton
public class YouTrackConfigClassDocumentConfigurationSource extends AbstractDocumentConfigurationSource
{
    private static final String SPACE = "YouTrack";

    private static final LocalDocumentReference CLASS_REFERENCE = new LocalDocumentReference(SPACE,
            "YouTrackConfigClass");

    /**
     * The local reference of the {@code YouTrack.YouTrackConfig} document.
     */
    private static final LocalDocumentReference DOC_REFERENCE =
        new LocalDocumentReference(SPACE, "YouTrackConfig");

    @Override
    protected String getCacheId()
    {
        // Use the same cache for all config wiki pages data. This is working because this extension is forced to be
        // installed on the root namespace (and thus there's a single YouTrackConfigClassDocumentConfigurationSource
        // component across the whole wiki farm). We prefix each cache key with the wiki id, see #getCacheKeyPrefix()
        return "configuration.document.YouTrack";
    }

    @Override
    protected String getCacheKeyPrefix()
    {
        // Override the default behavior which is to use the full wiki document reference. Since the doc reference is
        // the same in all wikis, we only need the wiki reference in the cache.
        return this.wikiManager.getCurrentWikiId();
    }

    @Override
    protected DocumentReference getDocumentReference()
    {
        return new DocumentReference(DOC_REFERENCE, getCurrentWikiReference());
    }

    @Override
    protected LocalDocumentReference getClassReference()
    {
        return CLASS_REFERENCE;
    }

    @Override
    protected Object getBaseProperty(String propertyName, boolean text) throws XWikiException
    {
        // Only handle the "serverMappings" property and when we get asked for it, we get all XObjects in the document
        // and transform them into a Map
        if (!propertyName.equals("serverMappings")) {
            return super.getBaseProperty(propertyName, text);
        }

        Map<String, YouTrackServer> youTrackServers = new HashMap<>();
        List<BaseObject> baseObjects = getYouTrackServerBaseObjects();
        for (BaseObject baseObject : baseObjects) {
            YouTrackServer youTrackServer;
            StringProperty idProperty = (StringProperty) baseObject.getField("id");
            StringProperty urlProperty = (StringProperty) baseObject.getField("url");
            if (!isPropertyEmpty(idProperty) && !isPropertyEmpty(urlProperty)) {
                StringProperty usernameProperty = (StringProperty) baseObject.getField("username");
                StringProperty passswordProperty = (StringProperty) baseObject.getField("password");
                if (!isPropertyEmpty(usernameProperty) && !isPropertyEmpty(passswordProperty)) {
                    youTrackServer = new YouTrackServer(urlProperty.getValue(),
                        usernameProperty.getValue(), passswordProperty.getValue());
                } else {
                    youTrackServer = new YouTrackServer(urlProperty.getValue());
                }
                youTrackServers.put(idProperty.getValue(), youTrackServer);
            }
        }
        if (youTrackServers.isEmpty()) {
            youTrackServers = null;
        }
        return youTrackServers;
    }

    private boolean isPropertyEmpty(BaseProperty property)
    {
        return property == null || isEmpty(property.getValue());
    }

    private List<BaseObject> getYouTrackServerBaseObjects() throws XWikiException
    {
        List<BaseObject> youTrackServerObjects = new ArrayList<>();

        DocumentReference documentReference = getFailsafeDocumentReference();
        LocalDocumentReference classReference = getClassReference();
        if (documentReference != null && classReference != null) {
            XWikiContext xcontext = this.xcontextProvider.get();
            XWikiDocument document = xcontext.getWiki().getDocument(this.getDocumentReference(), xcontext);
            for (BaseObject baseObject : document.getXObjects(classReference)) {
                if (baseObject != null) {
                    youTrackServerObjects.add(baseObject);
                }
            }
        }
        return youTrackServerObjects;
    }
}
