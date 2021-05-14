package com.ts89park.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class WebSiteMapBuilderTest {

    @Nested
    class TestGetUrlFromHref {

        @ParameterizedTest
        @ValueSource(strings = {
                "href=\"https://www.youtube.com/about/policies/\"",
                "href=\"https://www.youtube.com/howyoutubeworks?utm_campaign=ytgen&amp;utm_source=ythp&amp;utm_medium=LeftNav&amp;utm_content=txt&amp;u=https%3A%2F%2Fwww.youtube.com%2Fhowyoutubeworks%3Futm_source%3Dythp%26utm_medium%3DLeftNav%26utm_campaign%3Dytgen\"",
                "href=\"https://www.youtube.com/s/desktop/264d4061/cssbin/www-main-desktop-watch-page-skeleton.css\"",
                "href  =  \"  https://www.youtube.com/s/desktop/264d4061/cssbin/www-main-desktop-watch-page-skeleton.css\"  ",
                "href \n\n  = \n \"  https://www.youtube.com/s/desktop/264d4061/cssbin/www-main-desktop-watch-page-skeleton.css\"  ",
        })
        public void getOneUrlFromHref(String href) {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();

            String url = mapper.getUrlFromHref(href);

            assertNotNull(url);
            assertTrue(url.length() > 0);
            assertTrue(url.startsWith("http"));
            assertFalse(url.contains("\""));
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        public void getNoUrlFromHref(String href) {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();

            String url = mapper.getUrlFromHref(href);

            assertNotNull(url);
            assertEquals(0, url.length());
        }
    }

    @Nested
    class TestFetchUrlFromHtml {

        @ParameterizedTest
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideOneValidHtmlLinkString")
        public void givenHtmlWithOneValidHrefItem_whenFetchHrefLink_thenReturnOneUrl(
                String content) {
            // Given: Content from @ValueSource annotation
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();

            // When: Call fetchHrefLink with content
            HashSet<String> links = mapper.fetchHrefLinksFromHttpContent(content);

            // Then: HashSet has one String that contains Href with Url
            assertNotNull(links);
            assertEquals(1, links.size());
            for (String link : links) {
                assertNotNull(link);
                assertTrue(link.length() > 0);
                assertTrue(link.startsWith("http"));
            }
        }

        @ParameterizedTest
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideDuplicateValidHtmlLinkString")
        public void givenHtmlWithDuplicateValidHrefItem_whenFetchHrefLink_thenReturnOneUrl(
                String content) {
            // Given: Content from @ValueSource annotation
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();

            // When: Call fetchHrefLink with content
            HashSet<String> links = mapper.fetchHrefLinksFromHttpContent(content);

            // Then: HashSet has one String that contains Href with Url
            assertNotNull(links);
            assertEquals(1, links.size());
            for (String link : links) {
                assertNotNull(link);
                assertTrue(link.length() > 0);
                assertTrue(link.startsWith("http"));
            }
        }

        @ParameterizedTest
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideMultipleValidHtmlLinkString")
        public void givenHtmlWithMultipleValidHrefItem_whenFetchHrefLink_thenReturnMultipleHrefWithUrl(
                String content) {
            // Given: Content from @ValueSource annotation
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();

            // When: Call fetchHrefLink with content
            HashSet<String> links = mapper.fetchHrefLinksFromHttpContent(content);

            // Then: HashSet has two or more String that contains Href with Url
            assertNotNull(links);
            assertTrue(links.size() > 1);
            for (String link : links) {
                assertNotNull(link);
                assertTrue(link.length() > 0);
                assertTrue(link.contains("http"));
            }
        }

        @ParameterizedTest
        @NullSource
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideNoValidHtmlLinkString")
        public void givenHtmlWithNoValidHrefItem_whenFetchHrefLink_thenReturnEmptyHashSet(
                String content) {
            // Given: Content from @ValueSource annotation
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();

            // When: Call fetchHrefLink with content
            HashSet<String> links = mapper.fetchHrefLinksFromHttpContent(content);

            // Then: HashSet has no String
            assertNotNull(links);
            assertEquals(0, links.size());
        }
    }

    @Nested
    class TestOnMockNetwork {

        @ParameterizedTest
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideOneValidHtmlLinkString")
        public void rootHasOneChild(String testRootContent) {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testEmptyContent = "";

            when(reader.readContentFromLink(testUrl))
                    .thenReturn(testRootContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertNotNull(root.getChildSet());
            assertEquals(1, root.getChildSet().size());
            assertTrue(root.getChildSet().toArray(new WebSiteMapNode[]{})[0]
                    .getUrl().contains("www.youtube.com/about/policies/"));
        }

        @ParameterizedTest
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideDuplicateValidHtmlLinkString")
        public void rootHasDuplicateChild(String testRootContent) {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testEmptyContent = "";

            when(reader.readContentFromLink(testUrl))
                    .thenReturn(testRootContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertNotNull(root.getChildSet());
            assertEquals(1, root.getChildSet().size());
            assertTrue(root.getChildSet().toArray(new WebSiteMapNode[]{})[0]
                    .getUrl().contains("www.youtube.com/about/policies/"));
        }

        @ParameterizedTest
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideMultipleValidHtmlLinkString")
        public void rootHasMultipleChild(String testRootContent) {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testEmptyContent = "";

            when(reader.readContentFromLink(testUrl))
                    .thenReturn(testRootContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertTrue(root.getChildSet().size() > 1);
            for (WebSiteMapNode node : root.getChildSet()) {
                assertTrue(node.getUrl().startsWith("http"));
            }
        }

        @ParameterizedTest
        @NullSource
        @MethodSource("com.ts89park.tdd.WebSiteMapBuilderTest#provideNoValidHtmlLinkString")
        public void rootHasNoChild(String testRootContent) {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testEmptyContent = "";

            when(reader.readContentFromLink(testUrl))
                    .thenReturn(testRootContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertEquals(0, root.getChildSet().size());
        }

        @Test
        public void rootHasTwoDepthChild() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testRootContent = "<a href=\"https://www.youtube.com/about/policies/\"></a>";
            String testChildContent = "<a href=\"https://www.youtube.com/about/policies/1\"></a>";
            String testEmptyContent = "";

            when(reader.readContentFromLink(anyString()))
                    .thenReturn(testRootContent)
                    .thenReturn(testChildContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl, 10);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertEquals(1, root.getChildSet().size());

            WebSiteMapNode childNode = root.getChildSet().toArray(new WebSiteMapNode[]{})[0];
            assertEquals("https://www.youtube.com/about/policies/", childNode.getUrl());
            assertEquals(1, childNode.getChildSet().size());

            assertEquals("https://www.youtube.com/about/policies/1",
                    childNode.getChildSet().toArray(new WebSiteMapNode[]{})[0].getUrl());
        }

        @Test
        public void rootHasThreeDepthChild() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testRootContent = "<a href=\"https://www.youtube.com/about/policies/\"></a>";
            String testChildContent = "<a href=\"https://www.youtube.com/about/policies/1\"></a>";
            String testGrandChildContent = "<a href=\"https://www.youtube.com/about/policies/2\"></a>";
            String testEmptyContent = "";

            when(reader.readContentFromLink(anyString()))
                    .thenReturn(testRootContent)
                    .thenReturn(testChildContent)
                    .thenReturn(testGrandChildContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl, 10);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertEquals(1, root.getChildSet().size());

            WebSiteMapNode childNode = root.getChildSet().toArray(new WebSiteMapNode[]{})[0];
            assertEquals("https://www.youtube.com/about/policies/", childNode.getUrl());
            assertEquals(1, childNode.getChildSet().size());

            WebSiteMapNode grandChildNode = childNode.getChildSet()
                    .toArray(new WebSiteMapNode[]{})[0];
            assertEquals("https://www.youtube.com/about/policies/1", grandChildNode.getUrl());
            assertEquals(1, grandChildNode.getChildSet().size());

            assertEquals("https://www.youtube.com/about/policies/2", grandChildNode.getChildSet()
                    .toArray(new WebSiteMapNode[]{})[0].getUrl());
        }

        @Test
        public void rootHasThreeDepthChildButZeroLimitDepth() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testRootContent = "<a href=\"https://www.youtube.com/about/policies/\"></a>";
            String testChildContent = "<a href=\"https://www.youtube.com/about/policies/1\"></a>";
            String testGrandChildContent = "<a href=\"https://www.youtube.com/about/policies/2\"></a>";
            String testEmptyContent = "";

            when(reader.readContentFromLink(anyString()))
                    .thenReturn(testRootContent)
                    .thenReturn(testChildContent)
                    .thenReturn(testGrandChildContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl, 0);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertEquals(0, root.getChildSet().size());
        }

        @Test
        public void rootHasThreeDepthChildButOneLimitDepth() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testRootContent = "<a href=\"https://www.youtube.com/about/policies/\"></a>";
            String testChildContent = "<a href=\"https://www.youtube.com/about/policies/1\"></a>";
            String testGrandChildContent = "<a href=\"https://www.youtube.com/about/policies/2\"></a>";
            String testEmptyContent = "";

            when(reader.readContentFromLink(anyString()))
                    .thenReturn(testRootContent)
                    .thenReturn(testChildContent)
                    .thenReturn(testGrandChildContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl, 1);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertEquals(1, root.getChildSet().size());

            WebSiteMapNode childNode = root.getChildSet().toArray(new WebSiteMapNode[]{})[0];
            assertEquals("https://www.youtube.com/about/policies/", childNode.getUrl());
            assertEquals(0, childNode.getChildSet().size());
        }

        @Test
        public void rootHasThreeDepthChildButTwoLimitDepth() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testUrl = "https://www.youtube.com";
            String testRootContent = "<a href=\"https://www.youtube.com/about/policies/\"></a>";
            String testChildContent = "<a href=\"https://www.youtube.com/about/policies/1\"></a>";
            String testGrandChildContent = "<a href=\"https://www.youtube.com/about/policies/2\"></a>";
            String testEmptyContent = "";

            when(reader.readContentFromLink(anyString()))
                    .thenReturn(testRootContent)
                    .thenReturn(testChildContent)
                    .thenReturn(testGrandChildContent)
                    .thenReturn(testEmptyContent);

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl, 2);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertEquals(1, root.getChildSet().size());

            WebSiteMapNode childNode = root.getChildSet().toArray(new WebSiteMapNode[]{})[0];
            assertEquals("https://www.youtube.com/about/policies/", childNode.getUrl());
            assertEquals(1, childNode.getChildSet().size());

            WebSiteMapNode grandChildNode = childNode.getChildSet()
                    .toArray(new WebSiteMapNode[]{})[0];
            assertEquals("https://www.youtube.com/about/policies/1", grandChildNode.getUrl());
            assertEquals(0, grandChildNode.getChildSet().size());
        }

        @Test
        public void rootHasLoopByOneNode() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testRootUrl = "https://www.youtube.com";
            String testRootContent = buildAnchorHref(testRootUrl);

            when(reader.readContentFromLink(testRootUrl))
                    .thenReturn(testRootContent);

            WebSiteMapNode root = mapper
                    .buildWebSiteMapFromRoot(reader, testRootUrl, Integer.MAX_VALUE);

            assertNotNull(root);
            assertEquals(testRootUrl, root.getUrl());
            assertEquals(0, root.getChildSet().size());
        }

        @Test
        public void rootHasLoopByTwoNodes() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testRootUrl = "https://www.youtube.com";
            String testChildUrl = "https://www.youtube.com/about/policies";
            String testRootContent = buildAnchorHref(testChildUrl);
            String testChildContent = buildAnchorHref(testRootUrl);

            when(reader.readContentFromLink(testRootUrl))
                    .thenReturn(testRootContent);
            when(reader.readContentFromLink(testChildUrl))
                    .thenReturn(testChildContent);

            WebSiteMapNode root = mapper
                    .buildWebSiteMapFromRoot(reader, testRootUrl, Integer.MAX_VALUE);

            assertNotNull(root);
            assertEquals(testRootUrl, root.getUrl());
            assertEquals(1, root.getChildSet().size());

            WebSiteMapNode childNode = root.getChildSet().toArray(new WebSiteMapNode[]{})[0];
            assertEquals(testChildUrl, childNode.getUrl());
            assertEquals(1, childNode.getChildSet().size());

            assertEquals(root, childNode.getChildSet().toArray(new WebSiteMapNode[]{})[0]);
        }

        @Test
        public void rootHasLoopByThreeNodes() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = mock(WebSiteReader.class);
            String testRootUrl = "https://www.youtube.com";
            String testChildUrl = "https://www.youtube.com/about/policies";
            String testGrandChildUrl = "https://www.naver.com/";
            String testRootContent = buildAnchorHref(testChildUrl);
            String testChildContent = buildAnchorHref(testGrandChildUrl);
            String testGrandChildContent = buildAnchorHref(testRootUrl);

            when(reader.readContentFromLink(testRootUrl))
                    .thenReturn(testRootContent);
            when(reader.readContentFromLink(testChildUrl))
                    .thenReturn(testChildContent);
            when(reader.readContentFromLink(testGrandChildUrl))
                    .thenReturn(testGrandChildContent);

            WebSiteMapNode root = mapper
                    .buildWebSiteMapFromRoot(reader, testRootUrl, Integer.MAX_VALUE);

            assertNotNull(root);
            assertEquals(testRootUrl, root.getUrl());
            assertEquals(1, root.getChildSet().size());

            WebSiteMapNode childNode = root.getChildSet().toArray(new WebSiteMapNode[]{})[0];
            assertEquals(testChildUrl, childNode.getUrl());
            assertEquals(1, childNode.getChildSet().size());

            WebSiteMapNode grandChildNode = childNode.getChildSet()
                    .toArray(new WebSiteMapNode[]{})[0];
            assertEquals(testGrandChildUrl, grandChildNode.getUrl());
            assertEquals(1, grandChildNode.getChildSet().size());

            assertEquals(root, grandChildNode.getChildSet().toArray(new WebSiteMapNode[]{})[0]);
        }
    }

    @Nested
    class TestOnRealNetwork {

        /*
         * TestUrl:
         * https://korehtml5.kr (Acceptable on depth 3)
         * https://demoqa.com/ (Only 1 depth)
         * https://covidartmuseum.com/ (Only 3 depth with cycle)
         * https://m.tv.naver.com/v/18416959 (Contains href wrapped by single quote)
         */

        @Test
        public void testBuildWebSiteMap() {
            WebSiteMapBuilder mapper = new WebSiteMapBuilder();
            WebSiteReader reader = new WebSiteReader();
            String testUrl = "https://www.naver.com";

            WebSiteMapNode root = mapper.buildWebSiteMapFromRoot(reader, testUrl);

            assertNotNull(root);
            assertEquals(testUrl, root.getUrl());
            assertNotNull(root.getChildSet());
            assertTrue(root.getChildSet().size() > 0);
        }
    }

    public static String buildAnchorHref(String url) {
        return "<a href=\"" + url + "\"></a>";
    }

    public static Stream<Arguments> provideOneValidHtmlLinkString() {
        return Stream.of(
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>"),
                Arguments.of("<a href=\"https://www.youtube.com/about/policies/\"></a>"),
                Arguments.of("<a   href=  \"https://www.youtube.com/about/policies/\"  >  </a>"),
                Arguments.of("<a href=\"https://www.youtube.com/about/policies/\">\n"
                        + "</a>"),
                Arguments.of("<a \n"
                        + "href=\"https://www.youtube.com/about/policies/\">\n"
                        + "</a>"),
                Arguments.of("<a \n"
                        + "href=\n"
                        + "\"https://www.youtube.com/about/policies/\"\n"
                        + ">\n"
                        + "</a>"),
                Arguments.of("<link href=\"http://www.youtube.com/about/policies/\"></link>"),
                Arguments.of("<a href=\"https://www.youtube.com/about/policies/\"></a>\n"
                        + "<img link=\"https://www.youtube.com/about/policies/\"></img>"),
                Arguments.of("<a href='http://www.youtube.com/about/policies/'></a>")
        );
    }

    public static Stream<Arguments> provideDuplicateValidHtmlLinkString() {
        return Stream.of(
                Arguments
                        .of("<a href=\"    http://www.youtube.com/about/policies/  \" href=\"  http://www.youtube.com/about/policies/     \"></a>"),
                Arguments
                        .of("<a href=\"http://www.youtube.com/about/policies/\"></a><a href=\"http://www.youtube.com/about/policies/\"></a>"),
                Arguments
                        .of("<a href=\"http://www.youtube.com/about/policies/\" href=\"http://www.youtube.com/about/policies/\"></a>"),
                Arguments
                        .of("<a href=\"https://www.youtube.com/about/policies/\"></a><link href=\"https://www.youtube.com/about/policies/\"></link>"),
                Arguments.of("<a \n"
                        + "href=\"https://www.youtube.com/about/policies/\"\n"
                        + "href=\"https://www.youtube.com/about/policies/\">\n"
                        + "</a>"),
                Arguments
                        .of("<link href=\"http://www.youtube.com/about/policies/\"></link><link href=\"http://www.youtube.com/about/policies/\"></link>"),
                Arguments
                        .of("<link href=\"http://www.youtube.com/about/policies/\" href=\"http://www.youtube.com/about/policies/\"></link>")
        );
    }

    public static Stream<Arguments> provideMultipleValidHtmlLinkString() {
        return Stream.of(
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>"
                        + "<a href=\"http://www.youtube.com/about/policies/1\"></a>"),
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>"
                        + "<a href=\"http://www.youtube.com/about/policies/1\">"
                        + "</a><a href=\"http://www.youtube.com/about/policies/2\"></a>"),
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>     "
                        + "<a href=\"http://www.youtube.com/about/policies/1\"></a>"),
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>     "
                        + "<a href=\"http://www.youtube.com/about/policies/1\">         "
                        + "</a><a href=\"http://www.youtube.com/about/policies/2\"></a>"),
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>\n"
                        + "<a href=\"http://www.youtube.com/about/policies/1\"></a>"),
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>\n\n\n\n\n"
                        + "<a href=\"http://www.youtube.com/about/policies/1\"></a>\n\n\n\n\n"
                        + "<a href=\"http://www.youtube.com/about/policies/2\"></a>"),
                Arguments.of("<a \n"
                        + "href=\n"
                        + "\"https://www.youtube.com/about/policies/\"\n"
                        + ">\n"
                        + "</a>\n"
                        + "<a \n"
                        + "href=\n"
                        + "\"https://www.youtube.com/about/policies/1\"\n"
                        + ">\n"
                        + "</a>"),
                Arguments.of("<a href=\"http://www.youtube.com/about/policies/\"></a>\n"
                        + "<img link=\"http://www.youtube.com/about/policies/1\"></link>\n"
                        + "<link href=\"http://www.youtube.com/about/policies/2\"></a>\n"
                        + "<script src=\"http://www.youtube.com/about/policies/3\"></script>"
                        + "<a href=\"http://www.youtube.com/about/policies/4\"></a>\n")
        );
    }

    public static Stream<Arguments> provideNoValidHtmlLinkString() {
        return Stream.of(
                Arguments.of("<a href=\"I'mNotHttp\"></a>"),
                Arguments.of("<a href=\"http\"></a>"),
                Arguments.of("<a href=\"http:\"></a>"),
                Arguments.of("<a href=\"http:/\"></a>"),
                Arguments.of("<a href=\"http://\"></a>"),
                Arguments.of("<a href=\"https\"></a>"),
                Arguments.of("<a href=\"https:\"></a>"),
                Arguments.of("<a href=\"https:/\"></a>"),
                Arguments.of("<a href=\"https://\"></a>"),
                Arguments.of("<a href=\"/\"></a>"),
                Arguments.of("<a></a>"),
                Arguments.of("<script src=\"http://www.youtube.com/about/policies/\"></script>"),
                Arguments.of("<img link=\"https://www.youtube.com/about/policies/\"></img>"),
                Arguments.of("https://www.youtube.com/about/policies/"),
                Arguments.of("I'm not http page"),
                Arguments.of(""),
                Arguments.of(" "),
                Arguments.of("  \n   ")
        );
    }
}
