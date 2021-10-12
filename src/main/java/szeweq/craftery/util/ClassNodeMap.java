package szeweq.craftery.util;

import kotlin.Pair;
import kotlin.Triple;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class ClassNodeMap {
    private final Map<String, ClassNode> nodes = new HashMap<>();

    public void add(ClassNode cn) {
        nodes.put(cn.name, cn);
    }

    public Collection<ClassNode> getClasses() {
        return nodes.values();
    }

    public Stream<ClassNode> getClassStream() {
        return nodes.values().stream();
    }

    public Stream<Pair<ClassNode, MethodNode>> getAllClassMethods(final boolean parallel) {
        return getFlattenedClassValues(parallel, c -> c.methods);
    }

    public Stream<Pair<ClassNode, FieldNode>> getAllClassFields(final boolean parallel) {
        return getFlattenedClassValues(parallel, c -> c.fields);
    }

    private <T> Stream<Pair<ClassNode, T>> getFlattenedClassValues(final boolean parallel, Function<ClassNode, List<T>> fn) {
        var vals = nodes.values();
        return (parallel ? vals.parallelStream() : vals.stream()).mapMulti((cl, c) -> {
            var l = fn.apply(cl);
            for (var x : l) {
                c.accept(new Pair<>(cl, x));
            }
        });
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

    public <N extends AbstractInsnNode> Stream<Triple<ClassNode, MethodNode, N>> instructionsStream(final boolean parallel, BiConsumer<AbstractInsnNode, Consumer<N>> tfn) {
        return getAllClassMethods(parallel).mapMulti((p, c) -> {
            final var cl = p.getFirst();
            final var m = p.getSecond();
            final Consumer<N> cx = n -> c.accept(new Triple<>(cl, m, n));
            for (var node : m.instructions) {
                tfn.accept(node, cx);
            }
        });
    }

    public Stream<Triple<ClassNode, MethodNode, FieldInsnNode>> streamUsagesOf(ClassNode cn, FieldNode fn, final boolean parallel) {
        return instructionsStream(parallel, (node, c) -> {
            if (node instanceof final FieldInsnNode it && fn.name.equals(it.name) && fn.desc.equals(it.desc) && cn.name.equals(it.owner))
                c.accept(it);
        });
    }

    public Stream<Triple<ClassNode, MethodNode, MethodInsnNode>> streamUsagesOf(ClassNode cn, MethodNode mn, final boolean parallel) {
        return instructionsStream(parallel, (node, c) -> {
            if (node instanceof final MethodInsnNode it && mn.name.equals(it.name) && mn.desc.equals(it.desc) && cn.name.equals(it.owner))
                c.accept(it);
        });
    }
}
