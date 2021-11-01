package szeweq.craftery.util;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.*;

public class ClassNodeMap {
    private static final String OBJ_TYPE = "java/lang/Object";
    private final Map<String, ClassNode> nodes = new HashMap<>();

    public void add(ClassNode cn) {
        nodes.put(cn.name, cn);
    }

    public Collection<ClassNode> getClasses() {
        return nodes.values();
    }

    private static String fixedDesc(String desc) {
        if (desc.length() > 0 && desc.charAt(0) == 'L') {
            return desc.substring(1, desc.length() - 1);
        }
        return desc;
    }

    public boolean isCompatible(FieldNode fn, String typename) {
        var desc = fixedDesc(fn.desc);
        if (desc.equals(typename)) {
            return true;
        }
        var cn = nodes.get(desc);
        return cn != null && classExtendsFrom(cn, typename);
    }

    public boolean classExtendsFrom(ClassNode cn, String typename) {
        var scn = cn;
        while (!scn.superName.equals(typename)) {
            scn = nodes.get(scn.superName);
            if (scn == null) {
                return false;
            }
        }
        return true;
    }

    public String getLastSuperClass(String typename) {
        var tn = typename;
        do {
            var cn = nodes.get(tn);
            if (cn == null) {
                return tn;
            }
            if (cn.superName == null || OBJ_TYPE.equals(cn.superName)) {
                return tn;
            }
            tn = cn.superName;
        } while (true);
    }

    public Set<String> getAllInterfaceTypes(String typename) {
        var l = new LinkedHashSet<String>();
        var q = new ArrayDeque<String>();
        var tn = typename;
        do {
            var cn = nodes.get(tn);
            if (cn == null) {
                return l;
            }
            if (cn.superName == null || OBJ_TYPE.equals(cn.superName)) {
                return l;
            }
            q.addAll(cn.interfaces);
            while (!q.isEmpty()) {
                var iface = q.removeFirst();
                l.add(iface);
                var icn = nodes.get(iface);
                if (icn != null) {
                    q.addAll(icn.interfaces);
                }
            }
            tn = cn.superName;
        } while (true);
    }
}
