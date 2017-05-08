package io.cdep.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {
    private PrintStream out = System.out;
    private File downloadFolder = null;
    private String entryPoint = null;
    private List<String> dependencies = new ArrayList<>();
    private List<File> localJars = new ArrayList<>();
    private String manifest = null;

    Bootstrap(File downloadFolder, PrintStream out) {
        this.out = out;
        this.downloadFolder = downloadFolder;
    }

    private static File getDownloadsFolder() {
      File userFolder = new File(System.getProperty("user.home"));
      return new File(userFolder, ".cdep/bootstrap/downloads");
    }

    public static void main(String[] args) throws Exception {

        new Bootstrap(getDownloadsFolder(), System.out).go(args);
    }

    void go(String[] args) throws IOException, URISyntaxException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (!handleVersion(args)) return;
        if (!handleMonkeyParse(args[0])) return;
        if (!handleDownload()) return;

        URL urls[] = new URL[localJars.size()];
        for (int i = 0; i < urls.length; ++i) {
            urls[i] = localJars.get(i).toURI().toURL();
        }
        URLClassLoader classLoader = new URLClassLoader(urls);
        Class clazz = classLoader.loadClass(entryPoint);
        Method method = clazz.getMethod("main", String[].class);
        String newArgs[] = new String[args.length - 1];
        for (int i = 0; i < newArgs.length; ++i) {
            newArgs[i] = args[i + 1];
        }
        try {
            method.invoke(null, (Object) newArgs);
        } catch(NoClassDefFoundError e) {
            out.printf(String.format("Possible bootstrap problem. Try deleting %s",
                    getDownloadsFolder()));

        }
    }

    private boolean handleDownload() throws IOException {
        for (String dependency : dependencies) {
            File file = new File(dependency);
            if (file.isFile()) {
                localJars.add(file);
                continue;
            }
            String name = dependency.substring(dependency.lastIndexOf('/'));

            File download = new File(downloadFolder, String.format("%s", manifest.hashCode()));
            download.mkdirs();
            download = new File(download, name);
            if (!download.exists()) {
                WebUtils.copyUrlToLocalFile(new URL(dependency), download);
            }
            localJars.add(download);
        }
        return true;
    }

    private boolean handleMonkeyParse(String manifestUrl) throws IOException {
        manifest = WebUtils.getUrlAsString(manifestUrl);
        String lines[] = manifest.split("\\r?\\n");
        String section = null;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("entry:")) {
                entryPoint = line.substring(6).trim();
                continue;
            }
            if (line.endsWith(":")) {
                section = line.substring(0, line.lastIndexOf(':'));
                continue;
            }
            if (line.startsWith("- ")) {
                if ("dependencies".equals(section)) {
                    dependencies.add(line.substring(2).trim());
                }
                continue;
            }
        }
        if (entryPoint == null) {
            throw new RuntimeException(
                    String.format("Bootstrap manifest %s is missing 'entry:'", manifestUrl));
        }
        if (dependencies.size() == 0) {
            throw new RuntimeException(
                    String.format("Bootstrap manifest %s is missing 'dependencies:'", manifestUrl));
        }
        return true;
    }

    private boolean handleVersion(String[] args) {
        if (args.length != 1 || !args[0].equals("--version")) {
            return true;
        }
        out.printf("bootstrap [%s]\n", BuildInfo.PROJECT_VERSION);
        return false;
    }
}