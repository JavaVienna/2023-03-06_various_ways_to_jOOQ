package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

public class PlantumlErdDslBuilder implements ErdDslBuilder {

    private final StringBuilder sb;

    public PlantumlErdDslBuilder() {
        sb = new StringBuilder();
    }

    @Override
    public String languageName() {
        return "puml";
    }

    @Override
    public void start() {
        sb
            .append("@startuml")
            .append("\n\n")
            .append("hide circle\n")
            .append("hide empty members\n")
            .append("skinparam linetype ortho\n\n");
    }

    @Override
    public void startEntity(String name) {
        sb.append("entity \"").append(name).append("\" as ").append(name).append(" {\n");
    }

    @Override
    public void startPrimaryKeySection() {

    }

    @Override
    public void endPrimaryKeySection() {
        sb.append("  --\n");
    }

    @Override
    public void addField(String name, String dataTypeName, boolean nullable) {
        sb.append("  ");

        if (!nullable) {
            sb.append("* ");
        }

        sb.append(name).append(" ").append(dataTypeName).append("\n");
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
            .append("\n");
    }

    @Override
    public void end() {
        sb.append("\n@enduml");
    }

    @Override
    public String build() {
        return sb.toString();
    }
}
