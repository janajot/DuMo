package DuMo.settings;

public class IntSetting {
    private int value;

    public IntSetting(int init) {
        value = init;
    }

    public int get() {
        return value;
    }

    public void set(int newValue) {
        this.value = newValue;
    }
}
