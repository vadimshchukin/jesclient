package jes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextTable {
	
    private class Column {
        String name;
        Integer width;
    }
    
    private boolean printHeader;
    private boolean useCSV;
    private String delimiterCSV;

    private List<Column> columns = new ArrayList<Column>();
    private List<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();
    
    public TextTable(boolean printHeader) {
    	this.printHeader = printHeader;
    	this.useCSV = false;
    }
    
    public TextTable(boolean printHeader, boolean useCSV, String delimiterCSV) {
    	this.printHeader = printHeader;
    	this.useCSV = useCSV;
    	this.delimiterCSV = delimiterCSV;
    }

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
    
    private String formatStringAsCSV(String string) {
    	if (string.contains("\n") || string.contains("\"") || string.contains(delimiterCSV)) {
            string = "\"" + string.replaceAll("\"", "\"\"") + "\"";
        }
    	return string;
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
        if (!useCSV) {
        	header.append(String.format("|"));        	
        }
        Iterator<Column> columnIterator = columns.iterator();
        while (columnIterator.hasNext()) {
        	Column column = columnIterator.next();
        	if (useCSV) {
        		header.append(formatStringAsCSV(column.name));
        		if (columnIterator.hasNext()) {
        			header.append(delimiterCSV);
        		}
        	} else {
        		String text = String.format("%-" + column.width + "s", column.name);
                header.append(String.format(" %s |", text));        		
        	}
        }
        return header.toString();
    }
    
    public String formatRow(List<String> row) {
        StringBuilder builder = new StringBuilder();
        if (!useCSV) {
        	builder.append(String.format("|"));        	
        }
        Iterator<Column> columnIterator = columns.iterator();
        Iterator<String> cellIterator = row.iterator();
        while (columnIterator.hasNext()) {
        	Column column = columnIterator.next(); 
        	
            String cellValue = "";
            if (cellIterator.hasNext()) {
            	cellValue = cellIterator.next();
            }
            
            if (useCSV) {
            	builder.append(formatStringAsCSV(cellValue));
            	if (columnIterator.hasNext()) {
            		builder.append(delimiterCSV);
            	}
            } else {
            	String text = String.format("%-" + column.width + "s", cellValue);
                builder.append(String.format(" %s |", text));            	
            }
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
        
        if (printHeader) {
        	if (!useCSV) {
                table.append(String.format("%s%n", separator));
            }
            
            table.append(String.format("%s%n", formatHeader()));        	
        }
        
        if (!useCSV) {
        	table.append(String.format("%s%n", separator)); 
        }

        for (ArrayList<String> row : rows) {
            table.append(String.format("%s%n", formatRow(row)));
        }

        if (!useCSV) {
        	table.append(String.format("%s%n", separator));        	
        }

        return table.toString();
    }
}
