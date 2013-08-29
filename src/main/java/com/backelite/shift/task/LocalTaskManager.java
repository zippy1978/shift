package com.backelite.shift.task;

/*
 * #%L
 * LocalTaskManager.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class LocalTaskManager implements TaskManager {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Set<WeakReference<TaskManagerListener>> listeners = new HashSet<WeakReference<TaskManagerListener>>();

    public void addTask(final Task task) {

        // Event handlers on task

        task.setOnRunning(new EventHandler() {
            public void handle(Event t) {
                for (TaskManagerListener listener : getActiveListeners()) {
                    listener.onTaskStarted(task);
                }
            }
        });

        task.setOnSucceeded(new EventHandler() {
             public void handle(Event t) {
                for (TaskManagerListener listener : getActiveListeners()) {
                    listener.onTaskSucceeded(task);
                }
            }
        });
        
        task.setOnFailed(new EventHandler() {

            public void handle(Event t) {
                 for (TaskManagerListener listener : getActiveListeners()) {
                    listener.onTaskFailed(task);
                }
            }
        });

        // Run service
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return task;
            }
        };
        service.setExecutor(executorService);
        service.start();
    }

    public void addListener(TaskManagerListener listener) {

        listeners.add(new WeakReference<TaskManagerListener>(listener));
    }

    protected synchronized Set<TaskManagerListener> getActiveListeners() {

        Set<TaskManagerListener> result = new HashSet<TaskManagerListener>();

        for (WeakReference<TaskManagerListener> listenerRef : listeners) {
            if (listenerRef.get() != null) {
                result.add(listenerRef.get());
            } else {
                // Clean up null references for next call
                listeners.remove(listenerRef);
            }
        }

        return result;
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
    
    
}
