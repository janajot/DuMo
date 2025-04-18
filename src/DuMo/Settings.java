package DuMo;

public class Settings {
    public boolean settingShowInvalid;
    public boolean settingLetInvalid;
    public boolean settingPeekInvalid;
    public boolean settingShowEdges;
    public boolean settingShowGrid;

    public int pxps;

    public Settings(int pxps) {
        settingShowInvalid = false;
        settingLetInvalid = false;
        settingPeekInvalid = true;
        settingShowEdges = true;
        settingShowGrid = true;
        this.pxps = pxps;
    }
}
