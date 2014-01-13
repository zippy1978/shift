package com.backelite.shift.gui.dialog;

/*
 * #%L
 * AbstractDialogController.java - shift - 2013
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

import com.backelite.shift.gui.AbstractController;
import java.lang.ref.WeakReference;
import javafx.stage.Stage;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public abstract class AbstractDialogController extends AbstractController implements DialogController {

    private WeakReference<Stage> parentStage = new WeakReference<>(null);
    private Object userData;

    @Override
    public Stage getParentStage() {
        return parentStage.get();
    }

    @Override
    public void setParentStage(Stage parentStage) {
         this.parentStage = new WeakReference<>(parentStage);
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
        if (getParentStage() != null) {
            getParentStage().close();
        }
    }
}
