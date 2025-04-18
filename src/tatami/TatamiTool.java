package tatami;

import DuMo.board.Board;
import DuMo.piece.Piece;

import java.util.Stack;

public class TatamiTool extends Tatami {
    private final Board game;
    private final Piece filler;
    private final Stack<Integer[]> posBuffer;

    public TatamiTool(Board game, int pxps, Piece filler) {
        super(game.getBoardX(), game.getBoardY(), pxps, game.getFilledTiles());
        this.game = game;
        this.filler = filler;
        posBuffer = new Stack<>();
    }

    @Override
    protected void fillForced(int pos, boolean[] arr) {
        if (isFilled(pos)) return;
        int freeHead = -1;
        getSurrounding(pos, arr);
        for (int i = 0; i < 4; i++)
            if (!arr[i]) {
                if (freeHead != -1) return;
                else freeHead = i;
            }
        if (freeHead == -1) throw new IllegalBoardException("Board contains a gap of only 1 tile");
        int[] hi = getHI(pos % trueSizeX, pos / trueSizeX, freeHead);
        posBuffer.add(new Integer[] {hi[0], hi[1]});
        set(pos, true);
        freeHead = getHead(pos, freeHead);
        set(freeHead, true);
        int[] is = getSurroundingIndices(freeHead);
        for (int index : is)
            fillForced(index, arr);
    }

    @Override
    public void fillForced() {
        boolean[] b = new boolean[4];
        try {
            for (int i = trueSizeX + 1; i < length - trueSizeX - 1; i++)
                fillForced(i, b);
            for (int i = 0, size = posBuffer.size(); i < size; i++) {
                Integer[] pos = posBuffer.pop();
                game.place(filler.clone(), pos[0], pos[1]);
            }
        } catch (IllegalBoardException e) {
            System.out.println(e.getMessage());
        }
    }

    public void regenerate() {
        board = game.getFilledTiles();
        sizeX = game.getBoardX();
        sizeY = game.getBoardY();
        trueSizeX = sizeX + 2;
        trueSizeY = sizeY + 2;
        length = trueSizeX * trueSizeY;
    }
}
