package com.backelite.shift.util;

/*
 * #%L
 * FileUtil.java - shift - 2013
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import org.apache.ant.compress.taskdefs.Unzip;
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
        byte[] content;
        try (FileInputStream fis = new FileInputStream(file)) {
            content = IOUtils.toByteArray(fis);
        }

        return content;
    }

    public static void saveContentToFile(byte[] content, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
        }
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

    public static boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    boolean deleted = deleteDirectory(f);
                    if (!deleted) {
                        return false;
                    }
                } else {
                    f.delete();
                }
            }
        }
        return directory.delete();
    }

    /**
     * Create a directory with a unique name inside a given parnet directory.
     *
     * @param parent Parent directory
     * @return Newly created directory
     */
    public static File createUniqueDirectory(File parent) {

        String uniqueName = UUID.randomUUID().toString();

        File directory = new File(parent, uniqueName);
        directory.mkdirs();

        return directory;
    }

    /**
     * Unzip ZIP file to a target directory.
     * @param zipFile Source zip file
     * @param destinationDirectory Destination directory
     */
    public static void unzipFile(File zipFile, File destinationDirectory) {
        Unzip unzipper = new Unzip();
        unzipper.setSrc(zipFile);
        unzipper.setDest(destinationDirectory);
        unzipper.execute();
    }
}
