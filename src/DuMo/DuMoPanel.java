package DuMo;

import DuMo.board.Board;
import DuMo.piece.Piece;

import javax.swing.*;
import java.awt.*;

public final class DuMoPanel extends JPanel {
    private final DuMo dumo;

    public DuMoPanel(int pxps, DuMo dumo) {
        this.dumo = dumo;
        setPreferredSize(new Dimension(this.dumo.board.getBoardX() * pxps, this.dumo.board.getBoardY() * pxps));
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
        if (dumo.settings.showGrid.get()) {
            g.setColor(GRID);
            for (int x = 1; x < dumo.board.getBoardX(); x++)
                g.drawLine((getWidth() * x) / dumo.board.getBoardX(), 0, (getWidth() * x) / dumo.board.getBoardX(), getHeight());
            for (int y = 1; y < dumo.board.getBoardY(); y++)
                g.drawLine(0, (getHeight() * y) / dumo.board.getBoardY(), getWidth(), (getHeight() * y) / dumo.board.getBoardY());

            //if (main.shiftHeld) {
            //    if (Game.isHorizontal(gH)) {
            //        paintEdgeV(g, gH, gI, HIGHLIGHT);
            //    } else {
            //        paintEdgeH(g, gH, gI, HIGHLIGHT);
            //    }
            //}
        }

        //render preview
        if (dumo.board.isValid(gH, gI))
            if (Board.isHorizontal(gH)) {
                paintPieceH(g, dumo.game.getCurPiece(), gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            } else {
                paintPieceV(g, dumo.game.getCurPiece(), gH, gI, new Color((BLACK.getRGB() & 0xffffff) | 0x80_000000, true), new Color((WHITE.getRGB() & 0xffffff) | 0x80_000000, true));
            }

        byte[][] edgeStatus = (dumo.settings.showInvalid.get() && dumo.settings.showEdges.get()) ? dumo.board.getEdgeStatus() : null;
        if (edgeStatus != null) for (int h = 0; h < edgeStatus.length; h++) {
            for (int i = 0; i < edgeStatus[h].length; i++) {
                if (edgeStatus[h][i] == Piece.EDGE_INVALID)
                    if (Board.isHorizontal(h)) paintEdgeV(g, h, i, HIGHLIGHT);
                    else paintEdgeH(g, h, i, HIGHLIGHT);
            }
        }

        //render horizontal pieces n edges
        dumo.board.forEachHorizontalPiece((piece, h, i) -> {
            if (dumo.board.isPiece(h, i))
                paintPieceH(g, piece, h, i, BLACK, WHITE);
        });

        //render vertical pieces n edges
        dumo.board.forEachVerticalPiece((piece, h, i) -> {
            if (dumo.board.isPiece(h, i))
                paintPieceV(g, piece, h, i, BLACK, WHITE);
        });
    }

    private void paintPieceH(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = dumo.settings.showEdges.get() ? 1 : 0;
        int x2 = dumo.settings.showEdges.get() ? -1 : 0;
        int y1 = dumo.settings.showEdges.get() ? 1 : 0;
        int y2 = dumo.settings.showEdges.get() ? -1 : 0;

        gH >>= 1;
        x1 += gI * getWidth() / dumo.board.getBoardX();
        y1 += gH * getHeight() / dumo.board.getBoardY();
        x2 += (gI + 2) * getWidth() / dumo.board.getBoardX();
        y2 += (gH + 1) * getHeight() / dumo.board.getBoardY();

        piece.paintH(g, x1, y1, x2, y2, black, white);
    }

    private void paintPieceV(Graphics g, Piece piece, int gH, int gI, Color black, Color white) {
        int x1 = dumo.settings.showEdges.get() ? 1 : 0;
        int x2 = dumo.settings.showEdges.get() ? -1 : 0;
        int y1 = dumo.settings.showEdges.get() ? 1 : 0;
        int y2 = dumo.settings.showEdges.get() ? -1 : 0;

        gH >>= 1;
        x1 += gI * getWidth() / dumo.board.getBoardX();
        y1 += gH * getHeight() / dumo.board.getBoardY();
        x2 += (gI + 1) * getWidth() / dumo.board.getBoardX();
        y2 += (gH + 2) * getHeight() / dumo.board.getBoardY();

        piece.paintV(g, x1, y1, x2, y2, black, white);
    }

    private void paintEdgeH(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h = (h >> 1) + 1;

        int x1 = i * getWidth() / dumo.board.getBoardX();
        int y1 = h * getHeight() / dumo.board.getBoardY();
        int x2 = (i + 1) * getWidth() / dumo.board.getBoardX();

        g.fillRect(x1, y1 - 1, x2 - x1, 3);
    }

    private void paintEdgeV(Graphics g, int h, int i, Color color) {
        g.setColor(color);
        h >>= 1;

        int y1 = h * getHeight() / dumo.board.getBoardY();
        int x2 = (i + 1) * getWidth() / dumo.board.getBoardX();
        int y2 = (h + 1) * getHeight() / dumo.board.getBoardY();

        g.fillRect(x2 - 1, y1, 3, y2 - y1);

    }

    //?_Offset are byte[4]s in integer form.
    private static final int H_OFFSET = (1 << (Board.DOWN << 3)) | (0xff << (Board.UP << 3)); //H_OFFSET[DOWN] = 1, H_OFFSET[UP] = -1, H_OFFSET[LEFT | RIGHT] = 0
    private static final int I_OFFSET = 0xff << (Board.LEFT << 3); //I_OFFSET[LEFT] = -1, I_OFFSET[RIGHT | DOWN | UP] = 0

    public int[] getHI() {
        Point G = dumo.frame.getMouse(this);
        int p = dumo.settings.pxps.get();
        int xB = G.x / p;
        int yB = G.y / p;
        int h, i;
        int filled = dumo.board.whereIsTileFilled(xB, yB);
        if (filled == Board.EMPTY) {
            boolean fD = dumo.board.whereIsTileFilled(xB, yB + 1) != Board.EMPTY; //Is the tile below this one filled / invalid?
            boolean fR = dumo.board.whereIsTileFilled(xB + 1, yB) != Board.EMPTY;
            boolean fU = dumo.board.whereIsTileFilled(xB, yB - 1) != Board.EMPTY;
            boolean fL = dumo.board.whereIsTileFilled(xB - 1, yB) != Board.EMPTY;
            boolean opposeX = fL && fR;
            boolean opposeY = fU && fD;

            if (opposeX ^ opposeY) {
                if (opposeX) {
                    boolean upFocused = (G.y % p) < p >> 1;
                    h = (yB << 1) + 1;
                    i = xB;
                    if (!upFocused && fD || upFocused && !fU)
                        h -= 2;
                } else {
                    boolean leftFocused = (G.x % p) < p >> 1;
                    h = yB << 1;
                    i = xB - 1;
                    if (leftFocused && fL || !leftFocused && !fR) {
                        i += 1;
                    }
                }
            } else { //maybe: if D & R & U & L -> ? (case nothing selected)
                double xRel = G.x % p;
                double yRel = G.y % p;
                boolean ur = xRel > yRel;
                boolean ul = p - xRel > yRel;
                if (ur ^ ul) { // < or >
                    h = yB * 2; // H (height) is known
                    i = (ul && fL) || !(ul || fR) ? // if right or left and filled
                            xB :
                            xB - 1;
                } else { // ^ or v
                    i = xB; // I (index) = same as the tile at X
                    h = (ul && fU) || !(ul || fD) ? // if down or up and filled
                            (yB << 1) + 1 :
                            (yB << 1) - 1;
                }
            }
        } else {
            //other idea:
            //h = (x << 1) + (dir >> 2)
            //i = y + (dir & 0b11)
            h = (yB << 1) + (byte) (H_OFFSET >> (filled << 3));
            i = xB + (byte) (I_OFFSET >> (filled << 3));
        }
        return new int[] {h, i};
    }
}