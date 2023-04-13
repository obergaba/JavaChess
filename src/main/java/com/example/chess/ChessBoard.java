package com.example.chess;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;




public class ChessBoard extends Application {

        static double BOARD_SIZE = 800.0;
        static double CELL_SIZE = BOARD_SIZE / 8;
        static int WINDOW_SIZE = 1000;

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

            initializeBoolBoard();

            chessBoard = new GridPane();
            chessBoard.setGridLinesVisible(true);
            chessBoard.setAlignment(Pos.CENTER);

            chessBoard.setTranslateX((WINDOW_SIZE - BOARD_SIZE) / 2);
            chessBoard.setTranslateY((WINDOW_SIZE - BOARD_SIZE) / 2);

            System.out.println("White to move");

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {

                    final int currentRow = row;
                    final int currentCol = col;

                    StackPane square = createSquare(row, col, CELL_SIZE);
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

            Pane pane = new Pane();
            pane.getChildren().add(chessBoard);

            //Scene scene = new Scene(chessBoard, 800, 800);
            Scene scene = new Scene(pane, WINDOW_SIZE, WINDOW_SIZE);
            scene.setFill(Color.WHITE);


            CreateTexts(pane, (int)BOARD_SIZE, (int)(WINDOW_SIZE-BOARD_SIZE));
            primaryStage.getIcons().add(icon);
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

        private StackPane createSquare(int row, int col, double size) {

            StackPane square = new StackPane();
            String color = getSquareColor(row, col);
            square.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-padding: 0; -fx-background-color: " + color);
            square.setPrefSize(CELL_SIZE,CELL_SIZE );

            return square;
        }

        private ChessPiece createChessPiece(String pieceType, int row) {
            String color = (row < 2) ? "black" : "white";

            return new ChessPiece(pieceType, color);
        }

        private void CreateTexts(Pane pane, int boardSize, int offset)
        {
            int cellSize = boardSize / 8;
            for (int i = 0; i < 8; i++)
            {
                char a = 97;
                String aa = ""+a;
                Label label = new Label(aa);
                label.setTextFill(Color.BLUE);
                label.setTranslateX(i*(cellSize)+(offset/2));
                label.setTranslateY((WINDOW_SIZE-BOARD_SIZE)/2);

                pane.getChildren().add(label);

            }
        }
    }
