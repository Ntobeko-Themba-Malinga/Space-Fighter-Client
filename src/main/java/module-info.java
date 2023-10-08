module org.space_fighter_client.space_fighter_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires json;


    opens org.space_fighter_client to javafx.fxml;
    opens org.space_fighter_client.controllers to javafx.fxml;
    exports org.space_fighter_client;
}