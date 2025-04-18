package DuMo;

import DuMo.board.Board;
import DuMo.piece.Piece;

import javax.swing.*;
import java.awt.*;

public final class GameOut extends JPanel {
    private final Main main;

    public GameOut(int pxps, Main main) {
        this.main = main;
        setPreferredSize(new Dimension(this.main.board.getBoardX() * pxps, this.main.board.getBoardY() * pxps));
    }

    private static final Color WHITE = new Color(0xffffff);
    private static final Color BLACK = new Color(0);
    private static final Color HIGHLIGHT = new Color(0xff0000);
    private static final Color GRID = new Color(0x688D8A);

    @Override
    public void paint(Graphics g) {
        int[] gHgI = getHI();
        int gH = gHgI[0];
        int gI = gHgI[1];

        //render grid
        if (main.settings.settingShowGrid) {
            g.setColor(GRID);
            for (int x = 1; x < main.board.getBoardX(); x++)
                g.drawLine((getWidth() * x) / main.board.getBoardX(), 0, (getWidth() * x) / main.board.getBoardX(), getHeight());
            for (int y = 1; y < main.board.getBoardY(); y++)
                g.drawLine(0, (getHeight() * y) / main.board.getBoardY(), getWidth(), (getHeight() * y) / main.board.getBoardY());

            //if (main.shiftHeld) {
            //    if (Game.isHorizontal(gH)) {
            //        paintEdgeV(g, gH, gI, HIGHLIGHT);
            //    } else {
            //        paintEdgeH(g, gH, gI, HIGHLIGHT);
            //    }
            //}
        }

        //render preview
        if (main.board.isValid(gH, gI))
            if (Board.isHorizontal(gH)) {
                paintPieceH(g, main.game.getCurPiece(), gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            } else {
                paintPieceV(g, main.game.getCurPiece(), gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            }

        byte[][] edgeStatus = (main.settings.settingShowInvalid && main.settings.settingShowEdges) ? main.board.getEdgeStatus() : null;
        if (edgeStatus != null) for (int h = 0; h < edgeStatus.length; h++) {
            for (int i = 0; i < edgeStatus[h].length; i++) {
                if (edgeStatus[h][i] == Piece.EDGE_INVALID)
                    if (Board.isHorizontal(h)) paintEdgeV(g, h, i, HIGHLIGHT);
                    else paintEdgeH(g, h, i, HIGHLIGHT);
            }
        }

        //render horizontal pieces n edges
        main.board.forEachHorizontalPiece((piece, h, i) -> {
            if (main.board.isPiece(h, i))
                paintPieceH(g, piece, h, i, BLACK, WHITE);
        });

        //render vertical pieces n edges
        main.board.forEachVerticalPiece((piece, h, i) -> {
            if (main.board.isPiece(h, i))
                paintPieceV(g, piece, h, i, BLACK, WHITE);
        });
    }

    private void paintPieceH(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = main.settings.settingShowEdges ? 1 : 0;
        int x2 = main.settings.settingShowEdges ? -1 : 0;
        int y1 = main.settings.settingShowEdges ? 1 : 0;
        int y2 = main.settings.settingShowEdges ? -1 : 0;

        gH >>= 1;
        x1 += gI * getWidth() / main.board.getBoardX();
        y1 += gH * getHeight() / main.board.getBoardY();
        x2 += (gI + 2) * getWidth() / main.board.getBoardX();
        y2 += (gH + 1) * getHeight() / main.board.getBoardY();

        piece.paintH(g, x1, y1, x2, y2, black, white);
    }

    private void paintPieceV(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = main.settings.settingShowEdges ? 1 : 0;
        int x2 = main.settings.settingShowEdges ? -1 : 0;
        int y1 = main.settings.settingShowEdges ? 1 : 0;
        int y2 = main.settings.settingShowEdges ? -1 : 0;

        gH >>= 1;
        x1 += gI * getWidth() / main.board.getBoardX();
        y1 += gH * getHeight() / main.board.getBoardY();
        x2 += (gI + 1) * getWidth() / main.board.getBoardX();
        y2 += (gH + 2) * getHeight() / main.board.getBoardY();

        piece.paintV(g, x1, y1, x2, y2, black, white);
    }

    private void paintEdgeH(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h = (h >> 1) + 1;

        int x1 = i * getWidth() / main.board.getBoardX();
        int y1 = h * getHeight() / main.board.getBoardY();
        int x2 = (i + 1) * getWidth() / main.board.getBoardX();

        g.fillRect(x1, y1 - 1, x2 - x1, 3);
    }

    private void paintEdgeV(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h >>= 1;

        int y1 = h * getHeight() / main.board.getBoardY();
        int x2 = (i + 1) * getWidth() / main.board.getBoardX();
        int y2 = (h + 1) * getHeight() / main.board.getBoardY();

        g.fillRect(x2 - 1, y1, 3, y2 - y1);

    }

    //?_Offset are byte[4]s in integer form.
    private static final int H_OFFSET = (1 << (Board.DOWN << 3)) | (0xff << (Board.UP << 3)); //H_OFFSET[DOWN] = 1, H_OFFSET[UP] = -1, H_OFFSET[LEFT | RIGHT] = 0
    private static final int I_OFFSET = 0xff << (Board.LEFT << 3); //I_OFFSET[LEFT] = -1, I_OFFSET[RIGHT | DOWN | UP] = 0

    public int[] getHI() {
        int p = main.settings.pxps;
        int xB = main.mouseX / p;
        int yB = main.mouseY / p;
        int filled = main.board.whereIsTileFilled(xB, yB);
        int[] res = new int[2];

        if (filled == Board.EMPTY) {
            boolean fD = main.board.whereIsTileFilled(xB, yB + 1) != Board.EMPTY; //Is the tile below this one filled / invalid?
            boolean fR = main.board.whereIsTileFilled(xB + 1, yB) != Board.EMPTY;
            boolean fU = main.board.whereIsTileFilled(xB, yB - 1) != Board.EMPTY;
            boolean fL = main.board.whereIsTileFilled(xB - 1, yB) != Board.EMPTY;
            boolean opposeX = fL && fR;
            boolean opposeY = fU && fD;

            if (opposeX ^ opposeY) {
                if (opposeX) {
                    boolean upFocused = (main.mouseY % p) < p >> 1;
                    res[0] = (yB << 1) + 1;
                    res[1] = xB;
                    if (!upFocused && fD || upFocused && !fU)
                        res[0] -= 2;
                } else {
                    boolean leftFocused = (main.mouseX % p) < p >> 1;
                    res[0] = yB << 1;
                    res[1] = xB - 1;
                    if (leftFocused && fL || !leftFocused && !fR) {
                        res[1] += 1;
                    }
                }
            } else { //maybe: Falls D & R & U & L -> ? (case nothing selected)
                double xRel = main.mouseX % p;
                double yRel = main.mouseY % p;
                boolean ur = xRel > yRel;
                boolean ul = p - xRel > yRel;
                if (ur ^ ul) { // < or >
                    res[0] = yB * 2; // H (height) is known
                    res[1] = (ul && fL) || !(ul || fR) ? // if right or left and filled
                            xB :
                            xB - 1;
                } else { // ^ or v
                    res[1] = xB; // I (index) = same as the tile at X
                    res[0] = (ul && fU) || !(ul || fD) ? // if down or up and filled
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
        return res;
    }

    public int convertToRelativeDisplayX(int xM) {
        return xM - getX() - 10;
    }

    public int convertToRelativeDisplayY(int yM) {
        return yM - getY() - 30;
    }
}