package com.example.chess;

import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

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

    static void DoCheck(int kingRow, int kingCol, int atkPieceRow, int atkPieceCol) {

        System.out.println("Check!");

        StackPane squareToHighlightCheck = (StackPane) chessBoard.getChildren().get(kingRow * 8 + kingCol + 1);
        squareToHighlightCheck.setStyle("-fx-border-color: red; -fx-border-width: 2px;-fx-background-color:" + getSquareColor(kingRow, kingCol));

        List<List<Integer[]>> piecesThatCanBlock = squaresToBlock(kingRow, kingCol, atkPieceRow, atkPieceCol);

        List<Integer[]> temp = piecesThatCanBlock.get(0);
        List<Integer[]> temp2 = piecesThatCanBlock.get(1);


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

        for (Integer[] index : temp) {

            int rowTemp = index[0];
            int colTemp = index[1];

            System.out.println(index[0]);
            System.out.println(index[1]);

            StackPane canBlock = (StackPane) chessBoard.getChildren().get(rowTemp * 8 + colTemp + 1);
            canBlock.setOnMouseClicked(event ->
                    ChessClick.setClick(rowTemp, colTemp, canBlock, temp2));
        }

        List<Integer[]> list = new ArrayList<>(0);

        squareToHighlightCheck.setOnMouseClicked(event ->

                ChessClick.setClick(kingRow, kingCol, squareToHighlightCheck, list));
    }

    private static  List<List<Integer[]>> squaresToBlock(int kingRow, int kingCol, int atkPieceRow, int atkPieceCol) {

        List<Integer[]> blockIndexes = new ArrayList<>();
        List<List<Integer[]>> resultList = new ArrayList<>();

        if(kingCol == atkPieceCol) {

            blockIndexes.add(new Integer[] {atkPieceRow, atkPieceCol});

            int startRow = Math.min(kingRow, atkPieceRow);
            int endRow = Math.max(kingRow, atkPieceRow);

            for (int r = startRow; r < endRow; r++) {
                blockIndexes.add(new Integer[]{r, kingCol});
            }

            resultList.add(piecesThatCanBlock(blockIndexes, !isWhiteTurn));
            resultList.add(blockIndexes);

            return resultList;
        }

        if(kingRow == atkPieceRow) {

            blockIndexes.add(new Integer[] {atkPieceRow, atkPieceCol});

            int startCol = Math.min(kingCol, atkPieceCol) + 1;
            int endCol = Math.max(kingCol, atkPieceCol);

            for (int c = startCol; c < endCol; c++) {
                blockIndexes.add(new Integer[]{kingRow, c});
            }

            resultList.add(piecesThatCanBlock(blockIndexes, !isWhiteTurn));
            resultList.add(blockIndexes);

            return resultList;
        }

        if(Math.abs(kingRow - atkPieceRow) == Math.abs(kingCol - atkPieceCol)) {

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

            resultList.add(piecesThatCanBlock(blockIndexes, !isWhiteTurn));
            resultList.add(blockIndexes);

            return resultList;
        }

        System.out.println("Its not blockable");

        return resultList;
    }

    private static List<Integer[]> piecesThatCanBlock(List<Integer[]> squaresToBlock, boolean whiteTurn) {

        List<Integer[]> piecesThatCanBlock = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                if (startingPositions[i][j] != null) {

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
}
