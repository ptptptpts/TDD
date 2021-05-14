package com.ts89park.cucumber;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.ts89park.tdd.WebSiteMapBuilder;
import com.ts89park.tdd.WebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.CsvWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.TableWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.TreeWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteReader;
import com.ts89park.util.OutputVerifier;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.IOException;

public class StepDefinitions {

    private static final String ROOT_URL = "http://www.url-for-testing.com/";
    private static final String[] CHILDREN_URL = new String[]{
            "http://www.url-for-testing.com/store",
            "http://www.url-for-testing.com/about"
    };
    private static final String[] GRANDCHILDREN_URL = new String[]{
            "http://www.url-for-testing.com/store/meat",
            "http://www.url-for-testing.com/store/fruit",
            "http://www.url-for-testing.com/about/us",
            "http://www.url-for-testing.com/about/history"
    };

    private static final String ROOT_DEPTH_HTML_SAMPLE =
            buildAnchor(ROOT_URL) + "\n"
                    + buildAnchor(CHILDREN_URL[0]) + "\n"
                    + buildLink(CHILDREN_URL[1]) + "\n";

    private static final String[] CHILD_DEPTH_HTML_SAMPLE = new String[]{
            buildAnchor(ROOT_URL) + "\n"
                    + buildAnchor(CHILDREN_URL[0]) + "\n"
                    + buildAnchor(CHILDREN_URL[1]) + "\n"
                    + buildAnchor(GRANDCHILDREN_URL[0]) + "\n"
                    + buildAnchor(GRANDCHILDREN_URL[1]) + "\n",
            buildLink(ROOT_URL) + "\n"
                    + buildLink(CHILDREN_URL[0]) + "\n"
                    + buildLink(CHILDREN_URL[1]) + "\n"
                    + buildLink(GRANDCHILDREN_URL[2]) + "\n"
                    + buildLink(GRANDCHILDREN_URL[3]) + "\n"
    };

    private WebSiteReader reader;
    private String output;

    @Given("^Return two depth html document for testing when read from the given url$")
    public void returnTwoDepthHtmlDocumentForTestingWhenReadFromTheGivenUrl() {
        reader = mock(WebSiteReader.class);
        when(reader.readContentFromLink(ROOT_URL))
                .thenReturn(ROOT_DEPTH_HTML_SAMPLE);
        when(reader.readContentFromLink(CHILDREN_URL[0]))
                .thenReturn(CHILD_DEPTH_HTML_SAMPLE[0]);
        when(reader.readContentFromLink(CHILDREN_URL[1]))
                .thenReturn(CHILD_DEPTH_HTML_SAMPLE[1]);
        when(reader.readContentFromLink(GRANDCHILDREN_URL[0]))
                .thenReturn("");
        when(reader.readContentFromLink(GRANDCHILDREN_URL[1]))
                .thenReturn("");
        when(reader.readContentFromLink(GRANDCHILDREN_URL[2]))
                .thenReturn("");
        when(reader.readContentFromLink(GRANDCHILDREN_URL[3]))
                .thenReturn("");
    }

    @Given("^Return an empty html document when read from the given url$")
    public void returnAnEmptyHtmlDocumentWhenReadFromTheGivenUrl() {
        reader = mock(WebSiteReader.class);
        when(reader.readContentFromLink(anyString())).thenReturn("");
    }

    @Given("^Return a Exception when read from the given url$")
    public void returnAExceptionWhenReadFromTheGivenUrl() {
        reader = spy(WebSiteReader.class);
        try {
            when(reader.getContentFromUrl(any())).thenThrow(new IOException());
        } catch (Exception e) {
            // Ignore
        }
    }

    @When("^Build WebsiteMap with the given url with {int} depth$")
    public void buildWebsiteMapWithTheGivenUrlWithDepth(int depth) {
        WebSiteMapBuilder builder = new WebSiteMapBuilder();
        WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();

        output = printer.printWebSiteMap(
                builder.buildWebSiteMapFromRoot(reader, ROOT_URL, depth));
    }

