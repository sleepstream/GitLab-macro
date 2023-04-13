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
package org.xwiki.contrib.youtrack.macro.internal.source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.xwiki.contrib.youtrack.config.YouTrackConfiguration;
import org.xwiki.contrib.youtrack.config.YouTrackServer;
import org.xwiki.contrib.youtrack.macro.YouTrackDataSource;
import org.xwiki.contrib.youtrack.macro.YouTrackMacroParameters;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.CustomFields;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ItemObject;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ReporterObject;
import org.xwiki.contrib.youtrack.macro.internal.source.jsonData.ValueObject;
import org.xwiki.rendering.macro.MacroExecutionException;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Common implementation for YouTrack Data Source that knowns how to execute
 * a JQL query on a YouTrack instance and retrieve the
 * list of matching YouTrack issues.
 *
 * @version $Id$
 * @since 4.2M1
 */
public abstract class AbstractYouTrackDataSource implements YouTrackDataSource
{
    /**
     * URL Prefix to use to build the full JQL URL (doesn't contain the JQL query itself which needs to be appended).
     */
    private static final String JQL_URL_PREFIX =
        "/api/issues";

    @Inject
    private YouTrackConfiguration configuration;

    @Inject
    private Logger logger;

    @Inject
    private HTTPYouTrackFetcher youtrackFetcher;

    @Inject
    private YouTrackServerResolver youtrackServerResolver;

