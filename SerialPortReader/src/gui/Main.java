package gui;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import portLogic.PortHandler;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        PortHandler handler = new PortHandler(getAllLabels(root));
        handler.start();

        stage.setOnCloseRequest((event) -> {
            event.consume();
            shutDownSystem(handler);
        });
    }

    /**
     * Used to access all labels that are going to be updated with port data.
     *
     * @param root element of the application
     * @return ArrayList of Labels with id starting with underscore
     */
    private ArrayList<Label> getAllLabels(Parent root) {
        ArrayList<Label> nodes = new ArrayList<>();
        for (Node tabPane : root.getChildrenUnmodifiable()) {
            if (tabPane instanceof TabPane) {
                for (Tab t : ((TabPane) tabPane).getTabs()) {
                    for (Node node : ((Pane) t.getContent()).getChildren()) {
                        if (node instanceof Label && node.getId() != null && node.getId().startsWith("_")) {
                            nodes.add((Label) node);
                        } else if (node instanceof GridPane) {
                            for (Node label : ((Pane) node).getChildren()) {
                                if (label instanceof Label && node.getId() != null && node.getId().startsWith("_")) {
                                    nodes.add((Label) node);

                                }
                            }
                        }
                    }
                }
            }
        }

        return nodes;

    }

    private void shutDownSystem(PortHandler handler) {
        handler.close();
        Platform.exit();
        System.exit(0);
    }

}
