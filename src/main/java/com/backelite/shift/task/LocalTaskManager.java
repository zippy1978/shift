package com.backelite.shift.task;

/*
 * #%L
 * LocalTaskManager.java - shift - 2013
 * %%
 * Copyright (C) 2013 Gilles Grousset
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
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private Set<WeakReference<TaskManagerListener>> listeners = new HashSet<>();

    @Override
    public void addTask(final Task task) {

        // Event handlers on task

        task.setOnRunning((Event t) -> {
            for (TaskManagerListener listener : getActiveListeners()) {
                listener.onTaskStarted(task);
            }
        });

        task.setOnSucceeded((Event t) -> {
            for (TaskManagerListener listener : getActiveListeners()) {
                listener.onTaskSucceeded(task);
            }
        });
        
        task.setOnFailed((Event t) -> {
            for (TaskManagerListener listener : getActiveListeners()) {
                listener.onTaskFailed(task);
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

    @Override
    public void addListener(TaskManagerListener listener) {

        listeners.add(new WeakReference<>(listener));
    }

    protected synchronized Set<TaskManagerListener> getActiveListeners() {

        Set<TaskManagerListener> result = new HashSet<>();

        List<WeakReference<TaskManagerListener>> listenersToRemove = new ArrayList<>();
        for (WeakReference<TaskManagerListener> listenerRef : listeners) {
            if (listenerRef.get() != null) {
                result.add(listenerRef.get());
            } else {
                // Clean up null references for next call
                listenersToRemove.add(listenerRef);
            }
        }
        listeners.removeAll(listenersToRemove);

        return result;
    }

    @Override
    public void shutdown() {
        executorService.shutdownNow();
    }
    
    
}
