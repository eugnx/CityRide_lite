
public class RiderProfile {

    private String name;
    private CityRideDataset.PassengerType passengerType;
    private PaymentOption defaultPaymentOption;


    // Constructs a RiderProfile with all required fields.

    public RiderProfile(String name, CityRideDataset.PassengerType passengerType,
                        PaymentOption defaultPaymentOption) {
        this.name = name;
        this.passengerType = passengerType;
        this.defaultPaymentOption = defaultPaymentOption;
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CityRideDataset.PassengerType getPassengerType() {
        return passengerType;
    }

    public void setPassengerType(CityRideDataset.PassengerType passengerType) {
        this.passengerType = passengerType;
    }

    public PaymentOption getDefaultPaymentOption() {
        return defaultPaymentOption;
    }

    public void setDefaultPaymentOption(PaymentOption defaultPaymentOption) {
        this.defaultPaymentOption = defaultPaymentOption;
    }


    // Returns a human-readable summary of the profile.

    @Override
    public String toString() {
        return String.format("Name: %s | Passenger Type: %s | Payment: %s",
                name, passengerType, defaultPaymentOption.getDisplayLabel());
    }
}
