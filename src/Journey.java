import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Journey {

    private int journeyId;
    private LocalDate date;
    private LocalTime time;
    private CityRideDataset.TimeBand timeBand;
    private int fromZone;
    private int toZone;
    private CityRideDataset.PassengerType passengerType;
    private int zonesCrossed;
    private BigDecimal baseFare;
    private BigDecimal discountApplied;
    private BigDecimal chargedFare;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");


    // Constructs a new Journey with core travel details including time.
    // Fare fields default to zero and are set by FareCalculator.

    public Journey(int journeyId, LocalDate date, LocalTime time,
                   CityRideDataset.TimeBand timeBand,
                   int fromZone, int toZone,
                   CityRideDataset.PassengerType passengerType) {
        this.journeyId     = journeyId;
        this.date          = date;
        this.time          = time;
        this.timeBand      = timeBand;
        this.fromZone      = fromZone;
        this.toZone        = toZone;
        this.passengerType = passengerType;
        this.zonesCrossed  = 0;
        this.baseFare      = BigDecimal.ZERO;
        this.discountApplied = BigDecimal.ZERO;
        this.chargedFare   = BigDecimal.ZERO;
    }


    // Calculates the number of zones crossed: |toZone - fromZone| + 1.

    public int calculateZonesCrossed() {
        return Math.abs(toZone - fromZone) + 1;
    }


    // Returns the discount rate for this journey's passenger type.

    public BigDecimal getDiscountRate() {
        return ConfigManager.getInstance().getDiscountRate(passengerType);
    }


    // Returns the daily cap for this journey's passenger type.

    public BigDecimal getDailyCap() {
        return ConfigManager.getInstance().getDailyCap(passengerType);
    }

    // --- Getters and Setters ---

    public int getJourneyId() { return journeyId; }
    public void setJourneyId(int journeyId) { this.journeyId = journeyId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public CityRideDataset.TimeBand getTimeBand() { return timeBand; }
    public void setTimeBand(CityRideDataset.TimeBand timeBand) { this.timeBand = timeBand; }

    public int getFromZone() { return fromZone; }
    public void setFromZone(int fromZone) { this.fromZone = fromZone; }

    public int getToZone() { return toZone; }
    public void setToZone(int toZone) { this.toZone = toZone; }

    public CityRideDataset.PassengerType getPassengerType() { return passengerType; }
    public void setPassengerType(CityRideDataset.PassengerType passengerType) {
        this.passengerType = passengerType;
    }

    public int getZonesCrossed() { return zonesCrossed; }
    public void setZonesCrossed(int zonesCrossed) { this.zonesCrossed = zonesCrossed; }

    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }

    public BigDecimal getDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(BigDecimal discountApplied) { this.discountApplied = discountApplied; }

    public BigDecimal getChargedFare() { return chargedFare; }
    public void setChargedFare(BigDecimal chargedFare) { this.chargedFare = chargedFare; }


    // Returns a formatted one-line summary of this journey for display.

    @Override
    public String toString() {
        String timeStr = (time != null) ? time.format(TIME_FORMATTER) : "N/A";
        return String.format(
                "ID: %d | Date: %s %s | From: %d | To: %d | Time: %s | " +
                        "Passenger: %s | Zones: %d | Base: £%.2f | Charged: £%.2f",
                journeyId, date.format(DATE_FORMATTER), timeStr,
                fromZone, toZone, timeBand,
                passengerType, zonesCrossed, baseFare, chargedFare);
    }
}
