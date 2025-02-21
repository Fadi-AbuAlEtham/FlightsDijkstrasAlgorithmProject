package com.dijkstras_algorithm_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for displaying a world map with interactive capitals.
 * Capitals can be selected as origin and target via mouse clicks.
 */
public class FlightsController {

    @FXML
    private TextField costField;

    @FXML
    private TextField distanceField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private TextArea pathTextArea;

    @FXML
    private Button runButton;

    @FXML
    private ComboBox<Capital> sourceComboBox;

    @FXML
    private ComboBox<Capital> targetComboBox;

    @FXML
    private TextField timeField;

    @FXML
    private ImageView worldMapView;

    @FXML
    private Pane mapOverlayPane;

    @FXML
    private Pane mapWrapperPane;

    private Graph graph; // HashTable for graph representation
    static File file;
    private Capital originCapital = null; // Selected origin capital
    private Capital targetCapital = null; // Selected target capital
    private List<Capital> allCapitals;

    @FXML
    public void initialize() {
        allCapitals = new ArrayList<>();
        loadCapitalsFromFile(); // Load capitals from file
        plotCapitals(); // Plot capitals on map

        // Initially disable the target ComboBox
        targetComboBox.setDisable(true);
        filterComboBox.setDisable(true);
        runButton.setDisable(true);

        // Source ComboBox logic
        sourceComboBox.setOnAction(event -> {
            Capital selectedSource = sourceComboBox.getValue();

            if (selectedSource == null) {
                targetComboBox.setDisable(true); // Disable Target if Source is cleared
                originCapital = null;
                updateMarkerColors();
                return;
            }

            // Check if Source and Target are the same
            if (targetCapital != null && selectedSource.getName().equalsIgnoreCase(targetCapital.getName())) {
                showAlert(Alert.AlertType.ERROR, "Source and Target are equal", "Choose different Source and Target");
                sourceComboBox.setValue(originCapital); // Revert to the previous valid selection
            } else {
                // Update Source Selection
                originCapital = selectedSource;
                updateMarkerColors();

                // Enable the Target ComboBox and remove the selected source from Target
                targetComboBox.setDisable(false);
                targetComboBox.getItems().remove(originCapital);
            }
        });

        // Target ComboBox logic
        targetComboBox.setOnAction(event -> {
            Capital selectedTarget = targetComboBox.getValue();

            if (selectedTarget == null) {
                targetCapital = null;
                filterComboBox.setDisable(true);
                updateMarkerColors();
                return;
            }

            // Ensure Source is selected first
            if (originCapital == null) {
                showAlert(Alert.AlertType.ERROR, "Source is empty", "Choose Source first!");
                targetComboBox.setValue(targetCapital); // Revert to previous valid selection
                targetComboBox.setDisable(true); // Disable again until valid Source
                return;
            }

            // Check if Source and Target are the same
            if (selectedTarget.getName().equalsIgnoreCase(originCapital.getName())) {
                showAlert(Alert.AlertType.ERROR, "Source and Target are equal", "Choose different Source and Target");
                targetComboBox.setValue(targetCapital); // Revert to the previous valid selection
            } else {
                // Update Target Selection
                targetCapital = selectedTarget;

                // Remove selected target from Source ComboBox
                sourceComboBox.getItems().remove(targetCapital);
                filterComboBox.setDisable(false);

                // Update marker colors
                updateMarkerColors();
            }
        });

        filterComboBox.setOnAction(event -> {
            runButton.setDisable(filterComboBox.getValue() == null);
        });
    }

