package com.example.chess;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.List;

public class ChessPiece extends ImageView  {
    private final String type;
    private final String color;
    private static GridPane gridPane;
    private static StackPane fromStack;
    private static int prevHigh = -1;
    private static boolean isPressed = false;
    private static int currentX = -1; private static int currentY = -1;
    private static int fromX = -1; private static int fromY = -1;

    private static boolean isValidIndex = false;
    private static boolean isCapture = false;
    private static boolean isCastle = false;

    private final boolean hasMoved;
    public ChessPiece(String type, String color, boolean hasMoved) {

        this.type = type;
        this.color = color;
        this.hasMoved = hasMoved;

        String filePath =  type + "_" + color +".png";
        Image image = new Image(filePath);

        setPreserveRatio(true);

        double cell = ChessBoard.CELL_SIZE;

        setFitWidth(cell);
        setFitHeight(cell);
        setImage(image);

        setOnMousePressed(event -> {
            fromStack = (StackPane) this.getParent();
            fromX = (GridPane.getColumnIndex(fromStack));
            fromY = (GridPane.getRowIndex(fromStack));

            gridPane = (GridPane) fromStack.getParent();

            List<Integer[]> em = new ArrayList<>();
            Integer[] em_1 = {-1,-1};
            ChessClick.setClick(fromY, fromX, fromStack, em, em_1);

            if (this.getParent() != ChessBoard.pane) {
                double colS_2 = ((GridPane.getColumnIndex(fromStack)) * ChessBoard.CELL_SIZE) + ((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2);
                double rowS_2 = ((GridPane.getRowIndex(fromStack)) * ChessBoard.CELL_SIZE) + ((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2);

                fromStack.getChildren().remove(0);
                Pane mainPane = ChessBoard.pane;
                mainPane.getChildren().add(this);

                setX(colS_2);
                setY(rowS_2);
            }
            isPressed = true;
        });

        setOnMouseDragged(event -> {
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();

                setX(mouseX - getFitWidth()/2);
                setY(mouseY - getFitHeight()/2);

                currentX = (int) Math.min(Math.floor((Math.min((Math.max((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2, getX())),
                        ChessBoard.BOARD_SIZE + (ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2))
                        / ChessBoard.CELL_SIZE) - 1, 7);

                currentY = (int) Math.min(Math.floor((Math.min((Math.max((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2, getY())),
                        ChessBoard.BOARD_SIZE + (ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2))
                        / ChessBoard.CELL_SIZE) - 1, 7);

                StackPane stack = (StackPane) gridPane.getChildren().get(currentY * 8 + currentX+1);

                if (prevHigh != currentY * 8 + currentX+1 && prevHigh != -1){
                    StackPane sta = (StackPane) gridPane.getChildren().get(prevHigh);
                    sta.setStyle("-fx-border-color: transparent;-fx-border-width: 0px;-fx-background-color:" + ChessClick.getSquareColor((prevHigh - 1) / 8,  (prevHigh - 1) % 8));
                }

                if (stack.getChildren().size() == 0){isValidIndex = false;}
                for (Node e : stack.getChildren()) {
                    if (e instanceof VBox) {
                        stack.setStyle("-fx-border-color: transparent; -fx-border-width: 0.0; -fx-background-color: rgba(0, 0, 255, 0.5);");
                        prevHigh = currentY * 8 + currentX + 1;
                        isValidIndex = true;
                    }
                }
                if (stack.getId() != null){
                    if (stack.getId().equals("capture")) {
                        stack.setStyle("-fx-border-color: transparent; -fx-border-width: 0.0; -fx-background-color: rgba(255,200,16,0.5);");
                        prevHigh = currentY * 8 + currentX + 1;
                        isValidIndex = true;
                        isCapture = true;
                    }
                    if (stack.getId().equals("castle")) {
                        stack.setStyle("-fx-border-color: transparent; -fx-border-width: 0.0; -fx-background-color: rgba(182,0,253,0.5);");
                        prevHigh = currentY * 8 + currentX + 1;
                        isValidIndex = true;
                        isCapture = false;
                        isCastle = true;
                    }
                }

        });

        setOnMouseReleased(event -> {
            if (isPressed)
            {
                if (currentX != -1 && currentY != -1){
                    if (isValidIndex) {
                        StackPane toPane = (StackPane) gridPane.getChildren().get(currentY * 8 + currentX + 1);
                        fromStack.getChildren().add(this);
                        System.out.println("Castle: " + isCastle);
                        ChessClick.movePieceCapture(fromY, fromX, fromStack, toPane, "white", type, isCapture, isCastle);
                    }
                    else {
                        StackPane toPane = (StackPane) gridPane.getChildren().get(fromY * 8 + fromX + 1);
                        fromStack.getChildren().add(this);
                    }
                }
                setX(0);
                setY(0);
                prevHigh = -1;
                isValidIndex = false;
            }
        });
    }
    public String getColor() {

        return this.color;
    }

    public String getType() {

        return type;
    }

    public boolean hasMoved() {

        return this.hasMoved;
    }
}



