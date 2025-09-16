package DuMo.input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.function.IntConsumer;

import static java.awt.event.MouseEvent.*;


public class InputFrame extends JFrame {

    public static String rev(int i) {
        return switch (i) {
            case (-1) -> "KEY_NONE";
            case (-2) -> "BTN_LEFT";
            case (-3) -> "BTN_RIGHT";
            case (-4) -> "BTN_MIDDLE";
            case (10) -> "VK_ENTER";
            case (8) -> "VK_BACK_SPACE";
            case (9) -> "VK_TAB";
            case (3) -> "VK_CANCEL";
            case (12) -> "VK_CLEAR";
            case (16) -> "VK_SHIFT";
            case (17) -> "VK_CONTROL";
            case (18) -> "VK_ALT";
            case (19) -> "VK_PAUSE";
            case (20) -> "VK_CAPS_LOCK";
            case (27) -> "VK_ESCAPE";
            case (32) -> "VK_SPACE";
            case (33) -> "VK_PAGE_UP";
            case (34) -> "VK_PAGE_DOWN";
            case (35) -> "VK_END";
            case (36) -> "VK_HOME";
            case (37) -> "VK_LEFT";
            case (38) -> "VK_UP";
            case (39) -> "VK_RIGHT";
            case (40) -> "VK_DOWN";
            case (44) -> "VK_COMMA";
            case (45) -> "VK_MINUS";
            case (46) -> "VK_PERIOD";
            case (47) -> "VK_SLASH";
            case (48) -> "VK_0";
            case (49) -> "VK_1";
            case (50) -> "VK_2";
            case (51) -> "VK_3";
            case (52) -> "VK_4";
            case (53) -> "VK_5";
            case (54) -> "VK_6";
            case (55) -> "VK_7";
            case (56) -> "VK_8";
            case (57) -> "VK_9";
            case (59) -> "VK_SEMICOLON";
            case (61) -> "VK_EQUALS";
            case (65) -> "VK_A";
            case (66) -> "VK_B";
            case (67) -> "VK_C";
            case (68) -> "VK_D";
            case (69) -> "VK_E";
            case (70) -> "VK_F";
            case (71) -> "VK_G";
            case (72) -> "VK_H";
            case (73) -> "VK_I";
            case (74) -> "VK_J";
            case (75) -> "VK_K";
            case (76) -> "VK_L";
            case (77) -> "VK_M";
            case (78) -> "VK_N";
            case (79) -> "VK_O";
            case (80) -> "VK_P";
            case (81) -> "VK_Q";
            case (82) -> "VK_R";
            case (83) -> "VK_S";
            case (84) -> "VK_T";
            case (85) -> "VK_U";
            case (86) -> "VK_V";
            case (87) -> "VK_W";
            case (88) -> "VK_X";
            case (89) -> "VK_Y";
            case (90) -> "VK_Z";
            case (91) -> "VK_OPEN_BRACKET";
            case (92) -> "VK_BACK_SLASH";
            case (93) -> "VK_CLOSE_BRACKET";
            case (96) -> "VK_NUMPAD0";
            case (97) -> "VK_NUMPAD1";
            case (98) -> "VK_NUMPAD2";
            case (99) -> "VK_NUMPAD3";
            case (100) -> "VK_NUMPAD4";
            case (101) -> "VK_NUMPAD5";
            case (102) -> "VK_NUMPAD6";
            case (103) -> "VK_NUMPAD7";
            case (104) -> "VK_NUMPAD8";
            case (105) -> "VK_NUMPAD9";
            case (106) -> "VK_MULTIPLY";
            case (107) -> "VK_ADD";
            case (108) -> "VK_SEPARATOR";
            case (109) -> "VK_SUBTRACT";
            case (110) -> "VK_DECIMAL";
            case (111) -> "VK_DIVIDE";
            case (127) -> "VK_DELETE";
            case (144) -> "VK_NUM_LOCK";
            case (145) -> "VK_SCROLL_LOCK";
            case (112) -> "VK_F1";
            case (113) -> "VK_F2";
            case (114) -> "VK_F3";
            case (115) -> "VK_F4";
            case (116) -> "VK_F5";
            case (117) -> "VK_F6";
            case (118) -> "VK_F7";
            case (119) -> "VK_F8";
            case (120) -> "VK_F9";
            case (121) -> "VK_F10";
            case (122) -> "VK_F11";
            case (123) -> "VK_F12";
            case (61440) -> "VK_F13";
            case (61441) -> "VK_F14";
            case (61442) -> "VK_F15";
            case (61443) -> "VK_F16";
            case (61444) -> "VK_F17";
            case (61445) -> "VK_F18";
            case (61446) -> "VK_F19";
            case (61447) -> "VK_F20";
            case (61448) -> "VK_F21";
            case (61449) -> "VK_F22";
            case (61450) -> "VK_F23";
            case (61451) -> "VK_F24";
            case (154) -> "VK_PRINTSCREEN";
            case (155) -> "VK_INSERT";
            case (156) -> "VK_HELP";
            case (157) -> "VK_META";
            case (192) -> "VK_BACK_QUOTE";
            case (222) -> "VK_QUOTE";
            case (224) -> "VK_KP_UP";
            case (225) -> "VK_KP_DOWN";
            case (226) -> "VK_KP_LEFT";
            case (227) -> "VK_KP_RIGHT";
            case (128) -> "VK_DEAD_GRAVE";
            case (129) -> "VK_DEAD_ACUTE";
            case (130) -> "VK_DEAD_CIRCUMFLEX";
            case (131) -> "VK_DEAD_TILDE";
            case (132) -> "VK_DEAD_MACRON";
            case (133) -> "VK_DEAD_BREVE";
            case (134) -> "VK_DEAD_ABOVEDOT";
            case (135) -> "VK_DEAD_DIAERESIS";
            case (136) -> "VK_DEAD_ABOVERING";
            case (137) -> "VK_DEAD_DOUBLEACUTE";
            case (138) -> "VK_DEAD_CARON";
            case (139) -> "VK_DEAD_CEDILLA";
            case (140) -> "VK_DEAD_OGONEK";
            case (141) -> "VK_DEAD_IOTA";
            case (142) -> "VK_DEAD_VOICED_SOUND";
            case (143) -> "VK_DEAD_SEMIVOICED_SOUND";
            case (150) -> "VK_AMPERSAND";
            case (151) -> "VK_ASTERISK";
            case (152) -> "VK_QUOTEDBL";
            case (153) -> "VK_LESS";
            case (160) -> "VK_GREATER";
            case (161) -> "VK_BRACELEFT";
            case (162) -> "VK_BRACERIGHT";
            case (512) -> "VK_AT";
            case (513) -> "VK_COLON";
            case (514) -> "VK_CIRCUMFLEX";
            case (515) -> "VK_DOLLAR";
            case (516) -> "VK_EURO_SIGN";
            case (517) -> "VK_EXCLAMATION_MARK";
            case (518) -> "VK_INVERTED_EXCLAMATION_MARK";
            case (519) -> "VK_LEFT_PARENTHESIS";
            case (520) -> "VK_NUMBER_SIGN";
            case (521) -> "VK_PLUS";
            case (522) -> "VK_RIGHT_PARENTHESIS";
            case (523) -> "VK_UNDERSCORE";
            case (524) -> "VK_WINDOWS";
            case (525) -> "VK_CONTEXT_MENU";
            case (24) -> "VK_FINAL";
            case (28) -> "VK_CONVERT";
            case (29) -> "VK_NONCONVERT";
            case (30) -> "VK_ACCEPT";
            case (31) -> "VK_MODECHANGE";
            case (21) -> "VK_KANA";
            case (25) -> "VK_KANJI";
            case (240) -> "VK_ALPHANUMERIC";
            case (241) -> "VK_KATAKANA";
            case (242) -> "VK_HIRAGANA";
            case (243) -> "VK_FULL_WIDTH";
            case (244) -> "VK_HALF_WIDTH";
            case (245) -> "VK_ROMAN_CHARACTERS";
            case (256) -> "VK_ALL_CANDIDATES";
            case (257) -> "VK_PREVIOUS_CANDIDATE";
            case (258) -> "VK_CODE_INPUT";
            case (259) -> "VK_JAPANESE_KATAKANA";
            case (260) -> "VK_JAPANESE_HIRAGANA";
            case (261) -> "VK_JAPANESE_ROMAN";
            case (262) -> "VK_KANA_LOCK";
            case (263) -> "VK_INPUT_METHOD_ON_OFF";
            case (65489) -> "VK_CUT";
            case (65485) -> "VK_COPY";
            case (65487) -> "VK_PASTE";
            case (65483) -> "VK_UNDO";
            case (65481) -> "VK_AGAIN";
            case (65488) -> "VK_FIND";
            case (65482) -> "VK_PROPS";
            case (65480) -> "VK_STOP";
            case (65312) -> "VK_COMPOSE";
            case (65406) -> "VK_ALT_GRAPH";
            case (65368) -> "VK_BEGIN";
            case (0) -> "VK_UNDEFINED";
            default -> "Unassigned! check the key codes again.";
        };
    }

