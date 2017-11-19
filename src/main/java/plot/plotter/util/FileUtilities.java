package plot.plotter.util;

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtilities {

    private FileUtilities() {
    }

    private static void extractJarFile(String jarFileName, String directoryName)
            throws IOException {
        JarFile jarFile = new JarFile(jarFileName);
        Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            File destinationFile = new File(directoryName, jarEntry.getName());
            if (jarEntry.isDirectory()) {
                if (!destinationFile.isDirectory() && !destinationFile.mkdirs()) {
                    throw new IOException("Warning, failed to create "
                            + "directory for \"" + destinationFile + "\".");
                }
            } else {
                _binaryCopyStream(jarFile.getInputStream(jarEntry),
                        destinationFile);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: java -classpath $PTII "
                    + "ptolemy.util.FileUtilities jarFile [directory]\n"
                    + "where jarFile is the name of the jar file\n"
                    + "and directory is the optional directory in which to "
                    + "extract.");
            StringUtilities.exit(2);
        }
        String jarFileName = args[0];
        String directoryName = null;
        if (args.length >= 2) {
            directoryName = args[1];
        }
        try {
            extractJarFile(jarFileName, directoryName);
        } catch (Throwable throwable) {
            System.err.println("Failed to extract \"" + jarFileName + "\"");
            throwable.printStackTrace();
            StringUtilities.exit(3);
        }
    }

    private static void _binaryCopyStream(InputStream inputStream,
                                          File destinationFile) throws IOException {
        BufferedInputStream input = null;

        try {
            input = new BufferedInputStream(inputStream);

            BufferedOutputStream output = null;

            try {
                File parent = destinationFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    if (!parent.mkdirs()) {
                        throw new IOException("Failed to create directories "
                                + "for \"" + parent + "\".");
                    }
                }

                output = new BufferedOutputStream(new FileOutputStream(
                        destinationFile));

                int c;

                while ((c = input.read()) != -1) {
                    output.write(c);
                }
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable);
                    }
                }
            }
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        }
    }
}
