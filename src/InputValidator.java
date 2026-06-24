import java.math.BigDecimal;

public class InputValidator {


    public int parseInteger(String input) {
        int result;
        try {
            result = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            result = Integer.MIN_VALUE;
        }
        return result;
    }

    public boolean isValidZone(int zone) {
        return zone >= CityRideDataset.MIN_ZONE && zone <= CityRideDataset.MAX_ZONE;
    }


    public CityRideDataset.PassengerType parsePassengerType(String input) {
        CityRideDataset.PassengerType result;
        switch (input.trim().toUpperCase()) {
            case "ADULT":
                result = CityRideDataset.PassengerType.ADULT;
                break;
            case "STUDENT":
                result = CityRideDataset.PassengerType.STUDENT;
                break;
            case "CHILD":
                result = CityRideDataset.PassengerType.CHILD;
                break;
            case "SENIOR":
            case "SENIOR_CITIZEN":
                result = CityRideDataset.PassengerType.SENIOR_CITIZEN;
                break;
            default:
                result = null;
        }
        return result;
    }


    public CityRideDataset.TimeBand parseTimeBand(String input) {
        CityRideDataset.TimeBand result;
        String normalised = input.trim().toUpperCase()
                .replace("-", "_").replace(" ", "_");
        switch (normalised) {
            case "PEAK":
                result = CityRideDataset.TimeBand.PEAK;
                break;
            case "OFF_PEAK":
                result = CityRideDataset.TimeBand.OFF_PEAK;
                break;
            default:
                result = null;
        }
        return result;
    }


    public BigDecimal parseMoney(String input) {
        BigDecimal result;
        try {
            result = new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            result = null;
        }
        return result;
    }


    public boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }


    public boolean isValidDiscountRate(BigDecimal rate) {
        boolean valid = rate != null
                && rate.compareTo(BigDecimal.ZERO) >= 0
                && rate.compareTo(BigDecimal.ONE) <= 0;
        return valid;
    }

    public boolean isValidHour(int hour) {
        return hour >= 0 && hour <= 23;
    }

    public boolean isNonBlank(String input) {
        return input != null && !input.trim().isEmpty();
    }

}