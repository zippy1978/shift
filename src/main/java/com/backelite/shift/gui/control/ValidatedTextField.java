package com.backelite.shift.gui.control;

/*
 * #%L
 * ValidatedTextField.java - shift - 2013
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
import com.backelite.shift.gui.validation.Validator;
import com.backelite.shift.gui.validation.ValidatorResult;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.TextField;

/**
 * Custom TextField implementation supporting a Validator.
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class ValidatedTextField extends TextField {

    private Validator validator;
    private ValidatorResult lastValidatorResult;
    
    private final BooleanProperty valid = new SimpleBooleanProperty(false);
    
    private ChangeListener<String> textChangeListener;

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

        textChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                updateValid();
            }
        };
        this.textProperty().addListener(new WeakChangeListener<>(textChangeListener));

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
