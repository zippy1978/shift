package com.backelite.shift.util;

/*
 * #%L
 * FileUtil.java - shift - 2013
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;

/**
 * File utilities.
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FileUtils {

    public static void moveFile(File source, File destination) throws IOException {

        if (!source.renameTo(destination)) {
            throw new IOException(String.format("Failed to move %s to %s", source.getAbsolutePath(), destination.getAbsolutePath()));
        }
    }

    public static byte[] getFileContent(File file) throws IOException {

        FileInputStream fis = new FileInputStream(file);
        byte[] content = IOUtils.toByteArray(fis);
        fis.close();

        return content;
    }

    public static void saveContentToFile(byte[] content, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(content);
        fos.close();
    }

    public static String getFileContentAsStringFromClasspathResource(String resourcePath) {
        InputStream inStream = FileUtils.class.getResourceAsStream(resourcePath);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (IOException e) {
            // Nothing
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                // Nothing
            }
        }

        return builder.toString();
    }

    public static String getFileExtension(String filename) {

        String[] parts = filename.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1].toLowerCase();
        } else {
            // No extension found
            return "";
        }
    }

    public static boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    boolean deleted = deleteFolder(f);
                    if (!deleted) {
                        return false;
                    }
                } else {
                    f.delete();
                }
            }
        }
        return folder.delete();
    }
}
