import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game extends Player {
    int id;
    String name;
    List<Team> teams;
    Player player1;
    Player player2;
    Board board;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean init(int id){
        setId(id);
        if (id==1){
            name="Tic Tac Toe";
        }else if (id==2){
            name="Order and Chaos";
        }else if (id==3){
            name="Super Tic Tac Toe";
        }else {
            return false;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print("[+] Enter the number of teams: \nInput: ");
        int numTeams = scanner.nextInt();
        scanner.nextLine();  // Consume the newline character
        while (numTeams<2){
            System.out.print("[+] Enter the number of teams(at least 2): \nInput: ");
            numTeams = scanner.nextInt();
            scanner.nextLine();
        }
        teams = new ArrayList<>();
        for (int i = 1; i <= numTeams; i++) {
            Team team = new Team();
            System.out.print("[+] Team " + i + " , please enter your team name: \nInput: ");
            team.name = scanner.nextLine();

            System.out.print("[+] Enter the number of players for team " + team.name + ": \nInput: ");
            int numPlayers = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            System.out.print("[+] Team " + i + " , please enter your team color: \nInput: ");
            team.color = scanner.nextLine();

            team.players = new ArrayList<>();
            for (int j = 1; j <= numPlayers; j++) {
                Player player = new Player();
                System.out.print("[+] Team " + i + ", Player " + j + " , please enter your name: \nInput: ");
                player.name = scanner.nextLine();

                System.out.print("[+] Team " + i + ", Player " + j + " , please enter your symbol: \nInput: ");
                player.symbol = scanner.next().charAt(0);
                scanner.nextLine();  // Consume the newline character

                team.players.add(player);
            }

            teams.add(team);
        }

        // Output the information
        System.out.println("Output:");
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            System.out.println("[+] Team " + (i + 1) + ": " + team.name + " (" + team.color + ")");
            for (int j = 0; j < team.players.size(); j++) {
                Player player = team.players.get(j);
                System.out.println("  - Player " + (j + 1) + ": " + player.name + " (" + player.symbol + ")");
            }
        }
        return true;
    }

    public void on(){
        if (id==1){
            selectPlayer();
            TicTacToe ticTacToe= new TicTacToe();
            ticTacToe.on(player1,player2);
        }else if (id==2){
            selectPlayer();
            OrderAndChaos orderAndChaos = new OrderAndChaos();
            orderAndChaos.on(player1,player2);
        }else if (id==3){
            selectPlayer();
            SuperTicTacToe superTicTacToe = new SuperTicTacToe();
            superTicTacToe.on(player1,player2);
        }
        displayPlayerStats();
        System.out.println("[+] Would you like to play a different game? (y/n):");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equalsIgnoreCase("Y")&&!input.equalsIgnoreCase("N")&&!input.equalsIgnoreCase("quit")){
            System.out.println("[+] Would you like to play a different game? (y/n):");
            input = scanner.nextLine();
        }
        if (input.equalsIgnoreCase("Y")){
            System.out.println(Display.mainWelcome);
            int option = scanner.nextInt();
            while (option!=1&&option!=2&&option!=3){
                System.out.println("Just three options(1, 2, 3).");
                option = scanner.nextInt();
            }
            id=option;
            on();
        }
    }

    public void selectPlayer(){
        Random random = new Random();
        Team team1=teams.get(random.nextInt(teams.size()));
        Team team2=teams.get(random.nextInt(teams.size()));
        while (team1.getName().equals(team2.getName())){
            team2=teams.get(random.nextInt(teams.size()));
        }
        player1 = team1.getPlayers().get(random.nextInt(team1.getPlayers().size()));
        player2 = team2.getPlayers().get(random.nextInt(team2.getPlayers().size()));
    }

    public void displayPlayerStats(){
        for (Team team:teams) {
            List<Player> players = team.getPlayers();
            for (Player player : players) {
                System.out.println("TEAM: " + team.getName() + ", PLAYER: " + player.name + " played " + player.symbol + "'s.");
                System.out.println("WINS: " + player.getWin() + "\nLOSSES: " + (player.getPlay()-player.getWin())
                        + "\nGAMES PLAYED: " + player.getPlay() + "\n");
            }
        }
    }
}
