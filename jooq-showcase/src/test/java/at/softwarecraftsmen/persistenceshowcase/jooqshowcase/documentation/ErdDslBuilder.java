package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

public interface ErdDslBuilder {

    String languageName();

    void start();

    void startEntity(String name);

    void startPrimaryKeySection();

    void endPrimaryKeySection();

    void addField(String name, String dataTypeName, boolean nullable);

    void endEntity();

    void addRelationship(String manySideName, String oneSideName, String comment);

    void end();

    String build();
}
