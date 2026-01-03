module com.projectkamberinesludogame.ludogame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.graphics;
    requires java.naming;
    requires java.rmi;
    requires java.desktop;
    requires javafx.base;

    opens com.projectkamberinesludogame.ludogame to javafx.fxml;
    exports com.projectkamberinesludogame.ludogame;

    opens com.projectkamberinesludogame.ludogame.controller to javafx.fxml;
    exports com.projectkamberinesludogame.ludogame.controller;

    exports com.projectkamberinesludogame.ludogame.model;
    opens com.projectkamberinesludogame.ludogame.model to javafx.fxml;

    exports com.projectkamberinesludogame.ludogame.rmi;
}