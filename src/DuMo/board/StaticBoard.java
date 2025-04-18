package DuMo.board;

import DuMo.piece.kyap.PieceKyap;
import DuMo.piece.Piece;

import java.util.Arrays;
import java.util.Optional;

public class StaticBoard implements DuMo.board.Board {
    private int boardX;
    private int boardY;
    private int boardH;
    private int boardHI;
    private int boardVI;
    private Piece[][] board;

    public StaticBoard(int initialX, int initialY) {
        resetBoard(initialX, initialY);
    }

    //Board manipulation
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    private void setInvalid(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).get().setInvalid();
    }

    private void setValidated(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).get().setValidated();
    }

    private void setEmpty(int h, int i) {
        if (isOutOfBounds(h, i)) return;
        getPiece(h, i).get().setEmpty();
    }

    public Optional<Piece> place(Piece piece, int h, int i) {
        boolean wasPiece = isPiece(h, i);
        Piece prev = null;
        if (isValid(h, i)) {
            if (!wasPiece)
                if (isHorizontal(h)) {
                    setInvalid(h, i - 1);
                    setInvalid(h + 1, i);
                    setInvalid(h + 1, i + 1);
                    setInvalid(h, i + 1);
                    setInvalid(h - 1, i + 1);
                    setInvalid(h - 1, i);
                } else {
                    setInvalid(h + 2, i);
                    setInvalid(h + 1, i);
                    setInvalid(h - 1, i);
                    setInvalid(h - 2, i);
                    setInvalid(h - 1, i - 1);
                    setInvalid(h + 1, i - 1);
                }
            else prev = board[h][i];
            board[h][i] = piece;
        }
        return prev == null ? Optional.empty() : Optional.of(prev);
    }

    public Optional<Piece> remove(int h, int i) {
        if (!isPiece(h, i)) return Optional.empty();
        Optional<Piece> ret = getPiece(h, i);
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
        return ret;
    }

    public void resetBoard() {
        resetBoard(boardX, boardY);
    }

    public void resetBoard(int boardX, int boardY) {
        if (((boardX * boardY) & 1) == 1) throw new IllegalArgumentException("Board must have an even amount of tiles");
        if (boardX <= 0 || boardY <= 0) throw new IllegalArgumentException("Board must have positive size");
        this.boardX = boardX;
        this.boardY = boardY;
        boardH = (boardY << 1) - 1;
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
    }

    //Getters and checks
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public int getBoardX() {
        return boardX;
    }

    public int getBoardY() {
        return boardY;
    }

    public static boolean isHorizontal(int h) {
        return (h & 1) == 0;
    }

    public boolean isOutOfBounds(int h, int i) {
        return h >= boardH ||
                i >= (isHorizontal(h) ?
                        boardHI :
                        boardVI) ||
                h < 0 ||
                i < 0;
    }

    public boolean isValid(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).get().isValid();
    }

    public boolean isInvalid(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).get().isInvalid();
    }

    public boolean isPiece(int h, int i) {
        return !isOutOfBounds(h, i) && getPiece(h, i).get().isPiece();
    }

    public boolean isNoPiece(int h, int i) {
        return !(isOutOfBounds(h, i) || getPiece(h, i).get().isPiece());
    }

    public Optional<Piece> getPiece(int h, int i) {
        return Optional.of(board[h][i]);
    } //array indices Unchecked!!

    public int whereIsTileFilled(int x, int y) {
        int h = y * 2 + 1;
        //i = x
        if (x >= boardX || y >= boardY || x < 0 || y < 0) return OOB;
        return isPiece(h, x) ? DOWN :
                isPiece(h - 1, x) ? RIGHT :
                        isPiece(h - 2, x) ? UP :
                                isPiece(h - 1, x - 1) ? LEFT :
                                        EMPTY;
    }

    //TODO horrible:
    public byte[][] getEdgeStatus() {
        byte[][] edgeStatus = new byte[boardH][];
        for (int h = 0; h < boardH; h += 2) {
            edgeStatus[h] = new byte[boardHI];
            Arrays.fill(edgeStatus[h], (byte) 0b1_000000);
        }
        for (int h = 1; h < boardH; h += 2) {
            edgeStatus[h] = new byte[boardVI];
            Arrays.fill(edgeStatus[h], (byte) 0b1_000000);
        }

        for (int h = 0; h < boardH; h += 2)
            for (int i = 0; i < boardHI; i++) {
                byte right = isPiece(h - 1, i + 1) ? getPiece(h - 1, i + 1).get().getEdgeUnchecked(5) :
                        isPiece(h, i + 1) ? getPiece(h, i + 1).get().getEdgeUnchecked(0) :
                                isPiece(h + 1, i + 1) ? getPiece(h + 1, i + 1).get().getEdgeUnchecked(4) :
                                        Piece.EDGE_NONE;
                byte left = isPiece(h - 1, i) ? getPiece(h - 1, i).get().getEdgeUnchecked(1) :
                        isPiece(h, i - 1) ? getPiece(h, i - 1).get().getEdgeUnchecked(3) :
                                isPiece(h + 1, i) ? getPiece(h + 1, i).get().getEdgeUnchecked(2) :
                                        Piece.EDGE_NONE;
                edgeStatus[h][i] = right == left ? left :
                        left == Piece.EDGE_NONE ? right :
                                right == Piece.EDGE_NONE ? left :
                                        Piece.EDGE_INVALID;
            }

        for (int h = 1; h < boardH; h += 2)
            for (int i = 0; i < boardVI; i++) {
                byte upper = isPiece(h + 1, i - 1) ? getPiece(h + 1, i - 1).get().getEdgeUnchecked(4) :
                        isPiece(h + 2, i) ? getPiece(h + 2, i).get().getEdgeUnchecked(3) :
                                isPiece(h + 1, i) ? getPiece(h + 1, i).get().getEdgeUnchecked(5) :
                                        Piece.EDGE_NONE;
                byte lower = isPiece(h - 1, i - 1) ? getPiece(h - 1, i - 1).get().getEdgeUnchecked(2) :
                        isPiece(h - 2, i) ? getPiece(h - 2, i).get().getEdgeUnchecked(0) :
                                isPiece(h - 1, i) ? getPiece(h - 1, i).get().getEdgeUnchecked(1) :
                                        Piece.EDGE_NONE;
                edgeStatus[h][i] = upper == lower ? lower :
                        lower == Piece.EDGE_NONE ? upper :
                                upper == Piece.EDGE_NONE ? lower :
                                        Piece.EDGE_INVALID;
            }
        return edgeStatus;
    }

    public boolean[] getFilledTiles() {
        boolean[] tiles = new boolean[(boardX + 2) * (boardY + 2)];
        Arrays.fill(tiles, 0, boardX + 3, true);
        Arrays.fill(tiles, tiles.length - boardX - 3, tiles.length, true);
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

    //Other
    //------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void forEachVerticalPiece(PieceConsumer action) {
        if (action == null) return;
        for (int h = 1; h < boardH; h += 2)
            for (int i = 0; i < boardVI; i++)
                action.accept(getPiece(h, i).get(), h, i);
    }

    public void forEachHorizontalPiece(PieceConsumer action) {
        if (action == null) return;
        for (int h = 0; h < boardH; h += 2)
            for (int i = 0; i < boardHI; i++)
                action.accept(getPiece(h, i).get(), h, i);
    }

    public void forEachPiece(Board.PieceConsumer action) {
        forEachHorizontalPiece(action);
        forEachVerticalPiece(action);
    }
}