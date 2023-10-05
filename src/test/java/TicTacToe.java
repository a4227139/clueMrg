import java.util.Arrays;
import java.util.Scanner;

public class TicTacToe extends Game{
    private char[][] board;
    private Player currentPlayer;

    public void on(Player player1,Player player2) {
        this.player1=player1;
        this.player2=player2;
        player1.setPlay(player1.getPlay()+1);
        player2.setPlay(player2.getPlay()+1);
        currentPlayer=player1;
        Scanner scanner = new Scanner(System.in);
        int rows, columns;

        // Get the number of rows and columns from the user, ensuring they are at least 3
        do {
            System.out.print("Enter the number of rows (must be >= 3) to play Tic Tac Toe: ");
            rows = scanner.nextInt();
        } while (rows < 3);

        columns=rows;

        initializeBoard(rows, columns);
        System.out.println("Welcome to Tic Tac Toe!");
        System.out.println("Row and Column Start With 1.");
        displayBoard();

        while (true) {
            getPlayerMove();
            displayBoard();
            if (checkForWinner()) {
                System.out.println("Player " + currentPlayer.getName() + " wins! Congratulations!");
                currentPlayer.setWin(currentPlayer.getWin()+1);
                break;
            }
            if (isBoardFull()) {
                System.out.println("It's a draw! The game is a stalemate.");
                break;
            }
            togglePlayer();
        }
    }

    private void initializeBoard(int rows, int columns) {
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

    private void displayBoard() {
        System.out.println(repeatString("----+", board[0].length - 1) + "----");
        for (int i = 0; i < board.length; i++) {
            System.out.print("|");
            for (int j = 0; j < board[0].length; j++) {
                char symbol = (board[i][j] == ' ') ? ' ' : board[i][j];
                System.out.print(" " + symbol + " ");
                if (j < board[0].length - 1)
                    System.out.print("|");
            }
            System.out.print("|");
            System.out.println();
            if (i < board.length - 1)
                System.out.println(repeatString("----+", board[0].length - 1) + "----");
        }
        System.out.println(repeatString("----+", board[0].length - 1) + "----");
    }



    private void getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        int row, col;

        while (true) {
            System.out.print("Player " + currentPlayer.getName() + " it's your turn! Enter your move (row,column): ");
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
                board[row][col] = currentPlayer.getSymbol();
                break;
            }
        }
    }

    private boolean checkForWinner() {
        return checkRowsForWinner() || checkColumnsForWinner() || checkDiagonalsForWinner();
    }

    private boolean checkRowsForWinner() {
        int n = board.length;
        for (int i = 0; i < n; i++) {
            if (checkLine(board[i][0], board[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumnsForWinner() {
        int n = board.length;
        for (int j = 0; j < n; j++) {
            char[] column = new char[n];
            for (int i = 0; i < n; i++) {
                column[i] = board[i][j];
            }
            if (checkLine(column[0], column)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonalsForWinner() {
        int n = board.length;
        // Check main diagonal
        char[] mainDiagonal = new char[n];
        for (int i = 0; i < n; i++) {
            mainDiagonal[i] = board[i][i];
        }
        if (checkLine(mainDiagonal[0], mainDiagonal)) {
            return true;
        }

        // Check anti-diagonal
        char[] antiDiagonal = new char[n];
        for (int i = 0; i < n; i++) {
            antiDiagonal[i] = board[i][n - 1 - i];
        }
        if (checkLine(antiDiagonal[0], antiDiagonal)) {
            return true;
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

    private boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private void togglePlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}

