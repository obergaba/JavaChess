package com.example.chess;

import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ChessChecks extends ChessClick{

    public static int[] isCheck(boolean isWhiteTurn) {

        List<Integer[]> captureIndexes = new ArrayList<>();
        int[] result = new int[4];

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

                                result[2] = row;
                                result[3] = col;

                                return result;
                            }
                        }
                    }
                }
            }
        }

        return new int[2];
    }
    public static void DoCheck(int kingRow, int kingCol, int atkPieceRow, int atkPieceCol, String pt) {

        Integer[] temp = {-1, -1};

        isWhiteTurn = !isWhiteTurn;

        StackPane kingSquare = (StackPane) chessBoard.getChildren().get(kingRow * 8 + kingCol + 1);
        //kingSquare.setStyle("-fx-border-color: red; -fx-border-width: 2px;-fx-background-color:" + getSquareColor(kingRow, kingCol));
        kingSquare.setStyle("-fx-background-color: rgba(100, 255, 0, 0.7)");

        String wb;

        if(isWhiteTurn)
        {
            wb = "white";
        }else {
            wb = "black";
        }

        setAllClickToNull();

        List<Integer[]> list = new ArrayList<>(0);

        List<boolean[][]> moves = ChessLogic.getLegalMoves(board, kingRow, kingCol, "king", wb);
        boolean[][] legalMoves = moves.get(0);
        boolean[][] capMoves = moves.get(1);

        List<Integer[]> legalIndexes = ChessLogic.getTrueIndexes(legalMoves);
        List<Integer[]> capIndexes = ChessLogic.getTrueIndexes(capMoves);


        isWhiteTurn = !isWhiteTurn;

        List<List<Integer[]>> piecesThatCanBlock = squaresToBlock(kingRow, kingCol, atkPieceRow, atkPieceCol, pt);

        List<Integer[]> canBlockIndex = piecesThatCanBlock.get(0);
        List<Integer[]> toBlockIndex = piecesThatCanBlock.get(1);
        List<Integer[]> thruCheck = piecesThatCanBlock.get(2);

        Integer[] thruCheckArray = thruCheck.get(0);

        if(thruCheckArray[0] != -1) {
            for (Integer[] index : legalIndexes) {
                if (Arrays.equals(index, thruCheckArray)) {
                    legalIndexes.remove(index);
                    break;
                }
            }

            for (Integer[] index : capIndexes) {
                if (Arrays.equals(index, thruCheckArray)) {
                    capIndexes.remove(index);
                    break;
                }
            }
        }
        if(legalIndexes.isEmpty() && canBlockIndex.size() == 0 && capIndexes.isEmpty())
        {
            System.out.println("CHECK MATE!!");
            return;
        }

        if(!legalIndexes.isEmpty() || !capIndexes.isEmpty()) {
            kingSquare.setOnMouseClicked(event ->
                    ChessClick.setClick(kingRow, kingCol, kingSquare, list, thruCheckArray));
        }

        for (Integer[] index : canBlockIndex) {
            int rowTemp = index[0];
            int colTemp = index[1];
            StackPane canBlock = (StackPane) chessBoard.getChildren().get(rowTemp * 8 + colTemp + 1);
            canBlock.setOnMouseClicked(event ->
                    ChessClick.setClick(rowTemp, colTemp, canBlock, toBlockIndex, temp));
        }
    }

    private static List<List<Integer[]>> squaresToBlock(int kingRow, int kingCol, int atkPieceRow, int atkPieceCol, String pt) {

        List<List<Integer[]>> resultList = new ArrayList<>();
        List<Integer[]> blockIndexes = new ArrayList<>();
        List<Integer[]> thruCheck = new ArrayList<>();

        int[] array = {-1, -1};

        List<Integer[]> pieces;

        if(kingCol == atkPieceCol) {

            if(kingRow>atkPieceRow && kingRow+1<8)
            {
                array[0] = kingRow+1;
                array[1] = kingCol;
            }

            else if(kingRow<atkPieceRow && kingRow-1 >-1)
            {
                array[0] = kingRow-1;
                array[1] = kingCol;
            }



            blockIndexes.add(new Integer[] {atkPieceRow, atkPieceCol});

            int startRow = Math.min(kingRow, atkPieceRow);
            int endRow = Math.max(kingRow, atkPieceRow);

            for (int r = startRow+1; r < endRow; r++) {
                blockIndexes.add(new Integer[]{r, kingCol});
            }

            pieces = piecesThatCanBlock(blockIndexes, !isWhiteTurn);

            thruCheck.add(new Integer[]{array[0], array[1]});

            resultList.add(pieces);
            resultList.add(blockIndexes);
            resultList.add(thruCheck);

            return resultList;
        }

        if(kingRow == atkPieceRow) {

            if(kingCol>atkPieceCol && kingCol+1<8)
            {
                array[0] = kingRow;
                array[1] = kingCol+1;
            }

            if(kingCol<atkPieceCol && kingCol-1>-1)
            {
                array[0] = kingRow;
                array[1] = kingCol-1;
            }


            blockIndexes.add(new Integer[] {atkPieceRow, atkPieceCol});

            int startCol = Math.min(kingCol, atkPieceCol) + 1;
            int endCol = Math.max(kingCol, atkPieceCol);

            for (int c = startCol; c < endCol; c++) {
                blockIndexes.add(new Integer[]{kingRow, c});
            }

            pieces = piecesThatCanBlock(blockIndexes, !isWhiteTurn);

            thruCheck.add(new Integer[]{array[0], array[1]});

            resultList.add(pieces);
            resultList.add(blockIndexes);
            resultList.add(thruCheck);

            return resultList;
        }

        if(Math.abs(kingRow - atkPieceRow) == Math.abs(kingCol - atkPieceCol)) {

            if(kingCol>atkPieceCol)
            {
                if(kingRow>atkPieceRow && kingRow+1<8 && kingCol+1<8)
                {
                    array[0] = kingRow+1;
                    array[1] = kingCol+1;
                }

                if(kingRow<atkPieceRow && kingRow-1> -1 && kingCol+1<8)
                {
                    array[0] = kingRow-1;
                    array[1] = kingCol+1;
                }
            }

            if(kingCol<atkPieceCol)
            {
                if(kingRow>atkPieceRow && kingRow+1<8 && kingCol-1>-1)
                {
                    array[0] = kingRow+1;
                    array[1] = kingCol-1;
                }

                if(kingRow<atkPieceRow && kingRow-1> -1 && kingCol-1>-1)
                {
                    array[0] = kingRow-1;
                    array[1] = kingCol-1;
                }
            }


            blockIndexes.add(new Integer[] {atkPieceRow, atkPieceCol});

            int rowOffset = kingRow > atkPieceRow ? 1 : -1;
            int colOffset = kingCol > atkPieceCol ? 1 : -1;

            int row = atkPieceRow + rowOffset;
            int col = atkPieceCol + colOffset;

            while (row >= 0 && row < 8 && col >= 0 && col < 8 && (row != kingRow || col != kingCol)) {
                blockIndexes.add(new Integer[]{row, col});
                row += rowOffset;
                col += colOffset;
            }

            pieces = piecesThatCanBlock(blockIndexes, !isWhiteTurn);

            thruCheck.add(new Integer[]{array[0], array[1]});

            resultList.add(pieces);
            resultList.add(blockIndexes);
            resultList.add(thruCheck);

            return resultList;
        }

        if(pt.equals("knight") || pt.equals("pawn") || pt.equals("bpawn")) {

            blockIndexes.add(new Integer[] {atkPieceRow, atkPieceCol});

            pieces = piecesThatCanBlock(blockIndexes, !isWhiteTurn);

            thruCheck.add(new Integer[]{array[0], array[1]});

            resultList.add(pieces);
            resultList.add(blockIndexes);
            resultList.add(thruCheck);

            return resultList;
        }

        System.out.println("Its not blockable");
        return resultList;
    }

    private static List<Integer[]> piecesThatCanBlock(List<Integer[]> squaresToBlock, boolean whiteTurn) {

        List<Integer[]> piecesThatCanBlock = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (startingPositions[i][j] != null && !Objects.equals(startingPositions[i][j], "king")) {

                    StackPane turn = (StackPane) chessBoard.getChildren().get(i * 8 + j + 1);
                    ChessPiece piece = (ChessPiece) turn.getChildren().get(0);
                    String wb = piece.getColor();
                    String pt = piece.getType();

                    if (whiteTurn) {

                        if (wb.equals("white")) {

                            List<boolean[][]> moves = ChessLogic.getLegalMoves(board, i, j, pt, wb);

                            boolean[][] legalMoves = moves.get(0);
                            boolean[][] captureMoves = moves.get(1);

                            List<Integer[]> legalIndexes = ChessLogic.getTrueIndexes(legalMoves);
                            List<Integer[]> captureIndexes = ChessLogic.getTrueIndexes(captureMoves);

                            List<Integer[]> allMovesIndexes = new ArrayList<>();

                            allMovesIndexes.addAll(legalIndexes);
                            allMovesIndexes.addAll(captureIndexes);

                            for (Integer[] moveIndex : allMovesIndexes) {
                                for (Integer[] blockIndex : squaresToBlock) {
                                    if (moveIndex[0].equals(blockIndex[0]) && moveIndex[1].equals(blockIndex[1])) {
                                        Integer[] pieceToBlock = {i, j};
                                        piecesThatCanBlock.add(pieceToBlock);
                                    }
                                }
                            }
                        }
                    }

                    if(!whiteTurn)
                    {
                        if (wb.equals("black")) {

                            if(pt.equals("pawn"))
                                pt = "bpawn";

                            List<boolean[][]> moves = ChessLogic.getLegalMoves(board, i, j, pt, wb);

                            boolean[][] legalMoves = moves.get(0);
                            boolean[][] captureMoves = moves.get(1);

                            List<Integer[]> legalIndexes = ChessLogic.getTrueIndexes(legalMoves);
                            List<Integer[]> captureIndexes = ChessLogic.getTrueIndexes(captureMoves);

                            List<Integer[]> allMovesIndexes = new ArrayList<>();

                            allMovesIndexes.addAll(legalIndexes);
                            allMovesIndexes.addAll(captureIndexes);

                            for (Integer[] moveIndex : allMovesIndexes) {
                                for (Integer[] blockIndex : squaresToBlock) {
                                    if (moveIndex[0].equals(blockIndex[0]) && moveIndex[1].equals(blockIndex[1])) {
                                        Integer[] pieceToBlock = {i, j};
                                        piecesThatCanBlock.add(pieceToBlock);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return piecesThatCanBlock;
    }
    private static void setAllClickToNull() {

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
    }
}
