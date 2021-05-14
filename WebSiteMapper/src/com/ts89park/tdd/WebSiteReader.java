package com.ts89park.tdd;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WebSiteReader {

    public String readContentFromLink(final String link) {
        StringBuilder builder = new StringBuilder();

        try (InputStream urlStream = new URL(link).openStream()) {
            builder = getContentFromUrl(urlStream);
        } catch (Exception e) {
            // Ignore
        }

        return builder.toString();
    }

    @VisibleForTesting
    public StringBuilder getContentFromUrl(InputStream urlStream) throws Exception {
        StringBuilder content = new StringBuilder();
        BufferedReader urlReader = new BufferedReader(new InputStreamReader(urlStream));

        String line;
        while ((line = urlReader.readLine()) != null) {
            content.append(" ");
            content.append(line);
        }

        return content;
    }
}
