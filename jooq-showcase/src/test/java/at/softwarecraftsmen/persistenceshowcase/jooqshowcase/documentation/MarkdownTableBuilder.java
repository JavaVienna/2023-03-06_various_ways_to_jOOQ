package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

import java.util.LinkedList;
import java.util.List;

public class MarkdownTableBuilder {

    private final String[] header;
    private final int[] dimensions;
    private final List<String[]> rows;

    public MarkdownTableBuilder(String... header) {
        this.header = header;
        this.dimensions = new int[header.length];
        this.rows = new LinkedList<>();
        reDimension(header);
    }

    public void addRow(String... fields) {
        rows.add(fields);
        reDimension(fields);
    }

    public String toMarkdown() {
        var stringBuilder = new StringBuilder();

        buildRow(stringBuilder, header);

        buildSeparator(stringBuilder);

        for (String[] row : rows) {
            buildRow(stringBuilder, row);
        }
        return stringBuilder.toString();
    }

    private void buildRow(StringBuilder sb, String... fields) {
        sb.append("|");
        for (int i = 0; i < dimensions.length && i < fields.length; i++) {
            sb.append(" ");
            appendLeftPad(sb, fields[i], dimensions[i]);
            sb.append(" |");
        }
        sb.append('\n');
    }

    private void appendLeftPad(StringBuilder stringBuilder, String string, int length) {
        stringBuilder.append(string);
        stringBuilder.append(" ".repeat(Math.max(0, length - string.length())));
    }

    private void buildSeparator(StringBuilder stringBuilder) {
        stringBuilder.append("|");
        for (int dimension : dimensions) {
            stringBuilder.append("-".repeat(dimension + 2));
            stringBuilder.append("|");
        }
        stringBuilder.append("\n");
    }

    private void reDimension(String... fields) {
        for (int i = 0; i < dimensions.length && i < fields.length; i++) {
            dimensions[i] = Math.max(dimensions[i], fields[i].length());
        }
    }
}
