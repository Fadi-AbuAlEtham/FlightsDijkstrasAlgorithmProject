module com.dijkstras_algorithm_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.dijkstras_algorithm_project to javafx.fxml;
    exports com.dijkstras_algorithm_project;
}