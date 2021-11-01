package szeweq.craftery.util;

import kotlin.Pair;
import kotlin.Triple;
import kotlin.Unit;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.objectweb.asm.tree.*;

import java.util.*;
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

    public Flow<ClassNode> getClassFlow() {
        return FlowKt.asFlow(nodes.values());
    }

    public Flow<Pair<ClassNode, MethodNode>> getAllClassMethods() {
        return getFlattenedClassValues(c -> c.methods);
    }

    public Flow<Pair<ClassNode, FieldNode>> getAllClassFields() {
        return getFlattenedClassValues(c -> c.fields);
    }

    private <T> Flow<Pair<ClassNode, T>> getFlattenedClassValues(Function<ClassNode, List<T>> fn) {
        final var f = getClassFlow();
        return FlowKt.flow((fc, continuation) -> f.collect((cl, cont) -> {
            var l = fn.apply(cl);
            for (var x : l) {
                fc.emit(new Pair<>(cl, x), cont);
            }
            return Unit.INSTANCE;
        }, continuation));
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

    public <N extends AbstractInsnNode> Flow<Triple<ClassNode, MethodNode, N>> instructionsFlow(Function<AbstractInsnNode, N> tfn) {
        final var f = getAllClassMethods();
        return FlowKt.flow((fc, continuation) -> f.collect((p, cont) -> {
            final var cl = p.getFirst();
            final var m = p.getSecond();
            for (var node : m.instructions) {
                var n = tfn.apply(node);
                if (n != null) fc.emit(new Triple<>(cl, m, n), cont);
            }
            return Unit.INSTANCE;
        }, continuation));
    }

    public Flow<Triple<ClassNode, MethodNode, FieldInsnNode>> flowUsagesOf(ClassNode cn, FieldNode fn) {
        return instructionsFlow(node -> {
            if (node instanceof final FieldInsnNode it && fn.name.equals(it.name) && fn.desc.equals(it.desc) && cn.name.equals(it.owner))
                return it;
            return null;
        });
    }

    public Flow<Triple<ClassNode, MethodNode, MethodInsnNode>> flowUsagesOf(ClassNode cn, MethodNode mn) {
        return instructionsFlow(node -> {
            if (node instanceof final MethodInsnNode it && mn.name.equals(it.name) && mn.desc.equals(it.desc) && cn.name.equals(it.owner))
                return it;
            return null;
        });
    }
}