    @When("Build WebsiteMap with the given url with {int} depth by {string} format")
    public void buildWebsiteMapWithTheGivenUrlWithDepthByFormat(int depth, String type) {
        WebSiteMapBuilder builder = new WebSiteMapBuilder();

        type = type.toLowerCase();
        WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();
        switch (type) {
            case "csv":
                printer = new CsvWebSiteMapPrinter();
                break;
            case "table":
                printer = new TableWebSiteMapPrinter();
                break;
            case "tree":
                printer = new TreeWebSiteMapPrinter();
                break;
            default:
                fail();
                break;
        }

        output = printer.printWebSiteMap(
                builder.buildWebSiteMapFromRoot(reader, ROOT_URL, depth));
    }

    private static final String[] CSV_SOLUTION_PER_DEPTH = new String[]{
            ROOT_URL + "\n",
            ROOT_URL + "," + ROOT_URL + "," + CHILDREN_URL[0] + "," + CHILDREN_URL[1] + "\n"
                    + CHILDREN_URL[0] + "\n"
                    + CHILDREN_URL[1] + "\n",
            ROOT_URL + "," + ROOT_URL + "," + CHILDREN_URL[0] + "," + CHILDREN_URL[1] + "\n"
                    + CHILDREN_URL[0] + "," + ROOT_URL + "," + CHILDREN_URL[0] + ","
                    + CHILDREN_URL[1] + "," + GRANDCHILDREN_URL[0] + "," + GRANDCHILDREN_URL[1]
                    + "\n"
                    + CHILDREN_URL[1] + "," + ROOT_URL + "," + CHILDREN_URL[0] + ","
                    + CHILDREN_URL[1] + "," + GRANDCHILDREN_URL[2] + "," + GRANDCHILDREN_URL[3]
                    + "\n"
                    + GRANDCHILDREN_URL[0] + "\n"
                    + GRANDCHILDREN_URL[1] + "\n"
                    + GRANDCHILDREN_URL[2] + "\n"
                    + GRANDCHILDREN_URL[3] + "\n"
    };

    @Then("Output CSV format WebsiteMap is same with {int} depth CSV solution")
    public void outputCSVFormatWebsiteMapIsSameWithDepthCSVSolution(int depth) {
        System.out.println("Solution:\n" + CSV_SOLUTION_PER_DEPTH[depth] + "\n");
        System.out.println("Output:\n" + output + "\n");
        OutputVerifier.assertTwoCsvSame(CSV_SOLUTION_PER_DEPTH[depth], output);
    }

