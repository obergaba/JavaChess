package com.example.chess;

import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class ChessLogic extends ChessBoard {
    public static List<List<Integer[]>> getLegalMoves(int row, int col, String pieceType, String pColor) {

        List<Integer[]> legalIndexes = new ArrayList<>();
        List<Integer[]> capture2Indexes = new ArrayList<>();
        List<Integer[]> myCapture = new ArrayList<>();
        List<Integer[]> castleIndexes = new ArrayList<>();

        switch (pieceType) {

            case "pawn" -> {
                if (row > 0 && startingPositions[row-1][col] == null) {
                    legalIndexes.add(new Integer[]{row-1, col});
                    if (row == 6 && startingPositions[row-2][col] == null) {
                        legalIndexes.add(new Integer[]{row-2, col});
                    }
                }
                if (row > 0 && col > 0 && !(startingPositions[row-1][col-1] == null) && !getPieceColor(row-1, col-1).equals(pColor)) {
                    capture2Indexes.add(new Integer[]{row-1, col-1});
                }
                if (row > 0 && col < 7 && !(startingPositions[row-1][col+1] == null) && !getPieceColor(row-1, col+1).equals(pColor)) {
                    capture2Indexes.add(new Integer[]{row-1, col+1});
                }

                if (row > 0 && col > 0) {
                    myCapture.add(new Integer[]{row-1, col-1});
                }
                if (row > 0 && col < 7) {
                    myCapture.add(new Integer[]{row-1, col+1});
                }
            }

            case "bpawn" -> {
                if (row < 7 && startingPositions[row+1][col] == null) {
                    legalIndexes.add(new Integer[]{row+1, col});
                    if (row == 1 && startingPositions[row+2][col] == null) {
                        legalIndexes.add(new Integer[]{row+2, col});
                    }
                }
                if (row < 7 && col > 0 && !(startingPositions[row+1][col-1] == null) && !getPieceColor(row+1, col-1).equals(pColor)) {
                    capture2Indexes.add(new Integer[]{row+1, col-1});
                }
                if (row < 7 && col < 7 && !(startingPositions[row+1][col+1] == null) && !getPieceColor(row+1, col+1).equals(pColor)) {
                    capture2Indexes.add(new Integer[]{row+1, col+1});
                }

                if (row < 7 && col > 0) {
                    myCapture.add(new Integer[]{row+1, col-1});
                }
                if (row < 7 && col < 7) {
                    myCapture.add(new Integer[]{row+1, col+1});
                }
            }

            case "knight" -> {

                int[] rowOffsets = {2, 2, -2, -2, 1, 1, -1, -1};
                int[] colOffsets = {-1, 1, -1, 1, 2, -2, 2, -2};

                for (int i = 0; i < rowOffsets.length; i++) {
                    int newRow = row + rowOffsets[i];
                    int newCol = col + colOffsets[i];
                    if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                        if (startingPositions[newRow][newCol] == null) {
                            legalIndexes.add(new Integer[]{newRow, newCol});
                        } else if (!getPieceColor(newRow, newCol).equals(pColor)) {
                            capture2Indexes.add(new Integer[]{newRow, newCol});
                        } else if (getPieceColor(newRow, newCol).equals(pColor)) {
                            myCapture.add(new Integer[]{newRow, newCol});
                        }
                    }
                }
            }


            case "bishop" -> {

                int i = row - 1, j = col + 1;
                while (i >= 0 && j < 8) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i--;
                    j++;
                }

                i = row - 1;
                j = col - 1;
                while (i >= 0 && j >= 0) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i--;
                    j--;
                }

                i = row + 1;
                j = col + 1;
                while (i < 8 && j < 8) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i++;
                    j++;
                }

                i = row + 1;
                j = col - 1;
                while (i < 8 && j >= 0) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i++;
                    j--;
                }
            }

            case "rook" -> {
                for (int i = row - 1; i >= 0; i--) {
                    if (startingPositions[i][col] == null) {
                        legalIndexes.add(new Integer[]{i, col});
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, col});
                        break;
                    } else if (getPieceColor(i, col).equals(pColor)) {
                        myCapture.add(new Integer[]{i, col});
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = row + 1; i < 8; i++) {
                    if (startingPositions[i][col] == null) {
                        legalIndexes.add(new Integer[]{i, col});
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, col});
                        break;
                    } else if (getPieceColor(i, col).equals(pColor)) {
                        myCapture.add(new Integer[]{i, col});
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = col - 1; i >= 0; i--) {
                    if (startingPositions[row][i] == null) {
                        legalIndexes.add(new Integer[]{row, i});
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{row, i});
                        break;
                    } else if (getPieceColor(row, i).equals(pColor)) {
                        myCapture.add(new Integer[]{row, i});
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = col + 1; i < 8; i++) {
                    if (startingPositions[row][i] == null) {
                        legalIndexes.add(new Integer[]{row, i});
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{row, i});
                        break;
                    } else if (getPieceColor(row, i).equals(pColor)) {
                        myCapture.add(new Integer[]{row, i});
                        break;
                    } else {
                        break;
                    }
                }
            }

            case "king" -> {

                int[] rowOff = {0, 0, 1, -1, 1, -1, 1, -1};
                int[] colOff = {1, -1, 0, 0, 1, -1, -1, 1};

                List<Integer[]> captureIndexes = getChecks(!isWhiteTurn);

                if (hasMoved(row, col)) {
                    if (hasMoved(row, col + 3) && startingPositions[row][col+1] == null && startingPositions[row][col+2] == null &&
                            isAttacked(captureIndexes, row, col + 1) && isAttacked(captureIndexes, row, col + 2) && !boolCheck) {

                        castleIndexes.add(new Integer[]{row, col+2});
                    }
                    if (hasMoved(row, col - 4) && startingPositions[row][col-1] == null && startingPositions[row][col-2] == null && startingPositions[row][col-3] == null &&
                            isAttacked(captureIndexes, row, col - 1) && isAttacked(captureIndexes, row, col - 2) && !boolCheck) {
                        castleIndexes.add(new Integer[]{row, col-2});
                    }
                }

                for (int i = 0; i < rowOff.length; i++) {
                    int newRow = row + rowOff[i];
                    int newCol = col + colOff[i];
                    if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                        if (startingPositions[newRow][newCol] == null) {
                            boolean canMove = true;

                            for (Integer[] index : captureIndexes) {
                                int trow = index[0];
                                int tcol = index[1];
                                if (newRow == trow && newCol == tcol) {
                                    canMove = false;
                                    break;
                                }
                            }

                            if (canMove) {
                                legalIndexes.add(new Integer[]{newRow, newCol});
                            }
                        } else if (!getPieceColor(newRow, newCol).equals(pColor)) {
                            boolean canMove2 = true;

                            for (Integer[] index : captureIndexes) {
                                int trow = index[0];
                                int tcol = index[1];
                                if (newRow == trow && newCol == tcol) {
                                    canMove2 = false;
                                    break;
                                }
                            }
                            if(canMove2)
                                capture2Indexes.add(new Integer[]{newRow, newCol});
                        }
                    }
                }
            }

            case "king2" -> {
                int[] rowOff = {0, 0, 1, -1, 1, -1, 1, -1};
                int[] colOff = {1, -1, 0, 0, 1, -1, -1, 1};

                for (int i = 0; i < 8; i++) {

                    int newX = row + rowOff[i];
                    int newY = col + colOff[i];

                    if (newX >= 0 && newX < 8 && newY >= 0 && newY < 8) {

                        legalIndexes.add(new Integer[]{newX, newY});
                    }
                }
            }

            case "queen" -> {

                for (int i = row - 1; i >= 0; i--) {
                    if (startingPositions[i][col] == null) {
                        legalIndexes.add(new Integer[]{i, col});
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, col});
                        break;
                    } else if (getPieceColor(i, col).equals(pColor)) {
                        myCapture.add(new Integer[]{i, col});
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = row + 1; i < 8; i++) {
                    if (startingPositions[i][col] == null) {
                        legalIndexes.add(new Integer[]{i, col});
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, col});
                        break;
                    } else if (getPieceColor(i, col).equals(pColor)) {
                        myCapture.add(new Integer[]{i, col});
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = col - 1; i >= 0; i--) {
                    if (startingPositions[row][i] == null) {
                        legalIndexes.add(new Integer[]{row, i});
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{row, i});
                        break;
                    } else if (getPieceColor(row, i).equals(pColor)) {
                        myCapture.add(new Integer[]{row, i});
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = col + 1; i < 8; i++) {
                    if (startingPositions[row][i] == null) {
                        legalIndexes.add(new Integer[]{row, i});
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{row, i});
                        break;
                    } else if (getPieceColor(row, i).equals(pColor)) {
                        myCapture.add(new Integer[]{row, i});
                        break;
                    } else {
                        break;
                    }
                }

                int i = row - 1, j = col + 1;

                while (i >= 0 && j < 8) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i--;
                    j++;
                }

                i = row - 1;
                j = col - 1;
                while (i >= 0 && j >= 0) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i--;
                    j--;
                }

                i = row + 1;
                j = col + 1;
                while (i < 8 && j < 8) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i++;
                    j++;
                }

                i = row + 1;
                j = col - 1;
                while (i < 8 && j >= 0) {
                    if (startingPositions[i][j] == null) {
                        legalIndexes.add(new Integer[]{i, j});
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        capture2Indexes.add(new Integer[]{i, j});
                        break;
                    } else if (getPieceColor(i, j).equals(pColor)) {
                        myCapture.add(new Integer[]{i, j});
                        break;
                    } else {
                        break;
                    }
                    i++;
                    j--;
                }

            }
        }

        List<List<Integer[]>> allIndexes = new ArrayList<>();

        allIndexes.add(legalIndexes);
        allIndexes.add(capture2Indexes);
        allIndexes.add(myCapture);
        allIndexes.add(castleIndexes);

        return allIndexes;
    }

    public static List<Integer[]> getChecks(boolean isWitheTurn) {

        List<Integer[]> captureIndexes = new ArrayList<>();

        String color = "black";

        if(isWitheTurn)
            color = "white";

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {

                if(startingPositions[row][col] != null) {

                    StackPane temp = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);
                    ChessPiece piece = (ChessPiece) temp.getChildren().get(0);
                    String wb = piece.getColor();
                    String pt = piece.getType();

                    if(startingPositions[row][col].equals("king"))
                    {
                        pt = "king2";
                    }

                    if(wb.equals(color)) {

                        if(pt.equals("pawn") && wb.equals("black"))
                        {
                            pt = "bpawn";
                        }

                        List<List<Integer[]>> moves = ChessLogic.getLegalMoves(row, col, pt, wb);

                        List<Integer[]> legal = moves.get(0);
                        List<Integer[]> capture = moves.get(1);
                        List<Integer[]> captureOwn = moves.get(2);

                        if(!pt.equals("pawn") && !pt.equals("bpawn"))
                        {
                            captureIndexes.addAll(capture);
                            captureIndexes.addAll(legal);
                        }
                        captureIndexes.addAll(captureOwn);
                    }
                }
            }
        }
        return captureIndexes;
    }

    private static String getPieceColor(int row, int col) {

        StackPane squareToCheck = (StackPane) chessBoard.getChildren().get(row * 8 + col+1);
        ChessPiece piece = (ChessPiece) squareToCheck.getChildren().get(0);

        return piece.getColor();
    }
    private static boolean hasMoved(int row, int col) {


        StackPane squareToCheck = (StackPane) chessBoard.getChildren().get(row * 8 + col+1);
        if (squareToCheck.getChildren().size() > 0)
        {
            ChessPiece piece = (ChessPiece) squareToCheck.getChildren().get(0);
            return !piece.hasMoved();
        }
        return false;

    }

    private static boolean isAttacked(List<Integer[]> asd, int row, int col)
    {
        for (Integer[] index : asd) {
            int trow = index[0];
            int tcol = index[1];
            if (row == trow && col == tcol) {
                return false;
            }
        }
        return true;
    }
}