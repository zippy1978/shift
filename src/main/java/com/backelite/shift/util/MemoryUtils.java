package com.backelite.shift.util;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author Gilles Grousset (gi.grousset@gmail.com)
 */
public class MemoryUtils {

    public static void cleanUpTableView(TableView tableView) {

        // Remove cell factories
        for (Object col : tableView.getColumns()) {
            if (col instanceof TableColumn) {
                TableColumn tableColumn = (TableColumn) col;
                tableColumn.setCellFactory(null);
            }
        }
    }
}
