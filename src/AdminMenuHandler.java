import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;


public class AdminMenuHandler {

    private static final int ADMIN_VIEW_CONFIG       = 1;
    private static final int ADMIN_MANAGE_FARES      = 2;
    private static final int ADMIN_MANAGE_DISCOUNTS  = 3;
    private static final int ADMIN_MANAGE_CAPS       = 4;
    private static final int ADMIN_MANAGE_PEAK       = 5;
    private static final int ADMIN_CHANGE_PASSWORD   = 6;
    private static final int ADMIN_EXIT              = 7;

    private static final int SUB_ADD    = 1;
    private static final int SUB_UPDATE = 2;
    private static final int SUB_DELETE = 3;
    private static final int SUB_BACK   = 4;

    private final Scanner scanner;
    private final Admin admin;
    private final ConfigManager config;

    public AdminMenuHandler(Scanner scanner) {
        this.scanner = scanner;
        this.admin   = new Admin();
        this.config  = ConfigManager.getInstance();
    }


    // Entry point: prompts for password and, if correct, shows the admin menu.

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
                case ADMIN_VIEW_CONFIG:      viewConfig();          break;
                case ADMIN_MANAGE_FARES:     manageFares();         break;
                case ADMIN_MANAGE_DISCOUNTS: manageDiscounts();     break;
                case ADMIN_MANAGE_CAPS:      manageCaps();          break;
                case ADMIN_MANAGE_PEAK:      managePeakWindows();   break;
                case ADMIN_CHANGE_PASSWORD:  changePassword();      break;
                case ADMIN_EXIT:
                    active = false;
                    System.out.println("Exiting admin menu.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1-7.");
            }
        }
    }

    private void displayAdminMenu() {
        System.out.println("\n========== ADMIN MENU ==========");
        System.out.println("1. View Active Configuration");
        System.out.println("2. Manage Base Fares");
        System.out.println("3. Manage Passenger Discounts");
        System.out.println("4. Manage Daily Caps");
        System.out.println("5. Manage Peak Time Windows");
        System.out.println("6. Change Admin Password");
        System.out.println("7. Exit Admin Menu");
        System.out.println("=================================");
    }


    // Config display

    private void viewConfig() {
        System.out.println("\n========== ACTIVE CONFIGURATION ==========");

        System.out.println("\n--- Base Fares ---");
        System.out.printf("%-22s %-12s %-12s%n", "Route (From→To)", "PEAK (£)", "OFF_PEAK (£)");
        System.out.println("-".repeat(48));
//#(ChatGpt,2026)
        for (int from = CityRideDataset.MIN_ZONE; from <= CityRideDataset.MAX_ZONE; from++) {
            for (int to = CityRideDataset.MIN_ZONE; to <= CityRideDataset.MAX_ZONE; to++) {
                BigDecimal peak    = config.getBaseFare(from, to, CityRideDataset.TimeBand.PEAK);
                BigDecimal offPeak = config.getBaseFare(from, to, CityRideDataset.TimeBand.OFF_PEAK);
                System.out.printf("Zone %d → Zone %-9d £%-10.2f £%-10.2f%n",
                        from, to, peak, offPeak);
            }
        }

        System.out.println("\n--- Passenger Discounts ---");
        System.out.printf("%-18s %-12s%n", "Passenger Type", "Discount (%)");
        System.out.println("-".repeat(32));
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            BigDecimal rate = config.getDiscountRate(type);
            System.out.printf("%-18s %.0f%%%n", type, rate.multiply(BigDecimal.valueOf(100)));
        }

        System.out.println("\n--- Daily Caps ---");
        System.out.printf("%-18s %-10s%n", "Passenger Type", "Cap (£)");
        System.out.println("-".repeat(30));
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            BigDecimal cap = config.getDailyCap(type);
            System.out.printf("%-18s £%.2f%n", type, cap);
        }

        System.out.println("\n--- Peak Time Windows ---");
        System.out.printf("Morning peak : %02d:00 – %02d:00%n",
                config.getMorningPeakStart(), config.getMorningPeakEnd());
        System.out.printf("Evening peak : %02d:00 – %02d:00%n",
                config.getEveningPeakStart(), config.getEveningPeakEnd());
    }


    // --- Fare management ---

    private void manageFares() {
        System.out.println("\n--- Manage Base Fares ---");
        displaySubMenu("fare");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case SUB_ADD:    addBaseFare();    break;
            case SUB_UPDATE: updateBaseFare(); break;
            case SUB_DELETE: deleteBaseFare(); break;
            case SUB_BACK:   break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void addBaseFare() {
        System.out.println("\n--- Add Base Fare ---");
        System.out.println("Zone range: 1-5 | Time bands: PEAK, OFF_PEAK");
        int fromZone = getZoneInput("Enter FROM zone (1-5, e.g. 1): ");
        int toZone   = getZoneInput("Enter TO zone (1-5, e.g. 3): ");
        CityRideDataset.TimeBand band = getTimeBandInput();

        BigDecimal current = config.getBaseFare(fromZone, toZone, band);
        if (current.compareTo(BigDecimal.ZERO) > 0) {
            System.out.printf("A fare already exists for Zone %d → Zone %d (%s): £%.2f%n",
                    fromZone, toZone, band, current);
            System.out.print("Overwrite it? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                System.out.println("Add cancelled.");
                return;
            }
        }

        BigDecimal newFare = getMoneyInput("Enter new fare (e.g. 3.50): ");
        if (newFare.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Fare must be greater than £0.00. No changes saved.");
            return;
        }

        config.setBaseFare(fromZone, toZone, band, newFare);
        config.save();
        System.out.printf("Fare added: Zone %d → Zone %d (%s) = £%.2f%n",
                fromZone, toZone, band, newFare);
    }

    private void updateBaseFare() {
        System.out.println("\n--- Update Base Fare ---");
        System.out.println("Zone range: 1-5 | Time bands: PEAK, OFF_PEAK");
        int fromZone = getZoneInput("Enter FROM zone (1-5, e.g. 1): ");
        int toZone   = getZoneInput("Enter TO zone (1-5, e.g. 3): ");
        CityRideDataset.TimeBand band = getTimeBandInput();

        BigDecimal current = config.getBaseFare(fromZone, toZone, band);
        System.out.printf("Current fare for Zone %d → Zone %d (%s): £%.2f%n",
                fromZone, toZone, band, current);

        BigDecimal newFare = getMoneyInput("Enter new fare (e.g. 3.50): ");
        if (newFare.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Fare must be greater than £0.00. No changes saved.");
            return;
        }

        config.setBaseFare(fromZone, toZone, band, newFare);
        config.save();
        System.out.printf("Fare updated: Zone %d → Zone %d (%s) = £%.2f%n",
                fromZone, toZone, band, newFare);
    }

    private void deleteBaseFare() {
        System.out.println("\n--- Delete Base Fare ---");
        int fromZone = getZoneInput("Enter FROM zone (1-5, e.g. 1): ");
        int toZone   = getZoneInput("Enter TO zone (1-5, e.g. 3): ");
        CityRideDataset.TimeBand band = getTimeBandInput();

        BigDecimal current = config.getBaseFare(fromZone, toZone, band);
        System.out.printf("Current fare: Zone %d → Zone %d (%s): £%.2f%n",
                fromZone, toZone, band, current);
        System.out.print("Delete this fare? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            config.deleteBaseFare(fromZone, toZone, band);
            config.save();
            System.out.println("Fare deleted (will return £0.00 for this route).");
        } else {
            System.out.println("Delete cancelled.");
        }
    }


    // --- Discount management ---

    private void manageDiscounts() {
        System.out.println("\n--- Manage Passenger Discounts ---");
        displaySubMenu("discount");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case SUB_ADD:
            case SUB_UPDATE: updateDiscount(); break;
            case SUB_DELETE: deleteDiscount(); break;
            case SUB_BACK:   break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void updateDiscount() {
        System.out.println("\n--- Add / Update Passenger Discount ---");
        CityRideDataset.PassengerType type = getPassengerTypeInput();
        BigDecimal current = config.getDiscountRate(type);
        System.out.printf("Current discount for %s: %.0f%%%n",
                type, current.multiply(BigDecimal.valueOf(100)));
        System.out.println("Enter new discount rate as decimal (e.g. 0.25 = 25%, 0.00 = no discount):");

        BigDecimal newRate = getMoneyInput("New rate (e.g. 0.25): ");
        if (newRate.compareTo(BigDecimal.ZERO) < 0 || newRate.compareTo(BigDecimal.ONE) > 0) {
            System.out.println("Error: Discount rate must be between 0.00 and 1.00. No changes saved.");
            return;
        }

        config.setDiscountRate(type, newRate);
        config.save();
        System.out.printf("Discount for %s set to %.0f%%.%n",
                type, newRate.multiply(BigDecimal.valueOf(100)));
    }

    private void deleteDiscount() {
        System.out.println("\n--- Delete (Reset) Passenger Discount ---");
        CityRideDataset.PassengerType type = getPassengerTypeInput();
        System.out.print("Reset discount for " + type + " to 0%? (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            config.deleteDiscountRate(type);
            config.save();
            System.out.println("Discount for " + type + " reset to 0%.");
        } else {
            System.out.println("Reset cancelled.");
        }
    }


    // --- Cap management ---

    private void manageCaps() {
        System.out.println("\n--- Manage Daily Caps ---");
        displaySubMenu("cap");
        int choice = getIntInput("Enter choice: ");

        switch (choice) {
            case SUB_ADD:
            case SUB_UPDATE: updateDailyCap(); break;
            case SUB_DELETE: deleteDailyCap(); break;
            case SUB_BACK:   break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void updateDailyCap() {
        System.out.println("\n--- Add / Update Daily Cap ---");
        CityRideDataset.PassengerType type = getPassengerTypeInput();
        BigDecimal current = config.getDailyCap(type);
        System.out.printf("Current daily cap for %s: £%.2f%n", type, current);

        BigDecimal newCap = getMoneyInput("Enter new daily cap (e.g. 9.00): ");
        if (newCap.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Error: Daily cap must be greater than £0.00. No changes saved.");
            return;
        }

        config.setDailyCap(type, newCap);
        config.save();
        System.out.printf("Daily cap for %s set to £%.2f.%n", type, newCap);
    }

    private void deleteDailyCap() {
        System.out.println("\n--- Delete (Remove) Daily Cap ---");
        CityRideDataset.PassengerType type = getPassengerTypeInput();
        System.out.print("Remove daily cap for " + type +
                "? This sets it to £999.00 (effectively uncapped). (Y/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
            config.deleteDailyCap(type);
            config.save();
            System.out.println("Daily cap for " + type + " removed (set to £999.00).");
        } else {
            System.out.println("Delete cancelled.");
        }
    }


    // --- Peak window management ---

    private void managePeakWindows() {
        System.out.println("\n--- Manage Peak Time Windows ---");
        System.out.printf("Current morning peak : %02d:00 – %02d:00%n",
                config.getMorningPeakStart(), config.getMorningPeakEnd());
        System.out.printf("Current evening peak : %02d:00 – %02d:00%n",
                config.getEveningPeakStart(), config.getEveningPeakEnd());
        System.out.println("1. Update morning peak window");
        System.out.println("2. Update evening peak window");
        System.out.println("3. Back");

        int choice = getIntInput("Enter choice: ");
        switch (choice) {
            case 1: updateMorningPeak(); break;
            case 2: updateEveningPeak(); break;
            case 3: break;
            default: System.out.println("Invalid choice.");
        }
    }

    private void updateMorningPeak() {
        System.out.println("\n--- Update Morning Peak Window ---");
        System.out.println("Enter hours in 24h format (0-23). Example: start=7, end=9 means 07:00-09:00.");
        int start = getHourInput("New morning peak start hour (e.g. 7): ");
        int end   = getHourInput("New morning peak end hour (e.g. 9): ");
        if (end <= start) {
            System.out.println("Error: End hour must be after start hour. No changes saved.");
            return;
        }
        config.setMorningPeak(start, end);
        config.save();
        System.out.printf("Morning peak window updated to %02d:00 – %02d:00.%n", start, end);
    }

    private void updateEveningPeak() {
        System.out.println("\n--- Update Evening Peak Window ---");
        System.out.println("Enter hours in 24h format (0-23). Example: start=17, end=19 means 17:00-19:00.");
        int start = getHourInput("New evening peak start hour (e.g. 17): ");
        int end   = getHourInput("New evening peak end hour (e.g. 19): ");
        if (end <= start) {
            System.out.println("Error: End hour must be after start hour. No changes saved.");
            return;
        }
        config.setEveningPeak(start, end);
        config.save();
        System.out.printf("Evening peak window updated to %02d:00 – %02d:00.%n", start, end);
    }


    // --- Password change ---

    private void changePassword() {
        System.out.println("\n--- Change Admin Password ---");
        System.out.print("Enter current password: ");
        String current = scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPwd = scanner.nextLine().trim();
        System.out.print("Confirm new password: ");
        String confirm = scanner.nextLine().trim();

        if (!newPwd.equals(confirm)) {
            System.out.println("Error: Passwords do not match. No changes made.");
            return;
        }
        if (newPwd.isEmpty()) {
            System.out.println("Error: Password cannot be blank.");
            return;
        }

        boolean changed = admin.changePassword(current, newPwd);
        if (changed) {
            System.out.println("Password changed successfully.");
        } else {
            System.out.println("Error: Current password is incorrect.");
        }
    }


    // --- Input helpers ---

    private void displaySubMenu(String item) {
        System.out.println("1. Add " + item);
        System.out.println("2. Update " + item);
        System.out.println("3. Delete " + item);
        System.out.println("4. Back");
    }

    private int getZoneInput(String prompt) {
        while (true) {
            int zone = getIntInput(prompt);
            if (InputValidator.isValidZone(zone)) return zone;
            System.out.println("Error: Zone must be between 1 and 5.");
        }
    }

    private int getHourInput(String prompt) {
        while (true) {
            int hour = getIntInput(prompt);
            if (hour >= 0 && hour <= 23) return hour;
            System.out.println("Error: Hour must be between 0 and 23.");
        }
    }

    private CityRideDataset.TimeBand getTimeBandInput() {
        while (true) {
            System.out.print("Enter time band (PEAK or OFF_PEAK, e.g. PEAK): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidTimeBand(input)) return InputValidator.parseTimeBand(input);
            System.out.println("Error: Invalid time band. Enter PEAK or OFF_PEAK.");
        }
    }

    private CityRideDataset.PassengerType getPassengerTypeInput() {
        while (true) {
            System.out.print("Enter passenger type (ADULT/STUDENT/CHILD/SENIOR_CITIZEN): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (InputValidator.isValidPassengerType(input)) return InputValidator.parsePassengerType(input);
            System.out.println("Error: Invalid passenger type.");
        }
    }

    private BigDecimal getMoneyInput(String prompt) {
        InputValidator validator = new InputValidator();
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            BigDecimal value = validator.parseMoney(input);
            if (value != null) return value;
            System.out.println("Error: Please enter a valid monetary value (e.g. 3.50).");
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (InputValidator.isValidInteger(input)) return Integer.parseInt(input);
            System.out.println("Error: Please enter a valid number.");
        }
    }
}
