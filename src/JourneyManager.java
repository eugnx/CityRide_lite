import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JourneyManager {

    // Private fields
    private List<Journey> journeys;
    private int nextJourneyId;

    // Running totals per passenger type
    private Map<CityRideDataset.PassengerType, BigDecimal> runningTotals;
    private Map<CityRideDataset.PassengerType, Boolean> capReached;

    // Tracking for detailed reporting
    private Map<CityRideDataset.PassengerType, BigDecimal> preDiscountTotals;
    private Map<CityRideDataset.PassengerType, BigDecimal> discountedTotals;
    private Map<CityRideDataset.PassengerType, Integer> journeyCountByType;


    public JourneyManager() {
        this.journeys = new ArrayList<>();
        this.nextJourneyId = 1;
        this.runningTotals = new HashMap<>();
        this.capReached = new HashMap<>();
        this.preDiscountTotals = new HashMap<>();
        this.discountedTotals = new HashMap<>();
        this.journeyCountByType = new HashMap<>();

        // Initialize for each passenger type
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            runningTotals.put(type, BigDecimal.ZERO);
            capReached.put(type, false);
            preDiscountTotals.put(type, BigDecimal.ZERO);
            discountedTotals.put(type, BigDecimal.ZERO);
            journeyCountByType.put(type, 0);
        }
    }


    public Journey addJourney(Journey journey) {
        CityRideDataset.PassengerType passengerType;
        BigDecimal currentTotal;
        BigDecimal chargedFare;
        boolean capAlreadyReached;

        passengerType = journey.getPassengerType();
        currentTotal = runningTotals.get(passengerType);
        capAlreadyReached = capReached.get(passengerType);

        // Get base fare for pre-discount tracking
        BigDecimal baseFare = FareCalculator.getBaseFare(
                journey.getFromZone(), journey.getToZone(), journey.getTimeBand()
        );

        // Update pre-discount total
        BigDecimal newPreDiscountTotal = preDiscountTotals.get(passengerType).add(baseFare);
        preDiscountTotals.put(passengerType, newPreDiscountTotal);

        // Update journey count
        journeyCountByType.put(passengerType, journeyCountByType.get(passengerType) + 1);

        // Check if cap already reached
        if (capAlreadyReached) {
            chargedFare = BigDecimal.ZERO;
            journey.setChargedFare(chargedFare);
            journey.setZonesCrossed(journey.calculateZonesCrossed());
            journey.setBaseFare(baseFare);
            journey.setDiscountApplied(journey.getDiscountRate());
        } else {
            // Calculate fare with cap consideration
            chargedFare = FareCalculator.calculateFullFare(journey, currentTotal);

            // Update discounted total for reporting
            BigDecimal discountedFare = FareCalculator.applyDiscount(baseFare, journey.getDiscountRate());
            BigDecimal newDiscountedTotal = discountedTotals.get(passengerType).add(discountedFare);
            discountedTotals.put(passengerType, newDiscountedTotal);
        }

        // Set journey ID
        journey.setJourneyId(nextJourneyId);
        nextJourneyId++;

        // Update running total
        BigDecimal newTotal = runningTotals.get(passengerType).add(chargedFare);
        runningTotals.put(passengerType, newTotal);

        // Check if cap is now reached
        if (newTotal.compareTo(journey.getDailyCap()) >= 0) {
            capReached.put(passengerType, true);
        }

        // Add to list
        journeys.add(journey);

        return journey;
    }

    public List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }


    public Journey getJourneyById(int id) {
        Journey foundJourney = null;

        for (Journey journey : journeys) {
            if (journey.getJourneyId() == id) {
                foundJourney = journey;
                break;
            }
        }
        return foundJourney;
    }


    public boolean removeJourneyById(int id) {
        Journey journeyToRemove;
        boolean removed;

        journeyToRemove = getJourneyById(id);

        if (journeyToRemove == null) {
            removed = false;
        } else {
            journeys.remove(journeyToRemove);
            recalculateAllTotals();
            removed = true;
        }

        return removed;
    }

    private void recalculateAllTotals() {
        // Reset all totals and cap flags
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            runningTotals.put(type, BigDecimal.ZERO);
            capReached.put(type, false);
            preDiscountTotals.put(type, BigDecimal.ZERO);
            discountedTotals.put(type, BigDecimal.ZERO);
            journeyCountByType.put(type, 0);
        }

        // Re-add each journey in order (recalculate fares)
        for (Journey journey : journeys) {
            CityRideDataset.PassengerType passengerType = journey.getPassengerType();
            BigDecimal currentTotal = runningTotals.get(passengerType);
            boolean capAlreadyReached = capReached.get(passengerType);

            // Update pre-discount total
            BigDecimal newPreDiscountTotal = preDiscountTotals.get(passengerType).add(journey.getBaseFare());
            preDiscountTotals.put(passengerType, newPreDiscountTotal);

            // Update journey count
            journeyCountByType.put(passengerType, journeyCountByType.get(passengerType) + 1);

            // Update discounted total
            BigDecimal discountedFare = FareCalculator.applyDiscount(journey.getBaseFare(), journey.getDiscountRate());
            BigDecimal newDiscountedTotal = discountedTotals.get(passengerType).add(discountedFare);
            discountedTotals.put(passengerType, newDiscountedTotal);

            BigDecimal chargedFare;
            if (capAlreadyReached) {
                chargedFare = BigDecimal.ZERO;
            } else {
                chargedFare = FareCalculator.applyDailyCap(discountedFare, currentTotal, journey.getDailyCap());
            }

            journey.setChargedFare(chargedFare);

            BigDecimal newTotal = runningTotals.get(passengerType).add(chargedFare);
            runningTotals.put(passengerType, newTotal);

            if (newTotal.compareTo(journey.getDailyCap()) >= 0) {
                capReached.put(passengerType, true);
            }
        }
    }

    public void resetDay() {
        journeys.clear();
        nextJourneyId = 1;

        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            runningTotals.put(type, BigDecimal.ZERO);
            capReached.put(type, false);
            preDiscountTotals.put(type, BigDecimal.ZERO);
            discountedTotals.put(type, BigDecimal.ZERO);
            journeyCountByType.put(type, 0);
        }
    }


    public int getJourneyCount() {
        return journeys.size();
    }

    public BigDecimal getPassengerTotal(CityRideDataset.PassengerType passengerType) {
        return runningTotals.get(passengerType);
    }

    public BigDecimal getPreDiscountTotalByType(CityRideDataset.PassengerType passengerType) {
        return preDiscountTotals.get(passengerType);
    }

    public BigDecimal getDiscountedTotalByType(CityRideDataset.PassengerType passengerType) {
        return discountedTotals.get(passengerType);
    }

    public int getJourneyCountByType(CityRideDataset.PassengerType passengerType) {
        return journeyCountByType.get(passengerType);
    }

    public boolean isCapReached(CityRideDataset.PassengerType passengerType) {
        return capReached.get(passengerType);
    }

    public BigDecimal getTotalCost() {
        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal amount : runningTotals.values()) {
            total = total.add(amount);
        }

        return total;
    }

    public BigDecimal getAverageCostPerJourney() {
        int count = journeys.size();
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = getTotalCost();
        BigDecimal average = total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);

        return average;
    }

    public Journey getMostExpensiveJourney() {
        Journey mostExpensive = null;

        for (Journey journey : journeys) {
            if (mostExpensive == null ||
                    journey.getChargedFare().compareTo(mostExpensive.getChargedFare()) > 0) {
                mostExpensive = journey;
            }
        }

        return mostExpensive;
    }


    public Map<String, Integer> getTimeBandCounts() {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("PEAK", 0);
        counts.put("OFF_PEAK", 0);

        for (Journey journey : journeys) {
            if (journey.getTimeBand() == CityRideDataset.TimeBand.PEAK) {
                counts.put("PEAK", counts.get("PEAK") + 1);
            } else {
                counts.put("OFF_PEAK", counts.get("OFF_PEAK") + 1);
            }
        }

        return counts;
    }
