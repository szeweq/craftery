package szewek.craftery.cfapi;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

public record AddonSearch(
        int id,
        String name,
        AddonAuthor[] authors,
        String summary,
        String websiteUrl,
        String slug,
        long downloadCount,
        double popularityScore,
        long gamePopularityRank,
        AddonAttachment[] attachments,
        AddonSection categorySection,
        AddonFile[] latestFiles
) {
    @Nullable
    public AddonFile latestFile() {
        AddonFile lf = null;
        var ld = new Date(0);
        for (AddonFile f : latestFiles) {
            if (ld.before(f.fileDate())) {
                lf = f;
                ld = f.fileDate();
            }
        }
        return lf;
    }

    @Nullable
    public AddonAttachment defaultAttachment() {
        for (AddonAttachment a : attachments) {
            if (a.isDefault()) {
                return a;
            }
        }
        return null;
    }
}
