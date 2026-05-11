import java.math.BigDecimal;
import java.math.RoundingMode;


public class FareCalculator {
     // Calculates complete fare for a journey (base fare + discount + cap).

    public static BigDecimal calculateChargedFare(Journey journey, BigDecimal currentTotal) {
        // Get base fare from dataset
        BigDecimal baseFare = CityRideDataset.getBaseFare(
                journey.getFromZone(), journey.getToZone(), journey.getTimeBand()
        );

        if (baseFare == null) {
            baseFare = BigDecimal.ZERO;
        }
        journey.setBaseFare(baseFare);

        // Calculate zones crossed
        int zonesCrossed = journey.calculateZonesCrossed();
        journey.setZonesCrossed(zonesCrossed);

        // Apply discount
        BigDecimal discountRate = journey.getDiscountRate();
        BigDecimal discountedFare = baseFare.multiply(BigDecimal.ONE.subtract(discountRate));
        discountedFare = discountedFare.setScale(2, RoundingMode.HALF_UP);

        // Apply daily cap
        BigDecimal dailyCap = journey.getDailyCap();
        BigDecimal chargedFare;

        if (currentTotal.compareTo(dailyCap) >= 0) {
            chargedFare = BigDecimal.ZERO;
        } else {
            BigDecimal newTotal = currentTotal.add(discountedFare);
            if (newTotal.compareTo(dailyCap) > 0) {
                chargedFare = dailyCap.subtract(currentTotal);
            } else {
                chargedFare = discountedFare;
            }
        }

        journey.setDiscountApplied(discountRate);
        journey.setChargedFare(chargedFare);

        return chargedFare;
    }
}