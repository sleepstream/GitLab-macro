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

import org.xwiki.properties.annotation.PropertyDescription;

import java.util.List;
import java.util.Properties;

/**
 * Parameters for the {@link org.xwiki.contrib.youtrack.macro.internal.YouTrackMacro} Macro.
 *
 *  @version $Id$
 *  @since 1.1
 */
public class YouTrackMacroParameters
{
    /**
     * @see #getSource()
     */
    private String source = "list";

    /**
     * @see #getStyle()
     */
    private String style = "table";

    /**
     * @see #getId()
     */
    private String id = "1";

    private Properties extraParameters = new Properties();

    /**
     * @see #getFields()
     */
    private YouTrackFields fields = new YouTrackFields();

    private int maxCount = -1;

    /**
     * @param id see {@link #getId()}
     */
    @PropertyDescription("the configuration id of the youtrack. Server URL to use")
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * @return the configuration id of the youtrack. Server URL to use (defined in
     * the Macro configuration settings). Note
     *         that if a URL is specified it'll take precedence over this parameter.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @param source see {@link #getSource()}
     */
    @PropertyDescription("How YouTrack issues are defined (e.g. \"jql\", \"list\")")
    public void setSource(String source)
    {
        this.source = source;
    }

    /**
     * @return the hint of the data source to use to fetch youtrack. issues
     */
    public String getSource()
    {
        return this.source;
    }

    /**
     * @param style see {@link #getStyle()}
     */
    @PropertyDescription("how YouTrack issues are displayed (e.g. \"table\", \"list\", \"enum\")")
    public void setStyle(String style)
    {
        this.style = style;
    }

    /**
     * @return the hint of the Displayer to use to display YouTrack issues
     */
    public String getStyle()
    {
        return this.style;
    }

    /**
     * @return the list of extra configuration parameters that can be used by sources,
     * displayers and field displayers
     */
    public Properties getParameters()
    {
        return this.extraParameters;
    }

    /**
     * @return the maximum number of YouTrack issues to display (if not specified defaults
     * to the value configured in your
     *         YouTrack instance)
     */
    public int getMaxCount()
    {
        return this.maxCount;
    }

    /**
     * @param fields see {@link #getFields()}
     */
    @PropertyDescription("the fields to be displayed (default field list depends "
            + "on the style used). Format is {field1,field2,field3}; \n "
            + "Available fields: idReadable, summary, type, state, priority, sprints, reporter, assignee, reviewer, created, "
            + "updated, resolved, fix versions")
    public void setFields(YouTrackFields fields)
    {
        this.fields = fields;
    }

    /**
     * @return the list of YouTrack fields to display along with optional labels and types
     * (if not defined, a default list
     *         of fields defined by the chosen Displayer will be used).
     *         String format is {field1,field2,field3}
     */
    public YouTrackFields getFields()
    {
        return this.fields;
    }
}
