package org.example;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.controller.CSVReader;
import org.example.controller.EmployeePairCalculator;
import org.example.model.EmployeePair;
import org.example.model.EmployeeProject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainApp extends Application {

    private final TableView<EmployeePair> table = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Employee Pair Finder");

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        final Button openButton = new Button("Open CSV File");
        openButton.setTooltip(new Tooltip("Open a CSV file from your computer"));
        openButton.setOnAction(e -> {
            final File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                readFileAndDisplayResult(file.getPath());
            }
        });

        final Button button1 = new Button("Load Example1.csv");
        button1.setTooltip(new Tooltip("This file is OK."));
        button1.setOnAction(e -> loadFileFromResources("Example1.csv"));

        final Button button2 = new Button("Load Example2.csv");
        button2.setTooltip(new Tooltip("This file has a problem value."));
        button2.setOnAction(e -> loadFileFromResources("Example2.csv"));

        final Button button3 = new Button("Load Example3.csv");
        button3.setTooltip(new Tooltip("This file contains data about users who worked on the same project at the same time."));
        button3.setOnAction(e -> loadFileFromResources("Example3.csv"));

        final Button button4 = new Button("Load Example4.csv");
        button4.setTooltip(new Tooltip("This file is missing."));
        button4.setOnAction(e -> loadFileFromResources("Example4.csv"));

        final TableColumn<EmployeePair, Integer> empId1Col = new TableColumn<>("Employee ID #1");
        empId1Col.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().empId1()).asObject());

        final TableColumn<EmployeePair, Integer> empId2Col = new TableColumn<>("Employee ID #2");
        empId2Col.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().empId2()).asObject());

        final TableColumn<EmployeePair, Integer> projectIdCol = new TableColumn<>("Project ID");
        projectIdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().projectId()).asObject());

        final TableColumn<EmployeePair, Long> daysWorkedCol = new TableColumn<>("Days Worked");
        daysWorkedCol.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().daysWorked()).asObject());

        table.getColumns().addAll(empId1Col, empId2Col, projectIdCol, daysWorkedCol);

        final HBox hbox = new HBox(10); // Add 10 pixel between buttons
        hbox.getChildren().addAll(openButton, button1, button2, button3, button4);

        final VBox vbox = new VBox(10);
        vbox.getChildren().addAll(hbox, table);

        final Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadFileFromResources(final String fileName) {
        try {
            // Get path to file in resources
            final String filePath = getClass().getResource("/" + fileName).getPath();
            readFileAndDisplayResult(filePath);
        } catch (final Exception ex) {
            showAlert("Unexpected Error", "An unexpected error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void readFileAndDisplayResult(final String filePath) {
        try {
            final List<EmployeeProject> projects = CSVReader.readCSV(filePath);
            final List<EmployeePair> pairs = EmployeePairCalculator.findLongestWorkingPairs(projects);
            checkForDisplay(pairs);
        } catch (final IOException ex) {
            showAlert("Error Reading File", "There was an error reading the file: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (final NumberFormatException ex) {
            showAlert("Error Parsing File", "There was an error parsing the file: " + ex.getMessage(), Alert.AlertType.ERROR);
        } catch (final Exception ex) {
            showAlert("Unexpected Error", "An unexpected error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void checkForDisplay(final List<EmployeePair> pairs) {
        if (pairs.isEmpty()) {
            showAlert("Information Message", "No workers found working on the same project at the same time.", Alert.AlertType.INFORMATION);
        } else {
            displayPairs(pairs);
        }
    }

    private void displayPairs(final List<EmployeePair> pairs) {
        table.getItems().clear();
        table.getItems().addAll(pairs);
        table.refresh();
    }

    private void showAlert(final String title, final String message, final Alert.AlertType type) {
        final Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}