package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;

import java.util.List;

public class DatabaseDocumentationBuilder {

    public String generateDatabaseDocumentation(Schema schema) {
        return generateDatabaseDocumentation(schema.getName().toUpperCase(), schema.getTables());
    }

    public String generateDatabaseDocumentation(String title, List<Table<?>> tables) {
        StringBuilder sb = new StringBuilder();

        sb.append("# ").append(title).append("\n\n");

        sb.append("## Tables").append("\n\n");
        tables.forEach(table -> describeTable(sb, table));

        ErdDslBuilder diagramBuilder = new PlantumlErdDslBuilder();
        sb.append("## ERD").append("\n\n");
        sb.append("```").append(diagramBuilder.languageName()).append("\n");
        ErdBuilder erdBuilder = new ErdBuilder(diagramBuilder);
        erdBuilder.addTables(tables);
        sb.append(erdBuilder.buildErd());
        sb.append("```");

        return sb.toString();
    }

    private void describeTable(StringBuilder sb, Table<?> table) {
        MarkdownTableBuilder tableBuilder = new MarkdownTableBuilder(
            "Attribute", "Data Type", "Null", "Key", "Description"
        );

        for (Field<?> field : table.fields()) {
            tableBuilder.addRow(
                field.getName(),
                field.getDataType().getCastTypeName(),
                field.getDataType().nullable() ? "Y" : "N",
                "",
                field.getComment()
            );
        }

        sb.append("### ").append(table.getName()).append("\n\n");

        if (!table.getComment().isEmpty()) {
            sb.append(table.getComment()).append("\n\n");
        }

        sb.append(tableBuilder.toMarkdown()).append("\n");
    }
}
