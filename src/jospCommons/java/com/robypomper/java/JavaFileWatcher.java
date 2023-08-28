/*******************************************************************************
 * The John Operating System Project is the collection of software and configurations
 * to generate IoT EcoSystem, like the John Operating System Platform one.
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

package com.robypomper.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * FileWatcher utils class.
 * <p>
 * This class allow to register a listener for file changes.
 * <p>
 * For better performance, this class start a single thread to check all
 * registered files changes. It run in background and wait {@link #getWaitTime()}
 * ms between each check.
 */
public class JavaFileWatcher {

    // Class constant

    /**
     * Default internal thread name.
     */
    public static final String TH_NAME = "FileWatcher";

    /**
     * Default wait time in ms between each file changes check.
     */
    public static final int DEF_WAIT_TIME_MS = 1 * 1000;


    // Internal vars

    private static boolean watcherThreadMustShutdown = false;
    private static Thread watcherThread;
    private static int waitTimeMs = DEF_WAIT_TIME_MS;
    private static final Map<Path, List<JavaFileWatcherListener>> listenersMap = new HashMap<>();
    private static final Map<Path, WatchService> watcherMap = new HashMap<>();


    // Listeners mngm

    /**
     * Add listener for given file changes.
     * <p>
     * If internal thread is not started, this method start it.
     *
     * @param fileName file name relative or absolute.
     * @param listener listener for file changes.
     * @throws IOException if errors occurs on register file's
     *                     {@link WatchService}.
     */
    public static void addListener(String fileName, JavaFileWatcherListener listener) throws IOException {
        addListener(new File(fileName), listener);
    }

    /**
     * Add listener for given file changes.
     * <p>
     * If internal thread is not started, this method start it.
     *
     * @param filePath file path.
     * @param listener listener for file changes.
     * @throws IOException if errors occurs on register file's
     *                     {@link WatchService}.
     */
    public static void addListener(Path filePath, JavaFileWatcherListener listener) throws IOException {
        Path absFilePath = filePath.toAbsolutePath();
        synchronized (listenersMap) {
            if (!listenersMap.containsKey(absFilePath)) {
                int oldSize = listenersMap.size();
                listenersMap.put(absFilePath, new ArrayList<>());
                watchFile(absFilePath);
                if (oldSize == 0)
                    startListen();
            }
            listenersMap.get(absFilePath).add(listener);
        }
    }

    /**
     * Add listener for given file changes.
     * <p>
     * If internal thread is not started, this method start it.
     *
     * @param file     file instance.
     * @param listener listener for file changes.
     * @throws IOException if errors occurs on register file's
     *                     {@link WatchService}.
     */
    public static void addListener(File file, JavaFileWatcherListener listener) throws IOException {
        addListener(file.toPath(), listener);
    }

    /**
     * Remove listener for given file changes.
     * <p>
     * If internal thread is not started, this method start it.
     *
     * @param fileName file name relative or absolute.
     * @param listener listener for file changes.
     * @throws IOException if errors occurs on deregister file's
     *                     {@link WatchService}.
     */
    public static void removeListener(String fileName, JavaFileWatcherListener listener) throws IOException {
        removeListener(new File(fileName), listener);
    }

    /**
     * Remove listener for given file changes.
     * <p>
     * If internal thread is not started, this method start it.
     *
     * @param filePath file path.
     * @param listener listener for file changes.
     * @throws IOException if errors occurs on deregister file's
     *                     {@link WatchService}.
     */
    public static void removeListener(Path filePath, JavaFileWatcherListener listener) throws IOException {
        Path absFilePath = filePath.toAbsolutePath();
        synchronized (listenersMap) {
            if (listenersMap.containsKey(absFilePath)) {
                listenersMap.get(absFilePath).remove(listener);
                if (listenersMap.get(absFilePath).isEmpty()) {
                    listenersMap.remove(absFilePath);
                    unWatchFile(absFilePath);
                    if (listenersMap.isEmpty())
                        stopListen();
                }
            }
        }
    }

    /**
     * Remove listener for given file changes.
     * <p>
     * If internal thread is not started, this method start it.
     *
     * @param file     file instance.
     * @param listener listener for file changes.
     * @throws IOException if errors occurs on deregister file's
     *                     {@link WatchService}.
     */
    public static void removeListener(File file, JavaFileWatcherListener listener) throws IOException {
        removeListener(file.toPath(), listener);
    }


    // Internal Watcher mngm

    private static void watchFile(Path filePath) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        filePath = filePath.toAbsolutePath();
        Path parentPath = filePath.toFile().isDirectory() ? filePath : filePath.getParent();
        parentPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        watcherMap.put(parentPath, watchService);
    }

    private static void unWatchFile(Path filePath) throws IOException {
        Path parentPath = filePath.toFile().isDirectory() ? filePath : filePath.getParent();
        //...
        WatchService watchService = watcherMap.remove(parentPath);
        if (watchService != null)
            watchService.close();
    }


    // Watcher startup

    private static void startListen() {
        watcherThreadMustShutdown = false;
        watcherThread = JavaThreads.initAndStart(new FileWatcherThread(), TH_NAME);
    }

    private static void stopListen() {
        if (watcherThread == null) return;

        watcherThreadMustShutdown = true;
        watcherThread.interrupt();
    }

    private static class FileWatcherThread implements Runnable {

        @Override
        public void run() {
            while (!watcherThreadMustShutdown) {
                for (Map.Entry<Path, WatchService> entry : watcherMap.entrySet()) {
                    Path fileDir = entry.getKey();
                    WatchService watchService = entry.getValue();
                    WatchKey key;
                    while ((key = watchService.poll()) != null && !watcherThreadMustShutdown) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path filePath = Paths.get(fileDir.toString(), event.context().toString());
                            System.out.printf("FileWatched %s%n", filePath);
                            try {
                                List<JavaFileWatcherListener> fileListeners;
                                synchronized (listenersMap) {
                                    fileListeners = listenersMap.get(filePath);
                                }

                                if (fileListeners == null)
                                    continue;

                                if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                    for (JavaFileWatcherListener l : fileListeners) {
                                        try {
                                            l.onCreate(filePath);
                                            l.onAnyUpdate(filePath);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                    for (JavaFileWatcherListener l : fileListeners) {
                                        try {
                                            l.onDelete(filePath);
                                            l.onAnyUpdate(filePath);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                    for (JavaFileWatcherListener l : fileListeners) {
                                        try {
                                            l.onUpdate(filePath);
                                            l.onAnyUpdate(filePath);
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        key.reset();
                    }

                    try {
                        //noinspection BusyWait
                        Thread.sleep(getWaitTime());
                    } catch (InterruptedException ignore) {
                    }
                }
            }
        }

    }


    // Configs

    /**
     * @return wait ms between each file changes check.
     */
    public static int getWaitTime() {
        return waitTimeMs;
    }

    /**
     * Set current wait ms between each file changes check.
     *
     * @param ms wait time in ms.
     */
    public static void setWaitTime(int ms) {
        JavaAssertions.makeAssertion(ms >= 0, "Illegal argument: can't set negative wait time");
        waitTimeMs = ms;
    }


    // File watcher interface

    /**
     * File watcher listener interface.
     */
    public interface JavaFileWatcherListener {

        void onCreate(Path filePath);

        void onUpdate(Path filePath);

        void onDelete(Path filePath);

        void onAnyUpdate(Path filePath);

    }

}
