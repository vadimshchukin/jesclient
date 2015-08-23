package jesclient;
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

	public void addColumn(String name, int width) {
		Column column = new Column();
		column.name = name;
		if (name.length() > width) {
			width = name.length();
		}
		column.width = width;
		columns.add(column);
	}

	public void addRow(Object... cells) {
		ArrayList<String> row = new ArrayList<String>();
		for (Object cell : cells) {
			String value = cell != null ? cell.toString() : "";
			row.add(value);
		}
		rows.add(row);
	}

	public String format() {
		StringBuilder builder = new StringBuilder();

		StringBuilder separator = new StringBuilder();
		for (Column column : columns) {
			separator.append("+" + new String(new char[column.width + 2]).replace("\0", "-"));
		}
		separator.append(String.format("+%n"));
		builder.append(separator.toString());

		builder.append(String.format("|"));
		for (Column column : columns) {
			String text = String.format("%-" + column.width + "s", column.name);
			builder.append(String.format(" %s |", text));
		}
		builder.append(String.format("%n"));

		builder.append(separator.toString());

		for (ArrayList<String> row : rows) {
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
			builder.append(String.format("%n"));
		}

		builder.append(separator.toString());

		return builder.toString();
	}
}
