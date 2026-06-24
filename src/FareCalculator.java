import java.math.BigDecimal;
import java.math.RoundingMode;


public final class FareCalculator {

    private FareCalculator() {}

    // Calculates the number of zones crossed by a journey using |to - from| + 1.

    public static int calculateZonesCrossed(int fromZone, int toZone) {
        return Math.abs(toZone - fromZone) + 1;
    }


    //Retrieves the base fare for a zone pair and time band from the dataset.

    public static BigDecimal getBaseFare(int fromZone, int toZone, CityRideDataset.TimeBand timeBand) {
        BigDecimal fare = CityRideDataset.getBaseFare(fromZone, toZone, timeBand);
        return fare != null ? fare : BigDecimal.ZERO;
    }


    // Applies a discount rate to a base fare.

    public static BigDecimal applyDiscount(BigDecimal baseFare, BigDecimal discountRate) {
        BigDecimal discountAmount = baseFare.multiply(discountRate);
        BigDecimal discountedFare = baseFare.subtract(discountAmount);
        return discountedFare.setScale(2, RoundingMode.HALF_UP);
    }

    // Applies the daily cap, returning the portion of the discounted fare that

    public static BigDecimal applyDailyCap(BigDecimal discountedFare,
                                           BigDecimal currentTotal,
                                           BigDecimal dailyCap) {
        BigDecimal newTotal = currentTotal.add(discountedFare);
        BigDecimal chargedFare;

        if (newTotal.compareTo(dailyCap) > 0) {
            chargedFare = dailyCap.subtract(currentTotal);
            if (chargedFare.compareTo(BigDecimal.ZERO) < 0) {
                chargedFare = BigDecimal.ZERO;
            }
        } else {
            chargedFare = discountedFare;
        }

        return chargedFare.setScale(2, RoundingMode.HALF_UP);
    }


    // Calculates the complete fare for a journey, setting all intermediate values
    // (zones crossed, base fare, discount, charged fare) on the journey object.

    public static BigDecimal calculateFullFare(Journey journey, BigDecimal currentTotal) {
        int zonesCrossed = journey.calculateZonesCrossed();
        journey.setZonesCrossed(zonesCrossed);

        BigDecimal baseFare = getBaseFare(journey.getFromZone(), journey.getToZone(), journey.getTimeBand());
        journey.setBaseFare(baseFare);

        BigDecimal discountedFare = applyDiscount(baseFare, journey.getDiscountRate());

        BigDecimal dailyCap = journey.getDailyCap();
        BigDecimal chargedFare = applyDailyCap(discountedFare, currentTotal, dailyCap);

        journey.setDiscountApplied(journey.getDiscountRate());
        journey.setChargedFare(chargedFare);

        return chargedFare;
    }
}
