package szewek.craftery.cfapi;

public class AddonDependency {
    public final int addonId;
    public final int type;

    public AddonDependency(int addonId, int type) {
        this.addonId = addonId;
        this.type = type;
    }
}
