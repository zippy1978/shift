package com.backelite.shift.preferences;

/*
 * #%L
 * LocalPreferencesManager.java - shift - 2013
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
import java.io.File;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class LocalPreferencesManager extends BasePreferencesManager {

    private static final Logger log = LoggerFactory.getLogger(LocalPreferencesManager.class);
    private static final String FILENAME = "preferences.json";
    private File rootDirectory;

    public LocalPreferencesManager(File rootDirectory) {
        super();
        this.rootDirectory = rootDirectory;
        try {
            this.load();
        } catch (PreferencesException ex) {
            log.error("Failed to load preferences", ex);
        }
    }

    @Override
    public void commit() throws PreferencesException {

        File file = new File(rootDirectory, FILENAME);
        try {
            mapper.writeValue(file, loadedValues);
        } catch (Exception ex) {
            throw new PreferencesException(ex);
        }
    }

    @Override
    public void rollback() throws PreferencesException {
        this.load();
    }

    private void load() throws PreferencesException {

        File file = new File(rootDirectory, FILENAME);
        if (file.exists()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                loadedValues = mapper.readValue(file, Map.class);
            } catch (Exception ex) {
                throw new PreferencesException(ex);
            }
        }
    }
}
