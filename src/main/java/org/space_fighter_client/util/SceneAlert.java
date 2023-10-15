package org.space_fighter_client.util;

import javafx.scene.control.Alert;

public class SceneAlert {

    public static void warning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
