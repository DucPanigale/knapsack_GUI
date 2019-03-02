import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.controlsfx.control.textfield.TextFields;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class Main extends Application {

/*    @FXML
    private TextField commandInput;
    @FXML
    private TextArea commandHistory;*/

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
        label.setOnDragOver(new EventHandler<DragEvent>() {
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

        // lessons learned: NOT working with a stackpane, nothing was clickable
        AnchorPane root = new AnchorPane();

        // load scene
        root.getChildren().setAll(parent);
        // add 'drag and drop'
        root.getChildren().add(dragTarget);

        Scene scene = new Scene(root, 800.0, 770.0);
        primaryStage.setResizable(false);
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


                        Button startButton = (Button) scene.lookup("#startButton");
                        // todo: fill start button
                        startButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {

                            }
                        });
                        Button stepButton = (Button) scene.lookup("#stepButton");

                        stepButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                // todo: fill step button
                            }
                        });

                        TextField commandTextField = (TextField) scene.lookup("#commandTextField");
                        commandTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                            @Override
                            public void handle(KeyEvent keyEvent) {
                                if (keyEvent.getCode() == KeyCode.ENTER)  {
                                    String text = commandTextField.getText();
                                    executePressed(scene,text,commandTextField);
                                }
                            }
                        });

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

                        // elapsed time
                        Label timerLabel = (Label) scene.lookup("#runtimeLabel");
                        long startTime = System.currentTimeMillis();
                        new AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                long elapsedMillis = System.currentTimeMillis() - startTime ;
                                long seconds = elapsedMillis / 1000;
                                long minutes = seconds/60;
                                seconds = seconds%60;

                                String min = Long.toString(minutes);
                                String sec = Long.toString(seconds);
                                if (seconds<10){
                                    sec = "0" +Long.toString(seconds);
                                }
                                if (minutes<10){
                                    min = "0" + Long.toString(minutes);
                                }

                                timerLabel.setText(min + ":" + sec);
                            }
                        }.start();


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

            // convert Object to JSONObject
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
    }


    // todo: for commandTextField
    public void executePressed(Scene scene, String input, TextField textField) {

            if (input.equals("start")){
                // todo: act on input
                System.out.println("Started");
                // clear textfield
                textField.setText("");
                addCommandToHistory(input, scene);
            }
            else if(input.equals("step")){
                // todo: act on input
                System.out.println("Step");
                // clear textfield
                textField.setText("");
                addCommandToHistory(input, scene);
            }

            else {
                addCommandToHistory("input is no command", scene);
            }
    }


    private void addCommandToHistory(String input, Scene scene) {
        TextArea commandTextArea = (TextArea) scene.lookup("#commandTextArea");
        commandTextArea.appendText(commandTextArea.getText().isEmpty() ? input : "\n" + input);
    }

    // todo: https://openjfx.io/openjfx-docs/
    public static void main(String[] args) {
        launch(args);
    }
}


