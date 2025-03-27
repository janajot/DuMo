package DuMo;

import DuMo.piece.kyap.PieceKyap;
import DuMo.util.CoordinateTransform;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.awt.event.KeyEvent.*;
import static java.awt.event.MouseEvent.*;

public class Main {
    public static final JFrame frame = new JFrame();
    public static GameOut display;
    public static int mouseX;
    public static int mouseY;
    public static boolean leftBtn = false;
    public static boolean rightBtn = false;
    public static boolean middleBtn = false;
    public static int wheelMov = 0;
    public static boolean isDisplayHovered = false;
    public static int keyCode = 0;
    public static boolean settingDebug = false;
    public static boolean settingShowInvalid = false;
    public static boolean settingLetInvalid = false;
    public static boolean settingPeekInvalid = true;
    public static boolean settingShowEdges = true;
    public static boolean settingShowGrid = true;

    public static void main(String[] args) {
        int pxps;
        try {
            pxps = Integer.parseInt(args[0]);
            Game.boardX = Integer.parseInt(args[1]);
            Game.boardY = Integer.parseInt(args[2]);
        } catch (Exception e) {
            pxps = 100;
            Game.boardX = 8;
            Game.boardY = 8;
        }
        Game.resetBoard();

        frame.setContentPane(new JPanel());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("C:\\02Sys\\Ico\\not ico\\DuMo_1x1_01.png").getImage());
        frame.setTitle("DuMo Kyap v0-00");
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                switch (e.getButton()) {
                    case BUTTON1 -> leftBtn = true;
                    case BUTTON3 -> rightBtn = true;
                    case BUTTON2 -> middleBtn = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                advance();
                switch (e.getButton()) {
                    case BUTTON1 -> {
                        Game.onPlace();
                        leftBtn = false;
                    }
                    case BUTTON3 -> {
                        Game.onNext();
                        rightBtn = false;
                    }
                    case BUTTON2 -> {
                        Game.onRemove();
                        middleBtn = false;
                    }
                }
            }
        });
        frame.addMouseWheelListener((e) -> {
            wheelMov = e.getWheelRotation();

            if (isDisplayHovered) {
                if (wheelMov > 0) Game.rotateCurrentRight();
                else if (wheelMov < 0) Game.rotateCurrentLeft();
            }

            advance();
        });
        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = CoordinateTransform.convertToRelativeDisplayX(e.getX());
                mouseY = CoordinateTransform.convertToRelativeDisplayY(e.getY());
                isDisplayHovered = mouseX >= 0 &&
                        mouseY >= 0 &&
                        mouseX <= display.getWidth() &&
                        mouseY <= display.getHeight();
                advance();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyCode = e.getKeyCode();
                switch (keyCode) {
                    case VK_F1 -> settingShowEdges = !settingShowEdges;
                    case VK_F2 -> settingShowInvalid = !settingShowInvalid;
                    case VK_F3 -> settingShowGrid = !settingShowGrid;
                    case VK_F4 -> settingPeekInvalid = !settingPeekInvalid;
                    case VK_R -> Game.resetBoard();
                    case VK_W -> Game.invertCurrent();
                }
                advance();
            }
        });

        display = (GameOut) frame.getContentPane().add(new GameOut(pxps));
        frame.getContentPane().setBackground(new Color(0x89B0A8));

        frame.pack();
        frame.setVisible(true);

        Game.place(new PieceKyap((byte) 0b0_0_000110), 5, 2);
        Game.place(new PieceKyap((byte) 0b0_0_101001), 7, 3);
        Game.place(new PieceKyap((byte) 0b0_0_111110), 5, 4);
        Game.place(new PieceKyap((byte) 0b0_0_000011), 7, 5);
    }

    private static void advance() {
        Game.getEdgeStatus();
        frame.repaint();
        wheelMov = 0;
        keyCode = 0;
    }
}