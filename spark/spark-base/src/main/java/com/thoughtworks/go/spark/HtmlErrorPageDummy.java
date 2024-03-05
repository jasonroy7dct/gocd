/*
 * Copyright 2024 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.spark;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HtmlErrorPageDummy {

    private final String htmlTemplate;

    // Constructor that allows injection of the HTML template content
    public HtmlErrorPageDummy(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }

    // Method to generate the error page with the provided code and message
    public String errorPage(int code, String message) {
        return this.htmlTemplate
            .replaceAll(buildRegex("status_code"), String.valueOf(code))
            .replaceAll(buildRegex("error_message"), StringEscapeUtils.escapeHtml4(message));
    }

    private String buildRegex(final String value) {
        return "\\{\\{" + value + "\\}\\}";
    }


    // factory method to create an instance of HtmlErrorPageDummy with the HTML content loaded from a file
    public static HtmlErrorPageDummy createFromFile(String path) {
        try (InputStream in = HtmlErrorPageDummy.class.getResourceAsStream(path)) {
            String content = IOUtils.toString(in, StandardCharsets.UTF_8);
            return new HtmlErrorPageDummy(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
