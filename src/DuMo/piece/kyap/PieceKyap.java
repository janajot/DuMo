package DuMo.piece.kyap;

import DuMo.piece.Piece;

import java.awt.*;

public class PieceKyap implements Piece {
    private static final byte INVALID = (byte) 0b1_0_000000;
    private static final byte EMPTY = 0b0_1_000000;

    private byte state;

    //Constructor
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public PieceKyap(byte state) {
        this.state = state;
    }

    //GUI methods
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintH(Graphics g, int x0, int y0, int x1, int y1, Color black, Color white) {
        int xMid = (x1 + x0) / 2;
        int yMid = (y1 + y0) / 2;

        g.setColor(black);
        g.fillRect(x0, y0, x1 - x0, y1 - y0);

        g.setColor(white);
        if ((state & 0b1) != 0)
            g.fillPolygon(new int[]{x0, xMid, x0}, new int[]{y0, yMid, y1}, 3);
        if ((state & 0b10) != 0)
            g.fillPolygon(new int[]{x0, xMid, xMid}, new int[]{y1, yMid, y1}, 3);
        if ((state & 0b100) != 0)
            g.fillPolygon(new int[]{xMid, xMid, x1}, new int[]{y1, yMid, y1}, 3);
        if ((state & 0b1000) != 0)
            g.fillPolygon(new int[]{x1, xMid, x1}, new int[]{y1, yMid, y0}, 3);
        if ((state & 0b10000) != 0)
            g.fillPolygon(new int[]{x1, xMid, xMid}, new int[]{y0, yMid, y0}, 3);
        if ((state & 0b100000) != 0)
            g.fillPolygon(new int[]{xMid, xMid, x0}, new int[]{y0, yMid, y0}, 3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintV(Graphics g, int x0, int y0, int x1, int y1, Color black, Color white) {
        int xMid = (x1 + x0) / 2;
        int yMid = (y1 + y0) / 2;

        g.setColor(black);
        g.fillRect(x0, y0, x1 - x0, y1 - y0);

        g.setColor(white);
        if ((state & 0b1) != 0)
            g.fillPolygon(new int[]{x0, xMid, x1}, new int[]{y1, yMid, y1}, 3);
        if ((state & 0b10) != 0)
            g.fillPolygon(new int[]{x1, xMid, x1}, new int[]{y1, yMid, yMid}, 3);
        if ((state & 0b100) != 0)
            g.fillPolygon(new int[]{x1, xMid, x1}, new int[]{yMid, yMid, y0}, 3);
        if ((state & 0b1000) != 0)
            g.fillPolygon(new int[]{x1, xMid, x0}, new int[]{y0, yMid, y0}, 3);
        if ((state & 0b10000) != 0)
            g.fillPolygon(new int[]{x0, xMid, x0}, new int[]{y0, yMid, yMid}, 3);
        if ((state & 0b100000) != 0)
            g.fillPolygon(new int[]{x0, xMid, x0}, new int[]{yMid, yMid, y1}, 3);
    }

    //Manipulation
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void rotateLeft() {
        state <<= 1;
        if ((state & 64) != 0) state ^= 0b1_000001;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rotateRight() {
        if ((state & 1) != 0) state |= 0b1000000;
        state >>= 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invert() {
        state ^= 0b111111;
    }

    @Override
    public void setInvalid() {
        if (isInvalid()) state++;
        else state = INVALID;
    }

    @Override
    public void setNext() {
        if (state < 0b00111111) state++;
        else state = (byte) 0;
    }

    @Override
    public void setValidated() {
        if (isInvalid()) state--;
    }

    @Override
    public void setEmpty() {
        state = EMPTY;
    }

    //Checks
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvalid() {
        return state < 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        return state >= 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPiece() {
        return (state & 0b11000000) == 0;
    }

    //Getters
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getEdge(int n) {
        return isPiece() && n < 6 && n >= 0 ?
                getEdgeUnchecked(n) :
                Piece.EDGE_NONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte getEdgeUnchecked(int n) {
        return (byte) ((state & (1 << n)) >> n);
    }

    //Util
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public PieceKyap clone() {
        return new PieceKyap(state);
    }

    @Override
    public String toString() {
        return String.format("%8s", Integer.toBinaryString(state & 0xFF)).replace(' ', '0');
    }
}
