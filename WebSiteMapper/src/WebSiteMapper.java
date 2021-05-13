import com.google.common.annotations.VisibleForTesting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSiteMapper {

    public static class WebSiteNode {
        String url;
        ArrayList<WebSiteNode> childList;

        public WebSiteNode(String url) {
            this.url = url;
        }
    }

    public WebSiteNode buildWebSiteMap(final String url) {
        WebSiteNode root = new WebSiteNode(url);

        String content = retrieveUrl(url);
        HashSet<String> childUrls = fetchHrefLink(content);

        root.childList = new ArrayList<>();
        for (String childUrl : childUrls) {
            root.childList.add(new WebSiteNode(childUrl));
        }

        return root;
    }

    public String retrieveUrl(final String link) {
        StringBuilder content = new StringBuilder();

        try (InputStream urlStream = new URL(link).openStream()) {
            content = getContentFromUrl(urlStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return content.toString();
    }

    private StringBuilder getContentFromUrl(InputStream urlStream) throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader urlReader = new BufferedReader(new InputStreamReader(urlStream));

        String line;
        while ((line = urlReader.readLine()) != null) {
            content.append("\n");
            content.append(line);
        }

        return content;
    }

    public HashSet<String> fetchHrefLink(final String content) {
        HashSet<String> links = new HashSet<>();

        Pattern pattern = Pattern.compile("href=([\"'])http(\\S|^\")*\"");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String hrefLink = matcher.group();
            links.add(getUrlFromHref(hrefLink));
        }

        return links;
    }

    @VisibleForTesting
    String getUrlFromHref(final String href) {
        return href.split("=")[1].replace("\"", "");
    }
}
