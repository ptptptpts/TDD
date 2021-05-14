package com.ts89park.tdd;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import java.util.HashSet;

public class WebSiteMapNode {

    private String url;
    private HashSet<WebSiteMapNode> childSet = new HashSet<>();

    public WebSiteMapNode(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void putChild(WebSiteMapNode child) {
        if (child != null) {
            String childUrl = child.getUrl();

            if ((childUrl != null) && (childUrl.length() > 0)) {
                if (!childUrl.equals(this.url)) {
                    childSet.add(child);
                }
            }
        }
    }

    @VisibleForTesting
    public HashSet<WebSiteMapNode> getChildSet() {
        return childSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSiteMapNode that = (WebSiteMapNode) o;
        return Objects.equal(this.url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
