/*******************************************************************************
 * The John Object Daemon is the agent software to connect "objects"
 * to an IoT EcoSystem, like the John Operating System Platform one.
 * Copyright (C) 2021 Roberto Pompermaier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 ******************************************************************************/

package com.robypomper.josp.jod.executor;

import com.robypomper.java.JavaFileWatcher;
import com.robypomper.java.JavaFiles;
import com.robypomper.josp.jod.structure.JODComponent;
import com.robypomper.log.Mrk_JOD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


/**
 * JOD Listener for Files.
 * <p>
 * This listener wait for {@link JavaFileWatcher} events on specified file. On each file's update it read file's content
 * and parse it as component's status.
 */
public class ListenerFiles extends AbsJODListener {

    // Class constants

    private static final String PROP_FILE_PATH = "path";


    // Internal vars

    protected static final Logger log = LogManager.getLogger();
    protected final String filePath;
    protected boolean isWatching = false;


    // Constructor

    /**
     * Default ListenerFile constructor.
     *
     * @param name       name of the listener.
     * @param proto      proto of the listener.
     * @param configsStr configs string, can be an empty string.
     */
    public ListenerFiles(String name, String proto, String configsStr, JODComponent component) throws MissingPropertyException {
        super(name, proto, component);
        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' init with config string '%s://%s'", getName(), proto, configsStr));


        Map<String, String> configs = splitConfigsStrings(configsStr);
        filePath = parseConfigString(configs, PROP_FILE_PATH);

        log.trace(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' listen for changes on file '%s'", getName(), filePath));
        try {
            JavaFiles.createParentIfNotExist(filePath);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' file '%s' not exist and can't create watcher file", getName(), new File(filePath).getAbsolutePath()), e);
        }
    }


    // Getters

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return isWatching;
    }


    // Mngm

    /**
     * {@inheritDoc}
     */
    @Override
    public void listen() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Start '%s' listener", getName()));
        if (isEnabled()) return;

        try {
            JavaFileWatcher.addListener(Paths.get(filePath), fileListener);
            isWatching = true;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't register watcher on '%s' file", getName(), filePath), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void halt() {
        log.info(Mrk_JOD.JOD_EXEC_SUB, String.format("Stop '%s' listener server", getName()));
        if (!isEnabled()) return;

        try {
            JavaFileWatcher.removeListener(Paths.get(filePath), fileListener);
            isWatching = false;

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't register watcher on '%s' file", getName(), filePath), e);
        }
    }

    private void readAndUpdStatus(Path filePath) {
        String status = readStatus(filePath);
        if (status == null)
            return;

        updateStatus(status);
    }

    private String readStatus(Path filePath) {
        String state;
        try {
            state = JavaFiles.readString(filePath);

        } catch (IOException e) {
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't read status from '%s' file because %s", getName(), filePath, e.getMessage()), e);
            return null;
        }

        if (state.isEmpty())
            return null;

        return state.trim();
    }

    private void updateStatus(String newStatus) {
        if (!convertAndSetStatus(newStatus))
            log.warn(Mrk_JOD.JOD_EXEC_IMPL, String.format("ListenerFiles for component '%s' can't update his component because not supported (%s)", getName(), getComponent().getClass().getSimpleName()));
    }


    // File Watcher listener

    private final JavaFileWatcher.JavaFileWatcherListener fileListener = new JavaFileWatcher.JavaFileWatcherListener() {
        @Override
        public void onCreate(Path filePath) {
        }

        @Override
        public void onUpdate(Path filePath) {
        }

        @Override
        public void onDelete(Path filePath) {
        }

        @Override
        public void onAnyUpdate(Path filePath) {
            readAndUpdStatus(filePath);
        }
    };

}
