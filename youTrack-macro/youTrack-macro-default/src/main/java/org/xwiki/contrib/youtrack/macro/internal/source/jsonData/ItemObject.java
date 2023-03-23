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
package org.xwiki.contrib.youtrack.macro.internal.source.jsonData;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class ItemObject {

    @SerializedName("summary")
    private String summary;

    @SerializedName("idReadable")
    private String id;

    @SerializedName("updated")
    private String updated;

    @SerializedName("resolved")
    private String resolved;

    @SerializedName("created")
    private String created;

    @SerializedName("reporter")
    private ReporterObject reporter;

    @SerializedName("customFields")
    private List<CustomFields> customFieldsList;

    private String link;

    public ItemObject(String summary, String id, String updated, String resolved,
                      String created, List<CustomFields> customFieldsList, ReporterObject reporter,
                      String link) {
        this.summary = summary;
        this.id = id;
        this.updated = updated;
        this.resolved = resolved;
        this.created = created;
        this.customFieldsList = customFieldsList;
        this.reporter = reporter;
        this.link = link;
    }

    public String getSummary() {
        return summary;
    }

    public String getId() {
        return id;
    }

    public String getUpdated() {
        return updated;
    }

    public String getResolved() {
        return resolved;
    }

    public String getCreated() {
        return created;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public ReporterObject getReporter() {
        return reporter;
    }

    public List<CustomFields> getCustomFieldsList() {
        return customFieldsList;
    }

    public CustomFields getCustomField(String id) {
        Optional<CustomFields> result = customFieldsList.stream().filter(item -> item.getName().equalsIgnoreCase(id))
                .findFirst();

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new UnsupportedOperationException(String.format("Field %s is not supported for task %s", id, this.id));
        }
    }
}
