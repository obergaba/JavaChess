package com.example.chess;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import stockfish.Stockfish;

import java.util.*;

//TODO: En passant
//TODO: Highlight check square
//TODO: Double checks
//TODO: Stalemate
//TODO: Update Fen for Stockfish
//TODO: Optimize ChessLogic

public class ChessBoard extends Application {

        static double BOARD_SIZE = 600.0;
        public static double CELL_SIZE = BOARD_SIZE / 8;
        static double WINDOW_SIZE = 800;
        static String COLOR_1 = "#dee3e6";
        static String COLOR_2 = "#8ca2ad";

        final static String STARTING_FEN = "r3k2r/8/b7/8/8/8/P7/R3K3 w - - 0 27";

        public static String[][] startingPositions = new String[8][8];
        public static int[][] startingPositionColors = new int[8][8];

        public static boolean[][] board;
        public static boolean isWhiteTurn = true;

        public static boolean boolCheck= false;
        public static GridPane chessBoard;
        Image icon = new Image("icon.png");
        public static String pieceType;

        @Override

        public void start(Stage primaryStage) {

            Integer[] temp = {-1, -1};

            List<Integer[]> list = new ArrayList<>(0);

            startingPositions = StartingPositionFromFEN();
            initializeBoolBoard();

            chessBoard = new GridPane();
            chessBoard.setGridLinesVisible(true);
            chessBoard.setAlignment(Pos.CENTER);

            chessBoard.setTranslateX((WINDOW_SIZE - BOARD_SIZE) / 2);
            chessBoard.setTranslateY((WINDOW_SIZE - BOARD_SIZE) / 2);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {

                    final int currentRow = row;
                    final int currentCol = col;

                    StackPane square = createSquare(row, col);
                    chessBoard.add(square, col, row);

                    pieceType = startingPositions[currentRow][currentCol];

                    if (pieceType != null) {

                        //ChessPiece piece = createChessPiece(pieceType, row);
                        String color = (startingPositionColors[row][col] == 1) ? "white" : "black";
                        ChessPiece piece = new ChessPiece(pieceType, color, false);

                        square.getChildren().add(piece);

                        if(color.equals("white")) {
                            square.setOnMouseClicked(event ->
                                    ChessClick.setClick(currentRow, currentCol, square, list, temp)
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

            //Stockfish client = new Stockfish();

            // if (client.startEngine()) {
            //     System.out.println("Engine has started..");
            //} else {
            //   System.out.println("Oops! Something went wrong..");
            //}

            //System.out.println(client.getOutput(0));
            //System.out.println("Best move : " + client.getBestMove_inTime(STARTING_FEN, 100));

            //System.out.println("Evaluation score : " + client.getEvalScore(STARTING_FEN, 10000));
            //System.out.println("Evaluation with NNUE: " + client.getNNUE_Eval());

            //client.stopEngine();

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

        private String[][] StartingPositionFromFEN()
        {

            Dictionary<Character, String> dict = new Hashtable<>();
            dict.put('r', "rook");
            dict.put('n', "knight");
            dict.put('b', "bishop");
            dict.put('q', "queen");
            dict.put('k', "king");
            dict.put('p', "pawn");

            String[] boardFEN = new String[64];
            int[] boardFENcol = new int[64];
            String startPosition = ChessBoard.STARTING_FEN.split(" ")[0];

            int squareNum = 0;

            for (char piece : startPosition.toCharArray())
            {
                char pieceBoard = Character.toLowerCase(piece);
                int col = (Character.isLowerCase(piece)) ? 0 : 1;

                if (piece == '/'){continue;}

                if (Character.isDigit(piece))
                {
                    squareNum += (piece - '0');
                }
                else{
                    boardFEN[squareNum] = dict.get(pieceBoard);
                    boardFENcol[squareNum] = col;
                    squareNum += 1;
                }
            }

            String[][] returnFEN = new String[8][8];

            int counter = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    returnFEN[i][j] = boardFEN[counter];
                    startingPositionColors[i][j] = boardFENcol[counter];
                    counter++;
                }
            }

            return returnFEN;
        }
    }