module com.example {
    requires javafx.controls;
    requires javafx.graphics;

    opens com.example.scenecontrollers to javafx.fxml;
    exports com.example.scenecontrollers;
    exports com.example.backend;
    opens com.example.backend to javafx.fxml;
}