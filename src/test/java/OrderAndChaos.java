import java.util.Scanner;

public class OrderAndChaos extends Game{
    private char[][] board;
    private Player currentPlayer;

    public void on(Player player1,Player player2) {
        this.player1=player1;
        this.player2=player2;
        player1.setPlay(player1.getPlay()+1);
        player2.setPlay(player2.getPlay()+1);
        currentPlayer=player1;
        int rows = 6;
        int columns = 6;
        initializeBoard(rows, columns);
        System.out.println("Welcome to Order and Chaos!");
        System.out.println("Player " +player1.getName()+",you represent order. "+"Player " +player2.getName()+",you represent chaos.");
        System.out.println("Row and Column Start With 1.");
        displayBoard();

        while (true) {
            getPlayerMove();
            displayBoard();
            if (checkForWinner()) {
                System.out.println("Player " + currentPlayer.getName() + ",order wins! Congratulations!");
                player1.setWin(currentPlayer.getWin()+1);
                break;
            }
            if (isBoardFull()) {
                System.out.println("Player " + currentPlayer.getName() + ",chaos wins! Congratulations!");
                player2.setWin(currentPlayer.getWin()+1);
                break;
            }
            togglePlayer();
        }
    }

    private  void initializeBoard(int rows, int columns) {
        board = new char[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private void displayBoard() {
        System.out.println("-+-+-+-+-+-+-+");
        for (int i = 0; i < board.length; i++) {
            System.out.print("|");
            for (int j = 0; j < board[0].length; j++) {
                char symbol = (board[i][j] == ' ') ? ' ' : board[i][j];
                System.out.print(symbol + "|");
            }
            System.out.println("\n-+-+-+-+-+-+-+");
        }
    }

    private  void getPlayerMove() {
        Scanner scanner = new Scanner(System.in);
        int row, col;

        while (true) {
            System.out.print("Player " + currentPlayer.getName() + " it's your turn! Enter your move (symbol(X or O),row[1-6],column[1-6]): ");
            String move = scanner.nextLine();
            String[] parts = move.split(",");

            if (parts.length != 3||(!move.startsWith("X")&&!move.startsWith("x")&&!move.startsWith("O")&&!move.startsWith("o"))) {
                System.out.println("Invalid move format. Please use the format 'symbol(X or O),row,column'.");
                continue;
            }

            try {
                char symbol = parts[0].charAt(0);
                row = Integer.parseInt(parts[1]) - 1;
                col = Integer.parseInt(parts[2]) - 1;

                if (row < 0 || row >= board.length || col < 0 || col >= board[0].length || board[row][col] != ' ') {
                    System.out.println("Invalid move. Try again.");
                } else {
                    board[row][col] = symbol;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid move format. Please use numbers for row and column.");
            }
        }
    }

    private  boolean checkForWinner() {
        return checkRowsForWinner() || checkColumnsForWinner() || checkDiagonalsForWinner();
    }

    private  boolean checkRowsForWinner() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j <= board[i].length - 5; j++) {
                if (checkLine(board[i][j], board[i][j + 1], board[i][j + 2], board[i][j + 3], board[i][j + 4])) {
                    return true;
                }
            }
        }
        return false;
    }

    private  boolean checkColumnsForWinner() {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j <= board.length - 5; j++) {
                if (checkLine(board[j][i], board[j + 1][i], board[j + 2][i], board[j + 3][i], board[j + 4][i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private  boolean checkDiagonalsForWinner() {
        return checkMainDiagonalsForWinner() || checkAntiDiagonalsForWinner();
    }

    private  boolean checkMainDiagonalsForWinner() {
        for (int i = 0; i <= board.length - 5; i++) {
            for (int j = 0; j <= board[i].length - 5; j++) {
                if (checkLine(board[i][j], board[i + 1][j + 1], board[i + 2][j + 2], board[i + 3][j + 3], board[i + 4][j + 4])) {
                    return true;
                }
            }
        }
        return false;
    }

    private  boolean checkAntiDiagonalsForWinner() {
        for (int i = 0; i <= board.length - 5; i++) {
            for (int j = board[i].length - 1; j >= 4; j--) {
                if (checkLine(board[i][j], board[i + 1][j - 1], board[i + 2][j - 2], board[i + 3][j - 3], board[i + 4][j - 4])) {
                    return true;
                }
            }
        }
        return false;
    }

    private  boolean checkLine(char... symbols) {
        int count = 0;
        char prevSymbol = ' ';
        for (char symbol : symbols) {
            if (symbol != ' ' && symbol == prevSymbol) {
                count++;
                if (count >= 5) {
                    return true;
                }
            } else {
                count = 1;
                prevSymbol = symbol;
            }
        }
        return false;
    }

    private  boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    private  void togglePlayer() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }
}