    // Loads capitals from a file and adds them to the graph.
    private void loadCapitalsFromFile() {
        if (file == null) {
            showAlert(Alert.AlertType.ERROR, "No file selected", "Choose a file first!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Read the first line: NumberOfVertex|NumberOfEdges
            String metadata = br.readLine();
            String[] metaParts = metadata.split("\\|");
            int numberOfVertices;
            int numberOfEdges;

            // If the first line contains only two values separated by "|"
            if (metaParts.length == 2) {
                numberOfVertices = Integer.parseInt(metaParts[0]);
                numberOfEdges = Integer.parseInt(metaParts[1]);

                graph = new Graph(numberOfVertices);

                System.out.println("Graph initialized with size: " + numberOfVertices);
                System.out.println("Expected vertices: " + numberOfVertices + ", Expected edges: " + numberOfEdges);
            } else {
                throw new IOException("Invalid metadata format in the first line");
            }

            // Read the next lines (vertices)
            for (int i = 0; i < numberOfVertices; i++) {
                String vertexLine = br.readLine();
                if (vertexLine == null) {
                    throw new IOException("Insufficient vertex entries. Expected " + numberOfVertices + " entries.");
                }

                // Capital name, latitude, and longitude
                String[] parts = vertexLine.split("\\|");
                if (parts.length != 3) {
                    throw new IOException("Invalid vertex format at line " + (i + 2));
                }

                String name = parts[0].trim();
                double latitude = Double.parseDouble(parts[1]);
                double longitude = Double.parseDouble(parts[2]);

                Capital capital = new Capital(name, latitude, longitude);

                // Insert into the graph
                graph.addCapital(capital);
                sourceComboBox.getItems().add(capital);
                targetComboBox.getItems().add(capital);
                allCapitals.add(capital); // Backup list
            }

            filterComboBox.getItems().addAll("Time", "Cost", "Distance");

            System.out.println("Capitals successfully loaded into the graph.");

            // Process the remaining lines (edges) source, destination, cost, time
            String edgeLine;
            while ((edgeLine = br.readLine()) != null) {
                String[] parts = edgeLine.split("\\|");
                if (parts.length != 4) {
                    throw new IOException("Invalid edge format at line: " + edgeLine);
                }

                String source = parts[0].trim();
                String target = parts[1].trim();
                double cost = Double.parseDouble(parts[2].replace("$", "").trim());
                int time = Integer.parseInt(parts[3].replaceAll("[^0-9]", "").trim());

                // Check if the edge already exists in the graph
                if (graph.hasEdge(source, target)) {
                    throw new IOException("Duplicate edge detected between: " + source + " and " + target + " at line: " + edgeLine);
                }

                // Add route if not duplicate
                graph.addRoute(source, target, cost, time);
            }

            System.out.println("Edges successfully loaded into the graph.");

        } catch (IOException e) {
            System.out.println("Error reading capitals file: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Error reading capitals file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from file: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "Error parsing number: " + e.getMessage());
        }
    }

    private void plotCapitals() {
        if (allCapitals == null || allCapitals.isEmpty()) {
            return;
        }

        // Clear previous markers
        mapOverlayPane.getChildren().clear();

        for (Capital capital : allCapitals) {
            double[] coordinates = convertCoordinatesToMap(capital.getLatitude(), capital.getLongitude());

            // Create a circle for the capital
            Circle capitalMarker = new Circle(coordinates[0], coordinates[1], 3.5);
            capitalMarker.setFill(Color.YELLOW); // Default marker color

            // Add a label for the capital's name
            Label capitalLabel = new Label(capital.getName());
            capitalLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
            capitalLabel.setMouseTransparent(true); // Prevent label from blocking clicks

            capitalLabel.setLayoutX(coordinates[0] - (capitalLabel.getWidth() / 2) - 20);
            capitalLabel.setLayoutY(coordinates[1] - (capitalLabel.getHeight() + 22)); // Adjust label above with spacing

            // Ensure the circle is clickable and on top
            capitalMarker.toFront();

            // Add Hover Event to Capital Marker
            capitalMarker.setOnMouseEntered(event -> {
                capitalLabel.setStyle(
                        "-fx-font-size: 14px; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-color: rgba(0, 0, 0, 0.7); " +
                                "-fx-background-radius: 5px; " +
                                "-fx-padding: 3px;"
                );
                capitalLabel.toFront(); // Bring label to the front on hover
            });

            capitalMarker.setOnMouseExited(event -> {
                capitalLabel.setStyle(
                        "-fx-font-size: 10px; " +
                                "-fx-text-fill: white; " +
                                "-fx-background-color: transparent;"
                );
            });

            // Add Click Event to Capital Marker
            capitalMarker.setOnMouseClicked(event -> {
                if (originCapital == null) {
                    // First click: Select Source
                    capitalMarker.setFill(Color.RED); // Mark as source
                    originCapital = capital;
                    sourceComboBox.setValue(capital); // Update source ComboBox
                    System.out.println("Source selected: " + capital.getName());
                } else if (targetCapital == null) {
                    // Second click: Select Destination
                    if (capital == originCapital) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Source and Destination cannot be the same!");
                        return;
                    }
                    capitalMarker.setFill(Color.LIME); // Mark as destination
                    targetCapital = capital;
                    targetComboBox.setValue(capital); // Update target ComboBox
                    System.out.println("Destination selected: " + capital.getName());
                } else {
                    // If both are selected, check if clicked on one of them
                    if (capital == originCapital || capital == targetCapital) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Source and Destination cannot be the same!");
                        return;
                    }
                }
            });


            // Add marker and label to the overlay pane
            mapOverlayPane.getChildren().addAll(capitalMarker, capitalLabel);
        }
    }

