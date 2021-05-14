package com.ts89park.tdd;

import com.ts89park.tdd.WebSiteMapPrinter.CsvWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.TableWebSiteMapPrinter;
import com.ts89park.tdd.WebSiteMapPrinter.TreeWebSiteMapPrinter;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Url?");
        String url = sc.nextLine();

        System.out.println("Depth?");
        int maxDepth = sc.nextInt();

        System.out.println("1.CSV  2.Tree  3.Table");
        int type = sc.nextInt();

        WebSiteReader reader = new WebSiteReader();
        WebSiteMapBuilder builder = new WebSiteMapBuilder();

        WebSiteMapPrinter printer;
        if (type == 1) {
            printer = new CsvWebSiteMapPrinter();
        } else if (type == 2) {
            printer = new TreeWebSiteMapPrinter();
        } else {
            printer = new TableWebSiteMapPrinter();
        }

        System.out.println("\n\n");
        System.out.println(printer.printWebSiteMap(
                builder.buildWebSiteMapFromRoot(reader, url, maxDepth)));
    }
}
