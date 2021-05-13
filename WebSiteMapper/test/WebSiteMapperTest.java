import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class WebSiteMapperTest {

    @Test
    public void testRetrieveToWebsite() {
        WebSiteMapper mapper = new WebSiteMapper();

        String content = mapper.retrieveUrl("https://www.youtube.com");

        assertNotNull(content);
        assertNotEquals(0, content.length());
    }

    @Test
    public void testParseHrefUrl() {
        WebSiteMapper mapper = new WebSiteMapper();
        String content = mapper.retrieveUrl("https://www.youtube.com");

        HashSet<String> linkList = mapper.fetchHrefLink(content);

        assertNotNull(linkList);
        assertTrue(linkList.size() > 0);
    }

    @ParameterizedTest
    @ValueSource(strings = { "href=\"https://www.youtube.com/about/policies/\"",
            "href=\"https://www.youtube.com/howyoutubeworks?utm_campaign=ytgen&amp;utm_source=ythp&amp;utm_medium=LeftNav&amp;utm_content=txt&amp;u=https%3A%2F%2Fwww.youtube.com%2Fhowyoutubeworks%3Futm_source%3Dythp%26utm_medium%3DLeftNav%26utm_campaign%3Dytgen\"",
            "href=\"https://www.youtube.com/s/desktop/264d4061/cssbin/www-main-desktop-watch-page-skeleton.css\""
    })
    public void testGetUrlFromHref(String href) {
        WebSiteMapper mapper = new WebSiteMapper();

        String url = mapper.getUrlFromHref(href);

        assertNotNull(url);
        assertNotEquals(0, url.length());
        assertTrue(url.startsWith("http"));
        assertFalse(url.contains("\""));
    }

    @Test
    public void testBuildWebSiteMap() {
        WebSiteMapper mapper = new WebSiteMapper();
        String testUrl = "https://www.youtube.com";

        WebSiteMapper.WebSiteNode root = mapper.buildWebSiteMap(testUrl);

        assertNotNull(root);
        assertEquals(testUrl, root.url);
        assertNotNull(root.childList);
        assertTrue(root.childList.size() > 0);
    }
}