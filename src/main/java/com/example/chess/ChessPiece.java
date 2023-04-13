package com.example.chess;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class ChessPiece extends ImageView  {
    private final String type;
    private final String color;
    public ChessPiece(String type, String color) {

        this.type = type;
        this.color = color;

        String filePath =  type + "_" + color +".png";
        Image image = new Image(filePath);

        setPreserveRatio(true);

        double cell = ChessClick.CELL_SIZE;

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

}
