package com.backelite.shift.gui.dialog;

/*
 * #%L
 * AbstractDialogController.java - shift - 2013
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
import com.backelite.shift.gui.AbstractController;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractDialogController extends AbstractController implements DialogController {

    private Stage stage;
    private Object userData;

    private EventHandler<WindowEvent> closeWindowEventHandler;

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;

        // Set close window listener to track when stage closes
        if (stage != null) {
            closeWindowEventHandler = new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent t) {
                    close();
                }
            };
            stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new WeakEventHandler<>(closeWindowEventHandler));
        }
    }

    @Override
    public Object getUserData() {
        return userData;
    }

    @Override
    public void setUserData(Object userData) {
        this.userData = userData;
    }

    @Override
    public void close() {
        super.close();
        if (getStage() != null) {
            getStage().close();
        }
    }
}
