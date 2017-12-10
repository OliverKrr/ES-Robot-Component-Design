package edu.kit.expertsystem;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.Level;
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
import org.eclipse.swt.graphics.Color;

// from https://stackoverflow.com/questions/24205093/how-to-create-a-custom-appender-in-log4j2/24220688
// note: class name need not match the @Plugin name.
@Plugin(name = "MyAppenderForGui", category = "Core", elementType = "appender", printObject = true)
public final class MyAppenderForGui extends AbstractAppender {

    private static MyAppenderForGui lastCreatedInstance;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();

    private GUI gui;

    protected MyAppenderForGui(String name, Filter filter, Layout<? extends Serializable> layout,
            final boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void append(LogEvent event) {
        readLock.lock();
        try {
            if (gui != null) {
                gui.setErrorText(event.getMessage().getFormattedMessage(),
                        matchLevelToColor(event.getLevel()));
            }
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }

    private Color matchLevelToColor(Level level) {
        if (Level.FATAL.equals(level) || Level.ERROR.equals(level)) {
            return Configs.RED;
        }
        if (Level.WARN.equals(level)) {
            return Configs.YELLOW;
        }
        // default
        return null;
    }

    @PluginFactory
    public static MyAppenderForGui createAppender(@PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("otherAttribute") String otherAttribute) {
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
}