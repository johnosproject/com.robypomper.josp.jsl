/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jod.executor.impls.http;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.robypomper.java.JavaNotImplementedException;
import com.robypomper.josp.jod.executor.AbsJODWorker;
import com.robypomper.josp.jod.executor.JODWorker;
import com.robypomper.josp.jod.structure.JODComponent;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.Map;

public class FormatterInternal {

    // Class constants

    //@formatter:off
    private static final String PROP_FORMAT_TYPE                = "formatType";
    private static final String PROP_FORMAT_PATH                = "formatPath";
    private static final String PROP_FORMAT_PATH_DEF            = "/";
    private static final String PROP_FORMAT_PATH_TYPE           = "formatPathType";
    private static final String PROP_FORMAT_PATH_TYPE_HTML_XPATH    = "XPATH";
    private static final String PROP_FORMAT_PATH_TYPE_HTML_TAG_NAME = "TAG_NAME";
    private static final String PROP_FORMAT_PATH_TYPE_JSON_JSONPATH = "JSONPATH";
    //private static final String PROP_FREQ = "freq";                 // in seconds
    //public static final int UNIX_SHELL_POLLING_TIME = 30000;        // in milliseconds
    //@formatter:onn

    // Internal vars
    private final AbsJODWorker worker;
    private final String name;
    private final String proto;
    private final String configsStr;
    private final JODComponent component;
    private final FormatType formatType;
    private final String formatPath;
    private final String formatPathType;


    // Constructor

    public FormatterInternal(AbsJODWorker worker, String name, String proto, String configsStr, JODComponent component) throws JODWorker.ParsingPropertyException, JODWorker.MissingPropertyException {
        this.worker = worker;
        this.name = name;
        this.proto = proto;
        this.configsStr = configsStr;
        this.component = component;

        // Parse configs
        Map<String, String> configs = AbsJODWorker.splitConfigsStrings(configsStr);
        formatType = FormatType.valueOf(worker.parseConfigString(configs, PROP_FORMAT_TYPE, FormatType.HTML.name()));
        formatPath = worker.parseConfigString(configs, PROP_FORMAT_PATH, PROP_FORMAT_PATH_DEF);
        formatPathType = worker.parseConfigString(configs, PROP_FORMAT_PATH_TYPE, "");
    }

    public String parse(String response) throws ParsingException, PathNotFoundException {
        Object parsedObj = parse(formatType, formatPathType, response);
        return extractPath(formatType, formatPath, formatPathType, parsedObj);
    }

    private static Object parse(FormatType type, String formatPathType, String str) throws ParsingException {
        switch (type) {
            case TXT:
                return parseTXT(str);
            case HTML:
                return parseHTML(str, formatPathType);
            case XML:
                return parseXML(str, formatPathType);
            case JSON:
                return parseJSON(str, formatPathType);
            case YML:
                return parseYML(str, formatPathType);
            default:
                assert false;
        }
        return null;
    }

    private static String extractPath(FormatType type, String formatPath, String formatPathType, Object parsedObj) throws PathNotFoundException {
        switch (type) {
            case TXT:
                return extractTXT(parsedObj);
            case HTML:
                return extractHTML(formatPath, formatPathType, parsedObj);
            case XML:
                return extractXML(formatPath, formatPathType, parsedObj);
            case JSON:
                return extractJSON(formatPath, formatPathType, parsedObj);
            case YML:
                return extractYML(formatPath, formatPathType, parsedObj);
            default:
                assert false;
        }
        return null;
    }

    private static Object parseTXT(String str) throws ParsingException {
        return str;
    }

    private static Object parseHTML(String str, String formatPathType) throws ParsingException {
        if (formatPathType.compareToIgnoreCase(PROP_FORMAT_PATH_TYPE_HTML_XPATH) == 0) {
            try {
                TagNode tagNode = new HtmlCleaner().clean(str);
                org.w3c.dom.Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
                return doc;

            } catch (ParserConfigurationException e) {
                throw new ParsingException(e);
            }

        } else if (formatPathType.compareToIgnoreCase(PROP_FORMAT_PATH_TYPE_HTML_TAG_NAME) == 0) {
            throw new JavaNotImplementedException();

        } else
            throw new JavaNotImplementedException();
    }

    private static Object parseXML(String str, String formatPathType) throws ParsingException {
        throw new JavaNotImplementedException();
    }

    private static Object parseJSON(String str, String formatPathType) throws ParsingException {
        if (formatPathType.compareToIgnoreCase(PROP_FORMAT_PATH_TYPE_JSON_JSONPATH) == 0) {
            return JsonPath.parse(str);

        } else
            throw new JavaNotImplementedException();
    }

    private static Object parseYML(String str, String formatPathType) throws ParsingException {
        throw new JavaNotImplementedException();
    }

    private static String extractTXT(Object parsedObj) throws PathNotFoundException {
        return (String) parsedObj;
    }

    private static String extractHTML(String formatPath, String formatPathType, Object parsedObj) throws PathNotFoundException {
        if (formatPathType.compareToIgnoreCase(PROP_FORMAT_PATH_TYPE_HTML_XPATH) == 0) {
            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                //return (String) xpath.evaluate("//div//td[contains(@id, 'foo')]/text()", doc, XPathConstants.STRING);
                return (String) xpath.evaluate(formatPath, parsedObj, XPathConstants.STRING);

            } catch (XPathExpressionException e) {
                throw new PathNotFoundException(formatPath, formatPathType, parsedObj.toString(), e);
            }

        } else if (formatPathType.compareToIgnoreCase(PROP_FORMAT_PATH_TYPE_HTML_TAG_NAME) == 0) {
            return null;

        } else
            return null;
    }

    private static String extractXML(String formatPath, String formatPathType, Object parsedObj) throws PathNotFoundException {
        throw new JavaNotImplementedException();
    }

    private static String extractJSON(String formatPath, String formatPathType, Object parsedObj) throws PathNotFoundException {
        if (formatPathType.compareToIgnoreCase(PROP_FORMAT_PATH_TYPE_JSON_JSONPATH) == 0) {
            try {
                return ((DocumentContext) parsedObj).read(formatPath).toString();
            } catch (com.jayway.jsonpath.PathNotFoundException e) {
                throw new PathNotFoundException(formatPath, formatPathType, parsedObj.toString(), e);
            }

        } else
            throw new JavaNotImplementedException();
    }

    private static String extractYML(String formatPath, String formatPathType, Object parsedObj) throws PathNotFoundException {
        throw new JavaNotImplementedException();
    }


    // Exceptions

    public static class ParsingException extends Throwable {

        public ParsingException(Throwable cause) {
            super(cause);
        }

    }

    public static class PathNotFoundException extends Throwable {

        private final String path;
        private final String pathFormat;
        private final String content;
        private final String contentFormat;

        @Deprecated
        public PathNotFoundException(Throwable cause) {
            this(null, null, null, null, cause);
        }

        public PathNotFoundException(String path, String pathFormat, Throwable cause) {
            this(path, pathFormat, null, null, cause);
        }

        public PathNotFoundException(String path, String pathFormat, String content, Throwable cause) {
            this(path, pathFormat, content, null, cause);
        }

        public PathNotFoundException(String path, String pathFormat, String content, String contentFormat, Throwable cause) {
            super(cause);
            this.path = path;
            this.pathFormat = pathFormat;
            this.content = content;
            this.contentFormat = contentFormat;
        }

    }

    // FormatType

    public enum FormatType {
        TXT,
        HTML,
        XML,
        JSON,
        YML
    }
}
