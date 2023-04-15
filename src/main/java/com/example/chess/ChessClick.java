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


        Integer[] SelectedPrev = new Integer[]{row, col};
        square.setStyle("-fx-border-color: transparent; -fx-border-width: 0.0; -fx-background-color: rgba(0, 40, 0, 0.5);");

        removePrevHigh();

        String pieceType = ChessBoard.startingPositions[row][col];


        ChessPiece piece = (ChessPiece) square.getChildren().get(0);
        String wb = piece.getColor();

        if(pieceType.equals("pawn") && wb.equals("black"))
        {
            pieceType = "bpawn";
        }

        List<boolean[][]> moves = ChessLogic.getLegalMoves(board, row, col, pieceType, wb);

        boolean[][] legalMoves = moves.get(0);
        boolean[][] captureMoves = moves.get(1);
        boolean[][] castleMoves = moves.get(3);

        List<Integer[]> legalIndexes = ChessLogic.getTrueIndexes(legalMoves);
        List<Integer[]> captureIndexes = ChessLogic.getTrueIndexes(captureMoves);
        List<Integer[]> castleIndexes = ChessLogic.getTrueIndexes(castleMoves);

        for (Integer[] index : captureIndexes) {

            int crow = index[0];
            int ccol = index[1];

            highlightSquare(crow, ccol, "capture");

            StackPane captureMove = (StackPane) chessBoard.getChildren().get(crow * 8 + ccol+1);

            String finalPieceType = pieceType;
            captureMove.setOnMouseClicked(event1 ->
                    movePieceCapture(row, col, square, captureMove, wb, finalPieceType, true, false));
        }

        for (Integer[] index1 : legalIndexes) {

            int trow = index1[0];
            int tcol = index1[1];

            highlightSquare(trow, tcol, "legal");

            StackPane targetMove = (StackPane) chessBoard.getChildren().get(trow * 8 + tcol+1);

            String finalPieceType = pieceType;
            targetMove.setOnMouseClicked(event1 ->
                    movePieceCapture(row, col, square, targetMove, wb, finalPieceType, false, false));

        }

        for (Integer[] index2 : castleIndexes) {

            int trow = index2[0];
            int tcol = index2[1];

            highlightSquare(trow, tcol, "legal");

            StackPane castleMove = (StackPane) chessBoard.getChildren().get(trow * 8 + tcol+1);

            String finalPieceType = pieceType;
            castleMove.setOnMouseClicked(event1 ->
                    movePieceCapture(row, col, square, castleMove, wb, finalPieceType, false, true));

        }

        prevHigh = legalIndexes;
        prevHigh.addAll(captureIndexes);
        prevHigh.addAll(castleIndexes);
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
                if(startingPositions[row][col] != null && !startingPositions[row][col].equals("king")) {
                    StackPane temp = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);
                    ChessPiece piece = (ChessPiece) temp.getChildren().get(0);
                    String wb = piece.getColor();
                    String pt = piece.getType();

                    if(wb.equals(color)) {

                        if(pt.equals("pawn") && wb.equals("black"))
                        {
                            pt = "bpawn";
                        }
                        List<boolean[][]> moves = ChessLogic.getLegalMoves(board, row, col, pt, wb);
                        boolean[][] captureMoves = moves.get(1);
                        captureIndexes.addAll(ChessLogic.getTrueIndexes(captureMoves));

                        for (Integer[] index : captureIndexes) {
                            int trow = index[0];
                            int tcol = index[1];

                            if(startingPositions[trow][tcol].equals("king")) {
                                result[0] = trow;
                                result[1] = tcol;
                                System.out.println(pt);
                                return result;
                            }
                        }
                    }
                }
            }
        }

        return new int[2];
    }

    private static void movePieceCapture(int fromRow, int fromCol, StackPane from, StackPane to, String wb, String pieceType, boolean Capt, boolean castle) {

        int toCol = GridPane.getColumnIndex(to);
        int toRow = GridPane.getRowIndex(to);

        if(pieceType.equals("bpawn"))
        {
            pieceType = "pawn";
        }

        from.getChildren().remove(0);
        from.setOnMouseClicked(null);

        ChessPiece piece = new ChessPiece(pieceType, wb, true);

        if(Capt)
        {
            to.getChildren().remove(0);
        }

        if(castle)
        {
            castle(toCol, fromRow, fromCol, wb);
        }

        to.getChildren().add(piece);

        startingPositions[toRow][toCol] = pieceType;
        startingPositions[fromRow][fromCol] = null;

        board[toRow][toCol]= true;
        board[fromRow][fromCol]= false;

        removePrevHigh();

        turns(isWhiteTurn);

        if(!pieceType.equals("king"))
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
            Circle circle = new Circle((CELL_SIZE*0.2)/2, customColor);
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

    private static void castle(int toCol,int fromRow, int fromCol, String wb) {

        if(toCol == 6 && wb.equals("white"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(64);
            rookCastle.getChildren().remove(0);

            startingPositions[7][7] = null;
            board[7][7]= false;

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol+2);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol+1] = "rook";
            board[fromRow][fromCol+1]= true;
        }

        if(toCol == 2 && wb.equals("white"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(57);
            rookCastle.getChildren().remove(0);

            startingPositions[7][0] = null;
            board[7][0]= false;

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol-1] = "rook";
            board[fromRow][fromCol-1]= true;
        }

        if(toCol == 6 && wb.equals("black"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(8);
            rookCastle.getChildren().remove(0);

            startingPositions[0][7] = null;
            board[0][7]= false;

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol+2);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol+1] = "rook";
            board[fromRow][fromCol+1]= true;
        }

        if(toCol == 2 && wb.equals("black"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(1);
            rookCastle.getChildren().remove(0);

            startingPositions[0][0] = null;
            board[0][0]= false;

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol-1] = "rook";
            board[fromRow][fromCol-1]= true;
        }

    }
    private static void DoCheck(int row, int col) {

        System.out.println("Check!");
        StackPane squareToHighlightCheck = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);
        squareToHighlightCheck.setStyle("-fx-border-color: red; -fx-border-width: 0px;-fx-background-color:" + getSquareColor(row, col));

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

        squareToRemove.setStyle("-fx-border-color: transparent;-fx-border-width: 0px;-fx-background-color:" + getSquareColor(row, col));

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

