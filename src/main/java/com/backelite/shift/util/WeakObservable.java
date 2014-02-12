package com.backelite.shift.util;

/*
 * #%L
 * WeakObservable.java - shift - 2013
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
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    originalNotifyObservers();
                }
            });
        } catch (IllegalStateException e) {
            originalNotifyObservers();
        }

    }

    private void originalNotifyObservers() {
        super.notifyObservers();
    }

    @Override
    public void notifyObservers(final Object arg) {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    originalNotifyObservers(arg);
                }
            });
        } catch (IllegalStateException e) {
            originalNotifyObservers(arg);
        }
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
