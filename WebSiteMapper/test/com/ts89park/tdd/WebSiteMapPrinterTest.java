package com.ts89park.tdd;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ts89park.tdd.WebSiteMapPrinter.CsvWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.TableWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.TreeWebSiteMapPrinter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WebSiteMapPrinterTest {

    @Nested
    class CsvPrintTest {

        @Test
        public void printOneRoot() {
            String rootUrl = "https://www.google.com";
            WebSiteMapNode root = buildMockNode(rootUrl, null);
            WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(root);

            compareCsvResult(rootUrl, testOutput);
        }

        @Test
        public void printOneDepth() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = buildChildsUrl(rootUrl, 4);
            String solution = buildCsvLine(rootUrl, childsUrl) + "\n"
                    + buildCsvLine(childsUrl[0], null) + "\n"
                    + buildCsvLine(childsUrl[1], null) + "\n"
                    + buildCsvLine(childsUrl[2], null) + "\n"
                    + buildCsvLine(childsUrl[3], null) + "\n";
            WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();
            WebSiteMapNode root = buildMockNode(rootUrl, childsUrl);

            String testOutput = printer.printWebSiteMap(root);

            compareCsvResult(solution, testOutput);
        }

        @Test
        public void printTwoDepth() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = buildChildsUrl(rootUrl, 4);
            String[][] grandChildsUrl = new String[][]{
                    buildChildsUrl(childsUrl[0], 4),
                    buildChildsUrl(childsUrl[1], 4),
                    buildChildsUrl(childsUrl[2], 4),
                    buildChildsUrl(childsUrl[3], 4),
            };

            StringBuilder solution = new StringBuilder(buildCsvLine(rootUrl, childsUrl) + "\n");
            for (int i = 0; i < 4; i++) {
                solution.append(buildCsvLine(childsUrl[i], grandChildsUrl[i])).append("\n");
            }
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    solution.append(buildCsvLine(grandChildsUrl[i][j], null)).append("\n");
                }
            }

            WebSiteMapNode root = buildMockNodeByChildNode(rootUrl, new WebSiteMapNode[]{
                    buildMockNode(childsUrl[0], grandChildsUrl[0]),
                    buildMockNode(childsUrl[1], grandChildsUrl[1]),
                    buildMockNode(childsUrl[2], grandChildsUrl[2]),
                    buildMockNode(childsUrl[3], grandChildsUrl[3]),
            });

            WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution.toString());
            System.out.println(testOutput);

            compareCsvResult(solution.toString(), testOutput);
        }

        @Test
        public void printLoopOneNode() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = new String[]{rootUrl};
            String solution = buildCsvLine(rootUrl, childsUrl) + "\n";
            WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();
            WebSiteMapNode root = mock(WebSiteMapNode.class);
            HashSet<WebSiteMapNode> childs = new HashSet<>();

            childs.add(root);

            when(root.getUrl()).thenReturn(rootUrl);
            when(root.getChildSet()).thenReturn(childs);

            String testOutput = printer.printWebSiteMap(root);

            compareCsvResult(solution, testOutput);
        }

        @Test
        public void printLoopTwoNode() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";

            String[] rootChildsUrl = new String[]{childUrl};
            String[] childChildsUrl = new String[]{rootUrl};

            String solution = buildCsvLine(rootUrl, rootChildsUrl) + "\n"
                    + buildCsvLine(childUrl, childChildsUrl) + "\n";

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(rootNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            compareCsvResult(solution, testOutput);
        }

        @Test
        public void printLoopThreeNode() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";
            String grandChildUrl = "https://www.google.com/policy/0";

            String[] rootChildsUrl = new String[]{childUrl};
            String[] childChildsUrl = new String[]{grandChildUrl};
            String[] grandChildChildsUrl = new String[]{rootUrl};

            String solution = buildCsvLine(rootUrl, childChildsUrl) + "\n"
                    + buildCsvLine(childUrl, childChildsUrl) + "\n"
                    + buildCsvLine(grandChildUrl, grandChildChildsUrl) + "\n";

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);
            WebSiteMapNode grandChildNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(grandChildNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            HashSet<WebSiteMapNode> grandChildChilds = new HashSet<>();
            grandChildChilds.add(rootNode);
            when(grandChildNode.getUrl()).thenReturn(grandChildUrl);
            when(grandChildNode.getChildSet()).thenReturn(grandChildChilds);

            WebSiteMapPrinter printer = new CsvWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            compareCsvResult(solution, testOutput);
        }

        private String buildCsvLine(String head, String[] childs) {
            StringBuilder sb = new StringBuilder();
            sb.append(head);
            if (childs != null) {
                for (String child : childs) {
                    sb.append(",").append(child);
                }
            }
            return sb.toString();
        }

        private void compareCsvResult(String expected, String actual) {
            String[] expectedLines = expected.trim().split("\n");
            String[] actualLines = actual.trim().split("\n");

            for (String solutionLine : expectedLines) {
                boolean isFind = false;
                for (String testLine : actualLines) {
                    isFind |= compareCsvLine(solutionLine, testLine);
                }
                assertTrue(isFind);
            }
        }

        private boolean compareCsvLine(String expectedLine, String actualLine) {
            boolean ret = true;
            String[] expectedItems = expectedLine.split(",");
            String[] actualItems = actualLine.split(",");

            for (String expectedItem : expectedItems) {
                boolean isFind = false;
                for (String actualItem : actualItems) {
                    isFind |= expectedItem.equals(actualItem);
                }
                ret &= isFind;
            }

            for (String actualItem : actualItems) {
                boolean isFind = false;
                for (String expectedItem : expectedItems) {
                    isFind |= expectedItem.equals(actualItem);
                }
                ret &= isFind;
            }

            return ret;
        }
    }

    @Nested
    class TreePrintTest {

        @Test
        public void printOneRoot() {
            String rootUrl = "https://www.google.com";
            WebSiteMapNode root = buildMockNode(rootUrl, null);
            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(rootUrl);
            System.out.println(testOutput);
            assertIfTreeDifferent(rootUrl, testOutput);
        }

        @Test
        public void printOneDepth() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = buildChildsUrl(rootUrl, 2);
            String solution = buildTreeLine(rootUrl, 0) + "\n"
                    + buildTreeLine(childsUrl[0], 1) + "\n"
                    + buildTreeLine(childsUrl[1], 1) + "\n";
            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();
            WebSiteMapNode root = buildMockNode(rootUrl, childsUrl);

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTreeDifferent(solution, testOutput);
        }

        @Test
        public void printTwoDepth() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = buildChildsUrl(rootUrl, 2);
            String[][] grandChildsUrl = new String[][]{
                    buildChildsUrl(childsUrl[0], 2),
                    buildChildsUrl(childsUrl[1], 2)
            };

            String solution = buildTreeLine(rootUrl, 0) + "\n"
                    + buildTreeLine(childsUrl[0], 1) + "\n"
                    + buildTreeLine(grandChildsUrl[0][0], 2) + "\n"
                    + buildTreeLine(grandChildsUrl[0][1], 2) + "\n"
                    + buildTreeLine(childsUrl[1], 1) + "\n"
                    + buildTreeLine(grandChildsUrl[1][0], 2) + "\n"
                    + buildTreeLine(grandChildsUrl[1][1], 2) + "\n";

            WebSiteMapNode root = buildMockNodeByChildNode(rootUrl, new WebSiteMapNode[]{
                    buildMockNode(childsUrl[0], grandChildsUrl[0]),
                    buildMockNode(childsUrl[1], grandChildsUrl[1]),
            });

            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTreeDifferent(solution, testOutput);
        }

        @Test
        public void printLoopOneNode() {
            String rootUrl = "https://www.google.com";
            String solution = buildTreeLine(rootUrl, 0) + "\n"
                    + buildTreeLine(rootUrl, 1) + "\n";
            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();
            WebSiteMapNode root = mock(WebSiteMapNode.class);
            HashSet<WebSiteMapNode> childs = new HashSet<>();

            childs.add(root);

            when(root.getUrl()).thenReturn(rootUrl);
            when(root.getChildSet()).thenReturn(childs);

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTreeDifferent(solution, testOutput);
        }

        @Test
        public void printLoopTwoNode() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";

            String solution = buildTreeLine(rootUrl, 0) + "\n"
                    + buildTreeLine(childUrl, 1) + "\n"
                    + buildTreeLine(rootUrl, 2) + "\n";

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(rootNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTreeDifferent(solution, testOutput);
        }

        @Test
        public void printLoopThreeNode() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";
            String grandChildUrl = "https://www.google.com/policy/0";

            String solution = buildTreeLine(rootUrl, 0) + "\n"
                    + buildTreeLine(childUrl, 1) + "\n"
                    + buildTreeLine(grandChildUrl, 2) + "\n"
                    + buildTreeLine(rootUrl, 3) + "\n";

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);
            WebSiteMapNode grandChildNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(grandChildNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            HashSet<WebSiteMapNode> grandChildChilds = new HashSet<>();
            grandChildChilds.add(rootNode);
            when(grandChildNode.getUrl()).thenReturn(grandChildUrl);
            when(grandChildNode.getChildSet()).thenReturn(grandChildChilds);

            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTreeDifferent(solution, testOutput);
        }

        @Test
        public void printCycleWithTreeNodes() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";
            String grandChildUrl = "https://www.google.com/policy/0";

            String solution = buildTreeLine(rootUrl, 0) + "\n"
                    + buildTreeLine(childUrl, 1) + "\n"
                    + buildTreeLine(grandChildUrl, 2) + "\n"
                    + buildTreeLine(rootUrl, 2) + "\n"
                    + buildTreeLine(grandChildUrl, 1) + "\n"
                    + buildTreeLine(rootUrl, 2) + "\n"
                    + buildTreeLine(childUrl, 2) + "\n";

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);
            WebSiteMapNode grandChildNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            rootChilds.add(grandChildNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(grandChildNode);
            childChilds.add(rootNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            HashSet<WebSiteMapNode> grandChildChilds = new HashSet<>();
            grandChildChilds.add(rootNode);
            grandChildChilds.add(childNode);
            when(grandChildNode.getUrl()).thenReturn(grandChildUrl);
            when(grandChildNode.getChildSet()).thenReturn(grandChildChilds);

            WebSiteMapPrinter printer = new TreeWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTreeDifferent(solution, testOutput);
        }

        private String buildTreeLine(String url, int level) {
            if (level == 0) {
                return url;
            } else {
                return "  ".repeat(Math.max(0, level)) + "|- " + url;
            }
        }

        private void assertIfTreeDifferent(String expected, String actual) {
            String[] expectedLines = expected.trim().split("\n");
            String[] actualLines = actual.trim().split("\n");

            findTreeLineFromAToB(expectedLines, actualLines);
            findTreeLineFromAToB(actualLines, expectedLines);
        }

        private void findTreeLineFromAToB(String[] treeLinesA, String[] treeLinesB) {
            for (String treeLineA : treeLinesA) {
                boolean isFind = false;
                for (String treeLineB : treeLinesB) {
                    isFind |= treeLineA.equals(treeLineB);
                }
                assertTrue(isFind, "Expected String " + treeLineA + " is missing");
            }
        }
    }

    @Nested
    class TablePrintTest {

        @Test
        public void printOneRoot() {
            String rootUrl = "https://www.google.com";
            WebSiteMapNode root = buildMockNode(rootUrl, null);
            WebSiteMapPrinter printer = new TableWebSiteMapPrinter();

            String solution = buildLegends(new String[]{rootUrl})
                    + buildTableHeader(1)
                    + buildTableLine(0, 1, null);

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);
            assertIfTableDifferent(solution, testOutput);
        }

        @Test
        public void printOneDepth() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = buildChildsUrl(rootUrl, 2);
            String solution = buildLegends(new String[]{rootUrl, childsUrl[0], childsUrl[1]})
                    + buildTableHeader(3)
                    + buildTableLine(0, 3, new int[]{1, 2})
                    + buildTableLine(1, 3, null)
                    + buildTableLine(2, 3, null);
            WebSiteMapPrinter printer = new TableWebSiteMapPrinter();
            WebSiteMapNode root = buildMockNode(rootUrl, childsUrl);

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTableDifferent(solution, testOutput);
        }

        @Test
        public void printTwoDepth() {
            String rootUrl = "https://www.google.com";
            String[] childsUrl = buildChildsUrl(rootUrl, 2);
            String[][] grandChildsUrl = new String[][]{
                    buildChildsUrl(childsUrl[0], 2),
                    buildChildsUrl(childsUrl[1], 2)
            };

            int numberOfAllNodes = 7;
            String solution = buildLegends(new String[]{rootUrl, childsUrl[0], childsUrl[1],
                    grandChildsUrl[0][0], grandChildsUrl[0][1], grandChildsUrl[1][0],
                    grandChildsUrl[1][1]})
                    + buildTableHeader(numberOfAllNodes)
                    + buildTableLine(0, numberOfAllNodes, new int[]{1, 2})
                    + buildTableLine(1, numberOfAllNodes, new int[]{3, 4})
                    + buildTableLine(2, numberOfAllNodes, new int[]{5, 6})
                    + buildTableLine(3, numberOfAllNodes, null)
                    + buildTableLine(4, numberOfAllNodes, null)
                    + buildTableLine(5, numberOfAllNodes, null)
                    + buildTableLine(6, numberOfAllNodes, null);

            WebSiteMapNode root = buildMockNodeByChildNode(rootUrl, new WebSiteMapNode[]{
                    buildMockNode(childsUrl[0], grandChildsUrl[0]),
                    buildMockNode(childsUrl[1], grandChildsUrl[1]),
            });

            WebSiteMapPrinter printer = new TableWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTableDifferent(solution, testOutput);
        }

        @Test
        public void printLoopOneNode() {
            String rootUrl = "https://www.google.com";

            int numberOfAllNodes = 1;
            String solution = buildLegends(new String[]{rootUrl})
                    + buildTableHeader(numberOfAllNodes)
                    + buildTableLine(0, numberOfAllNodes, new int[]{0});

            WebSiteMapPrinter printer = new TableWebSiteMapPrinter();
            WebSiteMapNode root = mock(WebSiteMapNode.class);
            HashSet<WebSiteMapNode> childs = new HashSet<>();

            childs.add(root);

            when(root.getUrl()).thenReturn(rootUrl);
            when(root.getChildSet()).thenReturn(childs);

            String testOutput = printer.printWebSiteMap(root);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTableDifferent(solution, testOutput);
        }

        @Test
        public void printLoopTwoNode() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";

            int numberOfAllNodes = 2;
            String solution = buildLegends(new String[]{rootUrl, childUrl})
                    + buildTableHeader(numberOfAllNodes)
                    + buildTableLine(0, numberOfAllNodes, new int[]{1})
                    + buildTableLine(1, numberOfAllNodes, new int[]{0});

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(rootNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            WebSiteMapPrinter printer = new TableWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTableDifferent(solution, testOutput);
        }

        @Test
        public void printLoopThreeNode() {
            String rootUrl = "https://www.google.com";
            String childUrl = "https://www.google.com/policy";
            String grandChildUrl = "https://www.google.com/policy/0";

            int numberOfAllNodes = 3;
            String solution = buildLegends(new String[]{rootUrl, childUrl, grandChildUrl})
                    + buildTableHeader(numberOfAllNodes)
                    + buildTableLine(0, numberOfAllNodes, new int[]{1})
                    + buildTableLine(1, numberOfAllNodes, new int[]{2})
                    + buildTableLine(2, numberOfAllNodes, new int[]{0});

            WebSiteMapNode rootNode = mock(WebSiteMapNode.class);
            WebSiteMapNode childNode = mock(WebSiteMapNode.class);
            WebSiteMapNode grandChildNode = mock(WebSiteMapNode.class);

            HashSet<WebSiteMapNode> rootChilds = new HashSet<>();
            rootChilds.add(childNode);
            when(rootNode.getUrl()).thenReturn(rootUrl);
            when(rootNode.getChildSet()).thenReturn(rootChilds);

            HashSet<WebSiteMapNode> childChilds = new HashSet<>();
            childChilds.add(grandChildNode);
            when(childNode.getUrl()).thenReturn(childUrl);
            when(childNode.getChildSet()).thenReturn(childChilds);

            HashSet<WebSiteMapNode> grandChildChilds = new HashSet<>();
            grandChildChilds.add(rootNode);
            when(grandChildNode.getUrl()).thenReturn(grandChildUrl);
            when(grandChildNode.getChildSet()).thenReturn(grandChildChilds);

            WebSiteMapPrinter printer = new TableWebSiteMapPrinter();

            String testOutput = printer.printWebSiteMap(rootNode);

            System.out.println(solution);
            System.out.println(testOutput);

            assertIfTableDifferent(solution, testOutput);
        }

        private String buildLegends(String[] urls) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < urls.length; i++) {
                sb.append(i).append("\t").append(urls[i]).append("\n");
            }
            sb.append("\n");

            return sb.toString();
        }

        private String buildTableHeader(int numberOfAllNodes) {
            StringBuilder header = new StringBuilder();

            header.append(String.format("%5s", " "))
                    .append("|");
            for (int i = 0; i < numberOfAllNodes; i++) {
                header.append(String.format("%5d", i));
            }
            header.append("\n");

            header.append(String.valueOf(String.format("%5s", "-----"))
                    .repeat(Math.max(1, numberOfAllNodes + 1)))
                    .append("-")
                    .append("\n");

            return header.toString();
        }

        private String buildTableLine(int rootNumber, int numberOfAllNodes, int[] children) {
            StringBuilder sb = new StringBuilder();

            sb.append(String.format("%5d", rootNumber))
                    .append("|");
            if (children != null) {
                for (int nodeId = 0; nodeId < numberOfAllNodes; nodeId++) {
                    int finalI = nodeId;
                    if (Arrays.stream(children).anyMatch(value -> value == finalI)) {
                        sb.append(String.format("%5s", nodeId));
                    } else {
                        sb.append(String.format("%5s", " "));
                    }
                }
            }
            sb.append("\n");

            return sb.toString();
        }

        private class TableNode {

            int id;
            String url;
            HashSet<Integer> children = new HashSet<>();

            public TableNode(int id, String url) {
                this.id = id;
                this.url = url;
            }
        }

        private void assertIfTableDifferent(String expected, String actual) {
            TableNode[] expectedTableNode = parseTableString(expected);
            TableNode[] actualTableNode = parseTableString(actual);

            compareTableNodes(expectedTableNode, actualTableNode);
            compareTableNodes(actualTableNode, expectedTableNode);
        }

        private void compareTableNodes(TableNode[] expected, TableNode[] actual) {
            for (TableNode expectNode : expected) {
                boolean isExist = false;
                int actualId = findTableId(actual, expectNode.url);

                if (actualId != -1) {
                    isExist = isChildExistOnTable(expected, actual, expectNode);
                }

                assertTrue(isExist);
            }
        }

        private boolean isChildExistOnTable(TableNode[] expected, TableNode[] actual,
                TableNode expectNode) {
            boolean isExist = false;

            for (Integer expectChildId : expectNode.children) {
                int actualChildId = findTableId(actual, expected[expectChildId].url);
                if (actualChildId != -1) {
                    isExist = true;
                    break;
                }
            }

            return isExist;
        }

        private int findTableId(TableNode[] actual, String url) {
            int id = -1;

            for (TableNode actualNode : actual) {
                if (url.equals(actualNode.url)) {
                    id = actualNode.id;
                    break;
                }
            }

            return id;
        }

        private TableNode[] parseTableString(String table) {
            String[] splitTable = table.split("\n\n");
            if (splitTable.length == 2) {
                TableNode[] tableNodes = buildTableNodeFromLegends(splitTable[0].trim());
                return parseTableData(tableNodes, splitTable[1].trim());
            } else {
                return new TableNode[]{};
            }
        }

        private TableNode[] buildTableNodeFromLegends(String legends) {
            ArrayList<TableNode> nodes = new ArrayList<>();

            for (String legend : legends.split("\n")) {
                String[] parsedLegend = legend.split("\t");
                if (parsedLegend.length == 2) {
                    nodes.add(new TableNode(Integer.parseInt(parsedLegend[0]), parsedLegend[1]));
                }
            }

            return nodes.toArray(new TableNode[]{});
        }

        private TableNode[] parseTableData(TableNode[] tableNodes, String dataTable) {
            String[] tableRows = dataTable.split("\n");

            for (int i = 2; i < tableRows.length; i++) {
                String[] items = tableRows[i].split("[|\\s]");

                for (int itemCursor = 1; itemCursor < items.length; itemCursor++) {
                    String trimmedItem = items[itemCursor].trim();
                    if (trimmedItem.length() > 0) {
                        int childId = Integer.parseInt(trimmedItem);
                        tableNodes[i - 2].children.add(childId);
                    }
                }
            }

            return tableNodes;
        }
    }

    private String[] buildChildsUrl(String rootUrl, int numberOfChild) {
        ArrayList<String> childList = new ArrayList<>();

        for (int i = 0; i < numberOfChild; i++) {
            childList.add(rootUrl + "/" + i);
        }

        return childList.toArray(new String[]{});
    }

    private WebSiteMapNode buildMockNodeByChildNode(String rootUrl, WebSiteMapNode[] childsNode) {
        WebSiteMapNode root = mock(WebSiteMapNode.class);
        HashSet<WebSiteMapNode> rootChildSet = new HashSet<>(Arrays.asList(childsNode));

        when(root.getUrl()).thenReturn(rootUrl);
        when(root.getChildSet()).thenReturn(rootChildSet);

        return root;
    }

    private WebSiteMapNode buildMockNode(String rootUrl, String[] childsUrl) {
        WebSiteMapNode root = mock(WebSiteMapNode.class);
        HashSet<WebSiteMapNode> rootChildSet = buildChildSetFromUrl(childsUrl);

        when(root.getUrl()).thenReturn(rootUrl);
        when(root.getChildSet()).thenReturn(rootChildSet);

        return root;
    }

    private HashSet<WebSiteMapNode> buildChildSetFromUrl(String[] childsUrl) {
        HashSet<WebSiteMapNode> rootChildSet = new HashSet<>();

        if (childsUrl != null) {
            for (String childUrl : childsUrl) {
                WebSiteMapNode mockChild = mock(WebSiteMapNode.class);

                when(mockChild.getUrl()).thenReturn(childUrl);
                when(mockChild.getChildSet()).thenReturn(new HashSet<>());

                rootChildSet.add(mockChild);
            }
        }

        return rootChildSet;
    }
}
