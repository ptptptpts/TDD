package com.ts89park.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

public class OutputVerifier {

    public static void assertTwoCsvSame(String expected, String actual) {
        String[] expectedLines = expected.trim().split("\n");
        String[] actualLines = actual.trim().split("\n");

        findCsvLineFromAToB(expectedLines, actualLines);
        findCsvLineFromAToB(actualLines, expectedLines);
        findNoDuplicateCsvLine(actualLines);
    }

    private static void findCsvLineFromAToB(String[] csvLinesA, String[] csvLinesB) {
        for (String csvLineA : csvLinesA) {
            boolean isFind = false;
            for (String csvLineB : csvLinesB) {
                isFind |= compareCsvLine(csvLineA, csvLineB);
            }
            assertTrue(isFind, "Expected String " + csvLineA + " is missing");
        }
    }

    private static void findNoDuplicateCsvLine(String[] csvLinesA) {
        for (String csvLineA : csvLinesA) {
            int count = 0;
            for (String csvLineB : csvLinesA) {
                count += compareCsvLine(csvLineA, csvLineB) ? 1 : 0;
            }
            assertEquals(1, count, csvLineA + " is duplicate.");
        }
    }

    public static void assertTwoTreeSame(String expected, String actual) {
        String[] expectedLines = expected.trim().split("\n");
        String[] actualLines = actual.trim().split("\n");

        findTreeLineFromAToB(expectedLines, actualLines);
        findTreeLineFromAToB(actualLines, expectedLines);
    }

    public static void assertTwoTableSame(String expected, String actual) {
        TableNode[] expectedTableNode = parseTableString(expected);
        TableNode[] actualTableNode = parseTableString(actual);

        compareTableNodes(expectedTableNode, actualTableNode);
        compareTableNodes(actualTableNode, expectedTableNode);
    }

    private static boolean compareCsvLine(String expectedLine, String actualLine) {
        boolean ret = true;
        String[] expectedItems = expectedLine.split(",");
        String[] actualItems = actualLine.split(",");

        if ((expectedItems[0].equals(actualItems[0]))
                && (expectedItems.length == actualItems.length)) {
            ret = compareCsvItemsFromAToB(expectedItems, actualItems);
            ret &= compareCsvItemsFromAToB(actualItems, expectedItems);
        } else {
            ret = false;
        }

        return ret;
    }

    private static boolean compareCsvItemsFromAToB(String[] csvItemsA, String[] csvItemsB) {
        boolean ret = true;

        for (int i = 1; i < csvItemsA.length; i++) {
            boolean isFind = false;
            for (int j = 1; j < csvItemsB.length; j++) {
                isFind = csvItemsA[i].equals(csvItemsB[j]);
                if (isFind) {
                    break;
                }
            }
            ret &= isFind;
        }

        return ret;
    }

    private static void findTreeLineFromAToB(String[] treeLinesA, String[] treeLinesB) {
        for (String treeLineA : treeLinesA) {
            boolean isFind = false;
            for (String treeLineB : treeLinesB) {
                isFind |= treeLineA.equals(treeLineB);
            }
            assertTrue(isFind, "Expected String " + treeLineA + " is missing");
        }
    }

    private static TableNode[] parseTableString(String table) {
        String[] splitTable = table.split("\n\n");
        if (splitTable.length == 2) {
            TableNode[] tableNodes = buildTableNodeFromLegends(splitTable[0].trim());
            return parseTableData(tableNodes, splitTable[1].trim());
        } else {
            return new TableNode[]{};
        }
    }

    private static TableNode[] buildTableNodeFromLegends(String legends) {
        ArrayList<TableNode> nodes = new ArrayList<>();

        for (String legend : legends.split("\n")) {
            String[] parsedLegend = legend.split("\t");
            if (parsedLegend.length == 2) {
                nodes.add(new TableNode(Integer.parseInt(parsedLegend[0]), parsedLegend[1]));
            }
        }

        return nodes.toArray(new TableNode[]{});
    }

    private static TableNode[] parseTableData(TableNode[] tableNodes, String dataTable) {
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

    private static void compareTableNodes(TableNode[] expected, TableNode[] actual) {
        for (TableNode expectNode : expected) {
            boolean isExist = false;
            int actualId = findTableId(actual, expectNode.url);

            if (actualId != -1) {
                isExist = isChildExistOnTable(expected, actual, expectNode);
            }

            assertTrue(isExist);
        }
    }

    private static boolean isChildExistOnTable(TableNode[] expected, TableNode[] actual,
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

    private static int findTableId(TableNode[] actual, String url) {
        int id = -1;

        for (TableNode actualNode : actual) {
            if (url.equals(actualNode.url)) {
                id = actualNode.id;
                break;
            }
        }

        return id;
    }

    private static class TableNode {

        int id;
        String url;
        HashSet<Integer> children = new HashSet<>();

        public TableNode(int id, String url) {
            this.id = id;
            this.url = url;
        }
    }
}
