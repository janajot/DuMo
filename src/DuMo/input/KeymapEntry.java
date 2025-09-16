package DuMo.input;

import DuMo.settings.BoolSetting;

import java.awt.*;

class KeymapEntry {
    final String name;
    final KeyStroke keys;
    final boolean exclusive;
    BoolSetting[] conditions;
    Component environment;
    Runnable onPress;
    Runnable onRelease;

    KeymapEntry(String name, KeyStroke keys, BoolSetting[] conditions, Component environment, Runnable onPress, Runnable onRelease, boolean exclusive) {
        this.name = name;
        this.keys = keys;
        this.conditions = conditions;
        this.environment = environment;
        this.onPress = onPress;
        this.onRelease = onRelease;
        this.exclusive = exclusive;
    }

    boolean isDisabled() {
        for (BoolSetting b : conditions) if (!b.get()) return true;
        return false;
    }
}
