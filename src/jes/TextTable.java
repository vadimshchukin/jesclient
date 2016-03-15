package jes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextTable {
    public class Column {
        String name;
        Integer width;
    }

    private List<Column> columns = new ArrayList<Column>();
    private List<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();

    public void addColumn(String name, int width, Integer position) {
        Column column = new Column();
        column.name = name;
        if (name.length() > width) {
            width = name.length();
        }
        column.width = width;
        if (position != null) {
            columns.add(position, column);            
        } else {
            columns.add(column);
        }
    }
    
    public void addColumn(String name, int width) {
        addColumn(name, width, null);
    }

    public void addRow(Object... cells) {
        ArrayList<String> row = new ArrayList<String>();
        for (Object cell : cells) {
            String value = cell != null ? cell.toString() : "";
            row.add(value);
        }
        rows.add(row);
    }
    
    public String formatSeparator() {
        StringBuilder separator = new StringBuilder();
        for (Column column : columns) {
            separator.append("+" + new String(new char[column.width + 2]).replace("\0", "-"));
        }
        separator.append("+");
        return separator.toString();
    }
    
    public String formatHeader() {
        StringBuilder header = new StringBuilder();
        header.append(String.format("|"));
        for (Column column : columns) {
            String text = String.format("%-" + column.width + "s", column.name);
            header.append(String.format(" %s |", text));
        }
        return header.toString();
    }
    
    public String formatRow(List<String> row) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("|"));
        Iterator<Column> column = columns.iterator();
        Iterator<String> cell = row.iterator();
        while (column.hasNext()) {
            String value = "";
            if (cell.hasNext()) {
                value = cell.next();
            }
            String text = String.format("%-" + column.next().width + "s", value);
            builder.append(String.format(" %s |", text));
        }
        return builder.toString();
    }
    
    public String formatRow(Object... cells) {
        ArrayList<String> row = new ArrayList<String>();
        for (Object cell : cells) {
            String value = cell != null ? cell.toString() : "";
            row.add(value);
        }
        return formatRow(row);
    }

    public String format() {
        StringBuilder table = new StringBuilder();
        String separator = formatSeparator();
        
        table.append(String.format("%s%n", separator));
        table.append(String.format("%s%n", formatHeader()));
        table.append(String.format("%s%n", separator));

        for (ArrayList<String> row : rows) {
            table.append(String.format("%s%n", formatRow(row)));
        }

        table.append(String.format("%s%n", separator));

        return table.toString();
    }
}
