module com.example.chess {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens com.example.chess to javafx.fxml;
    exports com.example.chess;
    exports stockfish;
    opens stockfish to javafx.fxml;
}