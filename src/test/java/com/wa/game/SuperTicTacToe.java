package com.wa.game;

import java.util.Scanner;

public class SuperTicTacToe {
    private static char[][][] boards;
    private static char currentPlayer = 'X';
    private static char currentBoard = ' ';

    public static void main(String[] args) {
        initializeBoards();
        System.out.println("Welcome to Super Tic-Tac-Toe!");
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

    private static void initializeBoards() {
        boards = new char[9][3][3];
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
                    char boardLabel = (char) ('A' + (i * 3) + j);
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






    private static void getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        String move;

        while (true) {
            System.out.print("Player " + currentPlayer + ", enter your move (board_row,col): ");
            move = scanner.nextLine().toUpperCase();

            if (move.length() != 5 || move.charAt(3) != ',' || !isValidBoard(move.charAt(0)) || !isValidCell(move.charAt(2), move.charAt(4))) {
                System.out.println("Invalid move format. Please use the format 'board_row,col'.");
            } else {
                int boardIndex = move.charAt(0) - 'A';
                int row = Character.getNumericValue(move.charAt(2)) - 1;
                int col = Character.getNumericValue(move.charAt(4)) - 1;

                if (boards[boardIndex][row][col] == ' ') {
                    boards[boardIndex][row][col] = currentPlayer;
                    currentBoard = move.charAt(0);
                    break;
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            }
        }
    }

    private static boolean isValidBoard(char board) {
        return board >= 'A' && board <= 'I';
    }

    private static boolean isValidCell(char row, char col) {
        return row >= '1' && row <= '3' && col >= '1' && col <= '3';
    }

    private static boolean checkForWinner() {
        return checkRowsForWinner() || checkColumnsForWinner() || checkDiagonalsForWinner() || checkBoardsForWinner();
    }

    private static boolean checkRowsForWinner() {
        int boardIndex = currentBoard - 'A';
        for (int i = 0; i < 3; i++) {
            if (checkLine(boards[boardIndex][i][0], boards[boardIndex][i][1], boards[boardIndex][i][2])) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkColumnsForWinner() {
        int boardIndex = currentBoard - 'A';
        for (int i = 0; i < 3; i++) {
            if (checkLine(boards[boardIndex][0][i], boards[boardIndex][1][i], boards[boardIndex][2][i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkDiagonalsForWinner() {
        int boardIndex = currentBoard - 'A';
        return checkLine(boards[boardIndex][0][0], boards[boardIndex][1][1], boards[boardIndex][2][2]) ||
                checkLine(boards[boardIndex][0][2], boards[boardIndex][1][1], boards[boardIndex][2][0]);
    }

    private static boolean checkBoardsForWinner() {
        int row = (currentBoard - 'A') / 3;
        int col = (currentBoard - 'A') % 3;

        // Check if the current board's move leads to a win
        return checkLine(getBoardSymbol(row, col, 0, 0), getBoardSymbol(row, col, 0, 1), getBoardSymbol(row, col, 0, 2)) ||
                checkLine(getBoardSymbol(row, col, 1, 0), getBoardSymbol(row, col, 1, 1), getBoardSymbol(row, col, 1, 2)) ||
                checkLine(getBoardSymbol(row, col, 2, 0), getBoardSymbol(row, col, 2, 1), getBoardSymbol(row, col, 2, 2)) ||
                checkLine(getBoardSymbol(row, col, 0, 0), getBoardSymbol(row, col, 1, 0), getBoardSymbol(row, col, 2, 0)) ||
                checkLine(getBoardSymbol(row, col, 0, 1), getBoardSymbol(row, col, 1, 1), getBoardSymbol(row, col, 2, 1)) ||
                checkLine(getBoardSymbol(row, col, 0, 2), getBoardSymbol(row, col, 1, 2), getBoardSymbol(row, col, 2, 2)) ||
                checkLine(getBoardSymbol(row, col, 0, 0), getBoardSymbol(row, col, 1, 1), getBoardSymbol(row, col, 2, 2)) ||
                checkLine(getBoardSymbol(row, col, 0, 2), getBoardSymbol(row, col, 1, 1), getBoardSymbol(row, col, 2, 0));
    }

    private static char getBoardSymbol(int row, int col, int subRow, int subCol) {
        int boardIndex = (row * 3) + col;
        return boards[boardIndex][subRow][subCol];
    }

    private static boolean checkLine(char a, char b, char c) {
        return a != ' ' && a == b && b == c;
    }

    private static boolean isBoardFull() {
        int boardIndex = currentBoard - 'A';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boards[boardIndex][i][j] == ' ') {
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

