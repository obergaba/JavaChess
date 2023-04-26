package stockfish;

import java.io.*;

/**
 * https://github.com/rahular/chess-misc/tree/master/JavaStockfish/src/com/rahul/stockfish
 * A simple and efficient client to run Stockfish from Java
 *
 * @author Rahul A R
 *
 */
public class Stockfish {

    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    //replace PATH with your own after downloading stockfish from: https://stockfishchess.org/download/
    static String filePath = new File("").getAbsolutePath().concat("\\src\\main\\java\\stockfish\\stockfish_java\\stockfish.exe");

    private static final String PATH = filePath;

    /**
     * Starts Stockfish engine as a process and initializes it
     @param Level
      *            Skill level of stockfish engine
      *            level range min 0 max 20
      *            good luck
     * @return True on success. False otherwise
     */
    public boolean startEngine(int Level) {

        try {
            Process engineProcess = Runtime.getRuntime().exec(PATH);
            processReader = new BufferedReader(new InputStreamReader(
                    engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(
                    engineProcess.getOutputStream());
            sendCommand("uci");
            sendCommand("ucinewgame");
            sendCommand("setoption name Use NNUE");
            sendCommand("setoption name Skill Level value " + Level);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Takes in any valid UCI command and executes it
     *
     * @param command NONE
     */
    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     *
     * @param waitTime
     *            Time in milliseconds for which the function waits before
     *            reading the output. Useful when a long-running command is
     *            executed
     * @return Raw output from Stockfish
     */
    public String getOutput(int waitTime) {

        StringBuilder buffer = new StringBuilder();

        try {
            //Thread.sleep(waitTime);
            sendCommand("isready");
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok"))
                    break;
                else
                    buffer.append(text).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            processWriter.flush();
        }catch (Exception e){System.out.println("EXCEPTION ON FLUSH");}

        return buffer.toString();
    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param fen
     *            Position string
     * @param waitTime
     *            in milliseconds
     * @return Best Move in PGN format
     */
    public String getBestMove_inTime(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);
        String result = getOutput(waitTime + 300);
        int a = 1;
        try{
            String[] temp_result = result.split("\n");
            result = temp_result[temp_result.length - 1].split(" ")[1]; //.split(" ")[0];
        }
        catch (Exception e)
        {
            int as = 0;
        }
        return result;
    }
    public String getBestMove_inDepth(String fen, int depth) {
        sendCommand("position fen " + fen);
        sendCommand("go depth " + depth);

        String bestmove = "";
        //getOutput(20).split("bestmove ")[1].split(" ")[0];
        int maxLines = 1000;
        while (true)
        {
            String[] output = getOutput(20).split("\n");

            for (String line : output)
            {
                if (!line.equals("\n") && !line.equals("")) {
                    if (line.startsWith("bestmove ")) {
                        bestmove = line.split("bestmove ")[1].split(" ")[0];
                    }
                }
            }
            if (!bestmove.equals("") || maxLines <= 0)
            {
                break;
            }
            maxLines += output.length + 1;
        }

        return bestmove;
    }

    /**
     * Stops Stockfish and cleans up before closing it
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Get a list of all legal moves from the given position
     *
     * @param fen
     *            Position string
     * @return String of moves
     */
    public String getLegalMoves(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");
        return getOutput(0).split("Legal moves: ")[1];
    }

    /**
     * Draws the current state of the chess board
     *
     * @param fen
     *            Position string
     */
    public void drawBoard(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");

        String[] rows = getOutput(0).split("\n");
        for (int i = 1; i < 18; i++) {
            System.out.println(rows[i]);
        }
    }

    /**
     * Get the evaluation score of a given board position
     * @param fen Position string
     * @param waitTime in milliseconds
     * @return evalScore
     */
    public float getEvalScore(String fen, int waitTime) {

        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        float evalScore = 0.0f;
        String[] dump = getOutput(waitTime + 20).split("\n");
        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith("info depth ")) {
                try {
                    //System.out.println(dump[i] + "----------------------------------");
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" nodes")[0]);
                } catch(Exception e) {
                    try {
                        evalScore = Float.parseFloat(dump[i].split("score cp ")[1].split(" upperbound nodes")[0]);
                    }catch (Exception ii)
                    {
                        evalScore = 10000;
                    }
                }
            }
        }
        return evalScore/100;
    }


    public float getNNUE_Eval()
    {
        sendCommand("eval");
        String[] temp = getOutput(0).split("\n");
        String line = temp[temp.length -1];

        if (line.startsWith("Final evaluation"))
        {
            try {
                Float resault = Float.parseFloat(line.split("Final evaluation")[1].split("\\(")[0]);
                return resault;
            }
            catch (Exception x){
                System.out.println("Error on evaluating");
               return 333;
            }
        }
        return 0;
    }
}