    /**
     * @param jsonObject the XML document from which to extract YouTrack issues
     * @param youTrackServer
     * @return the list of XML Elements for each YouTrack issue, indexed in a Map with the issue id as the key
     */
    protected ItemObject buildIssues(JsonObject jsonObject, YouTrackServer youTrackServer) throws MacroExecutionException {

        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonDeserializer<ItemObject> deserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject1 = json.getAsJsonObject();
            List<CustomFields> customFieldsList = new ArrayList<>();

            for (JsonElement jsonElement : jsonObject1.get("customFields").getAsJsonArray()) {
                JsonObject item = jsonElement.getAsJsonObject();
                List<ValueObject> valueObjectList = null;
                ValueObject valueObject = null;
                if (item.get("value").isJsonArray()) {
                    valueObjectList = new ArrayList<>();
                    for (JsonElement element : item.get("value").getAsJsonArray()) {
                        JsonObject value = element.getAsJsonObject();
                        valueObjectList.add(new ValueObject(
                                        value.get("name").isJsonNull() ? null : value.get("name").getAsString(),
                                        value.get("fullName") == null || value.get("fullName").isJsonNull() ? null : value.get("fullName").getAsString(),
                                        value.get("avatarUrl") == null || value.get("avatarUrl").isJsonNull()
                                                ? null
                                                :  youTrackServer.getURL() + value.get("avatarUrl").getAsString()
                                )
                        );
                    }
                } else if(!item.get("value").isJsonNull()){
                    JsonObject value = item.get("value").getAsJsonObject();
                    valueObject = new ValueObject(
                            value.get("name").isJsonNull() ? null : value.get("name").getAsString(),
                            value.get("fullName") == null || value.get("fullName").isJsonNull() ? null : value.get("fullName").getAsString(),
                            value.get("avatarUrl") == null || value.get("avatarUrl").isJsonNull()
                                    ? null
                                    :  youTrackServer.getURL() + value.get("avatarUrl").getAsString()
                    );
                }
                customFieldsList.add(new CustomFields(item.get("name").getAsString(), valueObject, valueObjectList));
            }

            ReporterObject reporterObject = null;
            if(jsonObject1.get("reporter") != null && !jsonObject1.get("reporter").isJsonNull()) {
                JsonObject reporter = jsonObject1.get("reporter").getAsJsonObject();
                reporterObject = new ReporterObject(reporter.get("fullName").getAsString(),
                        youTrackServer.getURL() + reporter.get("avatarUrl").getAsString());
            }

            return new ItemObject(
                    jsonObject1.get("summary").isJsonNull() ? null : jsonObject1.get("summary").getAsString(),
                    jsonObject1.get("idReadable").isJsonNull() ? null : jsonObject1.get("idReadable").getAsString(),
                    jsonObject1.get("updated").isJsonNull() ? null : jsonObject1.get("updated").getAsString(),
                    jsonObject1.get("resolved").isJsonNull() ? null : jsonObject1.get("resolved").getAsString(),
                    jsonObject1.get("created").isJsonNull() ? null : jsonObject1.get("created").getAsString(),
                    customFieldsList, reporterObject,
                    null
            );
        };
        gsonBuilder.registerTypeAdapter(ItemObject.class, deserializer);
        Gson customGson = gsonBuilder.create();
        try {
            ItemObject itemObject = customGson.fromJson(jsonObject, ItemObject.class);
            itemObject.setLink(youTrackServer.getURL() + "/issue/" + itemObject.getId());
            return itemObject;
        } catch (JsonSyntaxException exception) {
            throw new MacroExecutionException("Error when parsing JSON: " + jsonObject + "\n"
                    + Arrays.toString(exception.getStackTrace()));
        }
    }

    /**
     * @param youTrackServer the YouTrack Server definition to use
     * @param jqlQuery the JQL query to execute
     * @param maxCount the max number of issues to get
     * @return the XML document containing the matching YouTrack issues
     * @throws MacroExecutionException if the YouTrack issues cannot be retrieved
     */
    public JsonObject getJsonDocument(YouTrackServer youTrackServer, String jqlQuery, int maxCount)
        throws MacroExecutionException
    {
        JsonObject document = null;
        String urlString = computeFullURL(youTrackServer, jqlQuery, maxCount);
        try {
            document = this.youtrackFetcher.fetch(urlString, youTrackServer);
        } catch (Exception e) {
            throw new MacroExecutionException(String.format("Failed to retrieve YouTrack data from [%s] for JQL [%s] "
                            + "url [%s]", youTrackServer.getURL(), jqlQuery, urlString), e);
        }
        return document;
    }

    /**
     * @param youTrackServer the YouTrack Server definition to use
     * @param jqlQuery the JQL query to execute
     * @param maxCount the max number of issues to get
     * @return the XML document containing the matching YouTrack issues
     * @throws MacroExecutionException if the YouTrack issues cannot be retrieved
     */
    public List<JsonObject> getJsonDocumentByJQL(YouTrackServer youTrackServer, String jqlQuery, int maxCount)
            throws MacroExecutionException
    {
        List<JsonObject> documentsList = null;
        String urlString = computeFullURLWithJQL(youTrackServer, jqlQuery, maxCount);
        try {
            documentsList = this.youtrackFetcher.fetchList(urlString, youTrackServer);
        } catch (Exception e) {
            throw new MacroExecutionException(String.format("Failed to retrieve YouTrack data from [%s] for JQL [%s] "
                    + "url [%s]", youTrackServer.getURL(), jqlQuery, urlString), e);
        }
        return documentsList;
    }

    protected String computeFullURL(YouTrackServer youTrackServer, String jqlQuery, int maxCount)
    {
        StringBuilder additionalQueryString = new StringBuilder();

        // Restrict number of issues returned if need be
//        if (maxCount > -1) {
//            additionalQueryString.append("&tempMax=").append(maxCount);
//        }

        additionalQueryString.append("?fields=idReadable,summary,reporter(fullName,avatarUrl)"
                + ",created,updated,resolved,"
                + "customFields(name,value(name,fullName,avatarUrl))"
                + "&customFields=type&customFields=assignee&customFields=priority&customFields=state"
                + "&customFields=reviewer&customFields=fix+versions&customFields=sprints");

        // Note: we encode using UTF8 since it's the W3C recommendation.
        // See http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars
        String fullURL = String.format("%s%s%s%s", youTrackServer.getURL(), JQL_URL_PREFIX + "/", encode(jqlQuery),
            additionalQueryString);
        this.logger.debug("Computed YouTrack URL [{}]", fullURL);

        return fullURL;
    }

    protected String computeFullURLWithJQL(YouTrackServer youTrackServer, String jqlQuery, int maxCount)
    {
        StringBuilder additionalQueryString = new StringBuilder();

        // Restrict number of issues returned if need be
//        if (maxCount > -1) {
//            additionalQueryString.append("&tempMax=").append(maxCount);
//        }

        additionalQueryString.append("?fields=idReadable,summary,reporter(fullName,avatarUrl)"
                + ",created,updated,resolved,"
                + "customFields(name,value(name,fullName,avatarUrl))"
                + "&customFields=type&customFields=assignee&customFields=priority&customFields=state"
                + "&customFields=reviewer&customFields=fix+versions&customFields=sprints&query=");

        // Note: we encode using UTF8 since it's the W3C recommendation.
        // See http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars
        String fullURL = String.format("%s%s%s%s", youTrackServer.getURL(), JQL_URL_PREFIX, additionalQueryString,
                encode(jqlQuery));
        this.logger.debug("Computed YouTrack URL [{}]", fullURL);

        return fullURL;
    }

    /**
     * @param parameters the macro's parameters
     * @return the url to the YouTrack instance (eg "http://youtrack.xwiki.org")
     * @throws MacroExecutionException if no URL has been specified (either in the macro parameter or configuration)
     */
    protected YouTrackServer getYouTrackServer(YouTrackMacroParameters parameters) throws MacroExecutionException
    {
        return this.youtrackServerResolver.resolve(parameters);
    }

    private String encode(String content)
    {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Missing UTF-8 encoding", e);
        }
    }
}
