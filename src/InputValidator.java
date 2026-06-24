import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InputValidator {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    // --- Integer helpers ---

    public static boolean isValidInteger(String input) {
        if (input == null || input.trim().isEmpty()) return false;
        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int parseInteger(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    // --- Zone helpers ---

    public static boolean isValidZone(int zone) {
        return zone >= CityRideDataset.MIN_ZONE && zone <= CityRideDataset.MAX_ZONE;
    }

    // --- Passenger type helpers ---

    public static boolean isValidPassengerType(String input) {
        if (input == null) return false;
        switch (input.trim().toUpperCase()) {
            case "ADULT":
            case "STUDENT":
            case "CHILD":
            case "SENIOR":
            case "SENIOR_CITIZEN":
                return true;
            default:
                return false;
        }
    }

    public static CityRideDataset.PassengerType parsePassengerType(String input) {
        if (input == null) return null;
        switch (input.trim().toUpperCase()) {
            case "ADULT":          return CityRideDataset.PassengerType.ADULT;
            case "STUDENT":        return CityRideDataset.PassengerType.STUDENT;
            case "CHILD":          return CityRideDataset.PassengerType.CHILD;
            case "SENIOR":
            case "SENIOR_CITIZEN": return CityRideDataset.PassengerType.SENIOR_CITIZEN;
            default:               return null;
        }
    }

    // --- Time band helpers ---

    public static boolean isValidTimeBand(String input) {
        if (input == null) return false;
        String n = input.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        return n.equals("PEAK") || n.equals("OFF_PEAK");
    }

    public static CityRideDataset.TimeBand parseTimeBand(String input) {
        if (input == null) return null;
        String n = input.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        switch (n) {
            case "PEAK":     return CityRideDataset.TimeBand.PEAK;
            case "OFF_PEAK": return CityRideDataset.TimeBand.OFF_PEAK;
            default:         return null;
        }
    }

    // --- Date helpers ---

    public static boolean isValidDate(String input) {
        if (input == null || input.trim().isEmpty()) return false;
        try {
            LocalDate.parse(input.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDate parseDate(String input) {
        return LocalDate.parse(input.trim(), DATE_FORMATTER);
    }

    // --- Time helpers ---

    public static boolean isValidTime(String input) {
        if (input == null || input.trim().isEmpty()) return false;
        try {
            LocalTime.parse(input.trim(), TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalTime parseTime(String input) {
        return LocalTime.parse(input.trim(), TIME_FORMATTER);
    }

    // --- Payment option helpers ---

    public static PaymentOption parsePaymentOption(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            int index = Integer.parseInt(input.trim()) - 1;
            PaymentOption[] options = PaymentOption.values();
            if (index >= 0 && index < options.length) return options[index];
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --- Money helpers ---

    public BigDecimal parseMoney(String input) {
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --- Misc helpers ---

    public static boolean isNonBlank(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isValidDiscountRate(BigDecimal rate) {
        return rate != null
                && rate.compareTo(BigDecimal.ZERO) >= 0
                && rate.compareTo(BigDecimal.ONE) <= 0;
    }

    public boolean isValidHour(int hour) {
        return hour >= 0 && hour <= 23;
    }
}
