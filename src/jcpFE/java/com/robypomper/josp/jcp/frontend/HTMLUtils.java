/*******************************************************************************
 * The John Cloud Platform is the set of infrastructure and software required to provide
 * the "cloud" to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jcp.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.servlet.http.HttpServletRequest;


/**
 * Support class to formatting HTML outputs as a Strings.
 */
public class HTMLUtils {

    // Class constants

    private static final int DEF_DELAY = 2;

    // Objects to JSON (Html formatted)

    /**
     * Serialize given <code>objToJSON</code> to JSON formatted String and
     * wraps it in a HTML tags.
     *
     * @param objToJSON the Object to serialize.
     * @return an HTML  String containing the Object serialized in JSON format.
     */
    public static String toHTMLFormattedJSON(Object objToJSON) throws JsonProcessingException {
        return toHTMLFormattedJSON(objToJSON, null, null);
    }

    /**
     * Serialize given <code>objToJSON</code> to JSON formatted String and
     * wraps it in a HTML tags.
     *
     * @param objToJSON the Object to serialize.
     * @param title     extra string added in 'title' position of returned HTML String.
     * @return an HTML  String containing the Object serialized in JSON format.
     */
    public static String toHTMLFormattedJSON(Object objToJSON, String title) throws JsonProcessingException {
        return toHTMLFormattedJSON(objToJSON, title, null);
    }

    /**
     * Serialize given <code>objToJSON</code> to JSON formatted String.
     *
     * @param objToJSON  the Object to serialize.
     * @param title      extra string added in 'title' position of returned HTML String.
     * @param otherLinks extra string added in 'footer/others...' position of returned HTML String.
     * @return an HTML  String containing the Object serialized in JSON format.
     */
    public static String toHTMLFormattedJSON(Object objToJSON, String title, String otherLinks) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(objToJSON);

        // Add <br>
        json = json.replaceAll("\n", "<br>\n");

        // Add \t
        json = json.replaceAll(" {4}", "&nbsp;&nbsp;&nbsp;&nbsp;");

        // Replace url properties with <a> tag
        String pattern = "\"path(.*)\" : \"(.+?)\"";
        String replace = "\"path$1\" : \"<a href=\"$2\">$2</a>\"";
        json = json.replaceAll(pattern, replace);

        String homeLink = "<a href=\"/test/\">Home</a>";
        if (title != null && title.isEmpty()) title = null;
        if (otherLinks == null) otherLinks = "";

        return String.format("%s\n%s\n<br>\n%s<code>\n%s\n</code>", homeLink, otherLinks, (title == null ? "" : String.format("<h1>%s</h1>\n", title)), json);
    }


    // Redirect methods

    /**
     * Generate HTML String containing the 'redirect' command to previous page.
     *
     * @param request the request object used to get the previous page.
     * @param success true if the request was executed successfully.
     * @return a String containing the Object serialized in JSON format.
     */
    public static String redirectBackAndReturn(HttpServletRequest request, boolean success) {
        return redirectBackAndReturn(request, success, DEF_DELAY);
    }

    /**
     * Generate HTML String containing the 'redirect' command to previous page.
     *
     * @param request the request object used to get the previous page.
     * @param success true if the request was executed successfully.
     * @param delay   seconds taht the browser wait before redirect.
     * @return a String containing the Object serialized in JSON format.
     */
    public static String redirectBackAndReturn(HttpServletRequest request, boolean success, int delay) {
        if (request.getHeader("Referer") == null)
            return returnString(success);

        String url = request.getHeader("Referer");
        return redirectAndReturn(url, success, delay);
    }

    /**
     * Generate HTML String containing the 'redirect' command to give <code>url</code> page.
     *
     * @param url     the url used to redirect.
     * @param success true if the request was executed successfully.
     * @return a String containing the Object serialized in JSON format.
     */
    public static String redirectAndReturn(String url, boolean success) {
        return redirectAndReturn(url, success, DEF_DELAY);
    }

    /**
     * Generate HTML String containing the 'redirect' command to give <code>url</code> page.
     *
     * @param url     the url used to redirect.
     * @param success true if the request was executed successfully.
     * @param delay   seconds taht the browser wait before redirect.
     * @return a String containing the Object serialized in JSON format.
     */
    public static String redirectAndReturn(String url, boolean success, int delay) {
        String refresh = String.format("<meta http-equiv=\"refresh\" content=\"%d;URL='%s'\">", delay, url);
        return refresh + returnString(success);
    }

    private static String returnString(boolean success) {
        return success ? "Executed successfully" : "Error on execution";
    }

}
