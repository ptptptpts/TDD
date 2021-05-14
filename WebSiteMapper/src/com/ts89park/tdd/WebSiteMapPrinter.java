package com.ts89park.tdd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public abstract class WebSiteMapPrinter {

    public abstract String printWebSiteMap(WebSiteMapNode root);

    public static class CsvWebSiteMapPrinter extends WebSiteMapPrinter {

        @Override
        public String printWebSiteMap(WebSiteMapNode root) {
            StringBuilder builder = new StringBuilder();
            HashSet<String> printedNodes = new HashSet<>();
            Queue<WebSiteMapNode> nextPrintQueue = new LinkedList<>();

            nextPrintQueue.add(root);
            while (!nextPrintQueue.isEmpty()) {
                WebSiteMapNode currentNode = nextPrintQueue.poll();
                printedNodes.add(currentNode.getUrl());

                builder.append(printOneNodeByCsv(currentNode));
                nextPrintQueue.addAll(findNotPrintedChilds(currentNode, printedNodes));
            }

            return builder.toString();
        }

        private String printOneNodeByCsv(WebSiteMapNode currentNode) {
            return currentNode.getUrl()
                    + printChildrenWithComma(currentNode.getChildSet())
                    + "\n";
        }

        private String printChildrenWithComma(HashSet<WebSiteMapNode> childSet) {
            StringBuilder builder = new StringBuilder();
            for (WebSiteMapNode child : childSet) {
                builder.append(",").append(child.getUrl());
            }
            return builder.toString();
        }

        private Queue<WebSiteMapNode> findNotPrintedChilds(WebSiteMapNode currentNode,
                HashSet<String> printedNodes) {
            Queue<WebSiteMapNode> nextPrintQueue = new LinkedList<>();

            for (WebSiteMapNode child : currentNode.getChildSet()) {
                if (!printedNodes.contains(child.getUrl())) {
                    nextPrintQueue.add(child);
                }
            }

            return nextPrintQueue;
        }
    }

    public static class TreeWebSiteMapPrinter extends WebSiteMapPrinter {

        @Override
        public String printWebSiteMap(WebSiteMapNode root) {
            StringBuilder builder = new StringBuilder();
            Stack<TreePrintItem> stack = new Stack<>();
            TreePrintItem treeRoot = buildTreeFromRootNode(root);

            stack.add(treeRoot);

            while (!stack.isEmpty()) {
                TreePrintItem currentItem = stack.pop();
                builder.append(currentItem.printAsTreeNode());
                stack.addAll(currentItem.children.values());
            }

            return builder.toString();
        }

        public TreePrintItem buildTreeFromRootNode(WebSiteMapNode rootNode) {
            TreePrintItem root = new TreePrintItem(0, rootNode);
            HashSet<String> printedNodes = new HashSet<>();
            Queue<TreePrintItem> currentVisitQueue = new LinkedList<>();
            Queue<TreePrintItem> nextVisitQueue = new LinkedList<>();

            currentVisitQueue.add(root);

            while (!currentVisitQueue.isEmpty()) {
                TreePrintItem currentItem = currentVisitQueue.poll();

                WebSiteMapNode currentNode = currentItem.node;
                String currentUrl = currentNode.getUrl();
                int currentLevel = currentItem.level;

                if (!printedNodes.contains(currentUrl)) {
                    printedNodes.add(currentUrl);

                    for (WebSiteMapNode childNode : currentNode.getChildSet()) {
                        TreePrintItem childItem = new TreePrintItem(currentLevel + 1, childNode);
                        currentItem.putChild(childItem);
                        nextVisitQueue.add(childItem);
                    }
                }

                currentVisitQueue = nextVisitQueue;
            }

            return root;
        }

        private static class TreePrintItem {

            int level;
            WebSiteMapNode node;
            HashMap<String, TreePrintItem> children = new HashMap<>();

            public TreePrintItem(int level, WebSiteMapNode node) {
                this.level = level;
                this.node = node;
            }

            void putChild(TreePrintItem child) {
                children.put(child.node.getUrl(), child);
            }

            String printAsTreeNode() {
                StringBuilder builder = new StringBuilder();

                builder.append(buildTreePrefix(level))
                        .append(node.getUrl())
                        .append("\n");

                return builder.toString();
            }

            private String buildTreePrefix(int level) {
                if (level == 0) {
                    return "";
                } else {
                    return "  ".repeat(Math.max(0, level)) + "|- ";
                }
            }
        }
    }

    public static class TableWebSiteMapPrinter extends WebSiteMapPrinter {

        @Override
        public String printWebSiteMap(WebSiteMapNode root) {
            WebSiteMapNode[] allNodesArray = getWebSiteMapNodes(root);
            StringBuilder tableContent = new StringBuilder();

            tableContent.append(buildTableLegends(allNodesArray));
            tableContent.append(buildChildTable(allNodesArray));

            return tableContent.toString();
        }

        private WebSiteMapNode[] getWebSiteMapNodes(WebSiteMapNode root) {
            HashMap<String, WebSiteMapNode> allNodes = new HashMap<>();
            Queue<WebSiteMapNode> nextPrintQueue = new LinkedList<>();

            nextPrintQueue.add(root);

            while (!nextPrintQueue.isEmpty()) {
                WebSiteMapNode currentNode = nextPrintQueue.poll();
                allNodes.put(currentNode.getUrl(), currentNode);

                for (WebSiteMapNode child : currentNode.getChildSet()) {
                    if (!allNodes.containsKey(child.getUrl())) {
                        nextPrintQueue.add(child);
                    }
                }
            }

            return allNodes.values().toArray(new WebSiteMapNode[0]);
        }

        private String buildTableLegends(WebSiteMapNode[] nodes) {
            StringBuilder sb = new StringBuilder();

            for (int cursor = 0; cursor < nodes.length; cursor++) {
                sb.append(cursor).append("\t").append(nodes[cursor].getUrl()).append("\n");
            }
            sb.append("\n");

            return sb.toString();
        }

        private String buildChildTable(WebSiteMapNode[] nodes) {
            StringBuilder sb = new StringBuilder();

            sb.append(buildTableHeader(nodes.length));
            for (int cursor = 0; cursor < nodes.length; cursor++) {
                sb.append(buildTableDataLine(cursor, nodes));
            }

            return sb.toString();
        }

        private String buildTableHeader(int size) {
            StringBuilder sb = new StringBuilder();

            sb.append(buildHeaderLine(size));
            sb.append(buildDivideLine(size));

            return sb.toString();
        }

        private String buildHeaderLine(int size) {
            StringBuilder sb = new StringBuilder();

            sb.append(String.format("%5s", "")).append("|");
            for (int i = 0; i < size; i++) {
                sb.append(String.format("%5d", i));
            }
            sb.append("\n");

            return sb.toString();
        }

        private String buildDivideLine(int size) {
            StringBuilder sb = new StringBuilder();

            sb.append(String.format("%5s", "-----"))
                    .append("|")
                    .append(String.valueOf(String.format("%5s", "-----")).repeat(Math.max(0, size)))
                    .append("\n");

            return sb.toString();
        }

        private String buildTableDataLine(int id, WebSiteMapNode[] nodes) {
            StringBuilder sb = new StringBuilder();
            WebSiteMapNode root = nodes[id];
            HashSet<WebSiteMapNode> rootChilds = root.getChildSet();

            sb.append(String.format("%5d", id)).append("|");
            for (int cursor = 0; cursor < nodes.length; cursor++) {
                String cursorUrl = nodes[cursor].getUrl();

                if (rootChilds.stream()
                        .anyMatch(webSiteMapNode -> cursorUrl.equals(webSiteMapNode.getUrl()))) {
                    sb.append(String.format("%5d", cursor));
                } else {
                    sb.append(String.format("%5s", ""));
                }
            }
            sb.append("\n");

            return sb.toString();
        }
    }
}
