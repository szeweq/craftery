package szewek.mctool.mcdata

import javafx.scene.image.Image

object Models {
    val modelMap = mutableMapOf<String, ModelData>()
    val textures = mutableMapOf<String, Image>()

    class ModelData(val parent: String?)
}