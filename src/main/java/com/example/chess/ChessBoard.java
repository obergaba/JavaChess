package com.example.chess;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

//import java.util.Dictionary;
//import java.util.Hashtable;
public class ChessBoard extends Application {

        static double BOARD_SIZE = 600.0;
        public static double CELL_SIZE = BOARD_SIZE / 8;
        static double WINDOW_SIZE = 1000;
        static String COLOR_1 = "#dee3e6";
        static String COLOR_2 = "#8ca2ad";

        private static String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq g6 0 6";

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
        Image icon = new Image("icon.png");
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

            Pane pane = new Pane();
            pane.getChildren().add(chessBoard);

            Scene scene = new Scene(pane, WINDOW_SIZE, WINDOW_SIZE);
            scene.setFill(Color.WHITE);


            CreateTexts(pane);

            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Juicer");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();

        }


        public static String getSquareColor(int row, int col) {
            if ((row + col) % 2 == 0) {
                return COLOR_1;
            } else {
                return COLOR_2;
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
            square.setPrefSize(CELL_SIZE,CELL_SIZE );

            return square;
        }

        private ChessPiece createChessPiece(String pieceType, int row) {
            String color = (row < 2) ? "black" : "white";

            return new ChessPiece(pieceType, color, false);
        }

        private void CreateTexts(Pane pane)
        {
            //PROBLEM: Windows size needs to be a square and equal offset by board

            double cellSize = BOARD_SIZE / 8;
            double offset = (WINDOW_SIZE - BOARD_SIZE) / 2;
            int[] charOffset = {97,49};

            for (int i = 0; i < 8; i++)
            {
                for (int j = 0; j < 2; j++) {

                    double posX = (j == 0) ? ((i * cellSize + offset) + cellSize * 0.05) : ((WINDOW_SIZE - ((WINDOW_SIZE - BOARD_SIZE) / 2)) - cellSize * 0.1);
                    double posY = (j == 0) ? (((WINDOW_SIZE - BOARD_SIZE) / 2 + BOARD_SIZE) - cellSize * 0.2) : ((WINDOW_SIZE-cellSize) - (i * cellSize + offset) );
                    String coordChar = "" + (char) (charOffset[j] + i);
                    Color textColor = ((i+j)%2 == 0) ? (Color.web(COLOR_1)) : (Color.web(COLOR_2));
                    int fontSize = (int)(cellSize*0.15);

                    Label label = new Label(coordChar);
                    label.setTextFill(textColor);
                    label.setTranslateX(posX);
                    label.setTranslateY(posY);
                    label.setStyle(String.format("-fx-font-weight: bold;-fx-font-size: %dpx;", fontSize));

                    pane.getChildren().add(label);
                }

            }
        }

     /*   private void StartingPositionFromFEN(String FEN)
        {
            //rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq g6 0 6
            //8/5k2/3p4/1p1Pp2p/pP2Pp1P/P4P1K/8/8


            Dictionary<String, String> dict = new Hashtable<>();
            dict.put("r", "rook");
            dict.put("k", "knight");
            dict.put("b", "bishop");
            dict.put("q", "queen");
            dict.put("k", "king");
            dict.put("p", "pawn");

            int[] boardFEN = new int[64];
            String startPosition = FEN.split(" ")[0];

            int squareNum = 0;

            for (char piece : FEN.toCharArray())
            {
                if (piece == '/'){}

                if (Character.isDigit(piece))
                {
                    squareNum += piece - '0';
                }
                else{

                }
            }
        }*/
    }