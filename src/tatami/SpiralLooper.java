package tatami;

import java.util.function.Consumer;

public class SpiralLooper<T> {
    public SpiralLooper(T[] array, int rows, int columns) {
        if (Math.min(columns, rows) <= 0) throw new IllegalArgumentException("Board size must be positive");
        if (array.length != rows * columns) throw new IllegalArgumentException("Board size must match array length");
        this.sizeX = columns;
        this.sizeY = rows;
        this.board = array;
    }

    private final int sizeX; // size of the board in X direction
    private final int sizeY; // size of the board in Y direction
    private final T[] board; //(board[0] -> board[sizeX - 1] is line 1, etc.)

    public void forEachSpiral(Consumer<T> consumer) {
        int[] offset = new int[] {1, sizeX, -1, -sizeX};
        int[] des = new int[] {sizeX, sizeY - 1, sizeX - 1, sizeY - 2};
        int pos = 0, dir = 0, i0 = 1;
        while (des[0] > 0) {
            for (int i = i0; i < des[0]; i++) {
                consumer.accept(board[pos]);
                pos += offset[dir];
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

    public static void main(String[] args) {
        Boolean[] booleans = new Boolean[] {
                true, false, false, true,   // > > > v
                true, false, false, false,  // > > v v
                true, false, true, false,   // ^ o < v
                true, false, false, false   // ^ < < <
        };
        new SpiralLooper<>(booleans, 4, 4).forEachSpiral(System.out::println);
    }
}