    public static final int
            KEY_NONE = -1,
            BTN_LEFT = -2,
            BTN_RIGHT = -3,
            BTN_MIDDLE = -4;

    private Point mouse;
    public final Keymap keymap;
    public final ArrayList<Runnable> pressListeners = new ArrayList<>();
    public final ArrayList<Runnable> releaseListeners = new ArrayList<>();
    public final ArrayList<Runnable> mouseListeners = new ArrayList<>();
    public final ArrayList<IntConsumer> wheelListeners = new ArrayList<>();
    public final ArrayList<Runnable> interactionListeners = new ArrayList<>();
    public Runnable afterInteraction = null;

    public InputFrame() {
        this(new Keymap());
    }

    public InputFrame(Keymap keymap) {
        super();

        this.keymap = keymap;
        this.mouse = new Point(0, 0);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int btn = KEY_NONE;
                switch (e.getButton()) {
                    case BUTTON1 -> btn = BTN_LEFT;
                    case BUTTON3 -> btn = BTN_RIGHT;
                    case BUTTON2 -> btn = BTN_MIDDLE;
                }
                onPress(btn);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int btn = KEY_NONE;
                switch (e.getButton()) {
                    case BUTTON1 -> btn = BTN_LEFT;
                    case BUTTON3 -> btn = BTN_RIGHT;
                    case BUTTON2 -> btn = BTN_MIDDLE;
                }
                onRelease(btn);
            }
        });
        addMouseWheelListener((e) -> onMouseWheel(e.getWheelRotation()));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse = e.getLocationOnScreen();
                onMouseMove();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMoved(e);
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int btn = e.getKeyCode();
                onPress(btn);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int btn = e.getKeyCode();
                onRelease(btn);
            }
        });
    }

    public Point getMouse(Component tile) {
        Point tilePos = tile.getLocationOnScreen();
        return new Point(
                mouse.x - tilePos.x,
                mouse.y - tilePos.y
        );
    }

    public int getMouseX(Component tile) {
        Point tilePos = tile.getLocationOnScreen();
        return mouse.x - tilePos.x;
    }

    public int getMouseY(Component tile) {
        Point tilePos = tile.getLocationOnScreen();
        return mouse.y - tilePos.y;
    }

    public boolean isMouseOver(Component tile) {
        Point pos = getMouse(tile);
        return pos.x >= 0 && pos.y >= 0 && pos.x < tile.getWidth() && pos.y < tile.getHeight();
    }

    private void onPress(int btn) {
        if (keymap.isHeld(btn)) return;
        keymap.press(btn);
        for (Runnable r : pressListeners) r.run();
        onAll();
    }

    private void onRelease(int btn) {
        if (!keymap.isHeld(btn)) return;
        keymap.release(btn);
        for (Runnable r : releaseListeners) r.run();
        onAll();
    }

    private void onMouseMove() {
        for (Runnable r : mouseListeners) r.run();
        onAll();
    }

    private void onMouseWheel(int movement) {
        for (IntConsumer r : wheelListeners) r.accept(movement);
        onAll();
    }

    private void onAll() {
        for (Runnable r : interactionListeners) r.run();
        if (afterInteraction != null) afterInteraction.run();
    }
}
