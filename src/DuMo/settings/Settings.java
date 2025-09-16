package DuMo.settings;

import java.io.File;

public class Settings {
    public static final String SETTINGS_PATH = new File("settings").getAbsolutePath();

    public final BoolSetting showInvalid;
    public final BoolSetting showEdges;
    public final BoolSetting showGrid;

    //unused
    public final BoolSetting _letInvalid;
    public final BoolSetting _peekInvalid;

    public final IntSetting pxps;

    public Settings(int pxps) {
        System.out.println(SETTINGS_PATH);
        showInvalid = new BoolSetting(false);
        _letInvalid = new BoolSetting(false);
        _peekInvalid = new BoolSetting(true);
        showEdges = new BoolSetting(true);
        showGrid = new BoolSetting(true);
        this.pxps = new IntSetting(pxps);
    }

    public void save() {

    }
}
