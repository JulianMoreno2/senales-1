package plot.plotter.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class StringUtilities {

    private StringUtilities() {
    }

    public static void exit(int returnValue) {
        if (!inApplet()) {
            System.exit(returnValue);
        }
    }

    public static String getProperty(String propertyName) {
        String property = null;

        try {
            property = System.getProperty(propertyName);
        } catch (SecurityException ex) {
            if (!propertyName.equals("ptolemy.ptII.dir")) {
                throw new SecurityException(
                        "Could not find '" + propertyName + "' System property", ex);
            }
        }

        if (propertyName.equals("user.dir")) {
            try {
                if (property == null) {
                    return null;
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
                } catch (SecurityException ignored) {
                }

                return _ptolemyPtIIDir;
            }
        }

        return property;
    }

    private static boolean inApplet() {
        boolean inApplet = false;
        try {
            StringUtilities.getProperty("HOME");
        } catch (SecurityException ex) {
            inApplet = true;
        }
        return inApplet;
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

    private static boolean _printedCygwinWarning = false;

    private static String _ptolemyPtIIDir = null;
}