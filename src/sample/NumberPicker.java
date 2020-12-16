package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.helper.FlingPane;
import sample.helper.OnScrollDone;


public class NumberPicker extends Region {
    private int minValue = 0;
    private int maxValue = 150;
    private int increment = 10;
    private int currentValue = 0;


    public NumberPicker(int width) {
        initSize(width);
        initUI();
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        this.currentValue = minValue;
        redraw();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        redraw();
    }

    public void setIncrement(int increment) {
        this.increment = increment;
        redraw();
    }

    private void redraw() {
        getChildren().remove(0, getChildren().size() - 1);
        initUI();
    }

    private void initSize(int width) {
        double height = width * 0.5;
        setPrefWidth(width);
        setPrefHeight(height);
        setMaxWidth(width);
        setMaxHeight(height);
    }

    private void initUI() {
        VBox mainContent = new VBox(30);
        for (int i = minValue; i <= maxValue; i += increment) {
            Label text = new Label(String.valueOf(i));
            text.setStyle("-fx-font-size: 22;-fx-background-color:red;");
            text.setPrefWidth(getPrefWidth());
            text.setPrefHeight(30);
            text.setAlignment(Pos.CENTER);
            mainContent.getChildren().add(text);
        }
        mainContent.setTranslateY(getPrefHeight()/2 - 15);


        FlingPane mainScrollable = new FlingPane(getPrefHeight()/2 - 15, current -> {
            //System.out.println(current*increment);
        });
        mainScrollable.setFlingDirection(FlingPane.FlingDirection.VERTICAL);
        mainScrollable.setContent(mainContent);
        mainScrollable.setMaxHeight(getPrefHeight());
        mainScrollable.setPrefWidth(getPrefWidth());
        mainScrollable.setStyle("-fx-background-color:blue;");

        StackPane stack = new StackPane();
        stack.setMaxHeight(getPrefHeight());
        stack.setPrefWidth(getPrefWidth());
        stack.getChildren().add(mainScrollable);
        VBox dividers = new VBox(50);
        Rectangle divider = new Rectangle(100,3, Color.BLACK);
        Rectangle divider2 = new Rectangle(100,3, Color.BLACK);
        dividers.getChildren().add(divider);
        dividers.getChildren().add(divider2);
        dividers.setMouseTransparent(true);
        dividers.setAlignment(Pos.CENTER);
        stack.getChildren().add(dividers);
        getChildren().add(stack);


    }
//85  28 -30 -94

}
