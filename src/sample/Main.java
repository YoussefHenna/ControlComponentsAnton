package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import sample.helper.OnButtonConfirm;

import java.util.ArrayList;
import java.util.Arrays;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color:white");
        vBox.setAlignment(Pos.CENTER);

        NumberPicker picker = new NumberPicker(400);
        vBox.getChildren().add(picker);

        Rectangle divider = new Rectangle(400, 100);
        divider.setFill(Color.TRANSPARENT);
        vBox.getChildren().add(divider);

        ArrayList<String> btnValues = new ArrayList<>();
        btnValues.add("1");
        btnValues.add("2");
        btnValues.add("3");
        btnValues.add("4");
        btnValues.add("5");
        btnValues.add("6");
        btnValues.add("7");
        btnValues.add("8");
        ButtonConfirmSelector selector = new ButtonConfirmSelector(500, 90, btnValues, new OnButtonConfirm() {
            @Override
            public void onButtonConfirm(String btnValue) {
                System.out.println("Button confirmed with value " + btnValue);
            }
        });
        vBox.getChildren().add(selector);


        primaryStage.setTitle("Testing App");
        primaryStage.setScene(new Scene(vBox, 600, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
