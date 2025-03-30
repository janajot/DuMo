package DuMo;

import DuMo.piece.kyap.PieceKyap;
import tatami.TatamiTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.awt.event.KeyEvent.*;
import static java.awt.event.MouseEvent.*;

public class Main {
    public final JFrame frame;
    public final Game game;
    public final TatamiTool tatami;
    public final GameOut display;
    public final Settings settings;
    public int mouseX;
    public int mouseY;
    public boolean leftBtn = false;
    public boolean rightBtn = false;
    public boolean middleBtn = false;
    public int wheelMov = 0;
    public boolean isDisplayHovered = false;
    public int keyCode = 0;

    public Main(int pxps, int x, int y) {
        settings = new Settings();
        game = new Game(this);
        game.boardX = x;
        game.boardY = y;
        game.resetBoard();

        tatami = new TatamiTool(game, pxps, new PieceKyap((byte) 0b0_0_000000));

        frame = new JFrame();
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
                        game.onPlace();
                        leftBtn = false;
                    }
                    case BUTTON3 -> {
                        game.onNext();
                        rightBtn = false;
                    }
                    case BUTTON2 -> {
                        game.onRemove();
                        middleBtn = false;
                    }
                }
            }
        });
        frame.addMouseWheelListener((e) -> {
            wheelMov = e.getWheelRotation();

            if (isDisplayHovered) {
                if (wheelMov > 0) game.rotateCurrentRight();
                else if (wheelMov < 0) game.rotateCurrentLeft();
            }

            advance();
        });
        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = display.convertToRelativeDisplayX(e.getX());
                mouseY = display.convertToRelativeDisplayY(e.getY());
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
                    case VK_F1 -> settings.settingShowEdges = !settings.settingShowEdges;
                    case VK_F2 -> settings.settingShowInvalid = !settings.settingShowInvalid;
                    case VK_F3 -> settings.settingShowGrid = !settings.settingShowGrid;
                    case VK_F4 -> settings.settingPeekInvalid = !settings.settingPeekInvalid;
                    case VK_R -> game.resetBoard();
                    case VK_W -> game.invertCurrent();
                    //TODO debug mode
                    case VK_F12 -> {
                        tatami.regenerate();
                        tatami.toggle();
                    }
                }
                advance();
            }
        });

        display = (GameOut) frame.getContentPane().add(new GameOut(pxps, this));
        frame.getContentPane().setBackground(new Color(0x89B0A8));

        frame.pack();
        frame.setVisible(true);

        game.place(new PieceKyap((byte) 0b0_0_000110), 5, 2);
        game.place(new PieceKyap((byte) 0b0_0_101001), 7, 3);
        game.place(new PieceKyap((byte) 0b0_0_111110), 5, 4);
        game.place(new PieceKyap((byte) 0b0_0_000011), 7, 5);
    }

    private void advance() {
        game.getEdgeStatus();
        frame.repaint();
        wheelMov = 0;
        keyCode = 0;
    }

    public static void main(String[] args) throws IOException {
        int x = 8;
        int y = 8;
        //try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
        //    String a = bufferedReader.readLine();
        //    x = Integer.parseInt(a);
        //    a = bufferedReader.readLine();
        //    y = Integer.parseInt(a);
        //} catch (NumberFormatException ignored) {
        //}
        Main main = new Main(100, x, y);
    }
}