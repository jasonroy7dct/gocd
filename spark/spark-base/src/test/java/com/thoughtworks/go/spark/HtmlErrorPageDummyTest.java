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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlErrorPageDummyTest {

    @Test
    public void shouldReplaceStatusCodeAndMessage() {
        // htmpTemplate used in place of dependency injection
        String htmlTemplate = "<html><body><h1>{{status_code}}</h1><h2>{{error_message}}</h2></body></html>";
        HtmlErrorPageDummy errorPage = new HtmlErrorPageDummy(htmlTemplate);

        String result = errorPage.errorPage(404, "Error message");

        assertThat(result)
            .contains("<h1>404</h1>")
            .contains("<h2>Error message</h2>");
    }

    @Test
    public void shouldEscapeHtmlInMessages() {
        // htmpTemplate used in place of dependency injection
        String htmlTemplate = "<html><body><h1>{{status_code}}</h1><h2>{{error_message}}</h2></body></html>";
        HtmlErrorPageDummy errorPage = new HtmlErrorPageDummy(htmlTemplate);
        String htmlInMessage = "<img src=\"blah\"/>";

        String result = errorPage.errorPage(404, htmlInMessage);

        assertThat(result)
            .doesNotContain(htmlInMessage)
            .contains("<h2>&lt;img src=&quot;blah&quot;/&gt;</h2>");
    }
}
