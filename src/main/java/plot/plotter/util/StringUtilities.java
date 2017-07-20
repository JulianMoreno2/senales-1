package plot.plotter.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class StringUtilities {

    private StringUtilities() {
    }

    public static String abbreviate(String longName) {
        if (longName == null) {
            return "<Unnamed>";
        }

        if (longName.length() <= 80) {
            return longName;
        }

        return longName.substring(0, 37) + ". . ."
                + longName.substring(longName.length() - 38);
    }

    public static String ellipsis(String string, int length) {

        string = StringUtilities.split(string, 160);

        StringTokenizer tokenizer = new StringTokenizer(string, LINE_SEPARATOR,
                true);

        if (tokenizer.countTokens() > 42) {
            StringBuffer results = new StringBuffer();

            for (int i = 0; (i < 42) && tokenizer.hasMoreTokens(); i++) {
                results.append(tokenizer.nextToken());
            }

            results.append("...");
            string = results.toString();
        }

        if (string.length() > length) {
            return string.substring(0, length - 3) + "...";
        }

        return string;
    }

    public static String escapeForXML(String string) {
        if (string == null) {
            return null;
        }
        StringBuffer buffer = new StringBuffer(string);
        int i = 0;
        int length = string.length();
        while (i < length) {
            switch (buffer.charAt(i)) {
                case '\n':
                    buffer.deleteCharAt(i);
                    buffer.insert(i, "&#10;");
                    length += 4;
                    break;
                case '\r':
                    buffer.deleteCharAt(i);
                    buffer.insert(i, "&#13;");
                    length += 4;
                    break;
                case '"':
                    buffer.deleteCharAt(i);
                    buffer.insert(i, "&quot;");
                    length += 5;
                    break;
                case '&':
                    buffer.deleteCharAt(i);
                    buffer.insert(i, "&amp;");
                    length += 4;
                    break;
                case '<':
                    buffer.deleteCharAt(i);
                    buffer.insert(i, "&lt;");
                    length += 3;
                    break;
                case '>':
                    buffer.deleteCharAt(i);
                    buffer.insert(i, "&gt;");
                    length += 3;
                    break;
            }
            i++;
        }
        return buffer.toString();
    }

    public static String escapeString(String string) {
        string = string.replaceAll("\\\\", "\\\\\\\\");
        string = string.replaceAll("\"", "\\\\\"");
        string = string.replaceAll("\n", "\\\\n");
        string = string.replaceAll("\t", "\\\\t");
        string = string.replaceAll("\b", "\\\\b");
        string = string.replaceAll("\r", "\\\\r");
        return string;
    }

    public static void exit(int returnValue) {
        try {
            if (StringUtilities.getProperty("ptolemy.ptII.doNotExit").length() > 0) {
                return;
            }
        } catch (SecurityException ex) {
            System.out.println("Warning: failed to get property \""
                    + "ptolemy.ptII.doNotExit\". "
                    + "(-sandbox always causes this)");
        }

        try {
            if (StringUtilities.getProperty("ptolemy.ptII.exitAfterWrapup")
                    .length() > 0) {
                throw new RuntimeException("Normally, we would "
                        + "exit here because Manager.exitAfterWrapup() "
                        + "was called.  However, because the "
                        + "ptolemy.ptII.exitAfterWrapup property "
                        + "is set, we throw this exception instead.");
            }
        } catch (SecurityException ex) {
            System.out.println("Warning: failed to get property \""
                    + "ptolemy.ptII.exitAfterWrapup\". "
                    + "(-sandbox always causes this)");

        }

        if (!inApplet()) {
            System.exit(returnValue);
        }
    }

    public static String getIndentPrefix(int level) {
        if (level <= 0) {
            return "";
        }

        StringBuffer result = new StringBuffer(level * 4);

        for (int i = 0; i < level; i++) {
            result.append("    ");
        }

        return result.toString();
    }

    public static String getProperty(String propertyName) {
        String property = null;

        try {
            property = System.getProperty(propertyName);
        } catch (SecurityException ex) {
            if (!propertyName.equals("ptolemy.ptII.dir")) {
                SecurityException security = new SecurityException(
                        "Could not find '" + propertyName + "' System property");
                security.initCause(ex);
                throw security;
            }
        }

        if (propertyName.equals("user.dir")) {
            try {
                if (property == null) {
                    return property;
                }
                File userDirFile = new File(property);
                return userDirFile.getCanonicalPath();
            } catch (IOException ex) {
                return property;
            }
        }

        if (property != null) {
            if (propertyName.equals("ptolemy.ptII.dir")
                    && property.startsWith("/cygdrive")
                    && !_printedCygwinWarning) {
                _printedCygwinWarning = true;
                System.err.println("ptolemy.ptII.dir property = \"" + property
                        + "\", which contains \"cygdrive\". "
                        + "This is almost always an error under Cygwin that "
                        + "is occurs when one does PTII=`pwd`.  Instead, do "
                        + "PTII=c:/foo/ptII");
            }

            return property;
        }

        if (propertyName.equals("ptolemy.ptII.dirAsURL")) {
            File ptIIAsFile = new File(getProperty("ptolemy.ptII.dir"));

            try {
                URI ptIIAsURI = ptIIAsFile.toURI();
                URL ptIIAsURL = ptIIAsURI.toURL();
                return ptIIAsURL.toString();
            } catch (java.net.MalformedURLException malformed) {
                throw new RuntimeException("While trying to find '"
                        + propertyName + "', could not convert '" + ptIIAsFile
                        + "' to a URL", malformed);
            }
        }

        if (propertyName.equals("ptolemy.ptII.dir")) {
            if (_ptolemyPtIIDir != null) {
                return _ptolemyPtIIDir;
            } else {
                String stringUtilitiesPath = "ptolemy/util/StringUtilities.class";

                URL namedObjURL = Thread.currentThread()
                        .getContextClassLoader().getResource(
                                stringUtilitiesPath);

                if (namedObjURL != null) {
                    String namedObjFileName = namedObjURL.getFile();

                    if (namedObjFileName.startsWith("file:")) {
                        if (namedObjFileName.startsWith("file:/")
                                || namedObjFileName.startsWith("file:\\")) {
                            namedObjFileName = namedObjFileName.substring(6);
                        } else {
                            namedObjFileName = namedObjFileName.substring(5);
                        }
                    }

                    String abnormalHome = namedObjFileName.substring(0,
                            namedObjFileName.length()
                                    - stringUtilitiesPath.length());

                    _ptolemyPtIIDir = (new File(abnormalHome)).toString();

                    if (_ptolemyPtIIDir.endsWith("/!")
                            || _ptolemyPtIIDir.endsWith("\\!")) {
                        _ptolemyPtIIDir = _ptolemyPtIIDir.substring(0,
                                _ptolemyPtIIDir.length() - 1);
                    }

                    String ptsupportJarName = File.separator + "DMptolemy"
                            + File.separator + "RMptsupport.jar";

                    if (_ptolemyPtIIDir.endsWith(ptsupportJarName)) {
                        _ptolemyPtIIDir = _ptolemyPtIIDir.substring(0,
                                _ptolemyPtIIDir.length()
                                        - ptsupportJarName.length());
                    } else {
                        ptsupportJarName = "/DMptolemy/XMptsupport.jar";

                        if (_ptolemyPtIIDir.lastIndexOf(ptsupportJarName) != -1) {
                            _ptolemyPtIIDir = _ptolemyPtIIDir.substring(0,
                                    _ptolemyPtIIDir
                                            .lastIndexOf(ptsupportJarName));
                        } else {
                            ptsupportJarName = File.separator + "ptolemy"
                                    + File.separator + "ptsupport.jar";

                            if (_ptolemyPtIIDir.lastIndexOf(ptsupportJarName) != -1) {
                                _ptolemyPtIIDir = _ptolemyPtIIDir.substring(0,
                                        _ptolemyPtIIDir
                                                .lastIndexOf(ptsupportJarName));
                            }
                        }
                    }
                }

                if (_ptolemyPtIIDir != null) {
                    _ptolemyPtIIDir = StringUtilities.substitute(
                            _ptolemyPtIIDir, "%20", " ");
                }

                if (_ptolemyPtIIDir == null) {
                    throw new RuntimeException("Could not find "
                            + "'ptolemy.ptII.dir'" + " property.  "
                            + "Also tried loading '" + stringUtilitiesPath
                            + "' as a resource and working from that. "
                            + "Vergil should be "
                            + "invoked with -Dptolemy.ptII.dir" + "=\"$PTII\"");
                }

                try {
                    System.setProperty("ptolemy.ptII.dir", _ptolemyPtIIDir);
                } catch (SecurityException security) {
                }

                return _ptolemyPtIIDir;
            }
        }

        if (property == null) {
            return "";
        }

        return property;
    }

    public static boolean inApplet() {
        boolean inApplet = false;
        try {
            StringUtilities.getProperty("HOME");
        } catch (SecurityException ex) {
            inApplet = true;
        }
        return inApplet;
    }

    public static void mergePropertiesFile() throws IOException {
        Properties systemProperties = System.getProperties();
        Properties newProperties = new Properties(systemProperties);
        String propertyFileName = "$CLASSPATH/lib/ptII.properties";

        URL propertyFileURL = FileUtilities.nameToURL(
                "xxxxxxCLASSPATHxxxxxx/lib/ptII.properties", null, null);

        if (propertyFileURL == null) {
            throw new IOException("Could not find " + propertyFileName);
        }

        newProperties.load(propertyFileURL.openStream());

        System.setProperties(newProperties);
    }

    public static String objectToSourceFileName(Object object) {
        String sourceFileNameBase = object.getClass().getName().replace('.',
                '/');

        if (sourceFileNameBase.indexOf("$") != -1) {
            sourceFileNameBase = sourceFileNameBase.substring(0,
                    sourceFileNameBase.indexOf("$"));
        }

        return sourceFileNameBase + ".java";
    }

    public static String preferencesDirectory() throws IOException {
        String preferencesDirectoryName = StringUtilities
                .getProperty("user.home")
                + File.separator
                + StringUtilities.PREFERENCES_DIRECTORY
                + File.separator;
        File preferencesDirectory = new File(preferencesDirectoryName);

        if (!preferencesDirectory.isDirectory()) {
            if (preferencesDirectory.mkdirs() == false) {
                throw new IOException("Could not create user preferences "
                        + "directory '" + preferencesDirectoryName + "'");
            }
        }

        return preferencesDirectoryName;
    }

    public static String propertiesFileName() throws IOException {
        return preferencesDirectory() + "ptII.properties";
    }

    public static LinkedList readLines(String lines) throws IOException {
        BufferedReader bufferedReader = null;
        LinkedList returnList = new LinkedList();
        String line;
        bufferedReader = new BufferedReader(new StringReader(lines));
        try {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (!(line.length() == 0 || line.startsWith("/*") || line
                        .startsWith("//"))) {
                    returnList.add(line);
                }
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return returnList;
    }

    public static String sanitizeName(String name) {
        char[] nameArray = name.toCharArray();

        for (int i = 0; i < nameArray.length; i++) {
            if (!Character.isJavaIdentifierPart(nameArray[i])) {
                nameArray[i] = '_';
            }
        }

        if (nameArray.length == 0) {
            return "";
        } else {
            if (!Character.isJavaIdentifierStart(nameArray[0])) {
                return "_" + new String(nameArray);
            } else {
                return new String(nameArray);
            }
        }
    }

    public static String split(String longName) {
        return split(longName, 79);
    }

    public static String split(String longName, int length) {
        if (longName == null) {
            return "<Unnamed>";
        }

        if (longName.length() <= length) {
            return longName;
        }

        StringBuffer results = new StringBuffer();

        StringTokenizer tokenizer = new StringTokenizer(longName,
                LINE_SEPARATOR, true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            int mark = 0;

            while (mark < (token.length() - length)) {
                int lastSpaceIndex = token.substring(mark, mark + length)
                        .lastIndexOf(" ");

                if (lastSpaceIndex < 0) {
                    results.append(token.substring(mark, mark + length)
                            + LINE_SEPARATOR);
                    mark += length;
                } else {
                    results.append(token.substring(mark, mark + lastSpaceIndex)
                            + LINE_SEPARATOR);
                    mark += (lastSpaceIndex + 1);
                }
            }

            results.append(token.substring(mark));
        }

        return results.toString();
    }

    public static URL stringToURL(String name, URI baseDirectory,
                                  ClassLoader classLoader) throws IOException {
        return FileUtilities.nameToURL(name, baseDirectory, classLoader);
    }

    public static String substitute(String string, String pattern,
                                    String replacement) {
        if (string == null) {
            return null;
        }
        int start = string.indexOf(pattern);

        while (start != -1) {
            StringBuffer buffer = new StringBuffer(string);
            buffer.delete(start, start + pattern.length());
            buffer.insert(start, replacement);
            string = new String(buffer);
            start = string.indexOf(pattern, start + replacement.length());
        }

        return string;
    }

    public static String substituteFilePrefix(String prefix, String string,
                                              String replacement) {
        if (string.startsWith(prefix)) {
            return replacement + string.substring(prefix.length());
        } else {
            try {
                String prefixCanonicalPath = (new File(prefix))
                        .getCanonicalPath();

                String stringCanonicalPath = (new File(string))
                        .getCanonicalPath();

                if (stringCanonicalPath.startsWith(prefixCanonicalPath)) {
                    return replacement
                            + stringCanonicalPath.substring(prefixCanonicalPath
                            .length());
                }
            } catch (Throwable throwable) {
            }
        }

        return string;
    }

    public static String[] tokenizeForExec(String inputString)
            throws IOException {
        List commandList = new LinkedList();

        StreamTokenizer streamTokenizer = new StreamTokenizer(new StringReader(
                inputString));

        streamTokenizer.resetSyntax();
        streamTokenizer.whitespaceChars(0, 32);
        streamTokenizer.wordChars(33, 127);

        streamTokenizer.ordinaryChar('"');

        streamTokenizer.eolIsSignificant(true);

        streamTokenizer.commentChar('#');

        String token = "";

        String singleToken = "";

        boolean inDoubleQuotedString = false;

        while (streamTokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            switch (streamTokenizer.ttype) {
                case StreamTokenizer.TT_WORD:

                    if (inDoubleQuotedString) {
                        if (token.length() > 0) {
                            token += " ";
                        }

                        token += (singleToken + streamTokenizer.sval);
                    } else {
                        token = singleToken + streamTokenizer.sval;
                        commandList.add(token);
                    }

                    singleToken = "";
                    break;

                case StreamTokenizer.TT_NUMBER:
                    throw new RuntimeException("Internal error: Found TT_NUMBER: '"
                            + streamTokenizer.nval + "'.  We should not be "
                            + "tokenizing numbers");

                case StreamTokenizer.TT_EOL:
                case StreamTokenizer.TT_EOF:
                    break;

                default:
                    singleToken = Character.toString((char) streamTokenizer.ttype);

                    if (singleToken.equals("\"")) {
                        if (inDoubleQuotedString) {
                            commandList.add(token);
                        }

                        inDoubleQuotedString = !inDoubleQuotedString;
                        singleToken = "";
                        token = "";
                    }

                    break;
            }
        }

        return (String[]) commandList.toArray(new String[commandList.size()]);
    }

    public static String truncateString(String string, int lineLength,
                                        int numberOfLines) {

        StringTokenizer tokenizer = new StringTokenizer(string, LINE_SEPARATOR,
                true);

        StringBuffer results = new StringBuffer();
        int lineCount = 0;
        while (tokenizer.hasMoreTokens()) {
            if (lineCount >= numberOfLines * 2) {
                results.append("...");
                break;
            }
            lineCount++;
            String line = tokenizer.nextToken();
            if (line.length() > lineLength) {
                line = line.substring(0, lineLength - 3) + "...";
            }
            results.append(line);
        }
        return results.toString();
    }

    public static String unescapeForXML(String string) {
        if (string.indexOf("&") != -1) {
            string = substitute(string, "&amp;", "&");
            string = substitute(string, "&quot;", "\"");
            string = substitute(string, "&lt;", "<");
            string = substitute(string, "&gt;", ">");
            string = substitute(string, "&#10;", "\n");
            string = substitute(string, "&#13;", "\r");
        }
        return string;
    }

    public static String usageString(String commandTemplate,
                                     String[][] commandOptions, String[] commandFlags) {
        String[][] commandFlagsWithDescriptions = new String[commandFlags.length][2];
        for (int i = 0; i < commandFlags.length; i++) {
            commandFlagsWithDescriptions[i][0] = commandFlags[i];
            commandFlagsWithDescriptions[i][1] = "";
        }
        return usageString(commandTemplate, commandOptions,
                commandFlagsWithDescriptions);
    }

    public static String usageString(String commandTemplate,
                                     String[][] commandOptions, String[][] commandFlagsWithDescriptions) {
        StringBuffer result = new StringBuffer("Usage: " + commandTemplate
                + "\n\n" + "Options that take values:\n");

        int i;

        for (i = 0; i < commandOptions.length; i++) {
            result.append(" " + commandOptions[i][0]);
            if (commandOptions[i][1].length() > 0) {
                result.append(" " + commandOptions[i][1]);
            }
            result.append("\n");
        }

        result.append("\nBoolean flags:\n");

        for (i = 0; i < commandFlagsWithDescriptions.length; i++) {
            result.append(" " + commandFlagsWithDescriptions[i][0]);
            if (commandFlagsWithDescriptions[i][1].length() > 0) {
                result.append("\t" + commandFlagsWithDescriptions[i][1]);
            }
            result.append("\n");
        }

        return result.toString();
    }

    public static final int ELLIPSIS_LENGTH_LONG = 2000;

    public static final int ELLIPSIS_LENGTH_SHORT = 400;

    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    public static final String PREFERENCES_DIRECTORY = ".ptolemyII";

    private static boolean _printedCygwinWarning = false;

    private static String _ptolemyPtIIDir = null;
}