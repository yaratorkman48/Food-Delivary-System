package FoodDeliverySystem;

/**
 * Vehicle types a delivery rider can use.
 *
 * Introduced in Assignment 4 (Part ט) to replace the previous free-text
 * {@code String vehicle} field on {@link Rider}. As an enum, the set of valid
 * vehicles is fixed and checked by the compiler, and the JavaFX add-rider
 * screen will populate its ComboBox directly from {@link #values()}.
 *
 * Persistence note: text files store the constant NAME (e.g. "MOTORCYCLE")
 * via {@link #name()}, while the UI shows the friendly label (e.g. "Motorcycle")
 * via {@link #toString()}. Loading uses {@link #fromString(String)}, which is
 * tolerant of case and of the old lowercase seed values ("scooter", "car").
 */
public enum VehicleType {

    BICYCLE("Bicycle"),
    SCOOTER("Scooter"),
    MOTORCYCLE("Motorcycle"),
    CAR("Car");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    /** @return the human-readable label shown in the UI / ComboBox. */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Parses a stored or typed string into a VehicleType. Tolerant of case and
     * surrounding spaces, and matches either the constant name ("MOTORCYCLE")
     * or the display label ("Motorcycle"). Falls back to BICYCLE for a null or
     * unrecognised value, so a corrupt line in a text file can never crash the
     * load (Part יב: the program must not crash).
     *
     * @param raw the text to parse
     * @return the matching VehicleType, or BICYCLE as a safe default
     */
    public static VehicleType fromString(String raw) {
        if (raw == null) {
            return BICYCLE;
        }
        String cleaned = raw.trim();
        for (VehicleType vt : values()) {
            if (vt.name().equalsIgnoreCase(cleaned)
                    || vt.displayName.equalsIgnoreCase(cleaned)) {
                return vt;
            }
        }
        return BICYCLE;
    }

    // UI / display string. Persistence must use {@link #name()} instead. 
    @Override
    public String toString() {
        return displayName;
    }
}
