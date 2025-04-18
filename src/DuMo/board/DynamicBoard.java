package DuMo.board;

import DuMo.piece.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class DynamicBoard implements Board {
    private int scopeX, scopeY, scopeH;
    private int offsetX, offsetY, offsetH;
    private boolean enforceScope;

    private final HashMap<Integer, HashMap<Integer, Piece>> board;

    public DynamicBoard(int scopeX, int scopeY) {
        setScope(scopeX, scopeY);
        setPos(0, 0);
        this.enforceScope = true;
        board = new HashMap<>();
    }

    @Override
    public int getBoardX() {
        return scopeX;
    }

    @Override
    public int getBoardY() {
        return scopeY;
    }

    public Optional<Piece> placeAbs(Piece piece, int h, int i) {
        Piece prev = null;
        if (isValidAbs(h, i)) {
            tryToAdd(h);
            prev = board.get(h).put(i, piece);
        }
        return Optional.ofNullable(prev);
    }

    @Override
    public Optional<Piece> place(Piece piece, int h, int i) {
        System.out.print("h=" + getHAbs(h));
        System.out.println(", i=" + getIAbs(i));
        return placeAbs(piece, getHAbs(h), getIAbs(i));
    }

    public Optional<Piece> removeAbs(int h, int i) {
        Piece prev = null;
        if (isPieceAbs(h, i)) {
            prev = board.get(h).remove(i);
            tryToRemove(h);
        }
        return Optional.ofNullable(prev);
    }

    @Override
    public Optional<Piece> remove(int h, int i) {
        return removeAbs(getHAbs(h), getIAbs(i));
    }

    @Override
    public void resetBoard() {
        board.clear();
    }

    public boolean isValidAbs(int h, int i) {
        if (isOutOfBoundsAbs(h, i)) return false;
        if (Board.isHorizontal(h)) {
            Map<Integer, Piece> map0 = board.get(h);
            Map<Integer, Piece> map1 = board.get(h - 1);
            Map<Integer, Piece> map2 = board.get(h + 1);
            return ((map0 == null || !(map0.containsKey(i - 1) || map0.containsKey(i + 1))) &&
                    (map1 == null || !(map1.containsKey(i) || map1.containsKey(i + 1))) &&
                    (map2 == null || !(map2.containsKey(i) || map2.containsKey(i + 1)))
            );
        }
        Map<Integer, Piece> map0 = board.get(h - 1);
        Map<Integer, Piece> map1 = board.get(h + 1);
        Map<Integer, Piece> map2 = board.get(h - 2);
        Map<Integer, Piece> map3 = board.get(h + 2);
        return ((map0 == null || !(map0.containsKey(i - 1) || map0.containsKey(i))) &&
                (map1 == null || !(map1.containsKey(i - 1) || map1.containsKey(i))) &&
                (map2 == null || !map2.containsKey(i)) &&
                (map3 == null || !map3.containsKey(i))
        );
    }

    @Override
    public boolean isValid(int h, int i) {
        return isValidAbs(getHAbs(h), getIAbs(i));
    }

    public boolean isInvalidAbs(int h, int i) {
        return !isValidAbs(h, i);
    }

    @Override
    public boolean isInvalid(int h, int i) {
        return !isValid(h, i);
    }

    public boolean isPieceAbs(int h, int i) {
        Optional<Piece> piece = getPieceAbs(h, i);
        return piece.isPresent() && piece.get().isPiece();
    }

    @Override
    public boolean isPiece(int h, int i) {
        return isPieceAbs(getHAbs(h), getIAbs(i));
    }

    public boolean isNoPieceAbs(int h, int i) {
        if (isOutOfBoundsAbs(h, i)) return false;
        Optional<Piece> piece = getPieceAbs(h, i);
        return piece.isEmpty() || !piece.get().isPiece();
    }

    @Override
    public boolean isNoPiece(int h, int i) {
        return isNoPieceAbs(getHAbs(h), getIAbs(i));
    }


    /**
     * Checks whether a given piece slot is out of scope,
     * if placing out of view is disallowed
     *
     * @param h the height of the piece slot
     * @param i the index of the piece slot
     * @return Whether any part of a Piece that would be
     * placed in the given piece slot would reach outside
     * the current scope, or always false if checking for
     * out-of-scope is disabled
     */
    public boolean isOutOfBoundsAbs(int h, int i) {
        return enforceScope && (
                h < offsetH
                        || h >= scopeH + offsetH + 1
                        || i < offsetX
                        || i > (Board.isHorizontal(h) ? scopeX + offsetX - 1 : scopeX + offsetX)
        );
    }

    public boolean isOutOfBounds(int h, int i) {
        return enforceScope && (
                h < 0
                        || h >= scopeH + 1
                        || i < 0
                        || i > (Board.isHorizontal(h) ? scopeX - 1 : scopeX)
        );
    }

    public boolean isOutOfScopeAbs(int h, int i) {
        return h < offsetH - 1
                || h >= scopeH + offsetH
                || i < (Board.isHorizontal(h) ? offsetX - 1 : offsetX)
                || i >= scopeX + offsetX;
    }

    public boolean isOutOfScope(int h, int i) {
        return h < -1
                || h >= scopeH
                || i < (Board.isHorizontal(h) ? -1 : 0)
                || i >= scopeX;
    }

    @Override
    public byte[][] getEdgeStatus() {
        return new byte[0][];
    } //TODO

    public Optional<Piece> getPieceAbs(int h, int i) {
        Map<Integer, Piece> atH = board.get(h);
        if (atH == null) return Optional.empty();
        Piece p = atH.get(i);
        return Optional.ofNullable(p);
    }

    @Override
    public Optional<Piece> getPiece(int h, int i) {
        return getPieceAbs(getHAbs(h), getIAbs(i));
    }

    /**
     * Checks whether the given tile is Filled or not, and if so, returns where.
     * If scope enforcing is enabled, {@link Board#OOB} can be returned.
     *
     * @see #isOutOfBoundsAbs
     */
    public int whereIsTileFilledAbs(int x, int y) {
        int h = (y << 1) + 1;
        //i = x
        if (enforceScope && (x >= scopeX + offsetX || y >= scopeY + offsetY || x < offsetX || y < offsetY)) return OOB;
        return isPieceAbs(h, x) ? DOWN :
                isPieceAbs(h - 1, x) ? RIGHT :
                        isPieceAbs(h - 2, x) ? UP :
                                isPieceAbs(h - 1, x - 1) ? LEFT :
                                        EMPTY;
    }

    @Override
    public int whereIsTileFilled(int x, int y) {
        return whereIsTileFilledAbs(x + offsetX, y + offsetY);
    }

    public void forEachPieceGlobal(PieceConsumer action) {
        board.forEach((h, row) -> {
            row.forEach((i, piece) -> {
                action.accept(piece, h, i);
            });
        });
    }

    public void forEachVerticalPieceGlobal(PieceConsumer action) {
        for (int h : board.keySet())
            if (!Board.isHorizontal(h))
                board.get(h).forEach((i, piece) -> action.accept(piece, h, i));
    }

    public void forEachHorizontalPieceGlobal(PieceConsumer action) {
        for (int h : board.keySet())
            if (Board.isHorizontal(h))
                board.get(h).forEach((i, piece) -> action.accept(piece, h, i));
    }

    //TODO test thoroughly
    @Override
    public void forEachPiece(PieceConsumer action) {
        if (board.size() < scopeH) {
            board.forEach((h, row) -> {
                if (h >= offsetH - 1 && h <= offsetH + scopeH) {
                    loopRowInScope(h, row, action);
                }
            });
        } else {
            for (int h = offsetH - 1; h <= scopeH + offsetH; h++) {
                Map<Integer, Piece> row = board.get(h);
                if (row != null) {
                    loopRowInScope(h, row, action);
                }
            }
        }
    }

    @Override
    public void forEachVerticalPiece(PieceConsumer action) {
        if (board.size() < scopeH) {
            board.forEach((h, row) -> {
                if (!Board.isHorizontal(h) && h >= offsetH - 1 && h <= offsetH + scopeH)
                    loopRowInScope(h, row, action);
            });
        } else {
            for (int h = offsetH - 1; h <= scopeH + offsetH; h += 2) {
                Map<Integer, Piece> row = board.get(h);
                if (row != null)
                    loopRowInScope(h, row, action);
            }
        }
    }

    @Override
    public void forEachHorizontalPiece(PieceConsumer action) {
        if (board.size() < scopeH) {
            board.forEach((h, row) -> {
                if (Board.isHorizontal(h) && h >= offsetH && h <= offsetH + scopeH)
                    loopRowInScope(h, row, action);
            });
        } else {
            for (int h = offsetH; h <= scopeH + offsetH; h += 2) {
                Map<Integer, Piece> row = board.get(h);
                if (row != null)
                    loopRowInScope(h, row, action);
            }
        }
    }

    private void loopRowInScope(int h, Map<Integer, Piece> row, PieceConsumer action) {
        if (row.size() < scopeX) {
            row.forEach((i, piece) -> {
                if (i >= (Board.isHorizontal(h) ? offsetX - 1 : offsetX) && i < scopeX + offsetX)
                    action.accept(piece, getHRel(h), getIRel(i));
            });
        } else {
            final int iMax = scopeX + offsetX;
            for (int i = offsetX - 1; i < iMax; i++) {
                Optional<Piece> p = getPieceAbs(h, i);
                if (p.isPresent()) action.accept(p.get(), getHRel(h), getIRel(i));
            }
        }
    }

    public void setPos(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetH = offsetY << 1;
    }

    public void panX(int offset) {
        offsetX += offset;
    }

    public void panY(int offset) {
        offsetY += offset;
        offsetH = offsetY << 1;
    }

    public void setScope(int x, int y) {
        scopeX = x;
        scopeY = y;
        scopeH = scopeY << 1;
    }

    public int getPosX() {
        return offsetX;
    }

    public int getPosY() {
        return offsetY;
    }

    public int getScopeX() {
        return scopeX;
    }

    public int getScopeY() {
        return scopeY;
    }

    public int getHAbs(int hRel) {
        return hRel + offsetH;
    }

    public int getIAbs(int iRel) {
        return iRel + offsetX;
    }

    public int getHRel(int hAbs) {
        return hAbs - offsetH;
    }

    public int getIRel(int iAbs) {
        return iAbs - offsetX;
    }

    @Override
    public boolean[] getFilledTiles() {
        return new boolean[0];
    }

    private void tryToAdd(int h) {
        if (!board.containsKey(h))
            board.put(h, new HashMap<>());
    }

    private void tryToRemove(int h) {
        Map<?, ?> atH = board.get(h);
        if (atH != null && atH.isEmpty())
            board.remove(h);
    }
}
