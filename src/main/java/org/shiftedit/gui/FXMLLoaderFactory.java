package org.shiftedit.gui;

/*
 * #%L
 * FXMLLoaderFactory.java - shift - 2013
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import org.shiftedit.ApplicationContext;
import org.shiftedit.plugin.Plugin;
import org.shiftedit.util.MergeResourceBundle;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class FXMLLoaderFactory {

    private static final String BUNDLE_I18N = "i18n";

    private static ResourceBundle bundle;

    public static FXMLLoader newInstance() {

        FXMLLoader loader = new FXMLLoader();

        loader.setResources(getBundle());

        return loader;
    }

    /**
     * Lazy load resource bundle.
     */
    private static synchronized ResourceBundle getBundle() {

        if (bundle == null) {
            MergeResourceBundle mergeBundle = new MergeResourceBundle();
            mergeBundle.addResource(ResourceBundle.getBundle(BUNDLE_I18N, Locale.getDefault()));

            // Plugin bundles
            List<Plugin> plugins = ApplicationContext.getPluginRegistry().getPlugins();
            plugins.stream().filter((plugin) -> (plugin.getI18nBundle() != null)).forEach((plugin) -> {
                mergeBundle.addResource(ResourceBundle.getBundle(plugin.getI18nBundle(), Locale.getDefault()));
            });
            
            bundle = mergeBundle;
        }

        return bundle;
    }
}
