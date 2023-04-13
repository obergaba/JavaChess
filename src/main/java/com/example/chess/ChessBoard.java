package com.example.chess;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

    public class ChessBoard extends Application {
        public static String[][] startingPositions = {
                {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"},
                {"pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn"},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {"pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn", "pawn"},
                {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"}
        };

        public static boolean[][] board;
        public static boolean isWhiteTurn = true;
        public static GridPane chessBoard;
        Image icon = new Image("icon.png"); ///move all icons from C:\Users\GABA\IdeaProjects\chess\src\main\asd\icon.png to resources
        public static String pieceType;

        @Override

        public void start(Stage primaryStage) {

            //This initializes a 8x8 boolean board with true if there is a piece and keeps updating while the players makes moves.
            initializeBoolBoard();

            chessBoard = new GridPane();
            chessBoard.setGridLinesVisible(true);

            System.out.println("White to move");

            //Created the 64 chess squares using the stack-pane method and add each stack-pane to the grid-pane
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {

                    final int currentRow = row;
                    final int currentCol = col;

                    StackPane square = createSquare(row, col);
                    chessBoard.add(square, col, row);

                    pieceType = startingPositions[currentRow][currentCol];

                    if (pieceType != null) {

                        ChessPiece piece = createChessPiece(pieceType, row);
                        square.getChildren().add(piece);

                        if(row > 2) {
                            square.setOnMouseClicked(event ->
                                    ChessClick.setClick(currentRow, currentCol, square)
                            );
                        }
                    }
                }
            }

            primaryStage.getIcons().add(icon);
            Scene scene = new Scene(chessBoard, 700, 700);
            primaryStage.setTitle("Juicer");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        public static String getSquareColor(int row, int col) {
            if ((row + col) % 2 == 0) {
                return "#dee3e6";
            } else {
                return "#8ca2ad ";
            }
        }

        private void initializeBoolBoard() {

            board = new boolean[8][8];

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    String pieceType = startingPositions[i][j];
                    if (pieceType != null) {
                        board[i][j] = true;
                    }
                }
            }
        }

        private StackPane createSquare(int row, int col) {

            StackPane square = new StackPane();
            String color = getSquareColor(row, col);
            square.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-background-color: " + color);
            square.setPrefSize(87.5,87.5 );

            return square;
        }

        private ChessPiece createChessPiece(String pieceType, int row) {
            String color = (row < 2) ? "black" : "white";

            return new ChessPiece(pieceType, color);
        }
    }

