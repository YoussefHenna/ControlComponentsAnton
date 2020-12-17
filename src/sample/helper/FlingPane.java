package sample.helper;

import java.util.concurrent.atomic.AtomicReference;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Fling pane is a scrollable list container like
 * you have it on mobile devices
 *
 * @author Tom Schindl
 */
//Slightly modified to fit with NumberPicker



public class FlingPane extends Region {
    private ObjectProperty<Node> content;
    private Rectangle clipRect = new Rectangle();

    private ObjectProperty<FlingDirection> flingDirection = new SimpleObjectProperty<FlingDirection>(FlingDirection.BOTH, "flingDirection");

    private VelocityTracker tracker = new VelocityTracker();

    public enum FlingDirection {
        BOTH, HORIZONTAL, VERTICAL
    }

    /**
     * Pane that simulates fling typically seen in mobile devices
     * @param offsetY initial offset of y, used to center number picker
     * @param itemHeight height of an item in number picker, should also match spacing between them
     * @param onScrollDone called when scrolling has finished and item has been selected
     */
    public FlingPane(double offsetY, double itemHeight, OnScrollDone onScrollDone) {
        setFocusTraversable(false);
        setClip(clipRect);
        contentProperty().addListener(new ChangeListener<Node>() {

            @Override
            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
                getChildren().clear();
                if (newValue != null) {
                    getChildren().add(newValue);
                }
                layout();
            }
        });

