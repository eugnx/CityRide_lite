import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class MenuHandler {

    private static final int MENU_ADD_JOURNEY = 1;
    private static final int MENU_LIST_JOURNEYS = 2;
    private static final int MENU_EXIT = 3;

    private Scanner scanner;
    private JourneyManager journeyManager;
    private boolean running;

    public MenuHandler() {
        this.scanner = new Scanner(System.in);
        this.journeyManager = new JourneyManager();
        this.running = true;
    }

    public void start() {
        displayWelcomeMessage();

        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            processMenuChoice(choice);
        }

        System.out.println("\nThank you for using CityRide Lite!");
        scanner.close(); //  used to close the Scanner object when you are finished using it
    }

    private void displayWelcomeMessage() {
        System.out.println("\n=========================================");
        System.out.println("     WELCOME TO CITYRIDE LITE");
        System.out.println("=========================================");
    }

    private void displayMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Add Journey");
        System.out.println("2. List All Journeys");
        System.out.println("3. Exit");
        System.out.println("================================");
    }

    private void processMenuChoice(int choice) {
        if (choice == MENU_ADD_JOURNEY) {
            addJourney();
        } else if (choice == MENU_LIST_JOURNEYS) {
            listAllJourneys();
        } else if (choice == MENU_EXIT) {
            running = false;
        } else {
            System.out.println("Invalid choice! Please enter 1-3.");
        }
    }

    private void addJourney() {
        System.out.println("\n--- Add New Journey ---");

        LocalDate date = getDateInput("Enter date (DD/MM/YYYY) or press Enter for today: ");
        CityRideDataset.TimeBand timeBand = getTimeBandInput();
        int fromZone = getZoneInput("Enter starting zone (1-5): ");
        int toZone = getZoneInput("Enter destination zone (1-5): ");
        CityRideDataset.PassengerType passengerType = getPassengerTypeInput();

        Journey journey = new Journey(0, date, timeBand, fromZone, toZone, passengerType);
        Journey completedJourney = journeyManager.addJourney(journey);

        System.out.println("\n✓ Journey added successfully!");
        System.out.println("  Journey ID: " + completedJourney.getJourneyId());
        System.out.printf("  Zones crossed: %d%n", completedJourney.getZonesCrossed());
        System.out.printf("  Base fare: £%.2f%n", completedJourney.getBaseFare());
        System.out.printf("  Charged fare: £%.2f%n", completedJourney.getChargedFare());

        if (journeyManager.isCapReached(passengerType)) {
            System.out.println("  ⚠ Daily cap reached for " + passengerType + "!");
        }
    }

    private void listAllJourneys() {
        List<Journey> journeys = journeyManager.getAllJourneys();

        System.out.println("\n--- All Journeys ---");

        if (journeys.isEmpty()) {
            System.out.println("No journeys recorded yet.");
        } else {
            System.out.println("\nTotal journeys: " + journeys.size());
            System.out.println("-".repeat(80));

            for (Journey journey : journeys) {
                System.out.println(journey);
            }

            System.out.println("-".repeat(80));
            System.out.printf("Total cost for the day: £%.2f%n", journeyManager.getTotalCost());
        }
    }

    private LocalDate getDateInput(String prompt) {
        boolean valid = false;
        String input = "";

        while (valid == false) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return LocalDate.now();
            }

            if (InputValidator.isValidDate(input)) {
                valid = true;
            } else {
                System.out.println("Error: Invalid date format! Use DD/MM/YYYY");
            }
        }

        return InputValidator.parseDate(input);
    }

    private CityRideDataset.TimeBand getTimeBandInput() {
        boolean valid = false;
        CityRideDataset.TimeBand band = null;

        while (valid == false) {
            System.out.print("Enter time band (PEAK / OFF_PEAK): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (InputValidator.isValidTimeBand(input)) {
                band = InputValidator.parseTimeBand(input);
                valid = true;
            } else {
                System.out.println("Error: Invalid time band! Enter PEAK or OFF_PEAK");
            }
        }
        return band;
    }


    private int getZoneInput(String prompt) {
        boolean valid = false;
        int zone = -1;

        while (valid == false) {
            zone = getIntInput(prompt);

            if (InputValidator.isValidZone(zone)) {
                valid = true;
            } else {
                System.out.println("Error: Zone must be between 1 and 5");
            }
        }

        return zone;
    }

    private CityRideDataset.PassengerType getPassengerTypeInput() {
        boolean valid = false;
        CityRideDataset.PassengerType type = null;

        while (!valid) {
            System.out.print("Enter passenger type (ADULT/STUDENT/CHILD/SENIOR_CITIZEN): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (InputValidator.isValidPassengerType(input)) {
                type = InputValidator.parsePassengerType(input);
                valid = true;
            } else {
                System.out.println("Error: Invalid passenger type!");
            }
        }

        return type;
    }


    private int getIntInput(String prompt) {
        boolean valid = false;
        int value = 0;

        while (!valid) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Error: Input cannot be blank!");
            } else if (InputValidator.isValidInteger(input)) {
                value = Integer.parseInt(input);
                valid = true;
            } else {
                System.out.println("Error: Please enter a valid number!");
            }
        }

        return value;
    }
}