package com.example.chess;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class ChessPiece extends ImageView  {
    private final String type;
    private final String color;

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
