import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Journey {

    // Private fields
    private int journeyId;
    private LocalDate date;
    private CityRideDataset.TimeBand timeBand;
    private int fromZone;
    private int toZone;
    private CityRideDataset.PassengerType passengerType;
    private int zonesCrossed;
    private BigDecimal baseFare;
    private BigDecimal discountApplied;
    private BigDecimal chargedFare;


     // Constructor for creating a new journey.

    public Journey(int journeyId, LocalDate date, CityRideDataset.TimeBand timeBand,
                   int fromZone, int toZone, CityRideDataset.PassengerType passengerType) {
        this.journeyId = journeyId;
        this.date = date;
        this.timeBand = timeBand;
        this.fromZone = fromZone;
        this.toZone = toZone;
        this.passengerType = passengerType;
        this.zonesCrossed = 0;
        this.baseFare = BigDecimal.ZERO;
        this.discountApplied = BigDecimal.ZERO;
        this.chargedFare = BigDecimal.ZERO;
    }


     // Calculates zones crossed using formula: |toZone - fromZone| + 1

    public int calculateZonesCrossed() {
        return Math.abs(toZone - fromZone) + 1;
    }


     // Gets discount rate from CityRideDataset.

    public BigDecimal getDiscountRate() {
        return CityRideDataset.DISCOUNT_RATE.get(passengerType);
    }


     // Gets daily cap from CityRideDataset.

    public BigDecimal getDailyCap() {
        return CityRideDataset.DAILY_CAP.get(passengerType);
    }

    // Getters and Setters
    public int getJourneyId() { return journeyId; }
    public void setJourneyId(int journeyId) { this.journeyId = journeyId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public CityRideDataset.TimeBand getTimeBand() { return timeBand; }
    public void setTimeBand(CityRideDataset.TimeBand timeBand) { this.timeBand = timeBand; }

    public int getFromZone() { return fromZone; }
    public void setFromZone(int fromZone) { this.fromZone = fromZone; }

    public int getToZone() { return toZone; }
    public void setToZone(int toZone) { this.toZone = toZone; }

    public CityRideDataset.PassengerType getPassengerType() { return passengerType; }
    public void setPassengerType(CityRideDataset.PassengerType passengerType) { this.passengerType = passengerType; }

    public int getZonesCrossed() { return zonesCrossed; }
    public void setZonesCrossed(int zonesCrossed) { this.zonesCrossed = zonesCrossed; }

    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }

    public BigDecimal getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(BigDecimal discountApplied) { this.discountApplied = discountApplied; }

    public BigDecimal getChargedFare() { return chargedFare; }
    public void setChargedFare(BigDecimal chargedFare) { this.chargedFare = chargedFare; }


     // Returns formatted string representation of the journey.

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fomattdate = date.format(formatter);

        double discountPercentage = discountApplied != null ? discountApplied.doubleValue() * 100 : 0 ;
        return String.format("ID: %d | Date: %s | From: %d | To: %d | Time: %s | " +
                        "Passenger: %s | Zones: %d | Base: £%.2f | Charged: £%.2f",
                journeyId, date.format(formatter), fromZone, toZone, timeBand,
                passengerType, zonesCrossed, baseFare, chargedFare);
    }
}