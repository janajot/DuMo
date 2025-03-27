package tatami;

public class CircleForLoop {
    public CircleForLoop(int x, int y) {
        //if (((x & 1) & (y & 1)) == 1) {
        //    throw new IllegalArgumentException("Board size must be a round number");
        //}
        if (Math.min(x, y) <= 0) throw new IllegalArgumentException("Board size must be positive");
        this.sizeX = x;
        this.sizeY = y;
        this.board = new boolean[sizeX * sizeY];
    }

    private final int sizeX; // size of the board in X direction
    private final int sizeY; // size of the board in Y direction
    private final boolean[] board; //(board[0] -> board[sizeX - 1] is line 1, etc.)

    public void run() throws InterruptedException {
        int[] offset = new int[] {1, sizeX, -1, -sizeX};
        int[] des = new int[] {sizeX, sizeY - 1, sizeX - 1, sizeY - 2};
        int pos = 0, dir = 0, i0 = 1;
        while (des[0] > 0) {
            for (int i = i0; i < des[0]; i++) {
                board[pos] = true;
                pos += offset[dir];
                printState();
                System.out.println("-------------------");
                Thread.sleep(500);
            }
            i0 = 0;
            int temp = des[0];
            des[0] = des[1];
            des[1] = des[2];
            des[2] = des[3];
            des[3] = temp - 2;
            dir = (dir == 3) ? 0 : dir + 1;
        }
        board[pos] = true;
        printState();
    }

    public void printState() {
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++)
                System.out.print(board[x + y * sizeX] ? "o " : "x ");
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new CircleForLoop(8, 8).run();
    }
}
