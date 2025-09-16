package DuMo.settings;

public class DoubleSetting {
    private double value;

    public DoubleSetting(double init) {
        value = init;
    }

    public double get() {
        return value;
    }

    public void set(double newValue) {
        this.value = newValue;
    }
}
