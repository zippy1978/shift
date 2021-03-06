package org.shiftedit.util;

/*
 * #%L
 * PlatformUtils.java - Shift - 2013
 * %%
 * Copyright (C) 2013 - 2014 Shift
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


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class PlatformUtils {

    private static List<String> ignoredFileNames;

    public static boolean isMacOSX() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * Return file names to ignore on the current platform.
     * Usually hidden system files.
     * @return a List
     */
    public static synchronized List<String> getIgnoredFileNames() {
        if (ignoredFileNames == null) {
            ignoredFileNames = new ArrayList<>();

            if (isMacOSX()) {
                ignoredFileNames.add(".DS_Store");
            }
        }

        return ignoredFileNames;
    }
}