    // TODO:: Create table solution
    private static final String[] TABLE_SOLUTION_PER_DEPTH = new String[]{
            "0\t" + ROOT_URL + "\n"
                    + "\n"
                    + String.format("%5s|%5d", "", 0) + "\n"
                    + String.format("%5s|%5s", "-----", "-----") + "\n"
                    + String.format("%5d|%5s", 0, "") + "\n",
            "0\t" + ROOT_URL + "\n"
                    + "1\t" + CHILDREN_URL[0] + "\n"
                    + "2\t" + CHILDREN_URL[1] + "\n"
                    + "\n"
                    + String.format("%5s|%5d%5d%5d", "", 0, 1, 2) + "\n"
                    + String.format("%5s|%5s%5s%5s", "-----", "-----", "-----", "-----") + "\n"
                    + String.format("%5d|%5d%5d%5d", 0, 0, 1, 2) + "\n"
                    + String.format("%5d|%5s%5s%5s", 1, "", "", "") + "\n"
                    + String.format("%5d|%5s%5s%5s", 2, "", "", "") + "\n",
            "0\t" + ROOT_URL + "\n"
                    + "1\t" + CHILDREN_URL[0] + "\n"
                    + "2\t" + CHILDREN_URL[1] + "\n"
                    + "3\t" + GRANDCHILDREN_URL[0] + "\n"
                    + "4\t" + GRANDCHILDREN_URL[1] + "\n"
                    + "5\t" + GRANDCHILDREN_URL[2] + "\n"
                    + "6\t" + GRANDCHILDREN_URL[3] + "\n"
                    + "\n"
                    + String.format("%5s|%5d%5d%5d%5d%5d%5d%5d", "", 0, 1, 2, 3, 4, 5, 6) + "\n"
                    + String.format("%5s|%5s%5s%5s%5s%5s%5s%5s", "-----", "-----", "-----", "-----",
                    "-----", "-----", "-----", "-----")
                    + "\n"
                    + String.format("%5d|%5d%5d%5d%5s%5s%5s%5s", 0, 0, 1, 2, "", "", "", "") + "\n"
                    + String.format("%5d|%5d%5d%5d%5d%5d%5s%5s", 1, 0, 1, 2, 3, 4, "", "") + "\n"
                    + String.format("%5d|%5d%5d%5d%5s%5s%5d%5d", 2, 0, 1, 2, "", "", 5, 6) + "\n"
                    + String.format("%5d|%5s%5s%5s%5s%5s%5s%5s", 3, "", "", "", "", "", "", "")
                    + "\n"
                    + String.format("%5d|%5s%5s%5s%5s%5s%5s%5s", 4, "", "", "", "", "", "", "")
                    + "\n"
                    + String.format("%5d|%5s%5s%5s%5s%5s%5s%5s", 5, "", "", "", "", "", "", "")
                    + "\n"
                    + String.format("%5d|%5s%5s%5s%5s%5s%5s%5s", 6, "", "", "", "", "", "", "")
                    + "\n",
    };

    @Then("Output Table format WebsiteMap is same with {int} depth Table solution")
    public void outputTableFormatWebsiteMapIsSameWithDepthTableSolution(int depth) {
        System.out.println("Solution:\n" + TABLE_SOLUTION_PER_DEPTH[depth] + "\n");
        System.out.println("Output:\n" + output + "\n");
        OutputVerifier.assertTwoTableSame(TABLE_SOLUTION_PER_DEPTH[depth], output);
    }

    // TODO:: Create tree solution
    private static final String[] TREE_SOLUTION_PER_DEPTH = new String[]{
            ROOT_URL + "\n",
            ROOT_URL + "\n"
                    + "  |- " + ROOT_URL + "\n"
                    + "  |- " + CHILDREN_URL[0] + "\n"
                    + "  |- " + CHILDREN_URL[1] + "\n",
            ROOT_URL + "\n"
                    + "  |- " + ROOT_URL + "\n"
                    + "  |- " + CHILDREN_URL[0] + "\n"
                    + "    |- " + ROOT_URL + "\n"
                    + "    |- " + CHILDREN_URL[0] + "\n"
                    + "    |- " + GRANDCHILDREN_URL[0] + "\n"
                    + "    |- " + GRANDCHILDREN_URL[1] + "\n"
                    + "  |- " + CHILDREN_URL[1] + "\n"
                    + "    |- " + ROOT_URL + "\n"
                    + "    |- " + CHILDREN_URL[1] + "\n"
                    + "    |- " + GRANDCHILDREN_URL[2] + "\n"
                    + "    |- " + GRANDCHILDREN_URL[3] + "\n",
    };

    @Then("Output Tree format WebsiteMap is same with {int} depth Tree solution")
    public void outputTreeFormatWebsiteMapIsSameWithDepthTreeSolution(int depth) {
        System.out.println("Solution:\n" + TREE_SOLUTION_PER_DEPTH[depth] + "\n");
        System.out.println("Output:\n" + output + "\n");
        OutputVerifier.assertTwoTreeSame(TREE_SOLUTION_PER_DEPTH[depth], output);
    }

    private static String buildAnchor(String url) {
        return "<a href=\"" + url + "\"></a>";
    }

    private static String buildLink(String url) {
        return "<link href=\"" + url + "\"></a>";
    }
}
