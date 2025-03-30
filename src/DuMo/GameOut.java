package DuMo;

import DuMo.piece.Piece;

import javax.swing.*;
import java.awt.*;

public final class GameOut extends JPanel {
    private final Main main;

    public GameOut(int pxps, Main main) {
        this.main = main;
        setPreferredSize(new Dimension(this.main.game.boardX * pxps, this.main.game.boardY * pxps));
    }

    private static final Color WHITE = new Color(0xffffff);
    private static final Color BLACK = new Color(0);
    private static final Color HIGHLIGHT = new Color(0xff0000);
    private static final Color GRID = new Color(0x74938E);

    @Override
    public void paint(Graphics g) {
        if (main.settings.settingShowGrid) {
            g.setColor(GRID);
            for (int x = 1; x < main.game.boardX; x++)
                g.drawLine((getWidth() * x) / main.game.boardX, 0, (getWidth() * x) / main.game.boardX, getHeight());
            for (int y = 1; y < main.game.boardY; y++)
                g.drawLine(0, (getHeight() * y) / main.game.boardY, getWidth(), (getHeight() * y) / main.game.boardY);
        }

        int[] gHgI = getHI();
        int gH = gHgI[0];
        int gI = gHgI[1];
        if (main.game.isValid(gH, gI))
            if (main.game.isHorizontal(gH)) {
                paintPieceH(g, main.game.curPiece, gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            } else {
                paintPieceV(g, main.game.curPiece, gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            }

        for (int h = 0; h < main.game.boardH; h += 2)
            for (int i = 0; i < main.game.boardHI; i++) {
                if (main.game.isPiece(h, i))
                    paintPieceH(g, main.game.board[h][i], h, i, BLACK, WHITE);

                if (main.settings.settingShowInvalid && main.game.edgeStatus[h][i] == Piece.EDGE_INVALID)
                    paintEdgeV(g, h, i, HIGHLIGHT);
            }

        for (int h = 1; h < main.game.boardH; h += 2)
            for (int i = 0; i < main.game.boardVI; i++) {
                if (main.game.isPiece(h, i))
                    paintPieceV(g, main.game.board[h][i], h, i, BLACK, WHITE);

                if (main.settings.settingShowInvalid && main.game.edgeStatus[h][i] == Piece.EDGE_INVALID)
                    paintEdgeH(g, h, i, HIGHLIGHT);
            }
    }

    private void paintPieceH(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = main.settings.settingShowEdges ? 1 : 0;
        int x2 = main.settings.settingShowEdges ? -1 : 0;
        int y1 = main.settings.settingShowEdges ? 1 : 0;
        int y2 = main.settings.settingShowEdges ? -1 : 0;

        gH /= 2;
        x1 += gI * getWidth() / main.game.boardX;
        y1 += gH * getHeight() / main.game.boardY;
        x2 += (gI + 2) * getWidth() / main.game.boardX;
        y2 += (gH + 1) * getHeight() / main.game.boardY;

        piece.paintH(g, x1, y1, x2, y2, black, white);
    }

    private void paintPieceV(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = main.settings.settingShowEdges ? 1 : 0;
        int x2 = main.settings.settingShowEdges ? -1 : 0;
        int y1 = main.settings.settingShowEdges ? 1 : 0;
        int y2 = main.settings.settingShowEdges ? -1 : 0;

        gH /= 2;
        x1 += gI * getWidth() / main.game.boardX;
        y1 += gH * getHeight() / main.game.boardY;
        x2 += (gI + 1) * getWidth() / main.game.boardX;
        y2 += (gH + 2) * getHeight() / main.game.boardY;

        piece.paintV(g, x1, y1, x2, y2, black, white);
    }

    private void paintEdgeH(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h = h / 2 + 1;

        int x1 = i * getWidth() / main.game.boardX;
        int y1 = h * getHeight() / main.game.boardY;
        int x2 = (i + 1) * getWidth() / main.game.boardX;

        g.fillRect(x1, y1 - 1, x2 - x1, 3);
    }

    private void paintEdgeV(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h /= 2;

        int y1 = h * getHeight() / main.game.boardY;
        int x2 = (i + 1) * getWidth() / main.game.boardX;
        int y2 = (h + 1) * getHeight() / main.game.boardY;

        g.fillRect(x2 - 1, y1, 3, y2 - y1);

    }

    //?_Offset are byte[4]s in integer form.
    private static final int H_OFFSET = (1 << (Game.DOWN << 3)) | (0xff << (Game.UP << 3)); //H_OFFSET[DOWN] = 1, H_OFFSET[UP] = -1, H_OFFSET[LEFT | RIGHT] = 0
    private static final int I_OFFSET = 0xff << (Game.LEFT << 3); //I_OFFSET[LEFT] = -1, I_OFFSET[RIGHT | DOWN | UP] = 0

    public int[] getHI() {
        double bWidth = getWidth() / (double) main.game.boardX;
        double bHeight = getHeight() / (double) main.game.boardY;
        int xB = main.mouseX / (int) bWidth;
        int yB = main.mouseY / (int) bHeight;
        int filled = main.game.whereIsTileFilled(xB, yB);
        int[] res = new int[2];

        if (filled == Game.EMPTY) {
            boolean fD = main.game.whereIsTileFilled(xB, yB + 1) != Game.EMPTY; //Is the tile below this one filled / invalid?
            boolean fR = main.game.whereIsTileFilled(xB + 1, yB) != Game.EMPTY;
            boolean fU = main.game.whereIsTileFilled(xB, yB - 1) != Game.EMPTY;
            boolean fL = main.game.whereIsTileFilled(xB - 1, yB) != Game.EMPTY;

            boolean opposeX = fL && fR;
            boolean opposeY = fU && fD;

            if ((fD == fU) && (fR == fL) && (fD == fL)) {
                double xRel = main.mouseX % bWidth;
                double yRel = main.mouseY % bHeight;

                boolean ur = xRel > yRel;
                boolean ul = bWidth - xRel > yRel;

                if (ur ^ ul) { // < or >
                    res[0] = yB * 2; // H (height) is known
                    res[1] = ul ? // if left
                            xB - 1 :
                            xB;
                } else { // ^ or v
                    res[1] = xB; // I (index) = same as the tile at X
                    res[0] = ul ? // if up
                            (yB << 1) - 1 :
                            (yB << 1) + 1;
                }
            } else if (opposeX || opposeY) {

            } else {
                double xRel = main.mouseX % bWidth;
                double yRel = main.mouseY % bHeight;

                boolean ur = xRel > yRel;
                boolean ul = bWidth - xRel > yRel;
                int dir = (ur ? 1 : 0) | (ul ? 0b10 : 0);

                if (ur ^ ul) { // < or >
                    res[0] = yB * 2; // H (height) is known
                    res[1] = (ul && fL) || (ul && !fR) ? // if right or left and filled
                            xB :
                            xB - 1;
                } else { // ^ or v
                    res[1] = xB; // I (index) = same as the tile at X
                    res[0] = (ul && fU) || (ul && !fD) ? // if down or up and filled
                                    (yB << 1) + 1 :
                                    (yB << 1) - 1;
                }
            }
        } else {
            //other idea:
            //h = (x << 1) + (dir >> 2)
            //i = y + (dir & 0b11)
            res[0] = (yB << 1) + (byte) (H_OFFSET >> (filled << 3));
            res[1] = xB + (byte) (I_OFFSET >> (filled << 3));
        }
        System.out.println("h: " + res[0] + ", i: " + res[1]);
        return res;
    }

    public int convertToRelativeDisplayX(int xM) {
        return xM - getX() - 10;
    }

    public int convertToRelativeDisplayY(int yM) {
        return yM - getY() - 30;
    }
}