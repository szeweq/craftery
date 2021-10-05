package szeweq.craftery.scan;

import com.electronwill.nightconfig.toml.TomlParser;

public class Scanner {
    public static TomlParser TOML = new TomlParser();

    public static String genericFromSignature(String sig) {
        var aix = sig.indexOf('<');
        var zix = sig.lastIndexOf('>');
        return sig.substring(aix + 1, zix);
    }

    public static String pathToLocation(String path) {
        var p = path.split("/", 4);
        if (p.length < 4) {
            return p[p.length - 1];
        }
        return (p[1] + ':' + p[3]).intern();
    }

    private Scanner() {}
}
