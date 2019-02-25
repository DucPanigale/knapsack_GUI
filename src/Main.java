import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent parent = loader.load();

        Label label = new Label("Drag 'n Drop JSON here");
        // position of label
        label.setTranslateY(120);
        label.setTranslateX(450);
        label.setTextFill(Color.web("#FFFFFF"));

        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label, dropped);
        dragTarget.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragTarget
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });
        StackPane root = new StackPane();
        // load scene
        root.getChildren().setAll(parent);
        // add 'drag and drop'
        root.getChildren().add(dragTarget);


        Scene scene = new Scene(root, 800.0, 770.0);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Drag Test");
        primaryStage.setScene(scene);

        dragTarget.setOnDragDropped(new EventHandler<DragEvent>() {



            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    // open JSON fILE

                    try {
                        File file = new File(db.getFiles().toString());

                        JSONObject jsonObject = getJSONfile(db.getFiles().toString());
                        // fill combo boxes
                        ComboBox mutationComboBox = (ComboBox) scene.lookup("#mutationComboBox");
                        // works
                        mutationComboBox.setValue(jsonObject.get("mutation"));

                        // Lessons learned: needed to be right size for text to show
                        ComboBox crossoverComboBox = (ComboBox) scene.lookup("#crossoverComboBox");
                        crossoverComboBox.setValue(jsonObject.get("crossover"));

                        ComboBox crossoverRatioComboBox = (ComboBox) scene.lookup("#crossoverRatioComboBox");
                        crossoverRatioComboBox.setValue(jsonObject.get("crossoverRatio"));

                        ComboBox selectionComboBox = (ComboBox) scene.lookup("#selectionComboBox");
                        selectionComboBox.setValue(jsonObject.get("selection"));

                        ComboBox mutationRatioComboBox = (ComboBox) scene.lookup("#mutationRatioComboBox");
                        mutationRatioComboBox.setValue(jsonObject.get("mutationRatio"));

                        // set volume
                        ComboBox volumeComboBox = (ComboBox) scene.lookup("#volumeComboBox");
                        // is ok
                        volumeComboBox.setValue(822);
                        volumeComboBox.editableProperty().set(false);

                        // set name
                        Label nameLabel = (Label) scene.lookup("#nameLabel");
                        nameLabel.textProperty().setValue(jsonObject.get("name").toString());

                        // set max iterations
                        Label maxNumberLabel = (Label) scene.lookup("#maximumNumberOfIterationsLabel");
                        maxNumberLabel.textProperty().setValue(jsonObject.get("maximumNumberOfIterations").toString());

                        // todo: get knapsackArray
                        int knapsackArray[] = {1,12,17};

                        int knapsackPosition = 1;
                        // disables available items if they exist in the knapsack array
                        for (int value : knapsackArray){
                            TextField textField = (TextField) scene.lookup("#ai"+value+"TextField");
                            textField.disableProperty().setValue(true);
                            // fill knapsack fields
                            TextField knapsackField = (TextField) scene.lookup("#k"+knapsackPosition+"TextField");
                            knapsackField.textProperty().setValue(Integer.toString(value));
                            knapsackPosition++;
                        }

                    } catch (IOException e) {
                        System.out.println(e);
                    }

                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });



        primaryStage.show();
    }

    private JSONObject getJSONfile(String fileName) throws IOException {

        String newFileName = fileName.substring(1,fileName.length()-1);
        BufferedReader br = new BufferedReader(new FileReader(newFileName));
        String s = "";

        while ((s = br.readLine()) != null)
            System.out.println(s);

        JSONParser parser = new JSONParser();
        try
        {
            Object object = parser
                    .parse(new FileReader(newFileName));

            //convert Object to JSONObject
            JSONObject jsonObject = (JSONObject)object;

            return jsonObject;

        }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;


            //Printing the values
            //System.out.println("Mutation: " + mutation);
       // }

    }



    // todo: https://openjfx.io/openjfx-docs/
    public static void main(String[] args) {
        launch(args);
    }
}


