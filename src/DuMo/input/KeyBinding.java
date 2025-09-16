package DuMo.input;

import DuMo.settings.BoolSetting;

import java.awt.*;
import java.util.*;

public class KeyBinding {
    private final ArrayList<int[]> ordered;
    private final ArrayList<BoolSetting> toggles;
    public final String name;
    private final Component context;
    private Runnable onPress = null;
    private Runnable onRelease = null;
    private boolean exclusive = false;

    public KeyBinding(String name, Component context) {
        this.name = name;
        this.context = context;
        ordered = new ArrayList<>();
        toggles = new ArrayList<>();
    }

    public KeyBinding() {
        this(null, null);
    }

    public KeyBinding toggle(BoolSetting toggle) {
        Objects.requireNonNull(toggle);
        toggles.add(toggle);
        return this;
    }

    public KeyBinding then(int... keys) {
        Objects.requireNonNull(keys);
        ordered.add(keys);
        return this;
    }

    public KeyBinding exclusive() {
        exclusive = true;
        return this;
    }

    public KeyBinding onPress(Runnable action) {
        onPress = action;
        return this;
    }

    public KeyBinding onRelease(Runnable action) {
        onRelease = action;
        return this;
    }

    KeymapEntry build() {
        if (onRelease == null && onPress == null) {
            throw new IllegalKeyBindingException("No action assigned!");
        }
        BoolSetting[] conditions = toggles.toArray(new BoolSetting[0]);
        for (int i = 0; i < conditions.length; i++) {
            for (int j = i + 1; j < conditions.length; j++)
                if (conditions[i] == conditions[j])
                    throw new IllegalKeyBindingException("Same toggle included multiple times");
        }
        KeyStroke keys = getKeyStrokes();
        return new KeymapEntry(name, keys, conditions, context, onPress, onRelease, exclusive);
    }

    public KeyStroke getKeyStrokes() throws IllegalKeyBindingException {
        TreeSet<Integer> c0 = new TreeSet<>();
        int[][] keyCombination = new int[ordered.size()][];
        for (int j = 0; j < ordered.size(); j++) {
            int[] is = ordered.get(j);
            keyCombination[j] = new int[is.length];
            for (int i = 0; i < is.length; i++) {
                if (c0.contains(is[i]))
                    throw new IllegalKeyBindingException("Same key included multiple times");
                c0.add(is[i]);
                keyCombination[j][i] = is[i];
            }
        }
        return new KeyStroke(keyCombination);
    }

    @Override
    public String toString() {
        try {
            return build().toString();
        } catch (IllegalKeyBindingException exception) {
            return "InvalidKeyBinding";
        }
    }
}
