import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Load configuration from file on startup (falls back to defaults if missing).
        ConfigManager.getInstance();

        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=========================================");
        System.out.println("       WELCOME TO CITYRIDE LITE");
        System.out.println("=========================================");
        System.out.println("Please select a role:");
        System.out.println("  1. Rider");
        System.out.println("  2. Admin");
        System.out.println("  3. Exit");

        boolean running = true;

        while (running) {
            System.out.print("Enter your choice (1-3): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    MenuHandler menuHandler = new MenuHandler(scanner);
                    menuHandler.start();
                    running = false;
                    break;

                case "2":
                    AdminMenuHandler adminMenu = new AdminMenuHandler(scanner);
                    adminMenu.start();
                    scanner.close();
                    running = false;
                    break;

                case "3":
                    System.out.println("\nGoodbye!");
                    scanner.close();
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }
}