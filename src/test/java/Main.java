import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Display display = new Display();
        display.print(display.mainWelcome);
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        Game game = new Game();
        while (!game.init(option)){
            System.out.println("Just three options(1, 2, 3).");
            option = scanner.nextInt();
        }
        game.on();
        System.out.println("[+] Goodbye!");
    }
}
