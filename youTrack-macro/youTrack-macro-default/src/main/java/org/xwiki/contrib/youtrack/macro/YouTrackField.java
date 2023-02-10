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
package org.xwiki.contrib.youtrack.macro;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.xwiki.text.XWikiToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a YouTrack Field (id, label to display for displayer needing a label, type of the field, e.g.
 * {@code text}, {@code date}, {@code url}).
 *
 * @version $Id$
 * @since 8.3
 */
public class YouTrackField
{
    /**
     * YouTrack Summary field.
     */
    public static final YouTrackField SUMMARY;

    /**
     * YouTrack Key field (eg "XWIKI-1000").
     */
    public static final YouTrackField KEY;

    /**
     * YouTrack Type field (eg Bug, Improvement, etc).
     */
    public static final YouTrackField TYPE;

    /**
     * YouTrack Status field (eg Closed, Open, etc).
     */
    public static final YouTrackField STATUS;

    /**
     * YouTrack Assignee field (the person assigned to fix the issue).
     */
    public static final YouTrackField ASSIGNEE;

    /**
     * YouTrack Reporter field (the person who reported the issue).
     */
    public static final YouTrackField REPORTER;

    /**
     * YouTrack Created date field (the date the issue was created).
     */
    public static final YouTrackField CREATED;

    /**
     * YouTrack Updated date field (the date the issue was last modified).
     */
    public static final YouTrackField UPDATED;

    /**
     * YouTrack Resolved date field (the date the issue was resolved).
     */
    public static final YouTrackField RESOLVED;

    /**
     * YouTrack Fix Version field (the version in which the issue was resolved or closed).
     */
    public static final YouTrackField FIXVERSION;

    /**
     * YouTrack Affected Versions field (the list of Versions for which the issue was reported).
     */
    public static final YouTrackField VERSION;

    /**
     * YouTrack Component field (the list of domains/categories for the issue).
     */
    public static final YouTrackField COMPONENT;

    /**
     * YouTrack Vote field (the number of votes for the issue).
     */
    public static final YouTrackField VOTES;

    /**
     * YouTrack Resolution field (eg Closed, Won't Fix, Duplicate; etc).
     */
    public static final YouTrackField RESOLUTION;

    /**
     * YouTrack link field (the URL to the issue on the YouTrack instance).
     */
    public static final YouTrackField LINK;

    /**
     * Special field used by the List Data Source which allows the user to define notes for a given issue.
     */
    public static final String NOTE = "note";

    /**
     * Map of all known YouTrack fields (with their id, label and type).
     */
    public static final Map<String, YouTrackField> DEFAULT_FIELDS = new HashMap<>();

    private static final String TEXT_TYPE = "text";

    private static final String DATE_TYPE = "date";

    private static final String TYPE_ID = "type";

    static {
        SUMMARY = new YouTrackField("summary", "Summary", TEXT_TYPE);
        DEFAULT_FIELDS.put(SUMMARY.getId(), SUMMARY);

        KEY = new YouTrackField("key", "Key", TEXT_TYPE);
        DEFAULT_FIELDS.put(KEY.getId(), KEY);

        TYPE = new YouTrackField(TYPE_ID, "Type", TEXT_TYPE);
        DEFAULT_FIELDS.put(TYPE.getId(), TYPE);

        STATUS = new YouTrackField("status", "Status", TEXT_TYPE);
        DEFAULT_FIELDS.put(STATUS.getId(), STATUS);

        ASSIGNEE = new YouTrackField("assignee", "Assignee", TEXT_TYPE);
        DEFAULT_FIELDS.put(ASSIGNEE.getId(), ASSIGNEE);

        REPORTER = new YouTrackField("reporter", "Reporter", TEXT_TYPE);
        DEFAULT_FIELDS.put(REPORTER.getId(), REPORTER);

        CREATED = new YouTrackField("created", "Created Date", DATE_TYPE);
        DEFAULT_FIELDS.put(CREATED.getId(), CREATED);

        UPDATED = new YouTrackField("updated", "Updated Date", DATE_TYPE);
        DEFAULT_FIELDS.put(UPDATED.getId(), UPDATED);

        RESOLVED = new YouTrackField("resolved", "Resolved Date", DATE_TYPE);
        DEFAULT_FIELDS.put(RESOLVED.getId(), RESOLVED);

        FIXVERSION = new YouTrackField("fixVersion", "Fixed in", TEXT_TYPE);
        DEFAULT_FIELDS.put(FIXVERSION.getId(), FIXVERSION);

        VERSION = new YouTrackField("version", "Affected Versions", TEXT_TYPE);
        DEFAULT_FIELDS.put(VERSION.getId(), VERSION);

        COMPONENT = new YouTrackField("component", "Component", TEXT_TYPE);
        DEFAULT_FIELDS.put(COMPONENT.getId(), COMPONENT);

        VOTES = new YouTrackField("votes", "Votes", "number");
        DEFAULT_FIELDS.put(VOTES.getId(), VOTES);

        RESOLUTION = new YouTrackField("resolution", "Resolution", TEXT_TYPE);
        DEFAULT_FIELDS.put(RESOLUTION.getId(), RESOLUTION);

        LINK = new YouTrackField("link", "Link", "url");
        DEFAULT_FIELDS.put(LINK.getId(), LINK);
    }
    private String id;

    private String label;

    private String type;

    /**
     * @param id see {@link #getId()}
     */
    public YouTrackField(String id)
    {
        this.id = id;
    }

    /**
     * @param id see {@link #getId()}
     * @param label see {@link #getLabel()}
     * @param type see {@link #getType()}
     */
    public YouTrackField(String id, String label, String type)
    {
        this(id);
        setLabel(label);
        setType(type);
    }

    /**
     * @return the field id as represented by YouTrack. For custom fields, this is the custom field name.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @return the optional label (pretty name) representing the field, to be used by displayers if they need it
     */
    public String getLabel()
    {
        return this.label;
    }

    /**
     * @param label see {@link #getLabel()}
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * @return the optional type of the field (e.g. {@code text}, {@code date}, {@code url}), which is used to locate a
     *         field displayer when no specific displayer is found for the field id. If no type is specified then the
     *         default field displayer is used
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * @param type see {@link #getType()}
     */
    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(1, 5)
            .append(getId())
            .append(getLabel())
            .append(getType())
            .toHashCode();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object.getClass() != getClass()) {
            return false;
        }
        YouTrackField rhs = (YouTrackField) object;
        return new EqualsBuilder()
            .append(getId(), rhs.getId())
            .append(getLabel(), rhs.getLabel())
            .append(getType(), rhs.getType())
            .isEquals();
    }

    @Override
    public String toString()
    {
        ToStringBuilder builder = new XWikiToStringBuilder(this);
        builder.append("id", getId());
        builder.append("label", getLabel());
        builder.append(TYPE_ID, getType());
        return builder.toString();
    }
}
