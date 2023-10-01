package com.wa.game;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuperTicTacToe {
    private static char[][][] boards; // 3D array to represent the 9 boards
    private static boolean[] boardWon; // Array to track if a board is won
    private char currentPlayer;
    private char nextBoard; // Represents the next board to play in

    public SuperTicTacToe() {
        boards = new char[9][3][3];
        boardWon = new boolean[9]; // Initialize all boards as not won
        currentPlayer = 'X'; // Player 'X' starts the game
        nextBoard = '0'; // Start on any board
        initializeBoards();
    }

    private void initializeBoards() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    boards[i][j][k] = ' ';
                }
            }
        }
    }

    private static void displayBoard() {
        // Loop through each row in a board
        for (int i = 0; i < 3; i++) {
            // Loop through each board in a row
            for (int j = 0; j < 3; j++) {
                System.out.print("+---+---+---+   ");
            }
            System.out.println();

            // Loop through each row in a board
            for (int k = 0; k < 3; k++) {
                // Loop through each board in a row
                for (int j = 0; j < 3; j++) {
                    System.out.print("| ");
                    int boardIndex = (i * 3) + j;

                    // Loop through each column in a row of a board
                    for (int l = 0; l < 3; l++) {
                        char symbol = (boards[boardIndex][k][l] == ' ') ? ' ' : boards[boardIndex][k][l];
                        System.out.print(symbol + " | ");
                    }
                    System.out.print("  ");
                }
                System.out.println();
            }
        }

        // Print the horizontal line between boards
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
                System.out.print("+---");
            }
            System.out.print("+   ");
        }
        System.out.println("");
    }

    private boolean isBoardFull(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }


    private boolean updateBoardWon(char[][] board, char player, int boardIndex) {
        // Update the boardWon array for the specified board
        boardWon[boardIndex] = isBoardWon(board, player);
        return boardWon[boardIndex];
    }

    private boolean isBoardWon(char[][] board, char player) {
        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true; // Row win
            }
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true; // Column win
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true; // Diagonal win (top-left to bottom-right)
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true; // Diagonal win (top-right to bottom-left)
        }
        return false;
    }

    private boolean isGameWon(char player) {
        // Check if the player has won the game (won three boards in a line)
        // ... (updated logic)

        // Check rows, columns, and diagonals for a win
        for (int i = 0; i < 3; i++) {
            if (boardWon[i] && boardWon[i + 3] && boardWon[i + 6]) {
                return true; // Column win
            }
            if (boardWon[i * 3] && boardWon[i * 3 + 1] && boardWon[i * 3 + 2]) {
                return true; // Row win
            }
        }
        if (boardWon[0] && boardWon[4] && boardWon[8]) {
            return true; // Diagonal win (top-left to bottom-right)
        }
        if (boardWon[2] && boardWon[4] && boardWon[6]) {
            return true; // Diagonal win (top-right to bottom-left)
        }

        return false;
    }

    private char getNextBoard(char currentBoard) {
        char next = (char) (currentBoard + 1);
        if (next > 'I') {
            next = 'A';
        }
        return next;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    String regex = "^[A-Ia-i][,\\s][1-9][,\\s][1-9]$";

    Pattern pattern = Pattern.compile(regex);

    public void playGame() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Current board: " + (nextBoard=='0'?"Any":nextBoard));
            displayBoard();

            System.out.print("Player " + currentPlayer + ", enter your move (e.g., E 1,1): ");
            String move = scanner.nextLine().toUpperCase();
            if (move.equals("QUIT")) {
                System.out.println("Game ended.");
                break;
            }

            Matcher matcher = pattern.matcher(move);
            if (!matcher.matches()) {
                System.out.println("Invalid move.(e.g., E 1,1)");
                continue;
            }

            char selectedBoard = move.charAt(0);
            //to upper case
            if (selectedBoard>='a'&&selectedBoard<='i'){
                selectedBoard-=('a'-'A');
            }
            int row = Character.getNumericValue(move.charAt(2)) - 1;
            int col = Character.getNumericValue(move.charAt(4)) - 1;

            if (selectedBoard<'A'||selectedBoard>'I') {
                System.out.println("Invalid board, board must in A to I");
                continue;
            }
            int selectedBoardIndex = selectedBoard-'A';
            if (boardWon[selectedBoardIndex]) {
                System.out.println("Invalid board, board "+selectedBoard+" already end");
                continue;
            }

            if (nextBoard!='0'&&selectedBoard != nextBoard) {
                System.out.println("Invalid move. You must play in board " + nextBoard);
                continue;
            }

            int boardIndex = selectedBoard - 'A';

            if (boards[boardIndex][row][col] != ' ') {
                System.out.println("Invalid move. Cell already taken.");
                continue;
            }

            boards[boardIndex][row][col] = currentPlayer;
            nextBoard='1';//just mean it should be changed
            if (updateBoardWon(boards[boardIndex], currentPlayer, boardIndex)){
                System.out.println("Player "+currentPlayer+" Won Board "+(char)('A'+boardIndex));
                nextBoard='0';//Any
            }

            if (isGameWon(currentPlayer)) {
                System.out.println("Player " + currentPlayer + " wins the game!");
                break;
            } else if (isBoardFull(boards[boardIndex])) {
                System.out.println("Board " + selectedBoard + " is full. It's a draw on this board.");
                nextBoard='0';//Any
            } else if (nextBoard!='0'){
                nextBoard = (char) ('A' + (row * 3 + col));
                //if the next board already end,player can pick any board to continue
                if (boardWon[nextBoard-'A']){
                    nextBoard='0';
                }
            }

            switchPlayer();
        }

        scanner.close();
    }

    public static void main(String[] args) {
        SuperTicTacToe game = new SuperTicTacToe();
        game.playGame();
    }
}

