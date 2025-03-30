package DuMo;

import DuMo.piece.kyap.PieceKyap;
import DuMo.piece.Piece;

import java.util.Arrays;
import java.util.function.Consumer;

public class Game {
    private final Main main;
    public int boardX;
    public int boardY;
    public int boardH;
    public int boardHI;
    public int boardVI;
    public Piece[][] board;
    public Piece curPiece = new PieceKyap((byte) 0b0_0_000000);
    public byte[][] edgeStatus; //TODO super inefficient: 1 byte / edge

    public static final int EMPTY = -1, LEFT = 2, RIGHT = 0, UP = 3, DOWN = 1;

    public Game(Main main) {
        this.main = main;
    }

    //Actions
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void onPlace() {
        int[] gHgI = main.display.getHI();
        if (main.isDisplayHovered) {
            place(curPiece.clone(), gHgI[0], gHgI[1]);
        }
    }

    public void onRemove() {
        int[] gHgI = main.display.getHI();
        if (main.isDisplayHovered) {
            remove(gHgI[0], gHgI[1]);
        }
        System.out.println("h: " + gHgI[0] + ", i: " + gHgI[1]);
    }

    public void onNext() {
        if (main.isDisplayHovered) {
            curPiece.setNext();
        }
    }

    //Board manipulation
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void setInvalid(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).setInvalid();
    }

    private void setValidated(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).setValidated();
    }

    private void setEmpty(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).setEmpty();
    }

    public void place(Piece piece, int h, int i) {
        boolean wasPiece = isPiece(h, i);
        if (isValid(h, i)) {
            board[h][i] = piece;
            if (!wasPiece)
                if (isHorizontal(h)) {
                    setInvalid(h, i - 1);
                    setInvalid(h + 1, i);
                    setInvalid(h + 1, i + 1);
                    setInvalid(h, i + 1);
                    setInvalid(h - 1, i + 1);
                    setInvalid(h - 1, i);
                } else {
                    setInvalid(h + 2, i); //TODO HERE
                    setInvalid(h + 1, i);
                    setInvalid(h - 1, i);
                    setInvalid(h - 2, i);
                    setInvalid(h - 1, i - 1);
                    setInvalid(h + 1, i - 1);
                }
        }
    }

    public void remove(int h, int i) {
        if (!isPiece(h, i)) return;
        if (getPiece(h, i).isPiece()) {
            setEmpty(h, i);
            if (isHorizontal(h)) {
                setValidated(h, i + 1);
                setValidated(h, i - 1);
                setValidated(h - 1, i + 1);
                setValidated(h - 1, i);
                setValidated(h + 1, i + 1);
                setValidated(h + 1, i);
            } else {
                setValidated(h + 1, i);
                setValidated(h + 1, i - 1);
                setValidated(h + 2, i);
                setValidated(h - 1, i);
                setValidated(h - 1, i - 1);
                setValidated(h - 2, i);
            }
        }
    }

    public void resetBoard() {
        boardH = boardY * 2 - 1;
        boardHI = boardX - 1;
        boardVI = boardX;
        board = new Piece[boardH][];
        for (int h = 0; h < boardH; h += 2) {
            board[h] = new Piece[boardHI];
            for (int i = 0; i < boardHI; i++) board[h][i] = new PieceKyap((byte) 0b0_1_000000);
        }
        for (int h = 1; h < boardH; h += 2) {
            board[h] = new Piece[boardVI];
            for (int i = 0; i < boardVI; i++) board[h][i] = new PieceKyap((byte) 0b0_1_000000);
        }

        edgeStatus = new byte[boardH][];
        for (int h = 0; h < boardH; h += 2) {
            edgeStatus[h] = new byte[boardHI];
            Arrays.fill(edgeStatus[h], (byte) 0b1_000000);
        }
        for (int h = 1; h < boardH; h += 2) {
            edgeStatus[h] = new byte[boardVI];
            Arrays.fill(edgeStatus[h], (byte) 0b1_000000);
        }
    }

    //Board checks
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private boolean isOutOfBounds(int h, int i) {
        return h >= boardH ||
                i >= (isHorizontal(h) ?
                        boardHI :
                        boardVI) ||
                h < 0 ||
                i < 0;
    }

    public boolean isHorizontal(int h) {
        return (h & 1) == 0;
    }

    public boolean isValid(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).isValid();
    }

    public boolean isInvalid(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).isInvalid();
    }

    public boolean isPiece(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).isPiece();
    }

    public boolean isNoPiece(int h, int i) {
        return !(isOutOfBounds(h, i) || getPiece(h, i).isPiece());
    }

    public void getEdgeStatus() {
        for (int h = 0; h < boardH; h += 2)
            for (int i = 0; i < boardHI; i++) {
                byte right = getEdgeOnPositiveI(h, i);
                byte left = getEdgeOnNegativeI(h, i);
                edgeStatus[h][i] = right == left ?
                        left :
                        left == Piece.EDGE_NONE ?
                                right :
                                right == Piece.EDGE_NONE ?
                                        left :
                                        Piece.EDGE_INVALID;
            }

        for (int h = 1; h < boardH; h += 2)
            for (int i = 0; i < boardVI; i++) {
                byte upper = getEdgeOnPositiveH(h, i);
                byte lower = getEdgeOnNegativeH(h, i);
                edgeStatus[h][i] = upper == lower ?
                        lower :
                        lower == Piece.EDGE_NONE ?
                                upper :
                                upper == Piece.EDGE_NONE ?
                                        lower :
                                        Piece.EDGE_INVALID;
            }
    }

    private byte getEdgeOnPositiveH(int h, int i) {
        return isPiece(h + 1, i - 1) ?
                getPiece(h + 1, i - 1).getEdgeUnchecked(4) :
                isPiece(h + 2, i) ?
                        getPiece(h + 2, i).getEdgeUnchecked(3) :
                        isPiece(h + 1, i) ?
                                getPiece(h + 1, i).getEdgeUnchecked(5) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    private byte getEdgeOnNegativeH(int h, int i) {
        return isPiece(h - 1, i - 1) ?
                getPiece(h - 1, i - 1).getEdgeUnchecked(2) :
                isPiece(h - 2, i) ?
                        getPiece(h - 2, i).getEdgeUnchecked(0) :
                        isPiece(h - 1, i) ?
                                getPiece(h - 1, i).getEdgeUnchecked(1) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    private byte getEdgeOnPositiveI(int h, int i) {
        return isPiece(h - 1, i + 1) ?
                getPiece(h - 1, i + 1).getEdgeUnchecked(5) :
                isPiece(h, i + 1) ?
                        getPiece(h, i + 1).getEdgeUnchecked(0) :
                        isPiece(h + 1, i + 1) ?
                                getPiece(h + 1, i + 1).getEdgeUnchecked(4) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    private byte getEdgeOnNegativeI(int h, int i) {
        return isPiece(h - 1, i) ?
                getPiece(h - 1, i).getEdgeUnchecked(1) :
                isPiece(h, i - 1) ?
                        getPiece(h, i - 1).getEdgeUnchecked(3) :
                        isPiece(h + 1, i) ?
                                getPiece(h + 1, i).getEdgeUnchecked(2) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    public Piece getPiece(int h, int i) {
        return board[h][i];
    } //array indices Unchecked!!

    public int whereIsTileFilled(int x, int y) {
        int h = y * 2 + 1;
        //i = x
        return isPiece(h, x) ? DOWN :
                isPiece(h - 1, x) ? RIGHT :
                        isPiece(h - 2, x) ? UP :
                                isPiece(h - 1, x - 1) ? LEFT :
                                        EMPTY;
    }

    //Other manipulation
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void invertCurrent() {
        curPiece.invert();
    }

    public void rotateCurrentLeft() {
        curPiece.rotateLeft();
    }

    public void rotateCurrentRight() {
        curPiece.rotateRight();
    }

    public void forEachVertical(Consumer<Piece> consumer) {
        if (consumer == null) return;
        for (int h = 1; h < boardH; h += 2)
            for (int i = 0; i < boardVI; i++)
                consumer.accept(getPiece(h, i));
    }

    public void forEachHorizontal(Consumer<Piece> consumer) {
        if (consumer == null) return;
        for (int h = 0; h < boardH; h += 2)
            for (int i = 0; i < boardHI; i++)
                consumer.accept(getPiece(h, i));
    }

    public void forEach(Consumer<Piece> consumer) {
        forEachHorizontal(consumer);
        forEachVertical(consumer);
    }

    //Utility //TODO all untested
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Returns a boolean array that represents the state of the board
     * ignoring pieces as well as colouring.
     * Instead, only the shape of the laid down pieces is evaluated.
     * The edges of the board are filled in [all true].
     * @return the board state. true = filled, false = empty.
     * The first sizeX indices of the array contain the first row of tiles,
     * the second sizeX indices the second row, and so on.
     */
    public boolean[] getFilledTiles() {
        boolean[] tiles = new boolean[(boardX + 2) * (boardY + 2)];
        Arrays.fill(tiles, 0, boardX + 1, true);
        Arrays.fill(tiles, tiles.length - boardX - 1, tiles.length, true);
        for (int y = 0, i = boardX + 3; y < boardY; y++) {
            for (int x = 0; x < boardX; x++) {
                if (whereIsTileFilled(x, y) != EMPTY) tiles[i] = true;
                i++;
            }
            tiles[i++] = true;
            tiles[i++] = true;
        }
        return tiles;
    }

    /**
     * calculates the vertical piece slot in h|i format with the root of x|y
     * @param x the x position of the root
     * @param y the y position of the root
     * @return the h|i coordinates of the lowest vertical piece slot that contains the root position
     */
    public static int[] getHIVPositive(int x, int y) {
        return new int[] {
                y * 2 + 1,
                x
        };
    }

    /**
     * calculates the horizontal piece slot in h|i format with the root of x|y
     * @param x the x position of the root
     * @param y the y position of the root
     * @return the h|i coordinates of the rightmost horizontal piece slot that contains the root position
     */
    public static int[] getHIHPositive(int x, int y) {
        return new int[] {
                y * 2,
                x
        };
    }

    /**
     * converts from the position format h|i to x|y
     * @param h the given height
     * @param i the given index
     * @return the smallest [leftmost/highest] x|y position the given piece slot contains.
     */
    public static int[] getXY(int h, int i) {
        return new int[] {
                h >> 1,
                i
        };
    }
}