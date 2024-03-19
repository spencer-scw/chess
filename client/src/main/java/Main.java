import chess.*;
import ui.ChessClient;

import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var client = new ChessClient("localhost:8080");

        boolean openSession = true;

        while(openSession) {
            System.out.print("> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();

            if (Objects.equals(line, "quit")) {
                openSession = false;
            }
            System.out.println(client.eval(line));
        }

    }
}