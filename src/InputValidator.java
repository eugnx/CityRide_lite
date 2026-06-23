import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class InputValidator {
    //#(deepseek,2026)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    //#(deepseek,2026)

    private InputValidator() {}

    public static boolean isNotBlank(String input){
        return input != null && !input.trim().isEmpty();
    }

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
    //#(deepseek,2026)
    public static boolean isValidInteger(String input) {
        if (!isNotBlank(input)) {
            return false;
        }
        try {
            Integer.parseInt(input.trim()); // trim() removes any leading and trailing whitespace from the input
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // separates integers from strings

    public  Integer parseInteger(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
/// change

    public static boolean isValidTimeBand(String input) {
        return parseTimeBand(input) != null;
    }

   public static CityRideDataset.TimeBand parseTimeBand(String input) {
        if (input == null ){
            return null;
        }
        String upperInput = input.trim().toUpperCase();
        if (upperInput.equals("PEAK")){
            return CityRideDataset.TimeBand.PEAK;
        }
        if (upperInput.equals("OFF_PEAK")){
            return CityRideDataset.TimeBand.OFF_PEAK;
        }

        return null;

    }

    public static boolean isValidPassengerType(String input) {
        return parsePassengerType(input) != null;
    }

    public static CityRideDataset.PassengerType parsePassengerType(String input) {
        if (input == null) {
            return null;
        }
        String upperInput = input.trim().toUpperCase();
        switch (upperInput) {
            case "ADULT":
                return CityRideDataset.PassengerType.ADULT;
            case "STUDENT":
                return CityRideDataset.PassengerType.STUDENT;
            case "CHILD":
                return CityRideDataset.PassengerType.CHILD;
            case "SENIOR_CITIZEN":
                return CityRideDataset.PassengerType.SENIOR_CITIZEN;
                default:
                    return null;
        }
    }

    public static boolean isValidPaymentOption(String input) {
        return parsePaymentOption(input) != null;
    }

    public static PaymentOption parsePaymentOption(String input) {
        if (input == null) {
            return null;
        }

        String upperInput = input.trim().toUpperCase();
        switch (upperInput) {
            case "CONTACTLESS_CARD":
                return PaymentOption.CONTACTLESS_CARD;
            case "MOBILE_WALLET":
                return PaymentOption.MOBILE_WALLET;
            case "CASH":
                return PaymentOption.CASH;
            case "STUDENT_PASS":
                return PaymentOption.STUDENT_PASS;
            default:
                return null;
        }
    }


}