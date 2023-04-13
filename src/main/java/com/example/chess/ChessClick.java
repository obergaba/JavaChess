package com.example.chess;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChessClick extends ChessBoard {
    private static List<Integer[]> prevHigh = null;
    public static void setClick(int row, int col, StackPane square) {

        System.out.println((char)('A' + col)+ " "+ (8-row));

        Integer[] SelectedPrev = new Integer[]{row, col};
        square.setStyle("-fx-border-color: transparent; -fx-border-width: 0.5; -fx-background-color: rgba(0, 40, 0, 0.5);");

        removePrevHigh();

        String pieceType = ChessBoard.startingPositions[row][col];
        String pieceType2 = ChessBoard.startingPositions[row][col];

        ChessPiece piece = (ChessPiece) square.getChildren().get(0);
        String wb = piece.getColor();

        if(pieceType.equals("pawn") && wb.equals("black"))
        {
            pieceType2 = "bpawn";
        }

        List<boolean[][]> moves = ChessLogic.getLegalMoves(board, row, col, pieceType2, wb);

        boolean[][] legalMoves = moves.get(0);
        boolean[][] captureMoves = moves.get(1);

        List<Integer[]> legalIndexes = ChessLogic.getTrueIndexes(legalMoves);
        List<Integer[]> captureIndexes = ChessLogic.getTrueIndexes(captureMoves);

        for (Integer[] index : captureIndexes) {

            int crow = index[0];
            int ccol = index[1];

            highlightSquare(crow, ccol, "capture");

            StackPane captureMove = (StackPane) chessBoard.getChildren().get(crow * 8 + ccol+1);

            captureMove.setOnMouseClicked(event1 ->
                    movePieceCapture(row, col, square, captureMove, wb, pieceType, true));
        }

        for (Integer[] index1 : legalIndexes) {

            int trow = index1[0];
            int tcol = index1[1];

            highlightSquare(trow, tcol, "legal");

            StackPane targetMove = (StackPane) chessBoard.getChildren().get(trow * 8 + tcol+1);

            targetMove.setOnMouseClicked(event1 ->
                    movePieceCapture(row, col, square, targetMove, wb, pieceType, false));

        }

        prevHigh = legalIndexes;
        prevHigh.addAll(captureIndexes);
        prevHigh.add(SelectedPrev);

    }

    public static int[] isCheck(boolean isWhiteTurn) {

        List<Integer[]> captureIndexes = new ArrayList<>();
        int[] result = new int[2];

        String color = "black";
        if(isWhiteTurn)
            color = "white";

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                if(startingPositions[row][col] != null && startingPositions[row][col] != "king") {
                    StackPane temp = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);
                    ChessPiece piece = (ChessPiece) temp.getChildren().get(0);
                    String wb = piece.getColor();
                    String pt = piece.getType();

                    if(wb.equals(color)) {
                        List<boolean[][]> moves = ChessLogic.getLegalMoves(board, row, col, pt, wb);
                        boolean[][] captureMoves = moves.get(1);
                        captureIndexes.addAll(ChessLogic.getTrueIndexes(captureMoves));
                    }
                }
            }
        }

        for (Integer[] index : captureIndexes) {
            int trow = index[0];
            int tcol = index[1];

            if(startingPositions[trow][tcol].equals("king")) {
                result[0] = trow;
                result[1] = tcol;
                return result;
            }
        }

        return new int[2];
    }

    private static void movePieceCapture(int fromRow, int fromCol, StackPane from, StackPane to, String wb, String pieceType, boolean Capt) {

        int toCol = GridPane.getColumnIndex(to);
        int toRow = GridPane.getRowIndex(to);

        from.getChildren().remove(0);

        ChessPiece piece = new ChessPiece(pieceType, wb);

        if(Capt)
        {
            to.getChildren().remove(0);
        }

        to.getChildren().add(piece);

        startingPositions[toRow][toCol] = pieceType;
        startingPositions[fromRow][fromCol] = null;

        board[toRow][toCol]= true;
        board[fromRow][fromCol]= false;

        removePrevHigh();

        turns(isWhiteTurn);

        if(pieceType != "king")
        {
            int[] result = isCheck(isWhiteTurn);

            if (result[0] != 0 || result[1] != 0) {
                DoCheck(result[0], result[1]);
                }
        }


        isWhiteTurn = !isWhiteTurn;
        if(isWhiteTurn)
        {
            System.out.println("White to move");
        }

        if(!isWhiteTurn)
        {
            System.out.println("Black to move");
        }
    }

    private static void highlightSquare(int row, int col, String legOrCap) {

        StackPane squareToHighlight = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);

        if(Objects.equals(legOrCap, "legal")) {

            Color customColor = Color.rgb(0, 40, 0);
            Circle circle = new Circle(10, customColor);
            circle.setOpacity(0.5);

            VBox vBox = new VBox(circle);
            vBox.setAlignment(Pos.CENTER);

            squareToHighlight.getChildren().add(vBox);

        }

        else if(Objects.equals(legOrCap, "capture"))
        {
            squareToHighlight.setStyle("-fx-background-color: rgba(92, 32, 27, 0.7);");
        }
    }
    private static void DoCheck(int row, int col) {

        System.out.println("Check!");
        StackPane squareToHighlightCheck = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);
        squareToHighlightCheck.setStyle("-fx-border-color: red; -fx-border-width: 2px;-fx-background-color:" + getSquareColor(row, col));

        for(int i = 0; i<8; i++)
        {
            for(int j = 0; j<8; j++)
            {
                if(startingPositions[i][j] != null ) {
                    StackPane turn2 = (StackPane) chessBoard.getChildren().get(i * 8 + j + 1);
                    turn2.setOnMouseClicked(null);
                }
            }
        }

        squareToHighlightCheck.setOnMouseClicked(event ->
                ChessClick.setClick(row, col, squareToHighlightCheck));
    }

    private static void turns(boolean whiteTurn) {

        for(int i = 0; i<8; i++)
        {
            for(int j = 0; j<8; j++)
            {
                int finalI = i;
                int finalJ = j;

                if(startingPositions[i][j] != null) {

                    StackPane turn = (StackPane) chessBoard.getChildren().get(i * 8 + j + 1);
                    ChessPiece piece = (ChessPiece) turn.getChildren().get(0);
                    String wb = piece.getColor();

                    if(whiteTurn) {
                        if (Objects.equals(wb, "white")) {
                            turn.setOnMouseClicked(null);
                        }

                        if (Objects.equals(wb, "black")) {
                            turn.setOnMouseClicked(event ->
                                    ChessClick.setClick(finalI, finalJ, turn));
                        }
                    }

                    if(!whiteTurn) {
                        if (Objects.equals(wb, "white")) {
                            turn.setOnMouseClicked(event ->
                                    ChessClick.setClick(finalI, finalJ, turn));
                        }
                        if (Objects.equals(wb, "black")) {
                            turn.setOnMouseClicked(null);
                        }
                    }
                }
            }
        }
    }

    private static void removeHighlight(int row, int col) {

        Integer[] lastCoordinates = prevHigh.get(prevHigh.size() - 1);
        int secondToLast = lastCoordinates[lastCoordinates.length - 2];
        int last = lastCoordinates[lastCoordinates.length - 1];

        StackPane squareToRemove = (StackPane) chessBoard.getChildren().get(row * 8 + col+1);

        squareToRemove.setStyle("-fx-border-color: transparent;-fx-background-color:" + getSquareColor(row, col));

        VBox vBoxToRemove = null;

        for (Node node : squareToRemove.getChildren()) {
            if (node instanceof VBox) {
                vBoxToRemove = (VBox) node;
                break;
            }
        }

        if (vBoxToRemove != null) {
            squareToRemove.getChildren().remove(vBoxToRemove);
        }

        if(row != secondToLast || col != last)
        {
            squareToRemove.setOnMouseClicked(null);
        }
    }

    public static void removePrevHigh() {

        if(prevHigh !=null)
        {
            for (Integer[] index1 : prevHigh) {
                removeHighlight(index1[0], index1[1]);
            }
        }
    }
}

