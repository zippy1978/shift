package com.backelite.shift.gui.control;

/*
 * #%L
 * ValidatedTextField.java - shift - 2013
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
import com.backelite.shift.gui.validation.Validator;
import com.backelite.shift.gui.validation.ValidatorResult;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 * Custom TextField implementation supporting a Validator.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ValidatedTextField extends TextField {

    private Validator validator;
    private ValidatorResult lastValidatorResult;
    
    private final BooleanProperty valid = new SimpleBooleanProperty(false);

    public ValidatedTextField() {
        super();
        bind();
    }
    
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }

    public boolean isValid() {
        return valid.get();
    }

    private void bind() {
        
        updateValid();

        this.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                updateValid();
            }
        });

    }
    
    private void updateValid() {
        
        if (validator != null) {
            lastValidatorResult = validator.validate(getText());
            this.valid.set(getLastValidatorResult().isValid());
        } else {
            this.valid.set(true);
        }
        
        if (this.valid.get()) {
            this.getStyleClass().remove("invalid");
        } else {
            this.getStyleClass().add("invalid");
        }
    }

    /**
     * @return the validator
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * @param validator the validator to set
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
        this.updateValid();
    }

    /**
     * @return the lastValidatorResult
     */
    public ValidatorResult getLastValidatorResult() {
        return lastValidatorResult;
    }

}
