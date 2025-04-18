package tatami;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Tatami extends JPanel {
    protected int trueSizeX;
    protected int trueSizeY;
    protected int sizeX;
    protected int sizeY;
    protected boolean[] board;
    protected int length;
    protected final int[] dirOffset;
    protected int pxps;

    public static final int LEFT = 2, RIGHT = 0, UP = 3, DOWN = 1;

    public Tatami(int sizeX, int sizeY, int pxps) {
        this(sizeX, sizeY, pxps, null);
    }

    public Tatami(int sizeX, int sizeY, int pxps, boolean[] board) {
        if ((sizeX & sizeY & 1) == 1) throw new IllegalArgumentException("Board must have an even number of tiles");
        if (sizeX < 1 || sizeY < 1) throw new IllegalArgumentException("Board edges can only have a positive length");

        if (pxps < 1) throw new IllegalArgumentException("Board must have a valid size");
        this.pxps = pxps;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.trueSizeX = sizeX + 2;
        this.trueSizeY = sizeY + 2;
        this.length = trueSizeX * trueSizeY;
        if (board != null && board.length <= trueSizeX * trueSizeY) this.board = board;
        else {
            this.board = new boolean[trueSizeX * trueSizeY];
            System.out.println("No/invalid board given; creating new one.");
        }
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

        initOpen();
    }

    protected void set(int x, int y, boolean newValue) {
        if (x <= 0 || x >= trueSizeX - 1 || y <= 0 || y >= trueSizeY - 1)
            throw new IllegalArgumentException("position " + x + "|" + y + " is out of bounds.");
        board[x + y * trueSizeX] = newValue;
    }

    protected void set(int x, int y, int x1, int y1, boolean newValue) {
        for (; y <= y1; y++) {
            for (int x_ = x; x_ <= x1; x_++) {
                set(x_, y, newValue);
            }
        }
    }

    protected boolean isFilled(int pos) {
        return board[pos];
    }

    protected boolean isEmpty(int pos) {
        return !board[pos];
    }

    protected void set(int pos, boolean b) {
        board[pos] = b;
    }

    protected int getHead(int pos, int dir) {
        return pos + dirOffset[dir];
    }

    protected int[] getSurroundingIndices(int pos) {
        int[] indices = new int[4];
        indices[0] = pos + dirOffset[RIGHT];
        indices[1] = pos + dirOffset[DOWN];
        indices[2] = pos + dirOffset[LEFT];
        indices[3] = pos + dirOffset[UP];
        return indices;
    }

    protected void getSurrounding(int pos, boolean[] arr) {
        arr[0] = isFilled(getHead(pos, RIGHT));
        arr[1] = isFilled(getHead(pos, DOWN));
        arr[2] = isFilled(getHead(pos, LEFT));
        arr[3] = isFilled(getHead(pos, UP));
    }

    protected void fillForced(int pos, boolean[] arr) {
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
        for (int i = trueSizeX + 1; i < length - trueSizeX - 1; i++)
            fillForced(i, b);
    }

    public void clear() {
        set(1, 1, sizeX, sizeY, false);
    }

    public void loopingFiller(Random random) {
        BooleanSpiralLooper looper = new BooleanSpiralLooper(board, 0, trueSizeX, trueSizeY);
        looper.forEachIndex(2, 2, sizeX - 2, sizeY - 2, (i, spirI, dir) -> {
            random.nextBoolean();
            //TODO
        });
    }


    @Override
    public void paint(Graphics g) {
        int i = trueSizeX + 1;
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (isFilled(i)) g.setColor(new Color(0xffffff));
                else g.setColor(new Color(0x8080c0));
                g.fillRect(pxps * x, pxps * y, pxps, pxps);
                i++;
            }
            i += 2;
        }
    }

    protected JFrame frame = new JFrame();

    private void initOpen() {
        frame.setBackground(new Color(0x808080));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch (e.getKeyChar()) {
                    case 'f' -> {
                        try {
                            fillForced();
                        } catch (IllegalBoardException ibe) {
                            ibe.printStackTrace();
                        }
                        System.out.println("Board filled in automatically");
                        repaint();
                    }
                    case 'c' -> {
                        clear();
                        System.out.println("Cleared board.");
                        repaint();
                    }
                    default -> {
                    }
                }
            }
        });
    }

    public void toggle() {
        frame.setVisible(!frame.isVisible());
        frame.repaint();
    }

    public static int[] getHI(int x, int y, int dir) {
        int[] pos = new int[2];
        switch (dir) {
            case RIGHT -> {
                pos[0] = y * 2 - 2;
                pos[1] = x - 1;
            }
            case LEFT -> {
                pos[0] = y * 2 - 2;
                pos[1] = x - 2;
            }
            case UP -> {
                pos[0] = y * 2 - 3;
                pos[1] = x - 1;
            }
            case DOWN -> {
                pos[0] = y * 2 - 1;
                pos[1] = x - 1;
            }
            default -> throw new IllegalArgumentException("Unknown direction: " + dir);
        }
        return pos;
    }

    public static void main(String[] args) {
        Tatami t = new Tatami(8, 8, 100);
        t.initOpen();
        t.setVisible(true);
    }

}
