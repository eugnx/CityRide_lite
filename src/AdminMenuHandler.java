import java.math.BigDecimal;
import java.util.Scanner;


public class AdminMenuHandler {

    private static final int ADMIN_VIEW_CONFIG    = 1;
    private static final int ADMIN_UPDATE_FARE    = 2;
    private static final int ADMIN_UPDATE_DISCOUNT = 3;
    private static final int ADMIN_UPDATE_CAP     = 4;
    private static final int ADMIN_EXIT           = 5;

    private final Scanner scanner;
    private final Admin admin;

    public AdminMenuHandler(Scanner scanner) {
        this.scanner = scanner;
        this.admin = new Admin();
    }

     //Entry point: prompts for password and, if correct, shows the admin menu.

    public void start() {
        System.out.println("\n--- Admin Login ---");
        System.out.print("Enter admin password: ");
        String attempt = scanner.nextLine().trim();

        if (!admin.verifyPassword(attempt)) {
            System.out.println("Error: Incorrect password. Access denied.");
            return;
        }

        System.out.println("Access granted. Welcome, Admin.");
        runAdminMenu();
    }


    // Private menu methods


    private void runAdminMenu() {
        boolean active = true;

        while (active) {
            displayAdminMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case ADMIN_VIEW_CONFIG:
                    viewConfig();
                    break;
                case ADMIN_UPDATE_FARE:
                    updateBaseFare();
                    break;
                case ADMIN_UPDATE_DISCOUNT:
                    updateDiscount();
                    break;
                case ADMIN_UPDATE_CAP:
                    updateDailyCap();
                    break;
                case ADMIN_EXIT:
                    active = false;
                    System.out.println("Exiting admin menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-5.");
            }
        }
    }

    private void displayAdminMenu() {
        System.out.println("\n========== ADMIN MENU ==========");
        System.out.println("1. View Active Configuration");
        System.out.println("2. Update Base Fare");
        System.out.println("3. Update Passenger Discount");
        System.out.println("4. Update Daily Cap");
        System.out.println("5. Exit Admin Menu");
        System.out.println("=================================");
    }


    // Config display


     // Displays the full active system configuration including fares, discounts, and caps.

    private void viewConfig() {
        System.out.println("\n========== ACTIVE CONFIGURATION ==========");

        System.out.println("\n--- Base Fares ---");
        System.out.printf("%-20s %-12s %-12s%n", "Route (From→To)", "PEAK (£)", "OFF_PEAK (£)");
        System.out.println("-".repeat(46));

        for (int from = CityRideDataset.MIN_ZONE; from <= CityRideDataset.MAX_ZONE; from++) {
            for (int to = CityRideDataset.MIN_ZONE; to <= CityRideDataset.MAX_ZONE; to++) {
                BigDecimal peakFare = CityRideDataset.getBaseFare(from, to, CityRideDataset.TimeBand.PEAK);
                BigDecimal offPeakFare = CityRideDataset.getBaseFare(from, to, CityRideDataset.TimeBand.OFF_PEAK);
                System.out.printf("Zone %d → Zone %-7d £%-10.2f £%-10.2f%n",
                        from, to, peakFare, offPeakFare);
            }
        }

        System.out.println("\n--- Passenger Discounts ---");
        System.out.printf("%-18s %-12s%n", "Passenger Type", "Discount (%)");
        System.out.println("-".repeat(32));
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            BigDecimal rate = CityRideDataset.DISCOUNT_RATE.get(type);
            System.out.printf("%-18s %.0f%%%n", type, rate.multiply(BigDecimal.valueOf(100)));
        }

        System.out.println("\n--- Daily Caps ---");
        System.out.printf("%-18s %-10s%n", "Passenger Type", "Cap (£)");
        System.out.println("-".repeat(30));
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            BigDecimal cap = CityRideDataset.DAILY_CAP.get(type);
            System.out.printf("%-18s £%.2f%n", type, cap);
        }

        System.out.println("\nNote: Base fares are fixed in the current dataset.");
        System.out.println("      Discounts and caps are read from CityRideDataset.");
    }


    // Update operations (validated before applying)

     // Prompts the admin to enter a new base fare for a given zone pair and time band.
    private void updateBaseFare() {
        System.out.println("\n--- Update Base Fare ---");
        System.out.println("Zone range: 1-5 | Time bands: PEAK, OFF_PEAK");

        int fromZone = getZoneInput("Enter FROM zone (1-5): ");
        int toZone   = getZoneInput("Enter TO zone (1-5): ");
        CityRideDataset.TimeBand band = getTimeBandInput();

        BigDecimal current = CityRideDataset.getBaseFare(fromZone, toZone, band);
        System.out.printf("Current fare for Zone %d → Zone %d (%s): £%.2f%n",
                fromZone, toZone, band, current);

        BigDecimal newFare = getMoneyInput("Enter new fare (e.g. 3.50): ");
        if (newFare.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Fare must be greater than £0.00. No changes saved.");
            return;
        }

        // CityRideDataset.BASE_FARE is an unmodifiable map. In a production system
        // this would update a mutable config object or database. Here we confirm
        // the validated value and inform the admin.
        System.out.printf("Validated fare: £%.2f for Zone %d → Zone %d (%s).%n",
                newFare, fromZone, toZone, band);
        System.out.println("Note: Runtime fare overrides require a mutable config layer.");
        System.out.println("      This value has been validated and would be persisted in a full implementation.");
    }


     // Prompts the admin to update the discount rate for a passenger type.

    private void updateDiscount() {
        System.out.println("\n--- Update Passenger Discount ---");

        CityRideDataset.PassengerType type = getPassengerTypeInput();
        BigDecimal current = CityRideDataset.DISCOUNT_RATE.get(type);
        System.out.printf("Current discount for %s: %.0f%%%n",
                type, current.multiply(BigDecimal.valueOf(100)));

        System.out.println("Enter new discount rate as a decimal (0.00 = 0%, 0.25 = 25%, 1.00 = 100%):");
        BigDecimal newRate = getMoneyInput("New rate: ");

        if (newRate.compareTo(BigDecimal.ZERO) < 0 || newRate.compareTo(BigDecimal.ONE) > 0) {
            System.out.println("Error: Discount rate must be between 0.00 and 1.00. No changes saved.");
            return;
        }

        System.out.printf("Validated discount for %s: %.0f%%.%n",
                type, newRate.multiply(BigDecimal.valueOf(100)));
        System.out.println("Note: Runtime discount overrides require a mutable config layer.");
    }


     // Prompts the admin to update the daily cap for a passenger type.
    private void updateDailyCap() {
        System.out.println("\n--- Update Daily Cap ---");

        CityRideDataset.PassengerType type = getPassengerTypeInput();
        BigDecimal current = CityRideDataset.DAILY_CAP.get(type);
        System.out.printf("Current daily cap for %s: £%.2f%n", type, current);

        BigDecimal newCap = getMoneyInput("Enter new daily cap (e.g. 9.00): ");
        if (newCap.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Daily cap must be greater than £0.00. No changes saved.");
            return;
        }

        System.out.printf("Validated daily cap for %s: £%.2f.%n", type, newCap);
        System.out.println("Note: Runtime cap overrides require a mutable config layer.");
    }


    // Input helpers
    private int getZoneInput(String prompt) {
        while (true) {
            int zone = getIntInput(prompt);
            if (InputValidator.isValidZone(zone)) {
                return zone;
            }
            System.out.println("Error: Zone must be between 1 and 5.");
        }
    }

    private CityRideDataset.TimeBand getTimeBandInput() {
        while (true) {
            System.out.print("Enter time band (PEAK / OFF_PEAK): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidTimeBand(input)) {
                return InputValidator.parseTimeBand(input);
            }
            System.out.println("Error: Invalid time band. Enter PEAK or OFF_PEAK.");
        }
    }

    private CityRideDataset.PassengerType getPassengerTypeInput() {
        while (true) {
            System.out.print("Enter passenger type (ADULT/STUDENT/CHILD/SENIOR_CITIZEN): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidPassengerType(input)) {
                return InputValidator.parsePassengerType(input);
            }
            System.out.println("Error: Invalid passenger type.");
        }
    }

    private BigDecimal getMoneyInput(String prompt) {
        InputValidator validator = new InputValidator();
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            BigDecimal value = validator.parseMoney(input);
            if (value != null) {
                return value;
            }
            System.out.println("Error: Please enter a valid monetary value (e.g. 3.50).");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (InputValidator.isValidInteger(input)) {
                return Integer.parseInt(input);
            }
            System.out.println("Error: Please enter a valid number.");
        }
    }
}