        final AtomicReference<MouseEvent> deltaEvent = new AtomicReference<MouseEvent>();
        final AtomicReference<TranslateTransition> currentTransition = new AtomicReference<TranslateTransition>();
        setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                tracker.addMovement(event);
                deltaEvent.set(event);
                if (currentTransition.get() != null) {
                    currentTransition.get().stop();
                }
            }
        });
        setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                tracker.addMovement(event);
                if (flingDirection.get() == FlingDirection.HORIZONTAL || flingDirection.get() == FlingDirection.BOTH) {
                    double delta = event.getX() - deltaEvent.get().getX();
                    content.get().setTranslateX(content.get().getTranslateX() + delta);
                }

                if (flingDirection.get() == FlingDirection.VERTICAL || flingDirection.get() == FlingDirection.BOTH) {
                    double delta = event.getY() - deltaEvent.get().getY();
                    double targetY = content.get().getTranslateY() + delta;

                    content.get().setTranslateY(targetY);
                }

                deltaEvent.set(event);
            }
        });
        setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                tracker.addMovement(event);
                tracker.computeCurrentVelocity(500);
                float velocityX = tracker.getXVelocity();
                float velocityY = tracker.getYVelocity();

                if (content != null && content.get() != null) {
                    TranslateTransition translate = new TranslateTransition(new Duration(600), content.get());
                    translate.setInterpolator(Interpolator.EASE_OUT);

                    final Bounds b = content.get().getLayoutBounds();

                    Double backBouncingX = null;
                    Double backBouncingY = null;
                    Double targetX = null;
                    Double targetY = null;

                    if (Math.abs(velocityX) < 10) {
                        velocityX = 0;
                    }

                    if (Math.abs(velocityY) < 10) {
                        velocityY = 0;
                    }

                    if (flingDirection.get() == FlingDirection.HORIZONTAL || flingDirection.get() == FlingDirection.BOTH) {
                        targetX = content.get().getTranslateX();

                        double controlWidth = b.getWidth();
                        double viewWith = getWidth();

                        if (controlWidth < viewWith && targetX < controlWidth) {
                            targetX = 0.0;
                        } else if (targetX > 0) {
                            targetX = 0.0;
                        } else if ((targetX < (controlWidth - viewWith) * -1)) {
                            targetX = (controlWidth - viewWith) * -1;
                        } else {
                            targetX += velocityX;

                            if (controlWidth < viewWith && targetX < controlWidth) {
                                targetX = -100.0;
                                backBouncingX = 0.0;
                            } else if (targetX > 0) {
                                targetX = 100.0;
                                backBouncingX = 0.0;
                            } else if (targetX < (controlWidth - viewWith) * -1) {
                                targetX = (controlWidth - viewWith) * -1 - 100;
                                backBouncingX = (controlWidth - viewWith) * -1;
                            }
                        }
                    }

                    if (flingDirection.get() == FlingDirection.VERTICAL || flingDirection.get() == FlingDirection.BOTH) {
                        targetY = content.get().getTranslateY();

                        double controlHeight = b.getHeight();
                        double viewHeight = getHeight();

                        if (controlHeight < viewHeight && targetY < controlHeight) {
                            targetY = offsetY;
                        } else if (targetY > offsetY) {
                            targetY = offsetY;
                        } else if (targetY < (controlHeight - viewHeight) * -1) {
                            targetY = (controlHeight - viewHeight) * -1;
                        } else {
                            targetY += velocityY;

                            if (controlHeight < viewHeight && targetY < controlHeight) {
                                targetY = -100.0;
                                backBouncingY = offsetY;
                            } else if (targetY > offsetY) {
                                targetY = 100.0;
                                backBouncingY = offsetY;
                            } else if (targetY < (controlHeight - viewHeight) * -1) {
                                targetY = (controlHeight - viewHeight) * -1 - 100;
                                backBouncingY = (controlHeight - viewHeight) * -1;
                            }
                        }
                    }

                    if (targetX != null) {
                        translate.setFromX(content.get().getTranslateX());
                        translate.setToX(targetX);
                    }

                    if (targetY != null) {
                        translate.setFromY(content.get().getTranslateY());
                        //Modifications for NumberPicker///////////////////////////////
                        /*
                        When user stop scrolling,
                        gets closest number and snaps to it.

                        Sends current selected index to number picker
                         */
                        long height = (long) (itemHeight*2L);//height of item*2 to account for padding between items
                        long closest = height * (Math.round(targetY / height)) +((long)offsetY-height);
                        translate.setToY(closest);
                        onScrollDone.onItemSelected(closest == height ? 0 : Math.abs(closest/80) + 1);
                    }

                    if (backBouncingX != null || backBouncingY != null) {
                        final Double fbackFlingX = backBouncingX;
                        final Double fbackFlingY = backBouncingY;
                        translate.setOnFinished(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                currentTransition.set(null);
                                TranslateTransition translate = new TranslateTransition(new Duration(300), content.get());
                                if (fbackFlingX != null) {
                                    translate.setFromX(content.get().getTranslateX());
                                    translate.setToX(fbackFlingX);
                                }

                                if (fbackFlingY != null) {
                                    translate.setFromY(content.get().getTranslateY());
                                    translate.setToY(fbackFlingY);
                                }

                                translate.play();
                                currentTransition.set(translate);
                            }
                        });
                    } else {
                        translate.setOnFinished(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                currentTransition.set(null);

                            }
                        });
                    }

                    translate.play();
                    currentTransition.set(translate);
                }
            }
        });
    }

    public final void setContent(Node value) {
        contentProperty().set(value);
    }

    public final Node getContent() {
        return content == null ? null : content.get();
    }

    public final ObjectProperty<Node> contentProperty() {
        if (content == null) {
            content = new SimpleObjectProperty<Node>(this, "content");
        }
        return content;
    }

    public final ObjectProperty<FlingDirection> flingDirection() {
        return flingDirection;
    }

    public void setFlingDirection(FlingDirection value) {
        flingDirection.set(value);
    }

    public FlingDirection getFlingDirection() {
        return flingDirection.get();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        clipRect.setWidth(getWidth());
        clipRect.setHeight(getHeight());
    }

//	@Override
//	protected double computeMaxHeight(double width) {
//		return Double.MAX_VALUE;
//	}
//
//	@Override
//	protected double computeMinWidth(double height) {
//		return 0;
//	}
//
//	@Override
//	protected double computeMaxWidth(double height) {
//		return Double.MAX_VALUE;
//	}
//
//	@Override
//	protected double computeMinHeight(double width) {
//		return 0;
//	}
//
//	@Override
//	protected double computePrefHeight(double width) {
//		return Double.MAX_VALUE;
//	}
//
//	@Override
//	protected double computePrefWidth(double height) {
//		return Double.MAX_VALUE;
//	}
}