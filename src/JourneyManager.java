import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JourneyManager {

    private List<Journey> journeys;
    private int nextJourneyId;
    private Map<CityRideDataset.PassengerType, BigDecimal> runningTotals;
    private Map<CityRideDataset.PassengerType, Boolean> capReached;

    public JourneyManager() {
        this.journeys = new ArrayList<>();
        this.nextJourneyId = 1;
        this.runningTotals = new HashMap<>(); // A HashMap stores data as key-value pairs, where each key is unique.
        this.capReached = new HashMap<>();

        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            runningTotals.put(type, BigDecimal.ZERO);
            capReached.put(type, false);
        }
    }


     // Adds a journey with full fare calculation.
    public Journey addJourney(Journey journey) {
        CityRideDataset.PassengerType type = journey.getPassengerType();
        BigDecimal currentTotal = runningTotals.get(type);
        boolean capAlreadyReached = capReached.get(type);

        BigDecimal chargedFare;

        if (capAlreadyReached) {
            chargedFare = BigDecimal.ZERO;
            journey.setChargedFare(chargedFare);
            journey.setZonesCrossed(journey.calculateZonesCrossed());
            journey.setBaseFare(BigDecimal.ZERO);
        } else {
            chargedFare = FareCalculator.calculateChargedFare(journey, currentTotal);
        }

        journey.setJourneyId(nextJourneyId);
        nextJourneyId++;

        // Update running total
        BigDecimal newTotal = runningTotals.get(type).add(chargedFare);
        runningTotals.put(type, newTotal);

        // Check if cap reached
        if (newTotal.compareTo(journey.getDailyCap()) >= 0) {
            capReached.put(type, true);
        }

        journeys.add(journey);

        return journey;
    }


     // Gets all journeys.

    public List<Journey> getAllJourneys() {
        return new ArrayList<>(journeys);
    }


     // Gets journey by ID.

    public Journey getJourneyById(int id) {
        for (Journey journey : journeys) {
            if (journey.getJourneyId() == id) {
                return journey;
            }
        }
        return null;
    }

      //Gets journey count.

    public int getJourneyCount() {
        return journeys.size();
    }


     //Gets running total for a passenger type.

    public BigDecimal getPassengerTotal(CityRideDataset.PassengerType type) {
        return runningTotals.get(type);
    }


     // Checks if cap reached.

    public boolean isCapReached(CityRideDataset.PassengerType type) {
        return capReached.get(type);
    }


      //Gets total cost across all types.

    public BigDecimal getTotalCost() {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : runningTotals.values()) {
            total = total.add(amount);
        }
        return total;
    }


     // Resets all data.

    public void resetDay() {
        journeys.clear();
        nextJourneyId = 1;
        for (CityRideDataset.PassengerType type : CityRideDataset.PassengerType.values()) {
            runningTotals.put(type, BigDecimal.ZERO);
            capReached.put(type, false);
        }
    }
}