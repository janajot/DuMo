package DuMo;

import DuMo.piece.kyap.PieceKyap;
import DuMo.piece.Piece;
import DuMo.util.CoordinateTransform;

import java.util.Arrays;

public class Game {
    public static int boardX;
    public static int boardY;
    public static int boardH;
    public static int boardHI;
    public static int boardVI;
    public static Piece[][] board;
    public static Piece curPiece = new PieceKyap((byte) 0b0_0_000000);
    public static byte[][] edgeStatus; //TODO super inefficient: 1 byte / edge


    public static final int TILE_IS_EMPTY = -1;
    public static final int TILE_FILLED_D = 0;
    public static final int TILE_FILLED_R = 1;
    public static final int TILE_FILLED_U = 2;
    public static final int TILE_FILLED_L = 3;


    //Actions
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static void onPlace() {
        int[] gHgI = CoordinateTransform.getHI();
        if (Main.isDisplayHovered) {
            place(curPiece.clone(), gHgI[0], gHgI[1]);
        }
    }

    public static void onRemove() {
        int[] gHgI = CoordinateTransform.getHI();
        if (Main.isDisplayHovered) {
            remove(gHgI[0], gHgI[1]);
        }
    }

    public static void onNext() {
        if (Main.isDisplayHovered) {
            curPiece.setNext();
        }
    }

    //Board manipulation
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private static void setInvalid(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).setInvalid();
    }

    private static void setValidated(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).setValidated();
    }

    private static void setEmpty(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).setEmpty();
    }

    public static void place(Piece piece, int h, int i) {
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

    public static void remove(int h, int i) {
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

    public static void resetBoard() {
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
    private static boolean isOutOfBounds(int h, int i) {
        return h >= boardH ||
                i >= (isHorizontal(h) ?
                        boardHI :
                        boardVI) ||
                h < 0 ||
                i < 0;
    }

    public static boolean isHorizontal(int h) {
        return (h & 1) == 0;
    }

    public static boolean isValid(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).isValid();
    }

    public static boolean isInvalid(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).isInvalid();
    }

    public static boolean isPiece(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).isPiece();
    }

    public static boolean isNoPiece(int h, int i) {
        return !(isOutOfBounds(h, i) || getPiece(h, i).isPiece());
    }

    public static void getEdgeStatus() {
        for (int h = 0; h < Game.boardH; h += 2)
            for (int i = 0; i < Game.boardHI; i++) {
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

        for (int h = 1; h < Game.boardH; h += 2)
            for (int i = 0; i < Game.boardVI; i++) {
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

    private static byte getEdgeOnPositiveH(int h, int i) {
        return isPiece(h + 1, i - 1) ?
                getPiece(h + 1, i - 1).getEdgeUnchecked(4) :
                isPiece(h + 2, i) ?
                        getPiece(h + 2, i).getEdgeUnchecked(3) :
                        isPiece(h + 1, i) ?
                                getPiece(h + 1, i).getEdgeUnchecked(5) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    private static byte getEdgeOnNegativeH(int h, int i) {
        return isPiece(h - 1, i - 1) ?
                getPiece(h - 1, i - 1).getEdgeUnchecked(2) :
                isPiece(h - 2, i) ?
                        getPiece(h - 2, i).getEdgeUnchecked(0) :
                        isPiece(h - 1, i) ?
                                getPiece(h - 1, i).getEdgeUnchecked(1) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    private static byte getEdgeOnPositiveI(int h, int i) {
        return isPiece(h - 1, i + 1) ?
                getPiece(h - 1, i + 1).getEdgeUnchecked(5) :
                isPiece(h, i + 1) ?
                        getPiece(h, i + 1).getEdgeUnchecked(0) :
                        isPiece(h + 1, i + 1) ?
                                getPiece(h + 1, i + 1).getEdgeUnchecked(4) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    private static byte getEdgeOnNegativeI(int h, int i) {
        return isPiece(h - 1, i) ?
                getPiece(h - 1, i).getEdgeUnchecked(1) :
                isPiece(h, i - 1) ?
                        getPiece(h, i - 1).getEdgeUnchecked(3) :
                        isPiece(h + 1, i) ?
                                getPiece(h + 1, i).getEdgeUnchecked(2) :
                                Piece.EDGE_NONE;
    } //Axis Unchecked!

    public static Piece getPiece(int h, int i) {
        return board[h][i];
    } //array indices Unchecked!!

    public static int whereIsTileFilled(int x, int y) {
        int h = y * 2 + 1;
        //i = x
        return Game.isPiece(h, x) ? TILE_FILLED_D :
                Game.isPiece(h - 1, x) ? TILE_FILLED_R :
                        Game.isPiece(h - 2, x) ? TILE_FILLED_U :
                                Game.isPiece(h - 1, x - 1) ? TILE_FILLED_L :
                                        TILE_IS_EMPTY;
    }

    //Other manipulation
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static void invertCurrent() {
        curPiece.invert();
    }

    public static void rotateCurrentLeft() {
        curPiece.rotateLeft();
    }

    public static void rotateCurrentRight() {
        curPiece.rotateRight();
    }
}