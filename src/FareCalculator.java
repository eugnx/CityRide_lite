import java.math.BigDecimal;
import java.math.RoundingMode;


public class FareCalculator {

    private final SystemConfig config;

     //Calculates zones crossed using formula |to - from| + 1.

    public static int calculateZonesCrossed(int fromZone, int toZone) {
        int zones;
        zones = Math.abs(toZone - fromZone) + 1;
        return zones;
    }

     // Gets base fare from CityRideDataset.

    public static BigDecimal getBaseFare(int fromZone, int toZone, CityRideDataset.TimeBand timeBand) {
        BigDecimal fare;
        fare = CityRideDataset.getBaseFare(fromZone, toZone, timeBand);

        if (fare == null) {
            fare = BigDecimal.ZERO;
        }

        return fare;
    }

     // Applies discount to base fare.

    public static BigDecimal applyDiscount(BigDecimal baseFare, BigDecimal discountRate) {
        BigDecimal discountAmount;
        BigDecimal discountedFare;

        discountAmount = baseFare.multiply(discountRate);
        discountedFare = baseFare.subtract(discountAmount);
        discountedFare = discountedFare.setScale(2, RoundingMode.HALF_UP);

        return discountedFare;
    }

     // Calculates charged fare with daily cap consideration.

    public static BigDecimal applyDailyCap(BigDecimal discountedFare, BigDecimal currentTotal, BigDecimal dailyCap) {
        BigDecimal newTotal;
        BigDecimal chargedFare;

        newTotal = currentTotal.add(discountedFare);

        if (newTotal.compareTo(dailyCap) > 0) {
            chargedFare = dailyCap.subtract(currentTotal);
            if (chargedFare.compareTo(BigDecimal.ZERO) < 0) {
                chargedFare = BigDecimal.ZERO;
            }
        } else {
            chargedFare = discountedFare;
        }

        chargedFare = chargedFare.setScale(2, RoundingMode.HALF_UP);

        return chargedFare;
    }

     // Calculates the complete fare for a journey.

    public static BigDecimal calculateFullFare(Journey journey, BigDecimal currentTotal) {
        int zonesCrossed;
        BigDecimal baseFare;
        BigDecimal discountedFare;
        BigDecimal dailyCap;
        BigDecimal chargedFare;

        // Calculate zones crossed
        zonesCrossed = journey.calculateZonesCrossed();
        journey.setZonesCrossed(zonesCrossed);

        // Get base fare from dataset
        baseFare = getBaseFare(journey.getFromZone(), journey.getToZone(), journey.getTimeBand());
        journey.setBaseFare(baseFare);

        // Apply passenger discount
        discountedFare = applyDiscount(baseFare, journey.getDiscountRate());

        // Apply daily cap
        dailyCap = journey.getDailyCap();
        chargedFare = applyDailyCap(discountedFare, currentTotal, dailyCap);

        // Store discount rate and charged fare
        journey.setDiscountApplied(journey.getDiscountRate());
        journey.setChargedFare(chargedFare);

        return chargedFare;
    }
}