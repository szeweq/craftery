package szewek.mctool.util;

import kotlin.Pair;
import kotlin.Triple;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ClassNodeMap {
    private final Map<String, ClassNode> nodes = new HashMap<>();

    public void add(ClassNode cn) {
        nodes.put(cn.name, cn);
    }

    public Collection<ClassNode> getClasses() {
        return nodes.values();
    }

    public Stream<Pair<ClassNode, MethodNode>> getAllClassMethods() {
        return getFlattenedClassValues(c -> c.methods);
    }

    public Stream<Pair<ClassNode, FieldNode>> getAllClassFields() {
        return getFlattenedClassValues(c -> c.fields);
    }

    private <T> Stream<Pair<ClassNode, T>> getFlattenedClassValues(Function<ClassNode, List<T>> fn) {
        return nodes.values().stream()
                .flatMap(c -> fn.apply(c).stream().map(x -> new Pair<>(c, x)));
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
            if (cn.superName == null || cn.superName.equals("java/lang/Object")) {
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
            if (cn.superName == null || cn.superName.equals("java/lang/Object")) {
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

    public Stream<Triple<ClassNode, MethodNode, FieldInsnNode>> streamUsagesOf(ClassNode cn, FieldNode fn) {
        return getAllClassMethods().flatMap(p -> {
            var c = p.getFirst();
            var m = p.getSecond();
            return StreamSupport.stream(m.instructions.spliterator(), false)
                    .map(it -> it instanceof FieldInsnNode ? (FieldInsnNode) it : null)
                    .filter(Objects::nonNull)
                    .filter(it -> fn.name.equals(it.name) && fn.desc.equals(it.desc) && cn.name.equals(it.owner))
                    .map(it -> new Triple<>(c, m, it));
        });
    }

    public Stream<Triple<ClassNode, MethodNode, MethodInsnNode>> streamUsagesOf(ClassNode cn, MethodNode mn) {
        return getAllClassMethods().flatMap(p -> {
            var c = p.getFirst();
            var m = p.getSecond();
            return StreamSupport.stream(m.instructions.spliterator(), false)
                    .map(it -> it instanceof MethodInsnNode ? (MethodInsnNode) it : null)
                    .filter(Objects::nonNull)
                    .filter(it -> mn.name.equals(it.name) && mn.desc.equals(it.desc) && cn.name.equals(it.owner))
                    .map(it -> new Triple<>(c, m, it));
        });
    }
}
