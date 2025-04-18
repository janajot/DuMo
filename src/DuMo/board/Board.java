package DuMo.board;

import DuMo.piece.Piece;

import java.util.Optional;

public interface Board {
    int EMPTY = -1, LEFT = 2, RIGHT = 0, UP = 3, DOWN = 1, OOB = 4;

    int getBoardX();

    int getBoardY();

    /**
     * Tries to place the Piece at the given position.
     * If the position is invalid or out of bounds, nothing is changed.
     * If the position already contains a Piece, it is overridden
     * @param piece The Piece to be placed
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return the previous Piece at the position, or null if there was none or nothing has changed
     */
    Optional<Piece> place(Piece piece, int h, int i);

    /**
     * Tries to remove the Piece at the given position. If the position does not contain a Piece or is out of bounds, nothing is changed.
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return the Piece that was removed, or null if nothing changed
     */
    Optional<Piece> remove(int h, int i);

    /**
     * Removes all the Pieces on the board
     */
    void resetBoard();

    /**
     * Checks whether a piece slot is valid.
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return true, if and only if a Piece could be placed at the given slot
     * @see Piece#isValid
     * @see #isOutOfBounds
     */
    boolean isValid(int h, int i);

    /**
     * Checks whether a piece slot is invalid
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return true if and only if the specified Piece slot is invalid but in bounds
     * @see Piece#isInvalid
     * @see Board#isOutOfBounds
     */
    boolean isInvalid(int h, int i);

    /**
     * Checks whether a piece slot contains a Piece
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return true if and only if the specified Piece slot contains a Piece and is in bounds
     * @see Piece#isPiece
     * @see Board#isOutOfBounds
     */
    boolean isPiece(int h, int i);

    /**
     * Checks whether a piece slot contains no Piece
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return true if and only if the specified Piece slot contains no Piece but is in bounds
     * @see Piece#isPiece
     * @see Board#isOutOfBounds
     */
    boolean isNoPiece(int h, int i);

    /**
     * Checks whether the given piece slot is out of bounds
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return Whether any part of a Piece that would
     * be placed in the given piece slot would reach
     * outside the confines of the board
     */
    boolean isOutOfBounds(int h, int i);

    byte[][] getEdgeStatus();

    /**
     * Gets the piece at the specified position, if it contains one, otherwise empty.
     * @param h the height of the piece
     * @param i the index of the piece
     * @return the Piece
     * @see Piece
     */
    Optional<Piece> getPiece(int h, int i);

    /**
     * Checks whether the given tile is Filled or not, and if so, returns where.
     * @param x The x position of the Tile.
     * @param y The y position of the Tile.
     * @return Where the tile is filled.
     * ({@link #EMPTY}: {@value #EMPTY},
     * {@link #LEFT}: {@value #LEFT},
     * {@link #RIGHT}: {@value #RIGHT},
     * {@link #UP}: {@value #UP},
     * {@link #DOWN}: {@value #DOWN})
     * {@link #OOB}: {@value #OOB})
     * @see #isOutOfBounds
     * @see #getXY
     * @see #getHIVPositive
     * @see #getHIHPositive
     */
    int whereIsTileFilled(int x, int y);

    /**
     * Loops through all Pieces on the board.
     * @param action The method that will be invoked for each Piece.
     *                 The params are: piece, the Piece at the position h,
     *                 the height of the position and i, the index of it.
     */
    void forEachPiece(PieceConsumer action);

    void forEachHorizontalPiece(PieceConsumer action);

    void forEachVerticalPiece(PieceConsumer action);

    /**
     * Returns a boolean array that represents the state of the board
     * ignoring pieces as well as colouring.
     * Instead, only the shape of the laid down pieces is evaluated.
     * The edges of the board are filled in [all true].
     *
     * @return the board state. true = filled, false = empty.
     * The first sizeX indices of the array contain the first row of tiles,
     * the second sizeX indices the second row, and so on.
     */
    boolean[] getFilledTiles();

    /**
     * Checks whether the given height would contain a horizontal or vertical piece.
     *
     * @param h the height to check
     * @return true, if the given height would contain horizontal pieces.
     */
    static boolean isHorizontal(int h) {
        return (h & 1) == 0;
    }


    /**
     * calculates the vertical piece slot in h|i format with the root of x|y
     *
     * @param x the x position of the root
     * @param y the y position of the root
     * @return the h|i coordinates of the lowest vertical piece slot that contains the root position
     * @see #getHIHPositive
     * @see #getXY
     */
    static int[] getHIVPositive(int x, int y) {
        return new int[]{(y << 1) + 1, x};
    }

    /**
     * calculates the horizontal piece slot in h|i format with the root of x|y
     *
     * @param x the x position of the root
     * @param y the y position of the root
     * @return the h|i coordinates of the rightmost horizontal piece slot that contains the root position
     * @see #getHIVPositive
     * @see #getXY
     */
    static int[] getHIHPositive(int x, int y) {
        return new int[]{y << 1, x};
    }

    /**
     * converts from the position format h|i to x|y
     *
     * @param h the given height
     * @param i the given index
     * @return the smallest [leftmost/highest] x|y position the given piece slot contains.
     * @see #getHIHPositive
     * @see #getHIVPositive
     */
    static int[] getXY(int h, int i) {
        return new int[]{h >> 1, i};
    }

    @FunctionalInterface
    interface PieceConsumer {
        void accept(Piece piece, int h, int i);
    }
}
