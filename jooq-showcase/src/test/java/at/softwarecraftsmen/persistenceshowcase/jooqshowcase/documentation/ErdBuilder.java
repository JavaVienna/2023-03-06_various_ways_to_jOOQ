package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

import org.jooq.*;

import java.util.*;

public class ErdBuilder {

    private final List<Table<?>> tables = new ArrayList<>();
    private final ErdDslBuilder builder;

    public ErdBuilder(ErdDslBuilder builder) {
        this.builder = builder;
    }

    public void addTables(Table<?>... tables) {
        Collections.addAll(this.tables, tables);
    }

    public void addTables(Collection<Table<?>> tables) {
        this.tables.addAll(tables);
    }

    public String buildErd() {
        builder.start();

        for (Table<?> table : tables) {
            buildEntity(table);
        }

        buildRelationships(tables);

        builder.end();
        return builder.build();
    }

    private void buildEntity(Table<?> table) {
        builder.startEntity(table.getName());

        var primaryKeyFields = primaryKeyFields(table);

        if (!primaryKeyFields.isEmpty()) {
            builder.startPrimaryKeySection();
            primaryKeyFields.forEach(this::buildField);
            builder.endPrimaryKeySection();
        }

        table
            .fieldStream()
            .filter(field -> !primaryKeyFields.contains(field))
            .forEach(this::buildField);

        builder.endEntity();
    }

    private void buildField(Field<?> field) {
        builder.addField(field.getName(), field.getDataType().getTypeName(), field.getDataType().nullable());
    }

    private List<? extends Field<?>> primaryKeyFields(Table<?> table) {
        UniqueKey<?> primaryKey = table.getPrimaryKey();
        if (primaryKey == null) {
            return Collections.emptyList();
        }
        return primaryKey.getFields();
    }

    private void buildRelationships(List<Table<?>> tables) {
        tables.stream()
            .map(Qualified::getSchema)
            .distinct()
            .filter(Objects::nonNull)
            .flatMap(Schema::foreignKeyStream)
            .filter(foreignKey ->
                tables.contains(foreignKey.getKey().getTable()) && tables.contains(foreignKey.getTable())
            )
            .forEach(foreignKey -> {
                builder.addRelationship(
                    foreignKey.getKey().getTable().getName(),
                    foreignKey.getTable().getName(),
                    foreignKey.constraint().getComment()
                );
            });
    }
}
