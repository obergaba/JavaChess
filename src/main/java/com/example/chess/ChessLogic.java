package com.example.chess;

import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

public class ChessLogic extends ChessBoard {
    public static List<boolean[][]> getLegalMoves(boolean[][] board, int row, int col, String pieceType, String pColor) {

        boolean[][] legalMoves = new boolean[8][8];
        boolean[][] captureMoves = new boolean[8][8];

        switch (pieceType) {

            case "pawn" -> {

                if (row > 0 && !board[row-1][col]) {
                    legalMoves[row-1][col] = true;
                    if (row == 6 && !board[row-2][col]) {
                        legalMoves[row-2][col] = true;
                    }
                }
                if (row > 0 && col > 0 && board[row-1][col-1] && !getPieceColor(row-1, col-1).equals(pColor)) {
                    captureMoves[row-1][col-1] = true;
                }
                if (row > 0 && col < 7 && board[row-1][col+1] && !getPieceColor(row-1, col+1).equals(pColor)) {
                    captureMoves[row-1][col+1] = true;
                }
            }

            case "bpawn" -> {
                if (row < 7 && !board[row+1][col]) {
                    legalMoves[row+1][col] = true;
                    if (row == 1 && !board[row+2][col]) {
                        legalMoves[row+2][col] = true;
                    }
                }
                if (row < 7 && col > 0 && board[row+1][col-1] && !getPieceColor(row+1, col-1).equals(pColor)) {
                    captureMoves[row+1][col-1] = true;
                }
                if (row < 7 && col < 7 && board[row+1][col+1] && !getPieceColor(row+1, col+1).equals(pColor)) {
                    captureMoves[row+1][col+1] = true;
                }
            }


            case "knight" -> {

                int[] rowOffsets = {2, 2, -2, -2, 1, 1, -1, -1};
                int[] colOffsets = {-1, 1, -1, 1, 2, -2, 2, -2};

                for (int i = 0; i < rowOffsets.length; i++) {
                    int newRow = row + rowOffsets[i];
                    int newCol = col + colOffsets[i];
                    if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                        if (!board[newRow][newCol]) {
                            legalMoves[newRow][newCol] = true;
                        } else if (!getPieceColor(newRow, newCol).equals(pColor)) {
                            captureMoves[newRow][newCol] = true;
                        }
                    }
                }
            }

            case "bishop" -> {

                int i = row - 1, j = col + 1;
                while (i >= 0 && j < 8) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
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
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
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
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
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
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
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
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        captureMoves[i][col] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = row + 1; i < 8; i++) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        captureMoves[i][col] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = col - 1; i >= 0; i--) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        captureMoves[row][i] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = col + 1; i < 8; i++) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        captureMoves[row][i] = true;
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

                for (int i = 0; i < rowOff.length; i++) {
                    int newRow = row + rowOff[i];
                    int newCol = col + colOff[i];
                    if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                        if (!board[newRow][newCol]) {
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
                                legalMoves[newRow][newCol] = true;
                            }
                        } else if (!getPieceColor(newRow, newCol).equals(pColor)) {
                            boolean canMovew = true;

                            for (Integer[] index : captureIndexes) {
                                int trow = index[0];
                                int tcol = index[1];
                                if (newRow == trow && newCol == tcol) {
                                    canMovew = false;
                                    break;
                                }
                            }
                            if(canMovew)
                                captureMoves[newRow][newCol] = true;
                        }
                    }
                }
            }




            case "queen" -> {

                for (int i = row - 1; i >= 0; i--) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        captureMoves[i][col] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = row + 1; i < 8; i++) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else if (!getPieceColor(i, col).equals(pColor)) {
                        captureMoves[i][col] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = col - 1; i >= 0; i--) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        captureMoves[row][i] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = col + 1; i < 8; i++) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else if (!getPieceColor(row, i).equals(pColor)) {
                        captureMoves[row][i] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = row - 1, j = col + 1; i >= 0 && j < 8; i--, j++) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
                        break;
                    } else {
                        break;
                    }
                }

                for (int i = row + 1, j = col - 1; i < 8 && j >= 0; i++, j--) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
                        break;
                    } else {
                        break;
                    }
                }
                for (int i = row + 1, j = col + 1; i < 8 && j < 8; i++, j++) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else if (!getPieceColor(i, j).equals(pColor)) {
                        captureMoves[i][j] = true;
                        break;
                    } else {
                        break;
                    }
                }
            }
        }

        List<boolean[][]> movesList = new ArrayList<>();

        movesList.add(legalMoves);
        movesList.add(captureMoves);

        return movesList;
    }

    public static List<Integer[]> getTrueIndexes(boolean[][] arr) {

        List<Integer[]> trueIndexes = new ArrayList<>();

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j]) {
                    Integer[] index = {i, j};
                    trueIndexes.add(index);
                }
            }
        }

        return trueIndexes;
    }

    public static List<Integer[]> getChecks(boolean isWitheTurn) {

        List<Integer[]> captureIndexes = new ArrayList<>();

        String color = "black";

        if(isWitheTurn)
            color = "white";

        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {

                if(startingPositions[row][col] != null && !startingPositions[row][col].equals("king")) {

                    StackPane temp = (StackPane) chessBoard.getChildren().get(row * 8 + col + 1);
                    ChessPiece piece = (ChessPiece) temp.getChildren().get(0);
                    String wb = piece.getColor();
                    String pt = piece.getType();

                    if(wb.equals(color)) {

                        List<boolean[][]> moves = ChessLogic.getLegalMoves2(board, row, col, pt);

                        boolean[][] leg = moves.get(0);
                        boolean[][] capMoves = moves.get(1);

                        captureIndexes.addAll(ChessLogic.getTrueIndexes(capMoves));
                        captureIndexes.addAll(ChessLogic.getTrueIndexes(leg));

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
    public static List<boolean[][]> getLegalMoves2(boolean[][] board, int row, int col, String pieceType) {

        boolean[][] legalMoves = new boolean[8][8];
        boolean[][] captureMoves = new boolean[8][8];

        switch (pieceType) {

            case "pawn" -> {

                if (row > 0 && col > 0 && board[row-1][col-1]) {
                    captureMoves[row-1][col-1] = true;
                }
                if (row > 0 && col < 7 && board[row-1][col+1]) {
                    captureMoves[row-1][col+1] = true;
                }
            }


            case "knight" -> {

                int[] rowOffsets = {2, 2, -2, -2, 1, 1, -1, -1};
                int[] colOffsets = {-1, 1, -1, 1, 2, -2, 2, -2};
                for (int i = 0; i < rowOffsets.length; i++) {
                    int newRow = row + rowOffsets[i];
                    int newCol = col + colOffsets[i];
                    if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                            legalMoves[newRow][newCol] = true;
                    }
                }
            }

            case "bishop" -> {

                int i = row - 1, j = col + 1;
                while (i >= 0 && j < 8) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                    i--;
                    j++;
                }

                i = row - 1;
                j = col - 1;

                while (i >= 0 && j >= 0) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                    i--;
                    j--;
                }

                i = row + 1;
                j = col + 1;
                while (i < 8 && j < 8) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                    i++;
                    j++;
                }

                i = row + 1;
                j = col - 1;
                while (i < 8 && j >= 0) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                    i++;
                    j--;
                }
            }

            case "rook" -> {

                for (int i = row - 1; i >= 0; i--) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else  {
                        captureMoves[i][col] = true;
                        break;
                    }
                }

                for (int i = row + 1; i < 8; i++) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else  {
                        captureMoves[i][col] = true;
                        break;
                    }
                }

                for (int i = col - 1; i >= 0; i--) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else  {
                        captureMoves[row][i] = true;
                        break;
                    }
                }

                for (int i = col + 1; i < 8; i++) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else  {
                        captureMoves[row][i] = true;
                        break;
                    }
                }
            }


            case "queen" -> {

                for (int i = row - 1; i >= 0; i--) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else  {
                        captureMoves[i][col] = true;
                        break;
                    }
                }

                for (int i = row + 1; i < 8; i++) {
                    if (!board[i][col]) {
                        legalMoves[i][col] = true;
                    } else  {
                        captureMoves[i][col] = true;
                        break;
                    }
                }

                for (int i = col - 1; i >= 0; i--) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else  {
                        captureMoves[row][i] = true;
                        break;
                    }
                }

                for (int i = col + 1; i < 8; i++) {
                    if (!board[row][i]) {
                        legalMoves[row][i] = true;
                    } else {
                        captureMoves[row][i] = true;
                        break;
                    }
                }

                for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                }

                for (int i = row - 1, j = col + 1; i >= 0 && j < 8; i--, j++) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                }

                for (int i = row + 1, j = col - 1; i < 8 && j >= 0; i++, j--) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                }
                for (int i = row + 1, j = col + 1; i < 8 && j < 8; i++, j++) {
                    if (!board[i][j]) {
                        legalMoves[i][j] = true;
                    } else  {
                        captureMoves[i][j] = true;
                        break;
                    }
                }
            }
        }

        List<boolean[][]> movesList = new ArrayList<>();

        movesList.add(legalMoves);
        movesList.add(captureMoves);

        return movesList;
    }
}
