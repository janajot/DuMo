package DuMo;

import DuMo.board.*;
import DuMo.piece.Piece;
import DuMo.piece.kyap.PieceKyap;

public class Game {
    private final Piece curPiece = new PieceKyap((byte) 0b0_0_000000);
    private final Main main;
    private final Board board;

    public Game(Main main, int boardX, int boardY) {
        this.main = main;
        this.board = new DynamicBoard(boardX, boardY);
    }

    public Board getBoard() {
        return board;
    }

    public Piece getCurPiece() {
        return curPiece;
    }

    public void onPlace() {
        int[] gHgI = main.display.getHI();
        if (main.isDisplayHovered) {
            board.place(curPiece.clone(), gHgI[0], gHgI[1]);
        }
    }

    public void onRemove() {
        int[] gHgI = main.display.getHI();
        if (main.isDisplayHovered) {
            board.remove(gHgI[0], gHgI[1]);
        }
    }

    public void onNext() {
        if (main.isDisplayHovered) {
            curPiece.setNext();
        }
    }

    public void invertCurrent() {
        curPiece.invert();
    }

    public void rotateCurrentLeft() {
        curPiece.rotateLeft();
    }

    public void rotateCurrentRight() {
        curPiece.rotateRight();
    }

    public void rescale(int x, int y) {
        ((DynamicBoard)board).setScope(x, y);
    }

    public void pan(int xOffset, int yOffset) {
        ((DynamicBoard)board).panX(xOffset);
        ((DynamicBoard)board).panY(yOffset);
    }
}
