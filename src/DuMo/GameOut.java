package DuMo;

import DuMo.piece.Piece;
import DuMo.util.CoordinateTransform;

import javax.swing.*;
import java.awt.*;

public final class GameOut extends JPanel {
    public GameOut(int pxps) {
        setPreferredSize(new Dimension(Game.boardX * pxps, Game.boardY * pxps));
    }

    private static final Color WHITE = new Color(0xffffff);
    private static final Color BLACK = new Color(0);
    private static final Color HIGHLIGHT = new Color(0xff0000);
    private static final Color GRID = new Color(0x74938E);

    @Override
    public void paint(Graphics g) {
        if (Main.settingShowGrid) {
            g.setColor(GRID);
            for (int x = 1; x < Game.boardX; x++)
                g.drawLine((getWidth() * x) / Game.boardX, 0, (getWidth() * x) / Game.boardX, getHeight());
            for (int y = 1; y < Game.boardY; y++)
                g.drawLine(0, (getHeight() * y) / Game.boardY, getWidth(), (getHeight() * y) / Game.boardY);
        }

        int[] gHgI = CoordinateTransform.getHI();
        int gH = gHgI[0];
        int gI = gHgI[1];
        if (Game.isValid(gH, gI))
            if (Game.isHorizontal(gH)) {
                paintPieceH(g, Game.curPiece, gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            } else {
                paintPieceV(g, Game.curPiece, gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            }

        for (int h = 0; h < Game.boardH; h += 2)
            for (int i = 0; i < Game.boardHI; i++) {
                if (Game.isPiece(h, i))
                    paintPieceH(g, Game.board[h][i], h, i, BLACK, WHITE);

                if (Main.settingShowInvalid && Game.edgeStatus[h][i] == Piece.EDGE_INVALID)
                    paintEdgeV(g, h, i, HIGHLIGHT);
            }

        for (int h = 1; h < Game.boardH; h += 2)
            for (int i = 0; i < Game.boardVI; i++) {
                if (Game.isPiece(h, i))
                    paintPieceV(g, Game.board[h][i], h, i, BLACK, WHITE);

                if (Main.settingShowInvalid && Game.edgeStatus[h][i] == Piece.EDGE_INVALID)
                    paintEdgeH(g, h, i, HIGHLIGHT);
            }
    }

    private void paintPieceH(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = Main.settingShowEdges ? 1 : 0;
        int x2 = Main.settingShowEdges ? -1 : 0;
        int y1 = Main.settingShowEdges ? 1 : 0;
        int y2 = Main.settingShowEdges ? -1 : 0;

        gH /= 2;
        x1 += gI * getWidth() / Game.boardX;
        y1 += gH * getHeight() / Game.boardY;
        x2 += (gI + 2) * getWidth() / Game.boardX;
        y2 += (gH + 1) * getHeight() / Game.boardY;

        piece.paintH(g, x1, y1, x2, y2, black, white);
    }

    private void paintPieceV(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = Main.settingShowEdges ? 1 : 0;
        int x2 = Main.settingShowEdges ? -1 : 0;
        int y1 = Main.settingShowEdges ? 1 : 0;
        int y2 = Main.settingShowEdges ? -1 : 0;

        gH /= 2;
        x1 += gI * getWidth() / Game.boardX;
        y1 += gH * getHeight() / Game.boardY;
        x2 += (gI + 1) * getWidth() / Game.boardX;
        y2 += (gH + 2) * getHeight() / Game.boardY;

        piece.paintV(g, x1, y1, x2, y2, black, white);
    }

    private void paintEdgeH(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h = h / 2 + 1;

        int x1 = i * getWidth() / Game.boardX;
        int y1 = h * getHeight() / Game.boardY;
        int x2 = (i + 1) * getWidth() / Game.boardX;

        g.fillRect(x1, y1 - 1, x2 - x1, 3);
    }

    private void paintEdgeV(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h /= 2;

        int y1 = h * getHeight() / Game.boardY;
        int x2 = (i + 1) * getWidth() / Game.boardX;
        int y2 = (h + 1) * getHeight() / Game.boardY;

        g.fillRect(x2 - 1, y1, 3, y2 - y1);

    }
}