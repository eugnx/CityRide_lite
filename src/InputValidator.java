import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class InputValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");

    public static boolean isValidZone(int zone) {
        return zone >= CityRideDataset.MIN_ZONE && zone <= CityRideDataset.MAX_ZONE;
    }

    public static boolean isValidDate(String dateStr) {
        // try-catch is used to handle runtime errors so the program does not crash
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isValidInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(input.trim()); // trim() removes any leading and trailing whitespace from the input
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidTimeBand(String input) {
        if (input == null) return false;
        String upper = input.trim().toUpperCase();
        return upper.equals("PEAK") || upper.equals("OFF_PEAK");
    }

    public static CityRideDataset.TimeBand parseTimeBand(String input) {
        String upper = input.trim().toUpperCase();
        if (upper.equals("PEAK")) return CityRideDataset.TimeBand.PEAK;
        if (upper.equals("OFF_PEAK")) return CityRideDataset.TimeBand.OFF_PEAK;
        return null;
    }

    public static boolean isValidPassengerType(String input) {
        if (input == null) return false;
        String upper = input.trim().toUpperCase();
        return upper.equals("ADULT") || upper.equals("STUDENT") ||
                upper.equals("CHILD") || upper.equals("SENIOR_CITIZEN");
    }

    public static CityRideDataset.PassengerType parsePassengerType(String input) {
        String upper = input.trim().toUpperCase();
        switch (upper) {
            case "ADULT":
                return CityRideDataset.PassengerType.ADULT;
            case "STUDENT":
                return CityRideDataset.PassengerType.STUDENT;
            case "CHILD":
                return CityRideDataset.PassengerType.CHILD;
            case "SENIOR_CITIZEN":
                return CityRideDataset.PassengerType.SENIOR_CITIZEN;
            default: return null;
        }
    }
}