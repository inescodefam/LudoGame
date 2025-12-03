module com.projectkamberinesludogame.ludogame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.projectkamberinesludogame.ludogame to javafx.fxml;
    exports com.projectkamberinesludogame.ludogame;
    exports com.projectkamberinesludogame.ludogame.model;
    opens com.projectkamberinesludogame.ludogame.model to javafx.fxml;
}