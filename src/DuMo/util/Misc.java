package DuMo.util;

public class Misc {
    public static void forSurroundingHI(int x, int y, ABRunnable task) {
        int h = y * 2 + 1;
        
        task.run(h, x);
        task.run(h - 1, x);
        task.run(h - 2, x);
        task.run(h - 1, x - 1);
    }
}
