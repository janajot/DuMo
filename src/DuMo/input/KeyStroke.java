package DuMo.input;

import java.util.HashSet;
import java.util.Set;

public class KeyStroke {
    final int[][] arr;

    KeyStroke(int[][] keyStroke) {
        this.arr = keyStroke;
    }

    boolean matches(KeyStroke other) {
        if (other == null || arr.length != other.arr.length) return false;
        if (this == other) return true;
        Set<Integer> containedKeys = new HashSet<>();
        for (int nth = 0; nth < other.arr.length; nth++) {
            for (int key : other.arr[nth])
                containedKeys.add(key);
            for (int key : arr[nth])
                if (!containedKeys.remove(key)) return false;
            if (!containedKeys.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(arr.length << 4);
        for (int[] unordered : arr) {
            sb.append("[");
            for (int key : unordered) sb.append(InputFrame.rev(key)).append(" + ");
            int size = sb.length();
            sb.replace(size - 4, size - 1, "], ");
        }
        int size = sb.length();
        sb.delete(size - 3, size - 1);
        return sb.toString();
    }
}
