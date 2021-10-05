package szeweq.craftery.cfapi;

import java.util.Date;

public record AddonFile(
        int id,
        String fileName,
        Date fileDate,
        int fileLength,
        String downloadUrl,
        AddonDependency[] dependencies,
        String[] gameVersion
) {
}