    // Converts latitude and longitude to X and Y pixel coordinates on the canvas.
    private double[] convertCoordinatesToMap(double latitude, double longitude) {
        double mapWidth = mapWrapperPane.getPrefWidth(); // Use wrapper Pane width
        double mapHeight = mapWrapperPane.getPrefHeight(); // Use wrapper Pane height

        // Horizontal offset adjustment (fine-tuning for alignment)
        double horizontalOffset = -15.5; // Fine-tune if necessary

        // Normalize Longitude (-180° to 180°) → X-axis (0 to mapWidth)
        double x = ((longitude + 180.0) / 360.0) * mapWidth + horizontalOffset;

        // Normalize Latitude (-90° to 90°) → Y-axis (0 to mapHeight)
        double y = ((90.0 - latitude) / 180.0) * mapHeight ;

        return new double[]{x, y};
    }

    @FXML
    void onResetBtClicked(ActionEvent event) {
        // Clear the current selection
        sourceComboBox.setValue(null);
        targetComboBox.setValue(null);
        filterComboBox.setValue(null);
        originCapital = null;
        targetCapital = null;

        targetComboBox.setDisable(true);
        filterComboBox.setDisable(true);
        runButton.setDisable(true);

        pathTextArea.clear();
        timeField.clear();
        costField.clear();
        distanceField.clear();

        // Clear ComboBoxes and refill them from the backup list
        sourceComboBox.getItems().setAll(allCapitals);
        targetComboBox.getItems().setAll(allCapitals);

        // Clear and re-plot the map
        plotCapitals();
        updateMarkerColors();
    }

    @FXML
    void onRunBtClicked(ActionEvent event) {
        int factorIndex = -1;
        plotCapitals(); // Redraw only the capitals
        updateMarkerColors();

        if (sourceComboBox.getValue() != null && targetComboBox.getValue() != null &&
                !sourceComboBox.getValue().getName().equalsIgnoreCase(targetComboBox.getValue().getName()) &&
                filterComboBox.getValue() != null) {

            if(filterComboBox.getValue().equalsIgnoreCase("Distance")){
                factorIndex = 0;
            }else if(filterComboBox.getValue().equalsIgnoreCase("Time")){
                factorIndex = 2;
            } else if (filterComboBox.getValue().equalsIgnoreCase("Cost")) {
                factorIndex = 1;
            }

            // Get the Dijkstra result
            String result = graph.dijkstra(
                    sourceComboBox.getValue(),
                    targetComboBox.getValue(),
                    factorIndex,
                    costField, timeField, distanceField
            );

            // Check if a valid path exists in the result
            if (result == null || result.isBlank() || result.contains("No valid path from")) {
                pathTextArea.setText(result);
                showAlert(Alert.AlertType.INFORMATION, "No Path Found", "There is no valid path between the selected capitals.");
                return;
            }

            // Display the result in the TextArea
            pathTextArea.setText(result);

            // Extract and draw the path
            String[] path = extractCapitals(result);
            drawPathOnMap(path);

            // Highlight selected source and destination on the map
            updateMarkerColors();
        } else {
            // Handle invalid selections and show appropriate alerts
            if (sourceComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Source is empty!", "Please select a source capital first!");
            } else if (targetComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Target is empty!", "Please select a target capital first!");
            } else if (filterComboBox.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Filter is empty!", "Please select a filter method first!");
            } else if (sourceComboBox.getValue().getName().equalsIgnoreCase(targetComboBox.getValue().getName())) {
                showAlert(Alert.AlertType.ERROR, "Invalid Selection", "Source and Target cannot be the same!");
            }
        }
    }


