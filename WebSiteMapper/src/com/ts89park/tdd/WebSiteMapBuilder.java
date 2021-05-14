package com.ts89park.tdd;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSiteMapBuilder {

    private static final int DEFAULT_MAX_DEPTH = 1;
    public static final String REGEX_FIND_ANCHOR_OR_LINK = "<(a|link)[^>]*/?>";
    public static final String REGEX_FIND_HREF_WITH_HTTP = "href=[\\s]*[\"'][\\s]*https?://[^\"']+[\"']";

    public WebSiteMapBuilder() {
    }

    public WebSiteMapNode buildWebSiteMapFromRoot(final WebSiteReader reader, final String url) {
        return buildWebSiteMapFromRoot(reader, url, DEFAULT_MAX_DEPTH);
    }

    public WebSiteMapNode buildWebSiteMapFromRoot(final WebSiteReader reader, final String url,
            int maxDepth) {
        HashMap<String, WebSiteMapNode> entireMapNodes = new HashMap<>();

        WebSiteMapNode root = new WebSiteMapNode(url);
        entireMapNodes.put(url, root);

        Queue<WebSiteMapNode> searchNodeQueue = new LinkedList<>();
        searchNodeQueue.add(root);

        for (int depth = 0; (depth < maxDepth) && (searchNodeQueue.size() > 0); depth++) {
            System.out.println(depth);
            searchNodeQueue = buildChildMap(entireMapNodes, reader, searchNodeQueue);
        }

        return root;
    }

    private Queue<WebSiteMapNode> buildChildMap(HashMap<String, WebSiteMapNode> entireMapNodes,
            WebSiteReader reader, Queue<WebSiteMapNode> searchNodeQueue) {
        Queue<WebSiteMapNode> nextSearchNodeQueue = new LinkedList<>();

        while (searchNodeQueue.size() > 0) {
            WebSiteMapNode node = searchNodeQueue.poll();
            ArrayList<WebSiteMapNode> newNodes = buildChildMapFromNode(entireMapNodes, reader,
                    node);
            nextSearchNodeQueue.addAll(newNodes);
        }

        return nextSearchNodeQueue;
    }

    private ArrayList<WebSiteMapNode> buildChildMapFromNode(HashMap<String, WebSiteMapNode> nodeMap,
            WebSiteReader reader, WebSiteMapNode node) {
        String content = reader.readContentFromLink(node.getUrl());
        HashSet<String> childUrls = fetchHrefLinksFromHttpContent(content);
        return buildWebSiteMapFromChildUrls(nodeMap, node, childUrls);
    }

    @VisibleForTesting
    HashSet<String> fetchHrefLinksFromHttpContent(final String content) {
        HashSet<String> links = new HashSet<>();

        if (content != null) {
            Pattern itemPattern = Pattern.compile(REGEX_FIND_ANCHOR_OR_LINK);
            Matcher itemMatcher = itemPattern.matcher(content);

            while (itemMatcher.find()) {
                String itemContent = itemMatcher.group();
                links.addAll(extractHrefUrlsFromItem(itemContent));
            }
        }

        return links;
    }

    private HashSet<String> extractHrefUrlsFromItem(String itemContent) {
        HashSet<String> links = new HashSet<>();
        Pattern hrefPattern = Pattern.compile(REGEX_FIND_HREF_WITH_HTTP);
        Matcher hrefMatcher = hrefPattern.matcher(itemContent);

        while (hrefMatcher.find()) {
            String hrefLink = hrefMatcher.group();
            links.add(getUrlFromHref(hrefLink));
        }

        return links;
    }

    @VisibleForTesting
    String getUrlFromHref(final String href) {
        String ret = "";

        if ((href != null) && (href.length() > 0)) {
            ret = href.split("=")[1]
                    .replace("\"", "")
                    .replace("'", "")
                    .trim();
        }

        return ret;
    }

    private ArrayList<WebSiteMapNode> buildWebSiteMapFromChildUrls(
            HashMap<String, WebSiteMapNode> entireNodeMap, WebSiteMapNode parent,
            HashSet<String> childUrls) {
        ArrayList<WebSiteMapNode> newNodes = new ArrayList<>();

        for (String childUrl : childUrls) {
            parent.putChild(createNodeIfAbsent(entireNodeMap, newNodes, childUrl));
        }

        return newNodes;
    }

    private WebSiteMapNode createNodeIfAbsent(HashMap<String, WebSiteMapNode> entireNodeMap,
            ArrayList<WebSiteMapNode> newNodes, String url) {
        WebSiteMapNode childNode;

        if (entireNodeMap.containsKey(url)) {
            childNode = entireNodeMap.get(url);
        } else {
            childNode = new WebSiteMapNode(url);
            entireNodeMap.put(url, childNode);
            newNodes.add(childNode);
        }

        return childNode;
    }
}
