package DuMo.input;

import DuMo.settings.BoolSetting;
import DuMo.util.HashMultiMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Keymap {
    private final Map<Integer, Integer> heldKeys = new HashMap<>();
    private final HashMultiMap<Integer, KeymapEntry> pressTargets = new HashMultiMap<>();

    private int time = 0;

    public Keymap() {

    }

    public Keymap(KeyBinding... keyBindings) {
        if (keyBindings == null) return;
        for (KeyBinding builder : keyBindings)
            register(builder);
    }

    /**
     * Adds a new key binding to this keymap.
     * @param keyBinding the key binding to add.
     * @throws IllegalKeyBindingException
     * if the key binding contains a key multiple times,
     * the key binding does not have an associated action,
     * or the same toggle is used multiple times.
     */
    public void register(KeyBinding keyBinding) throws IllegalKeyBindingException {
        Objects.requireNonNull(keyBinding);
        KeymapEntry e = keyBinding.build();
        for (int i : e.keys.arr[e.keys.arr.length - 1])
            pressTargets.add(i, e);
    }

    /**
     * removes all key bindings associated with this ordered key combination.
     * This operation is expensive. If the goal is to disable a key binding,
     * use a boolean toggle.
     * @param toMatch the key binding to remove
     * @see KeyBinding#toggle(BoolSetting)
     */
    public void remove(KeyStroke toMatch) {
        int[] last = toMatch.arr[toMatch.arr.length - 1];
        for (int k : last) {
            for (KeymapEntry e : pressTargets.get(k))
                if (toMatch == e.keys || toMatch.matches(e.keys))
                    pressTargets.remove(k, e);
        }
    }

    /**
     * Checks whether the given key is held.
     * @param key the key to check for. Uses the VK provided
     *           by KeyEvent and mouse buttons from InputFrame.
     * @return true if the key is held
     * @see java.awt.event.KeyEvent#VK_S
     * @see InputFrame#BTN_LEFT
     */
    public boolean isHeld(int key) {
        return heldKeys.containsKey(key);
    }

    public boolean isHeld(KeyStroke keys) {
        int lowestTime = time + 1;
        for (int nth = keys.arr.length - 1; nth >= 0; nth--) {
            int nextLowestTime = lowestTime;
            for (int k : keys.arr[nth]) {
                Integer time = heldKeys.get(k);
                if (time == null || time >= lowestTime) return false;
                nextLowestTime = Math.min(nextLowestTime, time);
            }
            lowestTime = nextLowestTime;
        }
        return true;
    }

    public void press(int key) {
        heldKeys.put(key, time++);
        Collection<KeymapEntry> potentialTargets = pressTargets.get(key);
        if (potentialTargets == null) return;
        forKeyBindings:
        for (KeymapEntry keyBinding : potentialTargets) {
            if (keyBinding.onPress == null) continue;
            if (keyBinding.isDisabled()) continue;
            int n = 0;
            int lowestTime = time + 1;
            for (int nth = keyBinding.keys.arr.length - 1; nth >= 0; nth--) {
                int nextLowestTime = lowestTime;
                for (int k : keyBinding.keys.arr[nth]) {
                    Integer time = heldKeys.get(k);
                    if (time == null || time >= lowestTime) continue forKeyBindings;
                    nextLowestTime = Math.min(nextLowestTime, time);
                    n++;
                }
                lowestTime = nextLowestTime;
            }
            if (keyBinding.exclusive) {
                if (n == heldKeys.size()) keyBinding.onPress.run();
            }
            else keyBinding.onPress.run();
        }
    }

    public void release(int key) {
        int keyTime = heldKeys.get(key);
        Collection<KeymapEntry> potentialTargets = pressTargets.get(key);
        if (potentialTargets == null) {
            heldKeys.remove(key);
            if (heldKeys.isEmpty()) time = 0;
            return;
        }
        forKeyBindings:
        for (KeymapEntry keyBinding : potentialTargets) {
            if (keyBinding.onRelease == null) continue;
            if (keyBinding.isDisabled()) continue;
            int n = 0;
            int lowestTime = time + 1;
            boolean released = false;
            for (int nth = keyBinding.keys.arr.length - 1; nth >= 0; nth--) {
                int nextLowestTime = lowestTime;
                for (int k : keyBinding.keys.arr[nth]) {
                    Integer time = heldKeys.get(k);
                    if (time == null || time >= lowestTime) continue forKeyBindings;
                    if (time == keyTime) released = true;
                    nextLowestTime = Math.min(nextLowestTime, time);
                    n++;
                }
                lowestTime = nextLowestTime;
            }
            if (released) {
                if (keyBinding.exclusive) {
                    if (n == heldKeys.size())
                        keyBinding.onPress.run();
                }
                else keyBinding.onRelease.run();
            }
        }
        heldKeys.remove(key);
        if (heldKeys.isEmpty()) time = 0;
    }

    public void releaseAll() {
        heldKeys.clear();
    }

    public void clear() {
        pressTargets.clear();
        releaseAll();
    }
}
