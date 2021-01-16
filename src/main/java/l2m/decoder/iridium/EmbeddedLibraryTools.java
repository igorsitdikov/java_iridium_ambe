package l2m.decoder.iridium;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EmbeddedLibraryTools {

    public static final boolean LOADED_EMBEDDED_LIBRARY;

    static {
        LOADED_EMBEDDED_LIBRARY = loadEmbeddedLibrary();
    }

    private EmbeddedLibraryTools() {
    }

    public static String defineExtension() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            return ".dll";
        } else if (osName.toLowerCase().contains("mac os x")) {
            return ".dylib";
        } else {
            return ".so";
        }
    }

    public static String[] defineLibs() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            return new String[]{"libwinpthread-1.dll", "libgcc_s_seh-1.dll", "libstdc++-6.dll", "libiridium.dll"};
        } else if (osName.toLowerCase().contains("mac os x")) {
            return new String[]{};
        } else {
            return new String[]{"libiridium.so"};
        }
    }

    public static String getCurrentPlatformIdentifier() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            osName = "Windows";
        } else if (osName.toLowerCase().contains("mac os x")) {
            osName = "Mac OS X";
        } else {
            osName = osName.replaceAll("\\s+", "_");
        }
        return System.getProperty("os.arch") + "/" + osName;
    }

    public static Collection<String> getEmbeddedLibraryList() {

        final Collection<String> result = new ArrayList<String>();
        final Collection<String> files = catalogClasspath();

        for (final String file : files) {
            if (file.startsWith("NATIVE")) {
                result.add(file);
            }
        }

        return result;

    }

    private static void catalogArchive(final File jarfile, final Collection<String> files) {
        JarFile j = null;
        try {
            j = new JarFile(jarfile);
            final Enumeration<JarEntry> e = j.entries();
            while (e.hasMoreElements()) {
                final JarEntry entry = e.nextElement();
                if (!entry.isDirectory()) {
                    files.add(entry.getName());
                }
            }

        } catch (IOException x) {
            System.err.println(x.toString());
        } finally {
            try {
                j.close();
            } catch (Exception e) {
            }
        }

    }

    private static Collection<String> catalogClasspath() {

        final List<String> files = new ArrayList<String>();
        final String[] classpath = System.getProperty("java.class.path", "").split(File.pathSeparator);

        for (final String path : classpath) {
            final File tmp = new File(path);
            if (tmp.isFile() && path.toLowerCase().endsWith(".jar")) {
                catalogArchive(tmp, files);
            } else if (tmp.isDirectory()) {
                final int len = tmp.getPath().length() + 1;
                catalogFiles(len, tmp, files);
            }
        }

        return files;

    }

    private static void catalogFiles(final int prefixlen, final File root, final Collection<String> files) {
        final File[] ff = root.listFiles();
        if (ff == null) {
            throw new IllegalStateException("invalid path listed: " + root);
        }

        for (final File f : ff) {
            if (f.isDirectory()) {
                catalogFiles(prefixlen, f, files);
            } else {
                files.add(f.getPath().substring(prefixlen));
            }
        }
    }

    private static boolean loadEmbeddedLibrary() {

        boolean usingEmbedded = false;

        // attempt to locate embedded native library within JAR at following location:
        // /NATIVE/${os.arch}/${os.name}/libjzmq.[so|dylib|dll]
        String[] allowedExtensions = new String[]{"so", "dylib", "dll", "0", "1", "5", "6"};
        String[] libs;
        final String libsFromProps = System.getProperty("jzmq.libs");
        if (libsFromProps == null) {
            libs = defineLibs();
//            System.out.println(Arrays.asList(libs));
        }
        else
            libs = libsFromProps.split(",");
//        System.out.println(libs);
        StringBuilder url = new StringBuilder();
        url.append("/NATIVE/");
        url.append(getCurrentPlatformIdentifier()).append("/");
//        System.out.println(url.toString());
        for (String lib : libs) {
            URL nativeLibraryUrl = null;
            // loop through extensions, stopping after finding first one
            for (String ext : allowedExtensions) {
//                System.out.println(ext);
                String name = url.toString() + lib /*+ "." + ext*/;
//                System.out.println(name);
                nativeLibraryUrl = iridiumAmbeJNI.class.getResource(name);
                if (nativeLibraryUrl != null)
                    break;
            }

            if (nativeLibraryUrl != null) {
                // native library found within JAR, extract and load
                try {
                    String tDir = System.getProperty("java.io.tmpdir");
                    final File libfile = new File(tDir + "/" + lib);
//                    System.out.println(libfile.getAbsoluteFile());
                    libfile.deleteOnExit(); // just in case

                    final InputStream in = nativeLibraryUrl.openStream();
                    final OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));

                    int len = 0;
                    byte[] buffer = new byte[8192];
                    while ((len = in.read(buffer)) > -1)
                        out.write(buffer, 0, len);
                    out.close();
                    in.close();
//                    System.out.println("Saved new libfile: " + libfile.getAbsoluteFile());
                    System.load(libfile.getAbsolutePath());

                    usingEmbedded = true;

                } catch (IOException x) {
//                    System.out.println(x);
                    // mission failed, do nothing
                }

            } // nativeLibraryUrl exists
        }
        return usingEmbedded;
    }
}

