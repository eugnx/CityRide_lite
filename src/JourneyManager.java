import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JourneyManager {

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
        initialiseAllTypeMaps();
    }


    // CRUD operations


    //Adds a journey to the day, calculates its fare (considering the daily cap),and updates all running totals.

    public Journey addJourney(Journey journey) {
        CityRideDataset.PassengerType passengerType = journey.getPassengerType();
        BigDecimal currentTotal = runningTotals.get(passengerType);
        boolean capAlreadyReached = capReached.get(passengerType);

        BigDecimal baseFare = FareCalculator.getBaseFare(
                journey.getFromZone(), journey.getToZone(), journey.getTimeBand());

        // Update pre-discount total and journey count
        preDiscountTotals.put(passengerType,
                preDiscountTotals.get(passengerType).add(baseFare));
        journeyCountByType.put(passengerType,
                journeyCountByType.get(passengerType) + 1);

        BigDecimal chargedFare;
        if (capAlreadyReached) {
            // Cap already hit: no further charge
            chargedFare = BigDecimal.ZERO;
            journey.setChargedFare(chargedFare);
            journey.setZonesCrossed(journey.calculateZonesCrossed());
            journey.setBaseFare(baseFare);
            journey.setDiscountApplied(journey.getDiscountRate());
        } else {
            chargedFare = FareCalculator.calculateFullFare(journey, currentTotal);

            // Update discounted total for reporting
            BigDecimal discountedFare = FareCalculator.applyDiscount(baseFare, journey.getDiscountRate());
            discountedTotals.put(passengerType,
                    discountedTotals.get(passengerType).add(discountedFare));
        }

        // Assign ID
        journey.setJourneyId(nextJourneyId);
        nextJourneyId++;

        // Update running total and check cap
        BigDecimal newTotal = runningTotals.get(passengerType).add(chargedFare);
        runningTotals.put(passengerType, newTotal);

        if (newTotal.compareTo(journey.getDailyCap()) >= 0) {
            capReached.put(passengerType, true);
        }

        journeys.add(journey);
        return journey;
    }


    // Returns a copy of all journeys in the order they were added.

    public List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }

    //Finds and returns a journey by its unique ID, or null if not found.

    public Journey getJourneyById(int id) {
        for (Journey journey : journeys) {
            if (journey.getJourneyId() == id) {
                return journey;
            }
        }
        return null;
    }


    // Removes a journey by ID and recalculates all totals.

    public boolean removeJourneyById(int id) {
        Journey toRemove = getJourneyById(id);
        if (toRemove == null) {
            return false;
        }
        journeys.remove(toRemove);
        recalculateAllTotals();
        return true;
    }


    // Public method to trigger a full recalculation of fares after an edit.

    public void recalculateAllFares() {
        recalculateAllTotals();
    }


    // Day management

    // Clears all journeys and resets all totals for a new day.

    public void resetDay() {
        journeys.clear();
        nextJourneyId = 1;
        initialiseAllTypeMaps();
    }


    // Summary statistics

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


    // Returns the sum of all charged fares across all passenger types.

    public BigDecimal getTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : runningTotals.values()) {
            total = total.add(amount);
        }
        return total;
    }


    // Returns the average charged fare per journey, or zero if no journeys exist.

    public BigDecimal getAverageCostPerJourney() {
        int count = journeys.size();
        if (count == 0) {
            return BigDecimal.ZERO;
        }
        return getTotalCost().divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }


    //Returns the journey with the highest charged fare, or null if no journeys exist.

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


    // Returns a map of time band label → journey count.

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

    // Returns a map of "Zone X → Zone Y" pair label → journey count.

    public Map<String, Integer> getZonePairCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (Journey journey : journeys) {
            String key = "Zone " + journey.getFromZone() + " \u2192 Zone " + journey.getToZone();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }
        return counts;
    }

    // Returns individual zone departure/arrival usage counts.

    public Map<String, Integer> getZoneUsage() {
        Map<String, Integer> zoneUsage = new HashMap<>();
        for (Journey journey : journeys) {
            String fromKey = "From Zone " + journey.getFromZone();
            String toKey   = "To Zone "   + journey.getToZone();
            zoneUsage.put(fromKey, zoneUsage.getOrDefault(fromKey, 0) + 1);
            zoneUsage.put(toKey,   zoneUsage.getOrDefault(toKey, 0) + 1);
        }
        return zoneUsage;
    }


    // Filter methods
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


    // Private helpers

    private void initialiseAllTypeMaps() {
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            runningTotals.put(type, BigDecimal.ZERO);
            capReached.put(type, false);
            preDiscountTotals.put(type, BigDecimal.ZERO);
            discountedTotals.put(type, BigDecimal.ZERO);
            journeyCountByType.put(type, 0);
        }
    }


    // Recalculates all charged fares, totals, and cap flags from scratch.
    //Called after a journey is removed or edited.

    private void recalculateAllTotals() {
        initialiseAllTypeMaps();

        for (Journey journey : journeys) {
            CityRideDataset.PassengerType type = journey.getPassengerType();
            BigDecimal currentTotal = runningTotals.get(type);
            boolean capAlreadyReached = capReached.get(type);

            BigDecimal baseFare = FareCalculator.getBaseFare(
                    journey.getFromZone(), journey.getToZone(), journey.getTimeBand());

            preDiscountTotals.put(type, preDiscountTotals.get(type).add(baseFare));
            journeyCountByType.put(type, journeyCountByType.get(type) + 1);

            BigDecimal discountedFare = FareCalculator.applyDiscount(baseFare, journey.getDiscountRate());
            discountedTotals.put(type, discountedTotals.get(type).add(discountedFare));

            BigDecimal chargedFare;
            if (capAlreadyReached) {
                chargedFare = BigDecimal.ZERO;
            } else {
                chargedFare = FareCalculator.applyDailyCap(discountedFare, currentTotal, journey.getDailyCap());
            }

            journey.setChargedFare(chargedFare);
            journey.setBaseFare(baseFare);

            BigDecimal newTotal = runningTotals.get(type).add(chargedFare);
            runningTotals.put(type, newTotal);

            if (newTotal.compareTo(journey.getDailyCap()) >= 0) {
                capReached.put(type, true);
            }
        }
    }
}
