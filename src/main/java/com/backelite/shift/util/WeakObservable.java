package com.backelite.shift.util;

/*
 * #%L
 * WeakObservable.java - shift - 2013
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
import java.util.Observable;
import java.util.Observer;
import javafx.application.Platform;

/**
 * {@link Observable} with {@link WeakReference}s to its {@link Observer}s Taken
 * from
 * https://nightpiter.googlecode.com/svn-history/r567/trunk/AndergroundCore/src/org/anderground/core/util/WeakObservable.java
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class WeakObservable extends Observable {

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(new WeakObserverProxy(o));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(new WeakObserverProxy(o));
    }

    @Override
    public void notifyObservers() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                originalNotifyObservers();
            }
        });

    }

    private void originalNotifyObservers() {
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(final Object arg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                originalNotifyObservers(arg);
            }
        });
    }

    private void originalNotifyObservers(Object arg) {
        super.notifyObservers(arg);
    }

    /**
     * @author Eldar Abusalimov (eldar.abusalimov@gmail.com)
     */
    private class WeakObserverProxy extends WeakReference<Observer> implements
            Observer {

        /**
         * @param referent
         */
        public WeakObserverProxy(Observer referent) {
            super(referent);
        }

        @Override
        public void update(Observable o, Object arg) {
            Observer observer = get();
            if (observer != null) {
                observer.update(o, arg);
            } else {
                o.deleteObserver(this);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (obj instanceof WeakObserverProxy) {
                WeakObserverProxy proxy = (WeakObserverProxy) obj;
                Observer thisObserver = get();
                Observer proxyObserver = proxy.get();
                if (thisObserver == null) {
                    return proxyObserver == null;
                }
                return thisObserver.equals(proxyObserver);
            }
            return false;
        }
    }
}
