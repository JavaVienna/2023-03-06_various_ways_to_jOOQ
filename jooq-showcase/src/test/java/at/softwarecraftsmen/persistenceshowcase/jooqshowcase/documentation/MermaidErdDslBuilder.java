package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

public class MermaidErdDslBuilder implements ErdDslBuilder {

    private final StringBuilder sb;

    public MermaidErdDslBuilder() {
        sb = new StringBuilder();
    }

    @Override
    public String languageName() {
        return "mermaid";
    }

    @Override
    public void start() {
        sb.append("erDiagram\n\n");
    }

    @Override
    public void startEntity(String name) {
        sb.append(name).append(" {\n");
    }

    @Override
    public void startPrimaryKeySection() {

    }

    @Override
    public void endPrimaryKeySection() {

    }

    @Override
    public void addField(String name, String dataTypeName, boolean nullable) {
        sb.append("  ").append(name).append(" ").append(dataTypeName).append("\n");
    }

    @Override
    public void endEntity() {
        sb.append("}\n\n");
    }

    @Override
    public void addRelationship(String manySideName, String oneSideName, String comment) {
        sb.append(manySideName)
            .append(" }|--|| ")
            .append(oneSideName)
            .append(" : \"")
            .append(comment)
            .append("\"")
            .append("\n");
    }

    @Override
    public void end() {

    }

    @Override
    public String build() {
        return sb.toString();
    }
}
