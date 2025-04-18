package DuMo;

public class Settings {
    public boolean settingShowInvalid;
    public boolean settingShowEdges;
    public boolean settingShowGrid;

    /**
     * unused
     */
    public boolean settingLetInvalid;
    /**
     * unused
     */
    public boolean settingPeekInvalid;

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
