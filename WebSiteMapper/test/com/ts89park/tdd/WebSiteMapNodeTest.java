package com.ts89park.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

class WebSiteMapNodeTest {

    @Test
    public void putChildWithDifferentName() {
        String url = "https://www.test.com";
        WebSiteMapNode node = new WebSiteMapNode(url);
        WebSiteMapNode testNode = mock(WebSiteMapNode.class);

        when(testNode.getUrl()).thenReturn("https://www.fast.com");

        node.putChild(testNode);

        assertEquals(1, node.getChildSet().size());
    }

    @Test
    public void putChildWithSameName() {
        String url = "https://www.test.com";
        WebSiteMapNode node = new WebSiteMapNode(url);
        WebSiteMapNode testNode = mock(WebSiteMapNode.class);

        when(testNode.getUrl()).thenReturn("https://www.test.com");

        node.putChild(testNode);

        assertEquals(1, node.getChildSet().size());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void putChildWithInvalidName(String invalidString) {
        String url = "https://www.test.com";
        WebSiteMapNode node = new WebSiteMapNode(url);
        WebSiteMapNode testNode = mock(WebSiteMapNode.class);

        when(testNode.getUrl()).thenReturn(invalidString);

        node.putChild(testNode);

        assertEquals(0, node.getChildSet().size());
    }

    @Test
    public void putChildWithInvalidObject() {
        String url = "https://www.test.com";
        WebSiteMapNode node = new WebSiteMapNode(url);

        node.putChild(null);

        assertEquals(0, node.getChildSet().size());
    }
}