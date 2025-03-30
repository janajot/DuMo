package tatami;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class BooleanSpiralLooper {
    private final boolean[] board; //(board[0] -> board[sizeX - 1] is line 1, etc.)
    private final int length;
    private final int baseSizeX;
    private final int baseOffset;
    public static final int RIGHT = 0, DOWN = 1, LEFT = 2, UP = 3;

    public BooleanSpiralLooper(boolean[] array, int offset, int sizeX, int sizeY) {
        this.length = sizeX * sizeY + offset;
        if (array.length < length)
            throw new IllegalArgumentException("Board size mustn't exceed array length (" + array.length + " > " + length + ")");
        this.board = array;
        this.baseSizeX = sizeX;
        this.baseOffset = offset;
    }

    public void forEach(int offsetX, int offsetY, int sizeX, int sizeY, Consumer<Boolean> consumer) {
        if (Math.min(sizeX, sizeY) <= 0)
            throw new IllegalArgumentException("Board size must be positive (x=" + sizeX + ", y=" + sizeY + ")");
        if (sizeY * sizeX + baseOffset > length)
            throw new IllegalArgumentException("Subsection must be in bounds (" + (sizeX * sizeY + baseOffset) + " > " + length + ")");

        int[] dirOffset = new int[]{1, baseSizeX, -1, -baseSizeX};
        int[] des = new int[]{sizeX, sizeY - 1, sizeX - 1, sizeY - 2};
        int pos = baseOffset + offsetX + baseSizeX * offsetY;
        int dir = 0, i0 = 1;
        while (des[0] > 0) {
            for (int i = i0; i < des[0]; i++) {
                consumer.accept(board[pos]);
                pos += dirOffset[dir];
            }
            i0 = 0;
            int temp = des[0];
            des[0] = des[1];
            des[1] = des[2];
            des[2] = des[3];
            des[3] = temp - 2;
            dir = (dir == 3) ? 0 : dir + 1;
        }
        consumer.accept(board[pos]);
    }

    public void forEachIndex(int offsetX, int offsetY, int sizeX, int sizeY, IntConsumer consumer) {
        if (Math.min(sizeX, sizeY) <= 0)
            throw new IllegalArgumentException("Board size must be positive (x=" + sizeX + ", y=" + sizeY + ")");
        if (sizeY * sizeX + baseOffset > length)
            throw new IllegalArgumentException("Subsection must be in bounds (" + (sizeX * sizeY + baseOffset) + " > " + length + ")");

        int[] dirOffset = new int[]{1, baseSizeX, -1, -baseSizeX};
        int[] des = new int[]{sizeX, sizeY - 1, sizeX - 1, sizeY - 2};
        int pos = baseOffset + offsetX + baseSizeX * offsetY;
        int dir = 0, i0 = 1;
        while (des[0] > 0) {
            for (int i = i0; i < des[0]; i++) {
                consumer.accept(pos);
                pos += dirOffset[dir];
            }
            i0 = 0;
            int temp = des[0];
            des[0] = des[1];
            des[1] = des[2];
            des[2] = des[3];
            des[3] = temp - 2;
            dir = (dir == 3) ? 0 : dir + 1;
        }
        consumer.accept(pos);
    }

    public void forEachIndex(int offsetX, int offsetY, int sizeX, int sizeY, BiConsumer<Integer, Integer> consumer) {
        if (Math.min(sizeX, sizeY) <= 0)
            throw new IllegalArgumentException("Board size must be positive (x=" + sizeX + ", y=" + sizeY + ")");
        if (sizeY * sizeX + baseOffset > length)
            throw new IllegalArgumentException("Subsection must be in bounds (" + (sizeX * sizeY + baseOffset) + " > " + length + ")");

        int[] dirOffset = new int[]{1, baseSizeX, -1, -baseSizeX};
        int[] des = new int[]{sizeX, sizeY - 1, sizeX - 1, sizeY - 2};
        int pos = baseOffset + offsetX + baseSizeX * offsetY;
        int dir = 0, i0 = 1;
        int spiralIndex = 0;
        while (des[0] > 0) {
            for (int i = i0; i < des[0]; i++) {
                consumer.accept(pos, spiralIndex++);
                pos += dirOffset[dir];
            }
            i0 = 0;
            int temp = des[0];
            des[0] = des[1];
            des[1] = des[2];
            des[2] = des[3];
            des[3] = temp - 2;
            dir = (dir == 3) ? 0 : dir + 1;
        }
        consumer.accept(pos, spiralIndex);
    }

    public void forEachIndex(int offsetX, int offsetY, int sizeX, int sizeY, TriConsumer<Integer, Integer, Integer> consumer) {
        if (Math.min(sizeX, sizeY) <= 0)
            throw new IllegalArgumentException("Board size must be positive (x=" + sizeX + ", y=" + sizeY + ")");
        if (sizeY * sizeX + baseOffset > length)
            throw new IllegalArgumentException("Subsection must be in bounds (" + (sizeX * sizeY + baseOffset) + " > " + length + ")");

        int[] dirOffset = new int[]{1, baseSizeX, -1, -baseSizeX};
        int[] des = new int[]{sizeX, sizeY - 1, sizeX - 1, sizeY - 2};
        int pos = baseOffset + offsetX + baseSizeX * offsetY;
        int dir = 0, i0 = 1;
        int spiralIndex = 0;
        while (des[0] > 0) {
            for (int i = i0; i < des[0]; i++) {
                consumer.accept(pos, spiralIndex++, dir);
                pos += dirOffset[dir];
            }
            i0 = 0;
            int temp = des[0];
            des[0] = des[1];
            des[1] = des[2];
            des[2] = des[3];
            des[3] = temp - 2;
            dir = (dir == 3) ? 0 : dir + 1;
        }
        consumer.accept(pos, spiralIndex, dir);
    }

    @FunctionalInterface
    public interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }
}