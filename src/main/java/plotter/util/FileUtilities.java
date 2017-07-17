package plotter.util;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtilities {

    private FileUtilities() {
    }

    public static boolean binaryCopyURLToFile(URL sourceURL,
                                              File destinationFile) throws IOException {
        URL destinationURL = destinationFile.getCanonicalFile().toURI().toURL();

        if (sourceURL.sameFile(destinationURL)) {
            return false;
        }

        File sourceFile = new File(sourceURL.getFile());

        if ((sourceFile.getPath().indexOf("!/") == -1)
                && (sourceFile.getPath().indexOf("!\\") == -1)) {
            try {
                if (sourceFile.getCanonicalFile().toURI().toURL().sameFile(
                        destinationURL)) {
                    return false;
                }
            } catch (IOException ex) {
                IOException ioException = new IOException(
                        "Cannot find canonical file name of '" + sourceFile
                                + "'");
                ioException.initCause(ex);
                throw ioException;
            }
        }

        _binaryCopyStream(sourceURL.openStream(), destinationFile);

        return true;
    }

    public static byte[] binaryReadURLToByteArray(URL sourceURL)
            throws IOException {
        return _binaryReadStream(sourceURL.openStream());
    }

    public static void extractJarFile(String jarFileName, String directoryName)
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

    public static File nameToFile(String name, URI base) {
        if ((name == null) || name.trim().equals("")) {
            return null;
        }

        if (name.startsWith(_CLASSPATH_VALUE) || name.startsWith("$CLASSPATH")) {
            URL result = null;
            try {
                result = _searchClassPath(name, null);
            } catch (IOException ex) {
            }
            if (result != null) {
                return new File(result.getPath());
            } else {
                String ptII = StringUtilities.getProperty("ptolemy.ptII.dir");
                if (ptII != null && ptII.length() > 0) {
                    return new File(ptII, _trimClassPath(name));
                }
            }
        }

        File file = new File(name);

        if (!file.isAbsolute()) {
            if (base != null) {
                URI newURI = base.resolve(StringUtilities.substitute(name, " ",
                        "%20").replace('\\', '/'));

                String urlString = newURI.getPath();
                file = new File(StringUtilities.substitute(urlString, "%20",
                        " "));
            }
        }
        return file;
    }

    public static BufferedReader openForReading(String name, URI base,
                                                ClassLoader classLoader) throws IOException {
        if ((name == null) || name.trim().equals("")) {
            return null;
        }

        if (name.trim().equals("System.in")) {
            if (STD_IN == null) {
                STD_IN = new BufferedReader(new InputStreamReader(System.in));
            }

            return STD_IN;
        }

        URL url = nameToURL(name, base, classLoader);

        if (url == null) {
            throw new IOException("Could not convert \"" + name
                    + "\" with base \"" + base + "\" to a URL.");
        }

        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(url.openStream());
        } catch (IOException ex) {
            try {
                URL possibleJarURL = ClassUtilities.jarURLEntryResource(url
                        .toString());
                if (possibleJarURL != null) {
                    inputStreamReader = new InputStreamReader(possibleJarURL
                            .openStream());
                }
                return new BufferedReader(inputStreamReader);
            } catch (Exception ex2) {
                try {
                    if (inputStreamReader != null) {
                        inputStreamReader.close();
                    }
                } catch (IOException ex3) {
                }
                IOException ioException = new IOException("Failed to open \""
                        + url + "\".");
                ioException.initCause(ex);
                throw ioException;
            }
        }

        return new BufferedReader(inputStreamReader);
    }

    public static URL nameToURL(String name, URI baseDirectory,
                                ClassLoader classLoader) throws IOException {
        if ((name == null) || name.trim().equals("")) {
            return null;
        }

        if (name.startsWith(_CLASSPATH_VALUE) || name.startsWith("$CLASSPATH")) {
            URL result = _searchClassPath(name, classLoader);
            if (result == null) {
                throw new IOException("Cannot find file '"
                        + _trimClassPath(name) + "' in classpath");
            }

            return result;
        }

        File file = new File(name);

        if (file.isAbsolute()) {
            if (!file.canRead()) {
                file = new File(StringUtilities.substitute(name, "%20", " "));

                URL possibleJarURL = null;

                if (!file.canRead()) {
                    possibleJarURL = ClassUtilities.jarURLEntryResource(name);

                    if (possibleJarURL != null) {
                        file = new File(possibleJarURL.getFile());
                    }
                }

                if (!file.canRead()) {
                    throw new IOException("Cannot read file '"
                            + name
                            + "' or '"
                            + StringUtilities.substitute(name, "%20", " ")
                            + "'"
                            + ((possibleJarURL == null) ? "" : (" or '"
                            + possibleJarURL.getFile() + "")));
                }
            }

            return file.toURI().toURL();
        } else {
            if (baseDirectory != null) {
                URI newURI;

                try {
                    newURI = baseDirectory.resolve(name);
                } catch (Exception ex) {
                    String name2 = StringUtilities.substitute(name, "%20", " ");
                    try {
                        newURI = baseDirectory.resolve(name2);
                        name = name2;
                    } catch (Exception ex2) {
                        IOException io = new IOException(
                                "Problem with URI format in '"
                                        + name
                                        + "'. "
                                        + "and '"
                                        + name2
                                        + "' "
                                        + "This can happen if the file name "
                                        + "is not absolute "
                                        + "and is not present relative to the "
                                        + "directory in which the specified model "
                                        + "was read (which was '"
                                        + baseDirectory + "')");
                        io.initCause(ex2);
                        throw io;
                    }
                }

                String urlString = newURI.toString();

                try {
                    if ((newURI.getScheme() != null)
                            && (newURI.getAuthority() == null)) {
                        urlString = urlString.substring(0, 6) + "//"
                                + urlString.substring(6);
                    }
                    return new URL(urlString);
                } catch (Exception ex3) {
                    try {
                        return new URL(baseDirectory.toURL(), urlString);
                    } catch (Exception ex4) {

                        try {
                            return new URL(baseDirectory.toURL(), newURI
                                    .toString());
                        } catch (Exception ex5) {
                        }

                        IOException io = new IOException(
                                "Problem with URI format in '"
                                        + urlString
                                        + "'. "
                                        + "This can happen if the '"
                                        + urlString
                                        + "' is not absolute"
                                        + " and is not present relative to the directory"
                                        + " in which the specified model was read"
                                        + " (which was '" + baseDirectory
                                        + "')");
                        io.initCause(ex3);
                        throw io;
                    }
                }
            }

            URL url = new URL(name);
//            try {
//                String fixedURLAsString = url.toString().replaceFirst(
//                        "(https?:)//?", "$1//");
//                url = new URL(fixedURLAsString);
//            } catch (Exception e) {
//                // Ignore
//            }
            return url;
        }
    }

    public static Writer openForWriting(String name, URI base, boolean append)
            throws IOException {
        if ((name == null) || name.trim().equals("")) {
            return null;
        }

        if (name.trim().equals("System.out")) {
            if (STD_OUT == null) {
                STD_OUT = new PrintWriter(System.out);
            }

            return STD_OUT;
        }

        File file = nameToFile(name, base);
        return new FileWriter(file, append);
    }

    public static BufferedReader STD_IN = null;

    public static PrintWriter STD_OUT = null;

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

    private static byte[] _binaryReadStream(InputStream inputStream)
            throws IOException {
        BufferedInputStream input = null;

        ByteArrayOutputStream output = null;

        try {
            input = new BufferedInputStream(inputStream);

            try {
                output = new ByteArrayOutputStream();
                final int BUFFERSIZE = 8192;
                byte[] buffer = new byte[BUFFERSIZE];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, BUFFERSIZE)) != -1) {
                    output.write(buffer, 0, bytesRead);
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
        if (output != null) {
            return output.toByteArray();
        }
        return null;
    }

    private static URL _searchClassPath(String name, ClassLoader classLoader)
            throws IOException {

        URL result = null;

        if (name.startsWith(_CLASSPATH_VALUE) || name.startsWith("$CLASSPATH")) {
            String trimmedName = _trimClassPath(name);

            if (classLoader == null) {
                String referenceClassName = "ptolemy.util.FileUtilities";

                try {
                    Class referenceClass = Class.forName(referenceClassName);
                    classLoader = referenceClass.getClassLoader();
                } catch (Exception ex) {
                    IOException ioException = new IOException(
                            "Cannot look up class \"" + referenceClassName
                                    + "\" or get its ClassLoader.");
                    ioException.initCause(ex);
                    throw ioException;
                }
            }

            result = classLoader.getResource(trimmedName);
        }
        return result;
    }

    private static String _trimClassPath(String name) {
        String classpathKey;

        if (name.startsWith(_CLASSPATH_VALUE)) {
            classpathKey = _CLASSPATH_VALUE;
        } else {
            classpathKey = "$CLASSPATH";
        }

        return name.substring(classpathKey.length() + 1);
    }

    private static String _CLASSPATH_VALUE = "xxxxxxCLASSPATHxxxxxx";
}
