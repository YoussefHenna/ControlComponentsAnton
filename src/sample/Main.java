package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        NumberPicker picker = new NumberPicker(400);
        vBox.getChildren().add(picker);
        primaryStage.setTitle("Testing App");
        primaryStage.setScene(new Scene(vBox, 600, 700));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
