package com.backelite.shift.state;

/*
 * #%L
 * LocalStateManager.java - shift - 2013
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
