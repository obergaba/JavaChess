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
    private static List<Integer> highlighted = new ArrayList<>();

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

        setOnDragDetected(event -> {
            Dragboard db = startDragAndDrop(TransferMode.ANY);

            ClipboardContent content = new ClipboardContent();
            content.putString("drop");
            db.setContent(content);


            StackPane parent = (StackPane) this.getParent();
            int colS = (GridPane.getColumnIndex(parent));
            int rowS = (GridPane.getRowIndex(parent));

            gridPane = (GridPane) parent.getParent();

            List<Integer[]> em = new ArrayList<>();
            Integer[] em_1 = {-1,-1};
            ChessClick.setClick(rowS, colS, parent, em, em_1);

            startFullDrag();
            event.consume();
        });
        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = db.hasString();

            event.consume();

        });
        this.setOnDragOver(event -> {

            if (event.getGestureSource() == this && event.getDragboard().hasString()) {
                if (this.getParent() != ChessBoard.pane) {
                    StackPane parent = (StackPane) this.getParent();
                    double colS = ((GridPane.getColumnIndex(parent)) * ChessBoard.CELL_SIZE) + ((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2);
                    double rowS = ((GridPane.getRowIndex(parent)) * ChessBoard.CELL_SIZE) + ((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2);

                    parent.getChildren().remove(0);
                    Pane mainPane = ChessBoard.pane;
                    mainPane.getChildren().add(this);

                    setX(rowS);
                    setY(colS);
                }
                double mouseX = event.getSceneX();
                double mouseY = event.getSceneY();

                setX(mouseX - getFitWidth()/2);
                setY(mouseY - getFitHeight()/2);

                int posX = (int) Math.floor((Math.min((Math.max((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2, getX())),
                        ChessBoard.BOARD_SIZE + (ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2)) / ChessBoard.CELL_SIZE) - 1;

                int posY = (int) Math.floor((Math.min((Math.max((ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2, getY())),
                        ChessBoard.BOARD_SIZE + (ChessBoard.WINDOW_SIZE - ChessBoard.BOARD_SIZE)/2)) / ChessBoard.CELL_SIZE) - 1;

                StackPane stack = (StackPane) gridPane.getChildren().get(posY * 8 + posX+1);

                List<Integer> copy = highlighted;
                int count = 0;
                for (int index : copy) {
                    if (index != posY * 8 + posX + 1) {
                        StackPane sta = (StackPane) gridPane.getChildren().get(index);
                        sta.setStyle("-fx-border-color: transparent;-fx-border-width: 0px;-fx-background-color:" + ChessClick.getSquareColor((index - 1) / 8,  (index - 1) % 8));
                        highlighted.remove(count);
                    }
                    count += 1;
                }

                for (Node e : stack.getChildren()){
                    if (e instanceof VBox){
                        stack.setStyle("-fx-border-color: transparent; -fx-border-width: 0.0; -fx-background-color: rgba(0, 0, 255, 0.5);");
                        stack.setId("drag");
                        highlighted.add(posY * 8 + posX+1);
                    }
                }

                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
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



