module com.example {
    requires javafx.controls;
    requires javafx.graphics;

    opens com.example.scenecontrollers to javafx.fxml;
    exports com.example.scenecontrollers;
}