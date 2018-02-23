package edu.kit.anthropomatik.h2t.expertsystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    // from https://stackoverflow.com/questions/2706222/create-cross-platform-java-swt-application/3204032#3204032
    static {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();
        String swtFileNameOsPart = osName.contains("win") ? "win32.win32" : osName.contains("mac") ? "cocoa.macosx" :
                osName.contains("linux") || osName.contains("nix") ? "gtk.linux" : "";

        String swtFileNameArchPart = osArch.contains("64") ? ".x86_64" : ".x86";
        String swtFileName = "org.eclipse.swt." + swtFileNameOsPart + swtFileNameArchPart + "-4.3.jar";

        try {
            URLClassLoader classLoader = (URLClassLoader) Main.class.getClassLoader();
            Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);

            URL swtFileUrl = Main.class.getResource("/" + swtFileName);
            addUrlMethod.invoke(classLoader, swtFileUrl);
        } catch (Exception e) {
            throw new RuntimeException("Unable to add the SWT jar to the class path: " + swtFileName, e);
        }
    }

    /**
     * Launch the application.
     *
     * @wbp.parser.entryPoint
     */
    public static void main(String[] args) {
        try {
            new GUI().open();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
