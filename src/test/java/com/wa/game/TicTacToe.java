package com.wa.game;

import java.util.Arrays;
import java.util.Scanner;

public class TicTacToe {
    private static char[][] board;
    private static char currentPlayer = 'X';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int rows, columns;

        // Get the number of rows and columns from the user, ensuring they are at least 3
        do {
            System.out.print("Enter the number of rows (must be >= 3): ");
            rows = scanner.nextInt();
        } while (rows < 3);

        do {
            System.out.print("Enter the number of columns (must be >= 3): ");
            columns = scanner.nextInt();
        } while (columns < 3);

        initializeBoard(rows, columns);
        System.out.println("Welcome to Tic Tac Toe!");
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

    private static String repeatString(String str, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(str);
        }
        return result.toString();
    }

    private static void displayBoard() {
        System.out.println(repeatString("---+", board[0].length - 1) + "---");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                char symbol = (board[i][j] == ' ') ? ' ' : board[i][j];
                System.out.print(" " + symbol + " ");
                if (j < board[0].length - 1)
                    System.out.print("|");
            }
            System.out.println();
            if (i < board.length - 1)
                System.out.println(repeatString("---+", board[0].length - 1) + "---");
        }
        System.out.println(repeatString("---+", board[0].length - 1) + "---");
    }



    private static void getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        int row, col;

        while (true) {
            System.out.print("Player " + currentPlayer + ", enter your move (row,column): ");
            String input = scanner.next();
            String[] parts = input.split(",");

            if (parts.length != 2) {
                System.out.println("Invalid input. Please enter row and column numbers separated by a comma.");
                continue;
            }

            try {
                // Adjust input to be 0-based (subtract 1)
                row = Integer.parseInt(parts[0]) - 1;
                col = Integer.parseInt(parts[1]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numeric row and column numbers.");
                continue;
            }

            if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || board[row][col] != ' ') {
                System.out.println("Invalid move. Try again.");
            } else {
                board[row][col] = currentPlayer;
                break;
            }
        }
    }

    private static boolean checkForWinner() {
        return checkRowsForWinner() || checkColumnsForWinner() || checkDiagonalsForWinner();
    }

    private static boolean checkRowsForWinner() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= board[i].length - 3; j++) {
                if (checkLine(board[i][j], Arrays.copyOfRange(board[i], j + 1, j + 3))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkColumnsForWinner() {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j <= board.length - 3; j++) {
                char[] column = new char[3];
                for (int k = 0; k < 3; k++) {
                    column[k] = board[j + k][i];
                }
                if (checkLine(column[0], Arrays.copyOfRange(column, 1, 3))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkDiagonalsForWinner() {
        int rows = board.length;
        int columns = board[0].length;

        // Check main diagonal
        for (int i = 0; i <= rows - 3; i++) {
            for (int j = 0; j <= columns - 3; j++) {
                char[] mainDiagonal = { board[i][j], board[i + 1][j + 1], board[i + 2][j + 2] };
                if (checkLine(board[i][j], mainDiagonal)) {
                    return true;
                }
            }
        }

        // Check anti-diagonal
        for (int i = 0; i <= rows - 3; i++) {
            for (int j = columns - 1; j >= 2; j--) {
                char[] antiDiagonal = { board[i][j], board[i + 1][j - 1], board[i + 2][j - 2] };
                if (checkLine(board[i][j], antiDiagonal)) {
                    return true;
                }
            }
        }

        return false;
    }



    private static boolean checkLine(char firstSymbol, char[] line) {
        if (firstSymbol == ' ') {
            return false;
        }

        for (char symbol : line) {
            if (symbol != firstSymbol) {
                return false;
            }
        }
        return true;
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

