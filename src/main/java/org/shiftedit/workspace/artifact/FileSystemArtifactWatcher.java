package org.shiftedit.workspace.artifact;

/*
 * #%L
 * FileSystemArtifactWatcher.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Shift
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ggrousset
 */
public class FileSystemArtifactWatcher {

    private static final Logger log = LoggerFactory.getLogger(FileSystemArtifactWatcher.class);

    private final List<AbstractFileSystemArtifact> artifacts = new ArrayList<>();

    private Map<WatchKey, AbstractFileSystemArtifact> keyMap = new HashMap<>();

    /**
     * Used to monitor local file changes from outside the application.
     */
    private WatchService watchService;
    private Thread watchThread;

    public void addArtifact(AbstractFileSystemArtifact artifact) {
        artifacts.add(artifact);
        this.restartWatching();

    }

    public void removeArtifact(AbstractFileSystemArtifact artifact) {
        artifacts.remove(artifact);
        this.restartWatching();

    }

    private synchronized void restartWatching() {
        this.stopWatching();
        this.startWatching();
    }

    /**
     * Start watching for external file changes.
     */
    public synchronized void startWatching() {

        log.debug("Starting file system artifact watcher");

        try {

            keyMap.clear();
            watchService = FileSystems.getDefault().newWatchService();

            for (AbstractFileSystemArtifact artifact : artifacts) {

                // Path are registered on folders only
                if (artifact instanceof FileSystemFolder) {

                    if (new File(artifact.getPath()).exists()) {
                        Path dataPath = Paths.get(artifact.getPath());
                        WatchKey key = dataPath.register(watchService,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_DELETE);
                        keyMap.put(key, artifact);
                    }

                }
            }

            watchThread = new Thread() {

                @Override
                public void run() {

                    boolean interrupted = false;

                    try {

                        while (!interrupted) {

                            log.debug("Listening to file system changes...");
                            if (watchService != null) {
                                WatchKey key = watchService.take();
                                for (WatchEvent<?> event : key.pollEvents()) {
                                    WatchEvent.Kind kind = event.kind();
                                    if (StandardWatchEventKinds.OVERFLOW != kind) {

                                        AbstractFileSystemArtifact sourceArtifact = keyMap.get(key);
                                        if (sourceArtifact != null) {
                                            String path = String.format("%s%s%s", sourceArtifact.getPath(), File.separator, event.context().toString());

                                            log.debug(String.format("File system change detected on %s", path));

                                            try {
                                                // Refresh folder
                                                sourceArtifact.refresh();
                                            } catch (IOException ex) {
                                                log.error(String.format("Failed to refresh file system folder %s", sourceArtifact.getPath()), ex);
                                            }
                                            
                                            // Notify out of sync on matching artifact
                                            artifacts.stream().filter((artifact) -> (artifact.getPath().equals(path))).forEach((artifact) -> {
                                                artifact.notifyOutOfSync();
                                            });

                                        }
                                    }

                                }

                                if (!key.reset()) {
                                    break;
                                }
                            }

                        }

                    } catch (InterruptedException | ClosedWatchServiceException ex) {
                        // Thrown on thread restart, or when service is not ready
                        if (ex instanceof InterruptedException) {
                            log.debug("Watcher thread interrupted");
                            interrupted = true;
                        } else {
                            // Watch service close
                        }
                    }

                }

            };

            watchThread.start();

        } catch (IOException ex) {
            log.error("Failed to start file system artifact watcher", ex);
        }
    }

    /**
     * Stop watching for external file changes.
     *
     */
    public synchronized void stopWatching() {

        log.debug("Stopping file system artifact watcher");

        try {
            if (watchThread != null) {
                watchThread.interrupt();
            }
            if (watchService != null) {
                watchService.close();
                watchService = null;
            }
        } catch (IOException ex) {
            log.error("Failed to stop file system artifact watcher", ex);
        }
    }

}
