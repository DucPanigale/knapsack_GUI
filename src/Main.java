import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

//import org.controlsfx.control.textfield.TextFields;


public class Main extends Application {

    // testArray because real data was not given from other team
    int knapsackArray[] = {1,12,17};
    int lastKnapsackItem = -1;
    int knapsackPosition = 1;

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
                                start(scene);
                            }
                        });
                        Button stepButton = (Button) scene.lookup("#stepButton");

                        stepButton.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                // todo: fill step button
                                step(scene);
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


    public void executePressed(Scene scene, String input, TextField textField) {

        if (input.equals("start")){
            start(scene);
            System.out.println("Started");
            // clear textfield
            textField.setText("");
            addCommandToHistory(input, scene);
        }
        else if(input.equals("step")){
            step(scene);
            System.out.println("Step");
            // clear textfield
            textField.setText("");
            addCommandToHistory(input, scene);
        }

        else {
            addCommandToHistory("input is no command", scene);
        }
    }


    private void step(Scene scene){

        // disables available items if they exist in the knapsack array
        for (int i=0;i<knapsackArray.length;i++) {
            // works
            if((lastKnapsackItem == knapsackArray[0])||(knapsackArray[i] == lastKnapsackItem) && i!=knapsackArray.length-1) {
                TextField textField = (TextField) scene.lookup("#ai" + knapsackArray[i+1] + "TextField");
                textField.disableProperty().setValue(true);
                // fill knapsack fields
                TextField knapsackField = (TextField) scene.lookup("#k" + knapsackPosition + "TextField");
                knapsackField.textProperty().setValue(Integer.toString(knapsackArray[i+1]));
                lastKnapsackItem = knapsackArray[i+1];
                knapsackPosition++;
                break;
            }
        }
    }

    // works
    private void start(Scene scene){
        knapsackPosition = 1;

        // enable all fields
        for (int i=1;i<=150;i++){
            TextField textField = (TextField) scene.lookup("#ai"+i+"TextField");
            textField.setDisable(false);
        }
        for (int i=1;i<=75;i++){
            // empty knapsack fields
            TextField knapsackField = (TextField) scene.lookup("#k"+i+"TextField");
            knapsackField.textProperty().setValue("");
        }

        // set first value
        TextField textField = (TextField) scene.lookup("#ai"+knapsackArray[0]+"TextField");
        textField.disableProperty().setValue(true);
        TextField knapsackField = (TextField) scene.lookup("#k"+knapsackArray[0]+"TextField");
        knapsackField.textProperty().setValue(Integer.toString(knapsackArray[0]));
        lastKnapsackItem = knapsackArray[0];
        knapsackPosition++;
    }

    private void addCommandToHistory(String input, Scene scene) {
        TextArea commandTextArea = (TextArea) scene.lookup("#commandTextArea");
        commandTextArea.appendText(commandTextArea.getText().isEmpty() ? input : "\n" + input);
    }

    // https://openjfx.io/openjfx-docs/
    public static void main(String[] args) {
        launch(args);
    }
}


