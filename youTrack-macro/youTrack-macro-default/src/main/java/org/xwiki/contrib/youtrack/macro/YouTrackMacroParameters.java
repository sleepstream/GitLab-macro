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
//    /**
//     * @see #getURL()
//     */
//    private String url;

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

    /**
     * @see #getFieldNames()
     */
    private List<String> fieldNames;

    private int maxCount = -1;

//    /**
//     * @param url see {@link #getURL()}
//     */
//    @PropertyDescription("the youtrack. Server URL")
//    public void setURL(String url)
//    {
//        this.url = url;
//    }
//
//    /**
//     * @return the youtrack. Server URL (e.g. {@code http://youtrack..xwiki.org})
//     */
//    public String getURL()
//    {
//        return this.url;
//    }

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
    @PropertyDescription("how youtrack. issues are defined (e.g. \"jql\", \"list\")")
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
     * @param fields see {@link #getFields()}
     */
    @PropertyDescription("the fields to be displayed with optional labels and types (default field list depends "
            + "on the style used). Format is {@code id1:label1!type1,id2:label2!type2}")
    public void setFields(YouTrackFields fields)
    {
        this.fields = fields;
    }

    /**
     * @return the list of YouTrack fields to display along with optional labels and types
     * (if not defined, a default list
     *         of fields defined by the chosen Displayer will be used).
     *         String format is {@code id1:label1!type1,id2:label2!type2}
     */
    public YouTrackFields getFields()
    {
        return this.fields;
    }

    /**
     * @param fieldNames see {@link #getFieldNames()}
     * @deprecated starting with 8.3 use {@link #setFields(YouTrackFields)} using the format {@code id:label!type}
     */
    @PropertyDescription("the pretty names of the fields in the order in which they are displayed")
    @Deprecated
    public void setFieldNames(List<String> fieldNames)
    {
        this.fieldNames = fieldNames;
    }

    /**
     * @return the names to use for YouTrack issue fields for Displayers displaying the field names (eg the Table
     *         Data Source)
     * @deprecated starting with 8.3 use {@link #setFields(YouTrackFields)} using the format {@code id:label!type}
     */
    @Deprecated
    public List<String> getFieldNames()
    {
        return this.fieldNames;
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
}
