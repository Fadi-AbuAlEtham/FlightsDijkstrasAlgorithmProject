package com.dijkstras_algorithm_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainMenuController {

    @FXML
    private BorderPane bdPane;

    @FXML
    private Button fileBt;

    @FXML
    private Button infoBt;

    @FXML
    void onFileButtonClicked(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Capitals File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        // Show the FileChooser dialog
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile == null) {
            showAlert(Alert.AlertType.ERROR, "File Selection Cancelled", "No file was selected.");
            return;
        }else{
            FlightsController.file = selectedFile;
            FlightsController.file = selectedFile;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("flights2.fxml"));
            fxmlLoader.load();
            bdPane.getScene().setRoot(fxmlLoader.getRoot());
        }
    }

    @FXML
    void onInfoButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("info.fxml"));
        fxmlLoader.load();
        bdPane.getScene().setRoot(fxmlLoader.getRoot());
    }

    public void showAlert(Alert.AlertType alertType, String title, String content) {
        // Create a new Alert object with the specified alert type
        Alert alert = new Alert(alertType);

        // Set the title of the alert dialog
        alert.setTitle(title);

        // Disable the header text for a cleaner appearance
        alert.setHeaderText(null);

        // Set the main content of the alert dialog with the provided message
        alert.setContentText(content);

        // Display the alert and wait for the user to respond
        alert.showAndWait();
    }
}
