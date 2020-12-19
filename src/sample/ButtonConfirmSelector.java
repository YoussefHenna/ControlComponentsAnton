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
    private String unselectedButtonStyle = "";
    private String selectedButtonStyle = "";
    private String confirmTextStyle = "";
    private final ArrayList<StackPane> buttonStacks;
    private final ArrayList<Button> buttons;


    /**
     * Horizontal list of buttons, with a confirm click befor action
     *
     * @param width           width of the container
     * @param height          max height of container, to be used by selected item
     * @param buttonValues    list of values for buttons
     * @param onButtonConfirm on button confirm clicked
     */
    public ButtonConfirmSelector(int width, int height, ArrayList<String> buttonValues, OnButtonConfirm onButtonConfirm) {
        this.onButtonConfirm = onButtonConfirm;
        this.buttonValues = buttonValues;
        this.buttonStacks = new ArrayList<>();
        this.buttons = new ArrayList<>();
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
     *
     * @param buttonValues list of values for buttons
     */
    public void setButtonValues(ArrayList<String> buttonValues) {
        this.buttonValues = buttonValues;
        redraw();
    }

    /**
     * Sets action on confirm click
     *
     * @param onButtonConfirm on confirm click
     */
    public void setOnButtonConfirm(OnButtonConfirm onButtonConfirm) {
        this.onButtonConfirm = onButtonConfirm;
        redraw();
    }

    /**
     * Sets style of confirm text
     * @param confirmTextStyle style of confirm text in css
     */
    public void setConfirmTextStyle(String confirmTextStyle) {
        this.confirmTextStyle = confirmTextStyle;
        redraw();
    }

    /**
     * Sets style of selected button
     * @param selectedButtonStyle style of selected button in css
     */
    public void setSelectedButtonStyle(String selectedButtonStyle) {
        this.selectedButtonStyle = selectedButtonStyle;
        redraw();
    }

    /**
     * Sets style of unselected button
     * @param unselectedButtonStyle style of unselected button in css
     */
    public void setUnselectedButtonStyle(String unselectedButtonStyle) {
        this.unselectedButtonStyle = unselectedButtonStyle;
        redraw();
    }

    //Redraws picker -  removes all children then calls initUI
    private void redraw() {
        getChildren().clear();
        buttons.clear();
        buttonStacks.clear();
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
        int btnWidthNormal = (int) ((getPrefWidth()) / buttonValues.size());
        int btnDiff = 16; //must be divisible by 2
        int btnWidthBig = btnWidthNormal + btnDiff;
        int btnWidthSmall = btnWidthNormal - (btnDiff / 2);

        int selectedIndex = currentSelected == null ? -1 : buttonValues.indexOf(currentSelected);
        for (String s : buttonValues) {
            StackPane btnStack = new StackPane();
            btnStack.setAlignment(Pos.BOTTOM_CENTER);
            btnStack.setPrefHeight(getPrefHeight());
            btnStack.setMaxHeight(getPrefHeight());
            Button btn = new Button(s);
            btnStack.getChildren().add(btn);
            buttonStacks.add(btnStack);
            buttons.add(btn);

            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (currentSelected != null) {
                        currentSelected = null;
                    } else {
                        currentSelected = s;
                    }
                    redrawUIOnly();
                }
            });

            if (currentSelected == null || !currentSelected.equals(s)) {
                btn.setPrefWidth(btnWidthNormal);
                btn.setMaxWidth(btnWidthNormal);
                btn.setMaxHeight(getPrefHeight() - 20);
                btn.setPrefHeight(getPrefHeight() - 20);
                btn.setStyle(
                        "-fx-background-color:" + bgColor + ";" +
                                "-fx-border-color: black;" + "-fx-font-size: 16;" + unselectedButtonStyle);

                boolean shouldResize;
                int currentIndex = buttonValues.indexOf(s);
                //selected is first, should resize 2 after it
                if (selectedIndex == 0) {
                    shouldResize = currentIndex == selectedIndex + 1 || currentIndex == selectedIndex + 2;
                }
                //selected is last, should resize 2 before it
                else if (selectedIndex == buttonValues.size() - 1) {
                    shouldResize = currentIndex == selectedIndex - 1 || currentIndex == selectedIndex - 2;
                }
                //selected is in the middle, should resize 1 before it and 1 after
                else {
                    shouldResize = currentIndex == selectedIndex + 1 || currentIndex == selectedIndex - 1;
                }
                if (selectedIndex != -1 && shouldResize) {
                    btn.setPrefWidth(btnWidthSmall);
                    btn.setMaxWidth(btnWidthSmall);

                    //current is 1 after selected, and selected is the first element
                    if (currentIndex == selectedIndex + 1 && selectedIndex == 0) {
                        btn.setPadding(new Insets(0, btnDiff * 1.5, 0, 0));
                    }
                    //current is 1 before selected, and selected is the last element
                    else if (currentIndex == selectedIndex - 1 && selectedIndex == buttonValues.size() - 1) {
                        btn.setPadding(new Insets(0, 0, 0, btnDiff * 1.5));
                    }
                    //current is 1 after selected, and selected is in the middle
                    else if (currentIndex == selectedIndex + 1) {
                        btn.setPadding(new Insets(0, btnDiff / 2, 0, 0));
                    }
                    //current is 1 before selected, and selected is in the middle
                    else if (currentIndex == selectedIndex - 1) {
                        btn.setPadding(new Insets(0, 0, 0, btnDiff / 2));
                    }
                    //current is 2 after selected
                    else if (currentIndex == selectedIndex + 2) {
                        btn.setPadding(new Insets(0, btnDiff / 2, 0, 0));
                    }
                    //current is 2 before selected
                    else if (currentIndex == selectedIndex - 2) {
                        btn.setPadding(new Insets(0, 0, 0, btnDiff / 2));
                    }
                }

            } else {
                btn.setPrefWidth(btnWidthBig);
                btn.setMaxWidth(btnWidthBig);
                btn.setMaxHeight(getPrefHeight());
                btn.setPrefHeight(getPrefHeight());
                btn.setStyle(
                        "-fx-background-color:" + bgColor + ";" +
                                "-fx-border-color: black;" + "-fx-font-size: 20;" + "-fx-border-width: 3;-fx-font-weight: bold;" + selectedButtonStyle);
                Text confirmText = new Text("Confirm");
                confirmText.setStyle("-fx-font-size: 12;" + confirmTextStyle);
                StackPane.setMargin(confirmText, new Insets(0, 0, 6, 0));
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

    //Redraws ui, without removing existing elements. Just alters them.
    private void redrawUIOnly(){
        int btnWidthNormal = (int) ((getPrefWidth()) / buttonValues.size());
        int btnDiff = 16; //must be divisible by 2
        int btnWidthBig = btnWidthNormal + btnDiff;
        int btnWidthSmall = btnWidthNormal - (btnDiff / 2);

        int selectedIndex = currentSelected == null ? -1 : buttonValues.indexOf(currentSelected);
        for (int i = 0; i < buttonValues.size(); i++) {
            String s = buttonValues.get(i);
            StackPane btnStack = buttonStacks.get(i);

            btnStack.getChildren().clear();//clear the stack to remove confirm text, to add later in correct button
            Button btn = buttons.get(i);
            btn.setPadding(new Insets(0));
            btnStack.getChildren().add(btn);

            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (currentSelected != null) {
                        currentSelected = null;
                    } else {
                        currentSelected = s;
                    }
                    redrawUIOnly();
                }
            });

            //Will only execute if button wasn't already in the correct state
            if (((currentSelected == null || !currentSelected.equals(s))) && btn.getPrefWidth() != btnWidthNormal) {
                btn.setPrefWidth(btnWidthNormal);
                btn.setMaxWidth(btnWidthNormal);
                btn.setMaxHeight(getPrefHeight() - 20);
                btn.setPrefHeight(getPrefHeight() - 20);
                btn.setStyle(
                        "-fx-background-color:" + bgColor + ";" +
                                "-fx-border-color: black;" + "-fx-font-size: 16;" + unselectedButtonStyle);

            } else if(btn.getPrefWidth() != btnWidthBig && currentSelected != null && currentSelected.equals(s)) {
                btn.setPrefWidth(btnWidthBig);
                btn.setMaxWidth(btnWidthBig);
                btn.setMaxHeight(getPrefHeight());
                btn.setPrefHeight(getPrefHeight());
                btn.setStyle(
                        "-fx-background-color:" + bgColor + ";" +
                                "-fx-border-color: black;" + "-fx-font-size: 20;" + "-fx-border-width: 3;-fx-font-weight: bold;" + selectedButtonStyle);
                Text confirmText = new Text("Confirm");
                confirmText.setStyle("-fx-font-size: 12;" + confirmTextStyle);
                StackPane.setMargin(confirmText, new Insets(0, 0, 6, 0));
                btnStack.getChildren().add(confirmText);

                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        onButtonConfirm.onButtonConfirm(s);
                    }
                });

            }

            //Resizing done every time
            if(currentSelected == null || !currentSelected.equals(s)) {
                boolean shouldResize;
                int currentIndex = buttonValues.indexOf(s);
                //selected is first, should resize 2 after it
                if (selectedIndex == 0) {
                    shouldResize = currentIndex == selectedIndex + 1 || currentIndex == selectedIndex + 2;
                }
                //selected is last, should resize 2 before it
                else if (selectedIndex == buttonValues.size() - 1) {
                    shouldResize = currentIndex == selectedIndex - 1 || currentIndex == selectedIndex - 2;
                }
                //selected is in the middle, should resize 1 before it and 1 after
                else {
                    shouldResize = currentIndex == selectedIndex + 1 || currentIndex == selectedIndex - 1;
                }
                if (selectedIndex != -1 && shouldResize) {
                    btn.setPrefWidth(btnWidthSmall);
                    btn.setMaxWidth(btnWidthSmall);

                    //current is 1 after selected, and selected is the first element
                    if (currentIndex == selectedIndex + 1 && selectedIndex == 0) {
                        btn.setPadding(new Insets(0, btnDiff * 1.5, 0, 0));
                    }
                    //current is 1 before selected, and selected is the last element
                    else if (currentIndex == selectedIndex - 1 && selectedIndex == buttonValues.size() - 1) {
                        btn.setPadding(new Insets(0, 0, 0, btnDiff * 1.5));
                    }
                    //current is 1 after selected, and selected is in the middle
                    else if (currentIndex == selectedIndex + 1) {
                        btn.setPadding(new Insets(0, btnDiff / 2, 0, 0));
                    }
                    //current is 1 before selected, and selected is in the middle
                    else if (currentIndex == selectedIndex - 1) {
                        btn.setPadding(new Insets(0, 0, 0, btnDiff / 2));
                    }
                    //current is 2 after selected
                    else if (currentIndex == selectedIndex + 2) {
                        btn.setPadding(new Insets(0, btnDiff / 2, 0, 0));
                    }
                    //current is 2 before selected
                    else if (currentIndex == selectedIndex - 2) {
                        btn.setPadding(new Insets(0, 0, 0, btnDiff / 2));
                    }
                }
            }

        }
    }
}
