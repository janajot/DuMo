package DuMo.piece;

import java.awt.*;

public interface Piece {
    byte EDGE_NONE = 3;
    byte EDGE_INVALID = 2;
    byte EDGE_WHITE = 1;
    byte EDGE_BLACK = 0;

    void paintH(Graphics g, int x0, int y0, int x1, int y1, Color black, Color white);

    void paintV(Graphics g, int x0, int y0, int x1, int y1, Color black, Color white);

    /**
     * Inverts the all colors on the piece.
     */
    void invert();

    /**
     * Rotates the edge configuration of the piece by one edge counter-clockwise
     * There may be multiple different pieces that fit the new edge configuration, though.
     */
    void rotateLeft();

    /**
     * Rotates the edge configuration of the piece by one edge clockwise.
     * There may be multiple different pieces that fit the new edge configuration, though.
     */
    void rotateRight();

    /**
     * Checks whether a new piece can NOT be placed at the position of this piece object.
     *
     * @return true if a new piece can NOT be placed at the position of this piece object.
     * @see #isValid()
     */
    boolean isInvalid();

    /**
     * Checks whether a new piece could be placed at the position of this piece object.
     *
     * @return true if a new piece could be placed at the position of this piece object.
     * @see #isInvalid()
     */
    boolean isValid();

    /**
     * Checks whether the piece object actually contains a piece
     * (It could also contain an invalid or empty tile).
     *
     * @return true if the piece can be drawn.
     */
    boolean isPiece();

    /**
     * Returns the edge status at the specified edge index.
     *
     * @param n the index of the edge, can range from 0 to 5
     * <pre>
     *   3     |    5  4<br>
     *4     2  |  0      3<br>
     *5     1  |    1  2<br>
     *   0     |  <br>
     *</pre>
     *
     * @return the edge status, if the piece is specified (isPiece), otherwise empty.<br>
     *         0 -> the edge is black<br>
     *         1 -> the edge is white<br>
     *         3 -> the edge status is not specified
     * @see #EDGE_BLACK
     * @see #EDGE_WHITE
     * @see #EDGE_NONE
     * @see #isPiece()
     */
    byte getEdge(int n);

    /**
     * Returns the edge status at the specified edge index. If the piece is not specified (!isPiece), the result will not be useful.
     *
     * @param n the index of the edge, can range from 0 to 5
     * <pre>
     *   3     |    5  4<br>
     *4     2  |  0      3<br>
     *5     1  |    1  2<br>
     *   0     |  <br>
     *</pre>
     *
     * @return the edge status, regardless of if the piece is specified<br>
     *         0 -> the edge is black<br>
     *         1 -> the edge is white
     * @see #EDGE_BLACK
     * @see #EDGE_WHITE
     * @see #isPiece()
     */
    byte getEdgeUnchecked(int n);

    void setInvalid();

    void setNext();

    void setValidated();

    void setEmpty();

    Piece clone();
}
