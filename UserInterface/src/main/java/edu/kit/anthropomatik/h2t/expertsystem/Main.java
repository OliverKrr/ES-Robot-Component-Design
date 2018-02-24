/*
 * Copyright 2018 Oliver Karrenbauer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation * files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, * * * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
