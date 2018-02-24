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

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// from https://stackoverflow.com/questions/24205093/how-to-create-a-custom-appender-in-log4j2/24220688
// note: class name need not match the @Plugin name.
@Plugin(name = "MyAppenderForGui", category = "Core", elementType = "appender", printObject = true)
public final class MyAppenderForGui extends AbstractAppender {

    private static MyAppenderForGui lastCreatedInstance;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();

    private GUI gui;

    protected MyAppenderForGui(String name, Filter filter, Layout<? extends Serializable> layout, final boolean
            ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static MyAppenderForGui createAppender(@PluginAttribute("name") String name, @PluginElement("Layout")
            Layout<? extends Serializable> layout, @PluginElement("Filter") final Filter filter, @PluginAttribute
            ("otherAttribute") String otherAttribute) {
        if (name == null) {
            LOGGER.error("No name provided for MyAppenderForGui");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        lastCreatedInstance = new MyAppenderForGui(name, filter, layout, true);
        return lastCreatedInstance;
    }

    public static MyAppenderForGui getLastInstance() {
        return lastCreatedInstance;
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void append(LogEvent event) {
        readLock.lock();
        try {
            if (gui != null) {
                gui.setErrorText(event.getMessage().getFormattedMessage(), event.getLevel());
            }
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }
}