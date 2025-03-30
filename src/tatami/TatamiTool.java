package tatami;

import DuMo.Game;
import DuMo.piece.Piece;

public class TatamiTool extends Tatami {
    protected Game game;
    protected Piece filler;

    public TatamiTool(Game game, int pxps, Piece filler) {
        super(game.boardX, game.boardY, pxps, game.getFilledTiles());
        this.game = game;
        this.filler = filler;
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
        game.place(filler.clone(), hi[0], hi[1]);
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
        for (int i = trueSizeX + 1; i < length - trueSizeX - 1; i++)
            fillForced(i, b);
    }

    public void regenerate() {
        board = game.getFilledTiles();
        sizeX = game.boardX;
        sizeY = game.boardY;
        trueSizeX = sizeX + 2;
        trueSizeY = sizeY + 2;
        length = trueSizeX * trueSizeY;
    }
}