//(deepseek 2026)
    public Map<String, Integer> getZoneUsage() {
        Map<String, Integer> zoneUsage = new HashMap<>();

        for (Journey journey : journeys) {
            String fromKey = "From Zone " + journey.getFromZone();
            String toKey = "To Zone " + journey.getToZone();

            zoneUsage.put(fromKey, zoneUsage.getOrDefault(fromKey, 0) + 1);
            zoneUsage.put(toKey, zoneUsage.getOrDefault(toKey, 0) + 1);
        }

        return zoneUsage;
    }

    // FILTERING METHODS

    public List<Journey> getJourneysByPassengerType(CityRideDataset.PassengerType type) {
        List<Journey> filtered = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getPassengerType() == type) {
                filtered.add(journey);
            }
        }

        return filtered;
    }

    public List<Journey> getJourneysByTimeBand(CityRideDataset.TimeBand timeBand) {
        List<Journey> filtered = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getTimeBand() == timeBand) {
                filtered.add(journey);
            }
        }

        return filtered;
    }


    public List<Journey> getJourneysByFromZone(int zone) {
        List<Journey> filtered = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getFromZone() == zone) {
                filtered.add(journey);
            }
        }

        return filtered;
    }


    public List<Journey> getJourneysByToZone(int zone) {
        List<Journey> filtered = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getToZone() == zone) {
                filtered.add(journey);
            }
        }

        return filtered;
    }


    public List<Journey> getJourneysByZone(int zone) {
        List<Journey> filtered = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getFromZone() == zone || journey.getToZone() == zone) {
                filtered.add(journey);
            }
        }

        return filtered;
    }

    public List<Journey> getJourneysByDate(LocalDate date) {
        List<Journey> filtered = new ArrayList<>();

        for (Journey journey : journeys) {
            if (journey.getDate().equals(date)) {
                filtered.add(journey);
            }
        }

        return filtered;
    }
}