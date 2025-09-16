package DuMo.settings;

public class BoolSetting {
    private boolean value;

    public BoolSetting(boolean init) {
        value = init;
    }

    public void toggle() {
        this.value = !value;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean newValue) {
        this.value = newValue;
    }
}
