package szewek.craftery.cfapi;

import org.jetbrains.annotations.Nullable;

public record AddonAuthor(
        String name,
        String url,
        @Nullable Integer projectTitleId,
        @Nullable String projectTitleTitle,
        int userId,
        int twitchId
) {
}
