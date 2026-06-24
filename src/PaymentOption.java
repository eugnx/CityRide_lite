
public enum PaymentOption {

    CONTACTLESS("Contactless Card"),
    OYSTER_CARD("Oyster Card"),
    MOBILE_PAY("Mobile Pay"),
    CASH("Cash");

    private final String displayLabel;

    PaymentOption(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    @Override
    public String toString() {
        return displayLabel;
    }
}
