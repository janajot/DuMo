package DuMo.settings;

public class Setting<T> {
    private T value;

    public Setting(T init) {
        this.value = init;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        this.value = newValue;
    }
}
