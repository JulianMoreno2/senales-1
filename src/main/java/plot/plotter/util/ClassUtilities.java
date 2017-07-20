package plot.plotter.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtilities {

    private ClassUtilities() {
    }

    public static List jarURLDirectories(URL jarURL) throws IOException {
        List directories = new LinkedList();
        JarURLConnection connection = (JarURLConnection) (jarURL
                .openConnection());
        String jarEntryName = connection.getEntryName();
        if (jarEntryName.endsWith("/")) {
            jarEntryName = jarEntryName.substring(0, jarEntryName.length() - 1);
        }
        JarFile jarFile = connection.getJarFile();
        Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String name = entry.getName();
            int jarEntryIndex = name.indexOf(jarEntryName + "/");
            int jarEntrySlashIndex = jarEntryIndex + jarEntryName.length() + 1;

            int nextSlashIndex = name.indexOf("/", jarEntrySlashIndex);
            int lastSlashIndex = name.indexOf("/", jarEntrySlashIndex);

            if (jarEntryIndex > -1 && jarEntrySlashIndex > -1
                    && nextSlashIndex > -1 && nextSlashIndex == lastSlashIndex
                    && nextSlashIndex == name.length() - 1
                    && entry.isDirectory()) {
                directories.add(name);
            }
        }
        return directories;
    }

    public static URL jarURLEntryResource(String jarURLString)
            throws IOException {

        int jarEntry = jarURLString.indexOf("!/");

        if (jarEntry == -1) {
            jarEntry = jarURLString.indexOf("!\\");

            if (jarEntry == -1) {
                return null;
            }
        }

        try {
            String entry = jarURLString.substring(jarEntry + 2);

            Class refClass = Class.forName("ptolemy.util.ClassUtilities");
            URL entryURL = refClass.getClassLoader().getResource(entry);
            if (entryURL == null && entry.indexOf("#") != -1) {
                entryURL = refClass.getClassLoader().getResource(
                        entry.substring(0, entry.indexOf("#")));
            }
            return entryURL;
        } catch (Exception ex) {
            IOException ioException = new IOException("Cannot find \""
                    + jarURLString + "\".");
            ioException.initCause(ex);
            throw ioException;
        }
    }

    public static String lookupClassAsResource(String necessaryClass) {
        String necessaryResource = StringUtilities.substitute(necessaryClass,
                ".", "/")
                + ".class";

        URL necessaryURL = Thread.currentThread().getContextClassLoader()
                .getResource(necessaryResource);

        if (necessaryURL != null) {
            String resourceResults = necessaryURL.getFile();

            if (resourceResults.startsWith("file:/")) {
                resourceResults = resourceResults.substring(6);
            }

            resourceResults = resourceResults.substring(0, resourceResults
                    .length()
                    - necessaryResource.length());

            if (resourceResults.endsWith("!/")) {
                resourceResults = resourceResults.substring(0, resourceResults
                        .length() - 2);
            }

            File resourceFile = new File(resourceResults);

            String sanitizedResourceName = StringUtilities.substitute(
                    resourceFile.getPath(), "\\", "/");
            return sanitizedResourceName;
        }
        return null;
    }
}
