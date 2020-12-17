package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import sample.helper.FlingPane;
import sample.helper.OnButtonConfirm;

import java.util.ArrayList;

public class ButtonConfirmSelector extends Region {
    private String bgColor = "#ffffff";
    private ArrayList<String> buttonValues;
    private OnButtonConfirm onButtonConfirm;
    private String currentSelected;


    /**
     * Horizontal list of buttons, with a confirm click befor action
     * @param width width of the container
     * @param height max height of container, to be used by selected item
     * @param buttonValues list of values for buttons
     * @param onButtonConfirm on button confirm clicked
     */
    public ButtonConfirmSelector(int width, int height, ArrayList<String> buttonValues, OnButtonConfirm onButtonConfirm) {
        this.onButtonConfirm = onButtonConfirm;
        this.buttonValues = buttonValues;
        initSize(width, height);
        initUI();
        setStyle("-fx-background-color:" + bgColor);
    }


    /**
     * sets background color of selector
     *
     * @param bgColor color in hex code including # key
     */
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
        setStyle("-fx-background-color:" + bgColor);
        redraw();
    }

    /**
     * Sets values for buttons
     * @param buttonValues list of values for buttons
     */
    public void setButtonValues(ArrayList<String> buttonValues) {
        this.buttonValues = buttonValues;
    }

    /**
     * Sets action on confirm click
     * @param onButtonConfirm on confirm click
     */
    public void setOnButtonConfirm(OnButtonConfirm onButtonConfirm) {
        this.onButtonConfirm = onButtonConfirm;
    }

    //Redraws picker -  removes all children then calls initUI
    private void redraw() {
        getChildren().clear();
        initUI();
    }

    //Initializes sizes for the picker
    private void initSize(int width, int height) {
        setPrefWidth(width);
        setPrefHeight(height);
        setMaxWidth(width);
        setMaxHeight(height);
    }

    //Initializes UI items and adds them
    private void initUI() {
        HBox mainContent = new HBox(2);
        mainContent.setAlignment(Pos.CENTER);
        int btnWidthNormal = (int) ((getPrefWidth() - 20) / buttonValues.size());
        int btnWidthBig = btnWidthNormal + 20;

        for (String s : buttonValues) {
            StackPane btnStack = new StackPane();
            btnStack.setAlignment(Pos.BOTTOM_CENTER);
            Button btn = new Button(s);
            btnStack.getChildren().add(btn);

            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentSelected = s;
                    redraw();
                }
            });

            if (currentSelected == null || !currentSelected.equals(s)) {
                btn.setPrefWidth(btnWidthNormal);
                btn.setMaxWidth(btnWidthNormal);
                btn.setMaxHeight(getPrefHeight() - 20);
                btn.setPrefHeight(getPrefHeight() - 20);
                btn.setStyle(
                        "-fx-background-color:" + bgColor + ";" +
                                "-fx-border-color: black;" + "-fx-font-size: 16;");

            } else {
                btn.setPrefWidth(btnWidthBig);
                btn.setMaxWidth(btnWidthBig);
                btn.setMaxHeight(getPrefHeight());
                btn.setPrefHeight(getPrefHeight());
                btn.setStyle(
                        "-fx-background-color:" + bgColor + ";" +
                                "-fx-border-color: black;" + "-fx-font-size: 20;" + "-fx-border-width: 3;-fx-font-weight: bold;");
                Text confirmText = new Text("Confirm");
                confirmText.setStyle("-fx-font-size: 12;");
                StackPane.setMargin(confirmText,new Insets(0,0,6,0));
                btnStack.getChildren().add(confirmText);

                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        onButtonConfirm.onButtonConfirm(s);
                    }
                });

            }

            mainContent.getChildren().add(btnStack);
        }

        getChildren().add(mainContent);
    }
}
