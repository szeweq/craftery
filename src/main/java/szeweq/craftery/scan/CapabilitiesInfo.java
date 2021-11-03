package szeweq.craftery.scan;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.LinkedHashSet;
import java.util.Set;

public record CapabilitiesInfo(String name, Set<String> supclasses, Set<String> fields) {

    public static CapabilitiesInfo from(String name, InsnList instructions) {
        var supclasses = new LinkedHashSet<String>();
        var fields = new LinkedHashSet<String>();
        for (var inst : instructions) {
            if (inst instanceof MethodInsnNode min
                    && "getCapability".equals(min.name)
                    && !name.equals(min.owner)
            ) {
                supclasses.add(min.owner != null ? min.owner : "UNKNOWN");
            } else if (inst instanceof FieldInsnNode fin && TypeNames.CAPABILITY.equals(fin.desc)) {
                fields.add((fin.owner != null ? fin.owner : "UNKNOWN") + "::" + (fin.name != null ? fin.name : "UNKNOWN"));
            }
        }
        return new CapabilitiesInfo(name, Set.copyOf(supclasses), Set.copyOf(fields));
    }
}
