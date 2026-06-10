import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class InputValidator {
    //#(deepseek,2026)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    //#(deepseek,2026)
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

    public boolean  isNotBlank(String Input){
        boolean isValid;
        if(Input == null)
             isValid = false;
        return !Input.trim().isEmpty();
    }
    // separates integers from strings

    public  Integer parseInteger(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isValidTimeBand(String input) {
        boolean isValid = false;

        if (input != null) {
            String upperInput = input.trim().toUpperCase();
            isValid = upperInput.equals("PEAK") || upperInput.equals("OFF_PEAK");
        }

        return isValid;
    }

   public static CityRideDataset.TimeBand parseTimeBand(String input) {
        CityRideDataset.TimeBand band = null;

        if (input != null) {
            String upperInput = input.trim().toUpperCase();
            if (upperInput.equals("PEAK")) {
                band = CityRideDataset.TimeBand.PEAK;
            } else if (upperInput.equals("OFF_PEAK")) {
                band = CityRideDataset.TimeBand.OFF_PEAK;
            }
        }

        return band;
    }

    public static boolean isValidPassengerType(String input) {
        boolean isValid = false;

        if (input != null) {
            String upperInput = input.trim().toUpperCase();
            isValid = upperInput.equals("ADULT") ||
                    upperInput.equals("STUDENT") ||
                    upperInput.equals("CHILD") ||
                    upperInput.equals("SENIOR_CITIZEN");
        }

        return isValid;
    }

    public static CityRideDataset.PassengerType parsePassengerType(String input) {
        CityRideDataset.PassengerType type = null;

        if (input != null) {
            String upperInput = input.trim().toUpperCase();
            switch (upperInput) {
                case "ADULT":
                    type = CityRideDataset.PassengerType.ADULT;
                    break;
                case "STUDENT":
                    type = CityRideDataset.PassengerType.STUDENT;
                    break;
                case "CHILD":
                    type = CityRideDataset.PassengerType.CHILD;
                    break;
                case "SENIOR_CITIZEN":
                    type = CityRideDataset.PassengerType.SENIOR_CITIZEN;
                    break;
                default:
                    type = null;
            }
        }

        return type;
    }

}