package com.backelite.shift.state;

/*
 * #%L
 * LocalStateManager.java - shift - 2013
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
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local StateManager. Stores states to files (encoded as JSON)
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class LocalStateManager implements StateManager {

    private static final Logger log = LoggerFactory.getLogger(LocalStateManager.class);
    
    private File rootDirectory;

    public LocalStateManager(File rootDirectory) {
        super();
        this.rootDirectory = rootDirectory;
    }

    @Override
    public void save(PersistableState object) throws StateException {

        log.debug(String.format("Saving %s state", object.getClass().getSimpleName()));
        
        Map<String, Object> state = new HashMap<String, Object>();
        object.saveState(state);

        // Store to file
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(rootDirectory, this.getFilename(object)), state);
        } catch (Exception ex) {
            throw new StateException(ex);
        }

    }

    @Override
    public void restore(PersistableState object) throws StateException {

        log.debug(String.format("Restoring %s state", object.getClass().getSimpleName()));
        
        // Restore from file
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> state;
        File file = new File(rootDirectory, this.getFilename(object));

        if (file.exists()) {
            try {
                state = mapper.readValue(file, Map.class);
            } catch (Exception ex) {
                throw new StateException(ex);
            }

            object.restoreState(state);

        }
    }

    private String getFilename(PersistableState object) {

        String id = object.getInstanceIdentifier();
        if (id == null) {
            id = "";
        }

        String filename = object.getClass().getSimpleName().toLowerCase();

        if (!id.isEmpty()) {
            return String.format("%s@%s.json", filename, id);
        } else {
            return String.format("%s.json", filename);
        }
    }
}