    private String[] extractCapitals(String result) {
        // Step 1: Extract the line containing the capitals
        String[] lines = result.split("\n");
        if (lines.length < 1) {
            return new String[0]; // Invalid format
        }

        // Step 2: Remove the prefix "Shortest path (Cost): "
        String pathLine = lines[0].replaceFirst("Shortest path \\(.*\\): ", "").trim();

        // Step 3: Split the capitals using " --> "
        return pathLine.split(" --> ");
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

    @FXML
    void onMainMenuBtClicked(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
        fxmlLoader.load();
        costField.getScene().setRoot(fxmlLoader.getRoot());
    }

    private void updateMarkerColors() {
        for (Node node : mapOverlayPane.getChildren()) {
            if (node instanceof Circle) {
                Circle marker = (Circle) node;

                // Get the capital associated with this marker
                Capital markerCapital = getCapitalFromCoordinates(marker.getCenterX(), marker.getCenterY());
                if (markerCapital == null) {
                    continue;
                }

                // Update marker colors based on selection
                if (markerCapital.equals(originCapital)) {
                    marker.setFill(Color.RED); // Source
                } else if (markerCapital.equals(targetCapital)) {
                    marker.setFill(Color.LIME); // Destination
                } else {
                    marker.setFill(Color.YELLOW); // Default
                }
            }
        }
    }

    private Capital getCapitalFromCoordinates(double x, double y) {
        // Iterate through all available capitals
        for (Capital capital : allCapitals) {
            // Convert the capital's latitude and longitude to screen coordinates
            double[] coordinates = convertCoordinatesToMap(capital.getLatitude(), capital.getLongitude());

            /**
             * Check if the provided (x, y) coordinates are within a small threshold (2 pixels)
             * of the calculated coordinates of the capital.
             * This threshold accounts for minor mis-alignments or rendering inaccuracies.
             */
            if (Math.abs(coordinates[0] - x) < 2 && Math.abs(coordinates[1] - y) < 2) {
                // If a match is found, return the corresponding capital
                return capital;
            }
        }

        // If no capital matches the coordinates, return null
        return null;
    }

    private void drawPathOnMap(String[] path) {
        if (path == null || path.length < 2) {
            System.out.println("Invalid path: At least two capitals are required to draw a path.");
            return;
        }

        // Clear previous paths and planes
        mapOverlayPane.getChildren().removeIf(node ->
                node instanceof Path || (node instanceof Label && "plane".equals(node.getId())));

        for (int i = 0; i < path.length - 1; i++) {
            // Fetch source and target capitals
            GraphNode sourceNode = graph.findNodeByName(path[i]);
            GraphNode targetNode = graph.findNodeByName(path[i + 1]);

            if (sourceNode == null || targetNode == null) {
                System.out.println("Invalid capital in path: " + path[i] + " or " + path[i + 1]);
                continue; // Skip invalid segments
            }

            Capital sourceCapital = sourceNode.getCapital();
            Capital targetCapital = targetNode.getCapital();

            // Get coordinates
            double[] startCoords = convertCoordinatesToMap(sourceCapital.getLatitude(), sourceCapital.getLongitude());
            double[] endCoords = convertCoordinatesToMap(targetCapital.getLatitude(), targetCapital.getLongitude());

            // Draw dashed line
            drawDashedLine(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);

            // Draw plane icon
            drawPlaneIcon(startCoords, endCoords);
        }
    }

    private void drawDashedLine(double startX, double startY, double endX, double endY) {
        Path path = new Path();

        path.getElements().add(new MoveTo(startX, startY));
        path.getElements().add(new LineTo(endX, endY));

        path.setStroke(Color.web("#9999ff"));
        path.setStrokeWidth(2);
        path.getStrokeDashArray().addAll(10.0, 5.0); // Dashed pattern
        path.setOpacity(0.8);

        mapOverlayPane.getChildren().add(path);
    }


    private void drawPlaneIcon(double[] startCoords, double[] endCoords) {
        // Calculate midpoint
        double midX = (startCoords[0] + endCoords[0]) / 2;
        double midY = (startCoords[1] + endCoords[1]) / 2;

        // Calculate the angle of rotation and adjust for default orientation
        double angle = Math.toDegrees(Math.atan2(endCoords[1] - startCoords[1], endCoords[0] - startCoords[0])) - 270;

        // Load the plane image
        Image planeImage = new Image(getClass().getResource("plane.png").toExternalForm());
        ImageView planeImageView = new ImageView(planeImage);

        // Set image properties
        planeImageView.setFitWidth(25); // Set width for clarity
        planeImageView.setFitHeight(25); // Set height for clarity
        planeImageView.setPreserveRatio(true); // Maintain aspect ratio
        planeImageView.setSmooth(true); // Smooth rendering

        // Position at the midpoint
        planeImageView.setLayoutX(midX - planeImageView.getFitWidth() / 2);
        planeImageView.setLayoutY(midY - planeImageView.getFitHeight() / 2);

        // Rotate the image to align with the path
        planeImageView.setRotate(angle);

        // Bring it to the front
        planeImageView.toFront();

        // Add to the map overlay pane
        mapOverlayPane.getChildren().add(planeImageView);
    }
}
