package io.cdep.bootstrap;

import com.google.common.io.Files;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;

public class TestBootstrap {
    @Rule
    public TestName name = new TestName();
    File getDownloadFolder() {
        return new File(String.format(".test-files/%s/bootstrap/.download", name.getMethodName()));

    }

    @Before
    public void setup() {
        deleteDownloadFiles();
    }

    private void deleteDownloadFiles() {
        File files[] = getDownloadFolder().listFiles();
        if (files != null) {
            for (File download : files) {
                download.delete();
            }
        }
    }

    private String main(String... args) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new Bootstrap(getDownloadFolder(), ps).go(args);
        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }

    @Test
    public void testVersion() throws Exception {
        assertThat(main("--version")).contains(BuildInfo.PROJECT_VERSION);
    }

    @Test
    public void testCDep() throws Exception {
        main("https://raw.githubusercontent.com/jomof/cdep/master/boot.yml", "--version");
    }

    //@Test
    public void testLocalFile() throws Exception {
        File localCDep = new File("./local-test-files/cdep-alpha-0.0.29.jar").getAbsoluteFile();
        File localSnake = new File("./local-test-files/snakeyaml-1.17.jar").getAbsoluteFile();
        if (!localCDep.exists()) {
             localCDep = new File("../../bootstrap/local-test-files/cdep-alpha-0.0.29.jar")
                     .getAbsoluteFile().getCanonicalFile();
             localSnake = new File("../../bootstrap/local-test-files/snakeyaml-1.17.jar")
                     .getAbsoluteFile().getCanonicalFile();
        }
        assertThat(localCDep.exists()).isTrue();
        assertThat(localSnake.exists()).isTrue();
        getDownloadFolder().mkdirs();
        File manifest = new File(getDownloadFolder().getParentFile().getParentFile(),
                "bootstrap.yml").getAbsoluteFile();
        StringBuilder sb = new StringBuilder();
        sb.append("entry: com.jomofisher.cdep.CDep\n");
        sb.append("dependencies:\n");
        sb.append(String.format("- %s\n", localCDep.toString()));
        sb.append(String.format("- %s\n", localSnake.toString()));
        Files.write(sb.toString(), manifest, StandardCharsets.UTF_8);
        String result = main(manifest.getAbsolutePath(), "--version");
        System.out.printf(result);
    }
}