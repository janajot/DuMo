package tatami;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Tatami extends JPanel {
    private final int trueSizeX;
    private final int trueSizeY;
    private final boolean[] board;
    private final int[] dirOffset;

    private static final int LEFT = 2, RIGHT = 0, UP = 3, DOWN = 1;

    private final int pxps;


    public Tatami(int sizeX, int sizeY, int pxps) {
        if ((sizeX & sizeY & 1) == 1) throw new IllegalArgumentException("Board must have an even number of tiles");
        if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Board edges can only have a positive length");

        if (pxps < 1) throw new IllegalArgumentException("Board must have a valid size");
        this.pxps = pxps;

        this.trueSizeX = sizeX + 2;
        this.trueSizeY = sizeY + 2;
        board = new boolean[trueSizeX * trueSizeY];
        dirOffset = new int[]{1, trueSizeX, -1, -trueSizeX};

        int[] des = new int[]{trueSizeX - 1, trueSizeY - 1, trueSizeX - 1, trueSizeY - 1};
        for (int dir = 0, pos = 0; dir < 4; dir++) {
            for (int i = 0; i < des[dir]; i++) {
                set(pos, true);
                pos += dirOffset[dir];
            }
        }

        setPreferredSize(new Dimension(sizeX * pxps, sizeY * pxps));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                int x = e.getX() * sizeX / getWidth() + 1;
                int y = e.getY() * sizeY / getHeight() + 1;
                int pos = x + y * trueSizeX;
                boolean newValue = isEmpty(pos);
                set(pos, newValue);
                System.out.println("changed: " + x + "|" + y + " [" + pos + "] (" + newValue + ")");
                repaint();
            }
        });

        set(3, 3, 3, 8, true);
    }

    private void set(int x, int y, boolean newValue) {
        if (x <= 0 || x >= trueSizeX - 1 || y <= 0 || y >= trueSizeY - 1) throw new IllegalArgumentException("position " + x + "|" + y + " is out of bounds.");
        board[x + y * trueSizeX] = newValue;
    }

    private void set(int x, int y, int x1, int y1, boolean newValue) {
        for (; y <= y1; y++) {
            for (int x_ = x; x_ <= x1; x_++) {
                set(x_, y, newValue);
            }
        }
    }

    private boolean isFilled(int pos) {
        return board[pos];
    }

    private boolean isEmpty(int pos) {
        return !board[pos];
    }

    private void set(int pos, boolean b) {
        board[pos] = b;
    }

    private int getHead(int pos, int dir) {
        return pos + dirOffset[dir];
    }

    private int[] getSurroundingIndices(int pos) {
        int[] indices = new int[4];
        indices[0] = pos + dirOffset[RIGHT];
        indices[1] = pos + dirOffset[DOWN];
        indices[2] = pos + dirOffset[LEFT];
        indices[3] = pos + dirOffset[UP];
        return indices;
    }

    private void getSurrounding(int pos, boolean[] arr) {
        arr[0] = isFilled(getHead(pos, RIGHT));
        arr[1] = isFilled(getHead(pos, DOWN));
        arr[2] = isFilled(getHead(pos, LEFT));
        arr[3] = isFilled(getHead(pos, UP));
    }

    private void fillForced(int pos, boolean[] arr) {
        if (isFilled(pos)) return;

        int freeHead = -1;
        getSurrounding(pos, arr);
        for (int i = 0; i < 4; i++)
            if (!arr[i]) {
                if (freeHead != -1) return;
                else freeHead = i;
            }
        if (freeHead == -1) throw new IllegalBoardException("Board contains a gap of only 1 tile");
        set(pos, true);
        freeHead = getHead(pos, freeHead);
        set(freeHead, true);
        int[] is = getSurroundingIndices(freeHead);
        for (int index : is)
            fillForced(index, arr);
    }

    public void fillForced() {
        boolean[] b = new boolean[4];
        for (int i = trueSizeX + 1; i < board.length - trueSizeX - 1; i++)
            fillForced(i, b);
    }

    public void clear() {
        set(1, 1, trueSizeX - 2, trueSizeY - 2, false);
    }

    @Override
    public void paint(Graphics g) {
        int i = trueSizeX + 1;
        for (int y = 0; y < trueSizeY - 2; y++) {
            for (int x = 0; x < trueSizeX - 2; x++) {
                if (isFilled(i)) g.setColor(new Color(0xffffff));
                else g.setColor(new Color(0x8080c0));
                g.fillRect(pxps * x, pxps * y, pxps, pxps);
                i++;
            }
            i += 2;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setBackground(new Color(0x808080));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Tatami board = new Tatami(8, 8, 100);
        frame.setContentPane(board);
        frame.pack();

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'f' -> {
                        try {
                            board.fillForced();
                        } catch (IllegalBoardException ibe) {
                            ibe.printStackTrace();
                        }
                        System.out.println("Board filled in automatically");
                        frame.repaint();
                    }
                    case 'c' -> {
                        board.clear();
                        System.out.println("Cleared board.");
                        frame.repaint();
                    }
                    default -> {}
                }


            }
        });


        frame.setVisible(true);

        frame.repaint();

    }
}
