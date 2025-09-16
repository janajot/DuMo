package DuMo;

import DuMo.board.Board;
import DuMo.input.InputFrame;
import DuMo.input.KeyBinding;
import DuMo.piece.kyap.PieceKyap;
import DuMo.settings.Settings;
import tatami.TatamiTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.awt.event.KeyEvent.*;
import static DuMo.input.InputFrame.*;

public class DuMo {
    public final Settings settings;
    public final Board board;
    public final Game game;
    public final TatamiTool tatami;
    public final DuMoPanel dumoPanel;
    public final InputFrame frame;

    public boolean isDisplayHovered = false;

    public DuMo(int pxps, int x, int y) {
        settings = new Settings(pxps);
        game = new Game(this, x, y);
        board = game.getBoard();

        tatami = new TatamiTool(board, pxps, new PieceKyap((byte) 0b0_0_000000));

        frame = new InputFrame();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                settings.save();
            }
        });

        frame.setIconImage(new ImageIcon("C:\\02Sys\\Ico\\not ico\\DuMo_1x1_01.png").getImage());
        frame.setTitle("DuMo Kyap v0-03");

        JPanel contentPane = new JPanel();
        contentPane.setBackground(new Color(0x89B0A8));
        frame.setContentPane(contentPane);

        dumoPanel = new DuMoPanel(pxps, this);
        contentPane.add(dumoPanel);

        frame.afterInteraction = frame::repaint;
        frame.interactionListeners.add(board::getEdgeStatus);
        frame.wheelListeners.add((mov) -> {
            if (frame.isMouseOver(dumoPanel)) {
                if (mov > 0) game.rotateCurrentRight();
                else if (mov < 0) game.rotateCurrentLeft();
            }
        });
        frame.mouseListeners.add(() -> {
            isDisplayHovered = frame.isMouseOver(dumoPanel);
        });

        frame.keymap.register(new KeyBinding().then(BTN_LEFT).onPress(game::onPlace));
        frame.keymap.register(new KeyBinding().then(BTN_RIGHT).onPress(game::onNext));
        frame.keymap.register(new KeyBinding().then(BTN_MIDDLE).onPress(game::onRemove));
        frame.keymap.register(new KeyBinding().then(VK_F1).onPress(settings.showEdges::toggle));
        frame.keymap.register(new KeyBinding().then(VK_F2).onPress(settings.showInvalid::toggle));
        frame.keymap.register(new KeyBinding().then(VK_F3).onPress(settings.showGrid::toggle));
        frame.keymap.register(new KeyBinding().then(VK_F4)
                .onPress(() -> settings.showGrid.set(true))
                .onRelease(() -> settings.showGrid.set(false)));
        frame.keymap.register(new KeyBinding().then(VK_R).onPress(board::resetBoard));
        frame.keymap.register(new KeyBinding().then(VK_W).onPress(game::invertCurrent));
        frame.keymap.register(new KeyBinding().then(VK_F12).onPress(() -> {
            tatami.regenerate();
            tatami.toggle();
        }));
        frame.keymap.register(new KeyBinding().then(VK_SHIFT).then(VK_X).onPress(() -> {
            game.rescale(board.getBoardX() - 1, board.getBoardY());
            dumoPanel.setPreferredSize(new Dimension(board.getBoardX() * pxps, board.getBoardY() * pxps));
            frame.pack();
        }));
        frame.keymap.register(new KeyBinding().then(VK_X).exclusive().onPress(() -> {
            game.rescale(board.getBoardX() + 1, board.getBoardY());
            dumoPanel.setPreferredSize(new Dimension(board.getBoardX() * pxps, board.getBoardY() * pxps));
            frame.pack();
        }));
        frame.keymap.register(new KeyBinding().then(VK_SHIFT).then(VK_Y).onPress(() -> {
            game.rescale(board.getBoardX(), board.getBoardY() - 1);
            dumoPanel.setPreferredSize(new Dimension(board.getBoardX() * pxps, board.getBoardY() * pxps));
            frame.pack();
        }));
        frame.keymap.register(new KeyBinding().then(VK_Y).exclusive().onPress(() -> {
            game.rescale(board.getBoardX(), board.getBoardY() + 1);
            dumoPanel.setPreferredSize(new Dimension(board.getBoardX() * pxps, board.getBoardY() * pxps));
            frame.pack();
        }));
        frame.keymap.register(new KeyBinding().then(VK_RIGHT).onPress(() -> game.pan(1, 0)));
        frame.keymap.register(new KeyBinding().then(VK_LEFT).onPress(() -> game.pan(-1, 0)));
        frame.keymap.register(new KeyBinding().then(VK_UP).onPress(() -> game.pan(0, -1)));
        frame.keymap.register(new KeyBinding().then(VK_DOWN).onPress(() -> game.pan(0, 1)));

        frame.pack();
    }

    public static final boolean ASK_SIZE_ON_LAUNCH = false;

    public static void main(String[] args) throws IOException {
        int x = 8;
        int y = 8;
        int pxps = 100;
        if (args != null && args.length == 3) {
            try {
                pxps = Integer.parseInt(args[0]);
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {

            }
        }
        if (ASK_SIZE_ON_LAUNCH) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
                String a = bufferedReader.readLine();
                x = Integer.parseInt(a);
                a = bufferedReader.readLine();
                y = Integer.parseInt(a);
            } catch (NumberFormatException ignored) {
            }
        }

        DuMo dumo = new DuMo(pxps, x, y);
        dumo.board.place(new PieceKyap((byte) 0b0_0_000110), 5, 2);
        dumo.board.place(new PieceKyap((byte) 0b0_0_101001), 7, 3);
        dumo.board.place(new PieceKyap((byte) 0b0_0_111110), 5, 4);
        dumo.board.place(new PieceKyap((byte) 0b0_0_000011), 7, 5);
        dumo.frame.setVisible(true);
    }
}