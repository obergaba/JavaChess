package com.example.chess;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.util.*;
public class ChessClick extends ChessBoard {
    private static List<Integer[]> prevHigh = new ArrayList<>();
    public static void setClick(int row, int col, StackPane square, List<Integer[]> checkBlock, Integer[] thruCheck) {

        removePrevHigh();

        square.setStyle("-fx-border-color: transparent; -fx-border-width: 0.0; -fx-background-color: rgba(0, 40, 0, 0.5);");

        System.out.println("Turn:");
        System.out.println(isWhiteTurn);

        String pieceType = ChessBoard.startingPositions[row][col];
        Integer[] SelectedPrev = new Integer[]{row, col};

        ChessPiece piece = (ChessPiece) square.getChildren().get(0);
        String wb = piece.getColor();

        if(pieceType.equals("pawn") && wb.equals("black"))
        {
            pieceType = "bpawn";
        }

        List<List<Integer[]>> moves = ChessLogic.getLegalMoves(row, col, pieceType, wb);

        List<Integer[]> legalIndexes = moves.get(0);
        List<Integer[]> captureIndexes = moves.get(1);
        List<Integer[]> castleIndexes = moves.get(3);

        if(thruCheck[0] != -1) {
            for (Integer[] index : legalIndexes) {
                if (Arrays.equals(index, thruCheck)) {
                    legalIndexes.remove(index);
                    break;
                }
            }

            for (Integer[] index : captureIndexes) {
                if (Arrays.equals(index, thruCheck)) {
                    captureIndexes.remove(index);
                    break;
                }
            }
        }

        for (Integer[] index : captureIndexes) {

            int crow = index[0];
            int ccol = index[1];

            if(ArrayContains(checkBlock, index) && checkBlock.size() > 0)
            {
                highlightSquare(crow, ccol, "capture");

                StackPane captureMove = (StackPane) chessBoard.getChildren().get(crow * 8 + ccol + 1);

                String finalPieceType = pieceType;
                captureMove.setOnMouseClicked(event1 ->
                        movePieceCapture(row, col, square, captureMove, wb, finalPieceType, true, false));
                continue;

            } else if (checkBlock.size() > 0) {
                continue;
            }

            highlightSquare(crow, ccol, "capture");

            StackPane captureMove = (StackPane) chessBoard.getChildren().get(crow * 8 + ccol + 1);

            String finalPieceType = pieceType;
            captureMove.setOnMouseClicked(event1 ->
                    movePieceCapture(row, col, square, captureMove, wb, finalPieceType, true, false));
        }

        for (Integer[] index1 : legalIndexes) {

            int trow = index1[0];
            int tcol = index1[1];

            if(ArrayContains(checkBlock, index1) && checkBlock.size() > 0)
            {

                highlightSquare(trow, tcol, "legal");
                StackPane targetMove = (StackPane) chessBoard.getChildren().get(trow * 8 + tcol+1);

                String finalPieceType = pieceType;
                targetMove.setOnMouseClicked(event1 ->
                        movePieceCapture(row, col, square, targetMove, wb, finalPieceType, false, false));
                continue;

            } else if (checkBlock.size() > 0) {

                continue;

            }else {
                startingPositions[row][col] = null;
                int[] result = ChessChecks.isCheck(!isWhiteTurn);
                if (result[0] != 0 || result[1] != 0) {
                    startingPositions[row][col] = pieceType;
                    continue;
                }
                startingPositions[row][col] = pieceType;
            }

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
    private static void movePieceCapture(int fromRow, int fromCol, StackPane from, StackPane to, String wb, String pieceType, boolean Capt, boolean castle) {

        int toCol = GridPane.getColumnIndex(to);
        int toRow = GridPane.getRowIndex(to);

        if(pieceType.equals("bpawn"))
        {
            pieceType = "pawn";
        }

        if (pieceType.equals("pawn") && (toRow == 0 || toRow == 7))
        {
            pieceType ="queen";
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
            castle(toCol, toRow, fromRow, fromCol, wb);
        }

        //TODO: Update FEN, considering castle. Iterate all stacks? Update only single piece?
        //UpdateFEN(fromRow, fromCol, toRow, toCol);

        to.getChildren().add(piece);

        startingPositions[toRow][toCol] = pieceType;
        startingPositions[fromRow][fromCol] = null;

        if(boolCheck)
        {
            //TODO: take king coordinates and remove the highlight, maybe?
            boolCheck = false;
        }
        removePrevHigh();

        turns(isWhiteTurn);

        if(!pieceType.equals("king"))
        {
            int[] result = ChessChecks.isCheck(isWhiteTurn);

            if (result[0] != 0 || result[1] != 0) {

                boolCheck = true;
                int kingRow = result[0];
                int kingCol = result[1];

                Integer[] kingHigh = new Integer[]{kingRow, kingCol};

                int atkPieceRow = result[2];
                int atkPieceCol = result[3];

                prevHigh.add(kingHigh);

                ChessChecks.DoCheck(kingRow, kingCol, atkPieceRow, atkPieceCol, pieceType);
            }
        }

        isWhiteTurn = !isWhiteTurn;

    }
    static void UpdateFEN(int fromRow, int fromCol, int toRow, int toCol){

        // Edit old fen from row that give the current fen split and target one.
        // Function have to handle fen split = 8, P1P and so on
        // Row = 1, Col = 1

        // FEN example = rnbqkbnr/1p5p/8/8/8/8/PPPPPPPP/RNBQKBNR
        // fromFEN = 1P5P -> 115P -> 7P
        // toFEN = 8

        String oldFen = STARTING_FEN.split(" ")[0];

        String fromFen = oldFen.split("/")[fromRow];
        char[] fromChars = fromFen.toCharArray();

        String toFen = oldFen.split("/")[toRow];

        int charInd = 0;
        for (char ch : fromChars)
        {
            if (Character.isDigit(ch))
            {
                charInd += Character.getNumericValue(ch);
            } else if (Character.isLetter(ch)) {
                charInd += 1;
            }else
            {System.out.println("Error on parsing col FEN");}

            if (charInd-1 == fromCol)
            {
                break;
            }
        }
        fromChars[charInd-1] = '1';

        int prevIsDigit = 1;
        int count = 0;

        while (count < fromChars.length)
        {

            count += 1;
        }

        System.out.println(fromFen);
        System.out.println(String.valueOf(fromChars));
    }
    static void highlightSquare(int row, int col, String legOrCap) {

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
    private static void castle(int toCol, int toRow,int fromRow, int fromCol, String wb) {

        if(toCol == 6 && toRow == 7 && wb.equals("white"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(64);
            rookCastle.getChildren().remove(0);

            startingPositions[7][7] = null;

            rookCastle.setOnMouseClicked(null);

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol+2);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol+1] = "rook";

        }

        if(toCol == 2 && toRow == 7 && wb.equals("white"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(57);
            rookCastle.getChildren().remove(0);

            startingPositions[7][0] = null;


            rookCastle.setOnMouseClicked(null);

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol-1] = "rook";

        }

        if(toCol == 6 && toRow == 0 && wb.equals("black"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(8);
            rookCastle.getChildren().remove(0);

            startingPositions[0][7] = null;

            rookCastle.setOnMouseClicked(null);

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol+2);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol+1] = "rook";
        }

        if(toCol == 2 && toRow == 0 && wb.equals("black"))
        {
            StackPane rookCastle = (StackPane) chessBoard.getChildren().get(1);
            rookCastle.getChildren().remove(0);

            startingPositions[0][0] = null;

            rookCastle.setOnMouseClicked(null);

            ChessPiece rook = new ChessPiece("rook", wb, true);
            StackPane rookCastle2 = (StackPane) chessBoard.getChildren().get(fromRow * 8 + fromCol);
            rookCastle2.getChildren().add(rook);

            startingPositions[fromRow][fromCol-1] = "rook";
        }
    }

    private static void turns(boolean whiteTurn) {

        List<Integer[]> list = new ArrayList<>();
        Integer[] temp = {-1, -1};

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
                                    ChessClick.setClick(finalI, finalJ, turn, list, temp));
                        }
                    }
                    if(!whiteTurn) {
                        if (Objects.equals(wb, "white")) {
                            turn.setOnMouseClicked(event ->
                                    ChessClick.setClick(finalI, finalJ, turn, list, temp));
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
    }

    public static void removePrevHigh() {

        if(prevHigh.size() > 0)
        {
            Integer[] lastCoordinates = prevHigh.get(prevHigh.size() - 1);

            int selfRow = lastCoordinates[lastCoordinates.length - 2];
            int selfCol = lastCoordinates[lastCoordinates.length - 1];

            for (Integer[] index1 : prevHigh) {
                removeHighlight(index1[0], index1[1]);

                if (index1[0] != selfRow || index1[1] != selfCol)
                {
                    StackPane stack = (StackPane) chessBoard.getChildren().get(index1[0] * 8 + index1[1]+1);
                    stack.setOnMouseClicked(null);
                }
            }
        }
        prevHigh = new ArrayList<>();
    }

    private static boolean ArrayContains(List<Integer[]> array, Integer[] elem)
    {
        for (Integer[] el : array)
        {
            if (Arrays.equals(el, elem))
            {
                return true;
            }
        }
        return false;
    }
}
