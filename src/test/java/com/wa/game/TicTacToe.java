package com.wa.game;

import java.util.Scanner;

public class TicTacToe {
    private static char[][] board = new char[3][3];
    private static char currentPlayer = 'X';

    public static void main(String[] args) {
        initializeBoard();
        System.out.println("Welcome to Tic Tac Toe!");
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

    private static void initializeBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
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

            if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ') {
                System.out.println("Invalid move. Try again.");
            } else {
                board[row][col] = currentPlayer;
                break;
            }
        }
    }

    private static void displayBoard() {
        System.out.println("+---+---+---+");
        for (int i = 0; i < 3; i++) {
            System.out.print("|");
            for (int j = 0; j < 3; j++) {
                char symbol = (board[i][j] == ' ') ? ' ' : board[i][j];
                System.out.print(" " + symbol + " |");
            }
            System.out.println("\n+---+---+---+");
        }
    }



    private static boolean checkForWinner() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true; // Horizontal win
            }
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) {
                return true; // Vertical win
            }
        }

        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true; // Diagonal win (top-left to bottom-right)
        }
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) {
            return true; // Diagonal win (top-right to bottom-left)
        }

        return false;
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
