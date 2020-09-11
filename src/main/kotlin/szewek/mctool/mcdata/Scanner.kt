package szewek.mctool.mcdata

import com.electronwill.nightconfig.toml.TomlParser
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import szewek.mctool.util.ClassNodeMap
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonReaderFactory

object Scanner {
    val TOML = TomlParser()
    val JSON: JsonReaderFactory = Json.createReaderFactory(null)

    fun genericFromSignature(sig: String) = sig.substringAfter('<').substringBeforeLast('>')

    fun pathToLocation(path: String): String {
        val p = path.split("/", limit = 4)
        if (p.size < 4) {
            return p.last()
        }
        val (_, ns, _, v) = p
        return "$ns:$v"
    }

    class JsonInfo(val name: String, val namespace: String, val type: DataResourceType) {
        val details = mutableMapOf<String, String>()

        fun gatherDetails(obj: JsonObject) {
            when (type) {
                DataResourceType.RECIPE -> {
                    obj.getString("type", null)?.apply { details["Type"] = this }
                }
                else -> {}
            }
        }
    }

    class CapabilitiesInfo(val name: String, instructions: InsnList) {
        var supclasses: Set<String> = instructions.stream()
            .filterIsInstance<MethodInsnNode>()
            .filter { "getCapability" == it.name && name != it.owner }
            .map { it.owner ?: "UNKNOWN" }
            .toSet()
        var fields = instructions.stream()
            .filterIsInstance<FieldInsnNode>().filter { TypeNames.CAPABILITY == it.desc }
            .map { "${it.owner ?: "UNKNOWN"}::${it.name ?: "UNKNOWN"}" }
            .toSet()
    }

    class LazyOptionalInfo(classes: ClassNodeMap, classNode: ClassNode, fields: List<FieldNode>) {
        val name: String = classNode.name
        val warnings = fields.stream().filter { f ->
            !classes.streamUsagesOf(classNode, f).anyMatch { (_, _, i) ->
                if (i.opcode == Opcodes.GETFIELD) {
                    val ni = i.next
                    if (ni is MethodInsnNode
                            && ni.opcode == Opcodes.INVOKEVIRTUAL
                            && ni.owner == f.fixedDesc
                            && ni.name == "invalidate"
                    ) {
                        return@anyMatch true
                    }
                }
                false
            }
        }.map {
            if (it.signature == null) { it.name to "NONE" }
            else { it.name to genericFromSignature(it.signature) }
        }.toSet()
    }
}