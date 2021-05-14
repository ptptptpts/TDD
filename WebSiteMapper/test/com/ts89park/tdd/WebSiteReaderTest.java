package com.ts89park.tdd;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WebSiteReaderTest {

    @Test
    public void testRetrieveToWebsite() throws Exception {
        WebSiteReader mapper = new WebSiteReader();

        String content = mapper.readContentFromLink("https://www.youtube.com");

        assertNotNull(content);
        assertTrue(content.length() > 0);
    }

    // TODO: Invalid URL

    // TODO: Empty URL

    // TODO: null URL
}