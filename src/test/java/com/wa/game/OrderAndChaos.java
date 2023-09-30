package com.wa.game;

import java.util.Scanner;

public class OrderAndChaos {
    private static char[][] board;
    private static char currentPlayer = 'X';

    public static void main(String[] args) {
        int rows = 6;
        int columns = 6;
        initializeBoard(rows, columns);
        System.out.println("Welcome to Order and Chaos!");
        System.out.println("Row and Column Start With 1.");
        displayBoard();

        while (true) {
            getPlayerMove();
            displayBoard();
            if (checkForWinner()) {
                System.out.println("Player " + currentPlayer + " wins! Congratulations!");
                break;
            }
            if (isBoardFull()) {
                System.out.println("It's a draw! The game is a stalemate.");
                break;
            }
            togglePlayer();
        }
    }

    private static void initializeBoard(int rows, int columns) {
        board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private static void displayBoard() {
        System.out.println(" +-+-+-+-+-+-+");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                char symbol = (board[i][j] == ' ') ? ' ' : board[i][j];
                System.out.print(symbol + "|");
            }
            System.out.println("\n +-+-+-+-+-+-+");
        }
    }

    private static void getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        int row, col;

        while (true) {
            System.out.print("Player " + currentPlayer + ", enter your move (row[1-6],column[1-6]): ");
            String move = scanner.nextLine();
            String[] parts = move.split(",");

            if (parts.length != 2) {
                System.out.println("Invalid move format. Please use the format 'row,column'.");
                continue;
            }

            try {
                row = Integer.parseInt(parts[0]) - 1;
                col = Integer.parseInt(parts[1]) - 1;

                if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || board[row][col] != ' ') {
                    System.out.println("Invalid move. Try again.");
                } else {
                    board[row][col] = currentPlayer;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid move format. Please use numbers for row and column.");
            }
        }
    }

    private static boolean checkForWinner() {
        return checkRowsForWinner() || checkColumnsForWinner() || checkDiagonalsForWinner();
    }

    private static boolean checkRowsForWinner() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= board[i].length - 5; j++) {
                if (checkLine(board[i][j], board[i][j + 1], board[i][j + 2], board[i][j + 3], board[i][j + 4])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkColumnsForWinner() {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j <= board.length - 5; j++) {
                if (checkLine(board[j][i], board[j + 1][i], board[j + 2][i], board[j + 3][i], board[j + 4][i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkDiagonalsForWinner() {
        return checkMainDiagonalsForWinner() || checkAntiDiagonalsForWinner();
    }

    private static boolean checkMainDiagonalsForWinner() {
        for (int i = 0; i <= board.length - 5; i++) {
            for (int j = 0; j <= board[i].length - 5; j++) {
                if (checkLine(board[i][j], board[i + 1][j + 1], board[i + 2][j + 2], board[i + 3][j + 3], board[i + 4][j + 4])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkAntiDiagonalsForWinner() {
        for (int i = 0; i <= board.length - 5; i++) {
            for (int j = board[i].length - 1; j >= 4; j--) {
                if (checkLine(board[i][j], board[i + 1][j - 1], board[i + 2][j - 2], board[i + 3][j - 3], board[i + 4][j - 4])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkLine(char... symbols) {
        int count = 0;
        char prevSymbol = ' ';
        for (char symbol : symbols) {
            if (symbol != ' ' && symbol == prevSymbol) {
                count++;
                if (count >= 5) {
                    return true;  // Exactly 5 in a row, it's a win
                }
            } else {
                count = 1;
                prevSymbol = symbol;
            }
        }
        return false;  // Less than 5 in a row, not a win
    }

    private static boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private static void togglePlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }
}
