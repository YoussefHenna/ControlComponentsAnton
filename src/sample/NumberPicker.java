package sample;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import sample.helper.FlingPane;


public class NumberPicker extends Region {
    private int minValue = 0;
    private int maxValue = 150;
    private int increment = 10;
    private SimpleIntegerProperty currentValue;
    private String bgColor = "#ffffff";


    /**
     * Number picker for values from 0 - 150 with an increment of 10. Can be changed using appropriate methods
     * @param width width of component, height will be adjusted to half the width
     */
    public NumberPicker(int width) {
        initSize(width);
        initUI();
        currentValue = new SimpleIntegerProperty(0);
        setStyle("-fx-background-color:"+bgColor);
    }

    /**
     * Set the minimum value for the number picker
     * @param minValue minimum value available for selection
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
        currentValue = new SimpleIntegerProperty(minValue);
        redraw();
    }

    /**
     * Set the maximum value for the number picker
     * @param maxValue maximum value available for selection
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        redraw();
    }

    /**
     * Set the increment used to reach the values from min - max
     * @param increment value for increment
     */
    public void setIncrement(int increment) {
        this.increment = increment;
        redraw();
    }

    /**
     * Gets the current selected value
     * @return the currently selected value
     */
    public int getValue() {
        return currentValue.getValue();
    }

    /**
     * Gets an observable to the selected value, changes when value changes
     * @return an observable to the value
     */
    public ObservableValue<Integer> getValueObservable(){
        return currentValue.asObject();
    }

    /**
     * sets background color of number picker, important to maintain fade effect on numbers
     * @param bgColor color in hex code including # key
     */
    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
        setStyle("-fx-background-color:"+bgColor);
        redraw();
    }

    //Redraws picker -  removes all children then calls initUI
    private void redraw() {
        getChildren().clear();
        initUI();
    }

    //Initializes sizes for the picker
    private void initSize(int width) {
        double height = width * 0.5;
        setPrefWidth(width);
        setPrefHeight(height);
        setMaxWidth(width);
        setMaxHeight(height);
    }

    //Initializes UI items and adds them
    private void initUI() {
        int itemHeight = 40;
        VBox mainContent = new VBox(itemHeight);
        for (int i = minValue; i <= maxValue + increment; i += increment) {
            Label text = new Label(String.valueOf(i));
            text.setStyle("-fx-font-size: 22;");
            text.setPrefWidth(getPrefWidth());
            text.setPrefHeight(itemHeight);
            text.setAlignment(Pos.CENTER);
            mainContent.getChildren().add(text);
            if (i == maxValue + increment) {
                text.setVisible(false);
            }
        }
        double initialOffset = getPrefHeight() / 2 - (itemHeight / 2.0);
        mainContent.setTranslateY(initialOffset);


        FlingPane mainScrollable = new FlingPane(initialOffset, itemHeight, current -> {
            int selectedValue = current == 0 ? minValue : (int) (current * increment);
            if(selectedValue > maxValue){
                selectedValue = maxValue;
            }
           currentValue.set(selectedValue);
        });
        mainScrollable.setFlingDirection(FlingPane.FlingDirection.VERTICAL);
        mainScrollable.setContent(mainContent);
        mainScrollable.setMaxHeight(getPrefHeight());
        mainScrollable.setPrefWidth(getPrefWidth());

        StackPane stack = new StackPane();
        stack.setMaxHeight(getPrefHeight());
        stack.setPrefWidth(getPrefWidth());
        stack.getChildren().add(mainScrollable);
        VBox dividers = new VBox(50);
        Rectangle divider = new Rectangle(100, 3, Color.BLACK);
        Rectangle divider2 = new Rectangle(100, 3, Color.BLACK);
        dividers.getChildren().add(divider);
        dividers.getChildren().add(divider2);
        dividers.setMouseTransparent(true);
        dividers.setAlignment(Pos.CENTER);
        stack.getChildren().add(dividers);


        Stop[] stops1 = new Stop[] { new Stop(0, Color.valueOf(bgColor)), new Stop(1, Color.TRANSPARENT)};
        LinearGradient lg1 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops1);

        Stop[] stops2 = new Stop[] { new Stop(0, Color.TRANSPARENT), new Stop(1, Color.valueOf(bgColor))};
        LinearGradient lg2 = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops2);

        VBox fadeRects = new VBox(getPrefHeight()/3);
        fadeRects.setMouseTransparent(true);
        Rectangle fadeUp = new Rectangle(getPrefWidth(),getPrefHeight()/3);
        Rectangle fadeDown = new Rectangle(getPrefWidth(),getPrefHeight()/3);
        fadeUp.setFill(lg1);
        fadeDown.setFill(lg2);
        fadeRects.getChildren().add(fadeUp);
        fadeRects.getChildren().add(fadeDown);
        stack.getChildren().add(fadeRects);
        getChildren().add(stack);
    }

}
