package at.softwarecraftsmen.persistenceshowcase.jooqshowcase.documentation;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static at.softwarecraftsmen.jooqshowcase.Photography.PHOTOGRAPHY;

@SuppressWarnings("java:S2699")
class DatabaseDocumentationTest {

    @Test
    void build_textual_description() throws IOException {
        DatabaseDocumentationBuilder documentation = new DatabaseDocumentationBuilder();

        String markdown = documentation.generateDatabaseDocumentation(PHOTOGRAPHY);

        var targetDir = new File("build/generated/docs");
        targetDir.mkdirs();
        Files.writeString(targetDir.toPath().resolve("docs.md"), markdown);
    }
}
