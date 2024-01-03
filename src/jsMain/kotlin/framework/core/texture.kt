package framework.core

import js.typedarrays.Uint8Array
import web.gl.GLenum
import web.gl.TexImageSource
import web.gl.WebGL2RenderingContext
import web.gl.WebGL2RenderingContext.Companion.CLAMP_TO_EDGE
import web.gl.WebGL2RenderingContext.Companion.LINEAR
import web.gl.WebGL2RenderingContext.Companion.LINEAR_MIPMAP_LINEAR
import web.gl.WebGL2RenderingContext.Companion.MIRRORED_REPEAT
import web.gl.WebGL2RenderingContext.Companion.NEAREST
import web.gl.WebGL2RenderingContext.Companion.NEAREST_MIPMAP_LINEAR
import web.gl.WebGL2RenderingContext.Companion.NEAREST_MIPMAP_NEAREST
import web.gl.WebGL2RenderingContext.Companion.REPEAT
import web.gl.WebGL2RenderingContext.Companion.RGBA
import web.gl.WebGL2RenderingContext.Companion.TEXTURE_2D
import web.gl.WebGL2RenderingContext.Companion.TEXTURE_MAG_FILTER
import web.gl.WebGL2RenderingContext.Companion.TEXTURE_MIN_FILTER
import web.gl.WebGL2RenderingContext.Companion.TEXTURE_WRAP_S
import web.gl.WebGL2RenderingContext.Companion.TEXTURE_WRAP_T
import web.gl.WebGL2RenderingContext.Companion.UNSIGNED_BYTE
import web.gl.WebGLTexture
import web.html.Image

enum class MagFilter(val value: GLenum) {
    Nearest(NEAREST),
    Linear(LINEAR)
}

enum class MinFilter(val value: GLenum, val isMipMap: Boolean) {
    Nearest(NEAREST, false),
    Linear(LINEAR, false),
    NearestMipMapNearest(NEAREST_MIPMAP_NEAREST, true),
    NearestMipMapLinear(NEAREST_MIPMAP_LINEAR, true),
    LinearMipMapLinear(LINEAR_MIPMAP_LINEAR, true),
}

enum class Wrap(val value: GLenum) {
    Repeat(REPEAT),
    ClampToEdge(CLAMP_TO_EDGE),
    MirroredRepeat(MIRRORED_REPEAT)
}

class Texture private constructor(
    private val texture: WebGLTexture?,
    private val source: TexImageSource,
    private val magFilter: MagFilter = MagFilter.Linear,
    private val minFilter: MinFilter = MinFilter.NearestMipMapLinear,
    private val wrap: Wrap = Wrap.Repeat
) {
    fun uploadData(gl: WebGL2RenderingContext) {
        gl.bindTexture(TEXTURE_2D, texture)
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, source)
        if (minFilter.isMipMap) {
            gl.generateMipmap(TEXTURE_2D)
        }
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, magFilter.value)
        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, minFilter.value)
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, wrap.value)
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, wrap.value)
    }

    companion object {
        private val INITIAL_DATA = Uint8Array(arrayOf(0, 0, 0, 0))

        operator fun invoke(gl: WebGL2RenderingContext, source: TexImageSource): Texture {
            val texture = gl.createTexture()
            gl.bindTexture(TEXTURE_2D, texture)
            gl.texImage2D(TEXTURE_2D, 0, RGBA, 1, 1, 0, RGBA, UNSIGNED_BYTE, INITIAL_DATA)
            return Texture(texture, source)
        }

        fun load(
            gl: WebGL2RenderingContext, addressOrUrl: String,
            magFilter: MagFilter = MagFilter.Linear,
            minFilter: MinFilter = MinFilter.NearestMipMapLinear,
            wrap: Wrap = Wrap.Repeat
        ) {
            val image = Image().apply {
                src = addressOrUrl
            }
            val texture = Texture(gl, image)
            image.onload = {
                texture.uploadData(gl)
                console.log("Loaded image $addressOrUrl")
            }
            image.onerror = {
                console.error("Error while loading image $addressOrUrl")
            }
        }
    }
}
