package com.dijkstras_algorithm_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InfoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane bdPane;

    @FXML
    private Button btMainMenu;

    @FXML
    private HBox hbox;

    @FXML
    private Label lblInfo;

    @FXML
    private TextArea txtAreaInfo;

    @FXML
    void onMainMenuClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        BorderPane borderPane = fxmlLoader.load();
        bdPane.getScene().setRoot(borderPane);
    }

    @FXML
    void initialize() {
        assert bdPane != null : "fx:id=\"bdPane\" was not injected: check your FXML file 'information.fxml'.";
        assert btMainMenu != null : "fx:id=\"btMainMenu\" was not injected: check your FXML file 'information.fxml'.";
        assert hbox != null : "fx:id=\"hbox\" was not injected: check your FXML file 'information.fxml'.";
        assert lblInfo != null : "fx:id=\"lblInfo\" was not injected: check your FXML file 'information.fxml'.";
        assert txtAreaInfo != null : "fx:id=\"txtAreaInfo\" was not injected: check your FXML file 'information.fxml'.";

        txtAreaInfo.clear();
        txtAreaInfo.setEditable(false);
        txtAreaInfo.setWrapText(true);
        txtAreaInfo.setText(
                "Welcome to the Flight Navigation System, your all-in-one tool for seamless route planning and flight cost estimation. This intuitive system is designed to provide accurate navigation between global capitals, offering essential details such as travel time, distance, and cost. Whether you're an aviation enthusiast, a logistics planner, or simply exploring global connections, this system ensures a user-friendly experience.\n" +
                        "\n" +
                        "At the heart of the system lies an interactive world map, allowing you to visually select source and destination capitals. You can either click directly on the map to choose your capitals or use the dropdown menus conveniently placed on the side panel. Once your source and target cities are selected, you can further refine your results using the Filter option, enabling you to prioritize based on time, cost, or distance.\n" +
                        "\n" +
                        "When you're ready, simply click the Run button to calculate the optimal route. The system will generate a clear path on the map, showing a visual flight route alongside key information such as travel time, cost, and total distance in the designated fields. For better clarity, the selected source capital will be highlighted in green, and the target capital will appear in red.\n" +
                        "\n" +
                        "If you wish to start over, use the Reset button to clear all selections and return the system to its default state. The Main Menu button provides quick access to the system's primary functions, ensuring smooth navigation across features.\n" +
                        "\n" +
                        "The system is built with simplicity in mind, minimizing clutter while focusing on delivering precise results. Its responsive interface adapts seamlessly to user inputs, making it easy to explore different flight scenarios without hassle.\n" +
                        "\n" +
                        "We hope this manual serves as a helpful introduction to the system. Enjoy exploring the skies with the Flight Navigation System, where every route is a journey waiting to be discovered."
        );

    }

}
