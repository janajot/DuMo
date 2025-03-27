package DuMo.util;

import DuMo.Game;
import DuMo.Main;

public class CoordinateTransform {
    public static int[] getHI() {
        double bWidth = Main.display.getWidth() / (double) Game.boardX;
        double bHeight = Main.display.getHeight() / (double) Game.boardY;
        int xB = Main.mouseX / (int) bWidth;
        int yB = Main.mouseY / (int) bHeight;

        int[] res = new int[2];
        switch (Game.whereIsTileFilled(xB, yB)) {
            case 0 -> {
                res[0] = yB * 2 + 1;
                res[1] = xB;
            }
            case 1 -> {
                res[0] = yB * 2;
                res[1] = xB;
            }
            case 2 -> {
                res[0] = yB * 2 - 1;
                res[1] = xB;
            }
            case 3 -> {
                res[0] = yB * 2;
                res[1] = xB - 1;
            }
            default -> {
                boolean fD = Game.whereIsTileFilled(xB, yB + 1) != Game.TILE_IS_EMPTY; //Is the tile below this one filled / invalid?
                boolean fR = Game.whereIsTileFilled(xB + 1, yB) != Game.TILE_IS_EMPTY;
                boolean fU = Game.whereIsTileFilled(xB, yB - 1) != Game.TILE_IS_EMPTY;
                boolean fL = Game.whereIsTileFilled(xB, yB - 1) != Game.TILE_IS_EMPTY;
                boolean f = fD || fR || fU || fL;

                double xRel = Main.mouseX % bWidth;
                double yRel = Main.mouseY % bHeight;

                boolean ur = xRel > yRel;
                boolean ul = bWidth - xRel > yRel;

                //TODO: if tiles are blocked act accordingly (if f != false)!
                if (ur ^ ul) { // < or >
                    res[0] = yB * 2; // H (height) is known
                    res[1] = ul ? // if left
                            xB - 1 :
                            xB;
                } else { // ^ or v
                    res[1] = xB; // I (index) = same as the tile at X
                    res[0] = ul ? // if up
                            yB * 2 - 1 :
                            yB * 2 + 1;
                }
            }
        }
        return res;
    }

    public static int convertToRelativeDisplayX(int xM) {
        return xM - Main.display.getX() - 10;
    }

    public static int convertToRelativeDisplayY(int yM) {
        return yM - Main.display.getY() - 30;
    }
}
