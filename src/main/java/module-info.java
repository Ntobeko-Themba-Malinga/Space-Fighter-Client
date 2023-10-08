module org.space_fighter_client.space_fighter_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.space_fighter_client.space_fighter_client to javafx.fxml;
    exports org.space_fighter_client.space_fighter_client;
}