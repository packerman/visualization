package framework.core

import web.gl.*

class Program(
    private val program: WebGLProgram,
    val attributes: Map<String, ActiveAttribute>,
    val uniforms: Map<String, ActiveUniform>
) {

    fun use(gl: WebGL2RenderingContext) {
        gl.useProgram(program)
    }

    companion object {
        data class ActiveAttribute(val location: GLint, val type: GLenum)

        data class ActiveUniform(val location: WebGLUniformLocation?, val type: GLenum)

        fun build(gl: WebGL2RenderingContext, vertexSource: String, fragmentSource: String): Program {
            val vertexShader =
                compile(gl, decorateVertexShaderSource(vertexSource), WebGL2RenderingContext.VERTEX_SHADER)
            val fragmentShader =
                compile(gl, decorateFragmentShaderSource(fragmentSource), WebGL2RenderingContext.FRAGMENT_SHADER)
            val program = link(gl, vertexShader, fragmentShader)
            return Program(
                program,
                getActiveAttributes(gl, program),
                getActiveUniforms(gl, program)
            )
        }

        private fun link(gl: WebGL2RenderingContext, vertex: WebGLShader, fragment: WebGLShader): WebGLProgram {
            val program = checkNotNull(gl.createProgram()) { "Cannot create program." }
            gl.attachShader(program, vertex)
            gl.attachShader(program, fragment)
            gl.linkProgram(program)
            if (!(gl.getProgramParameter(program, WebGL2RenderingContext.LINK_STATUS) as Boolean)) {
                console.error(gl.getProgramInfoLog(program))
            }
            return program
        }

        private fun compile(gl: WebGL2RenderingContext, source: String, type: GLenum): WebGLShader {
            val shader = checkNotNull(gl.createShader(type)) { "Cannot create shader." }
            gl.shaderSource(shader, source)
            gl.compileShader(shader)
            if (!(gl.getShaderParameter(shader, WebGL2RenderingContext.COMPILE_STATUS) as Boolean)) {
                console.error(
                    "Error while compiling shader $type, source ${insertLineNumbers(source)}: ${
                        gl.getShaderInfoLog(shader)
                    }"
                )
            }
            return shader
        }

        private fun getActiveAttributes(
            gl: WebGL2RenderingContext,
            program: WebGLProgram
        ): Map<String, ActiveAttribute> {
            val attributeCount = gl.getProgramParameter(program, WebGL2RenderingContext.ACTIVE_ATTRIBUTES) as Int
            return (0..<attributeCount).asSequence()
                .map { i -> requireNotNull(gl.getActiveAttrib(program, i)) { "Cannot get info for attribute #$i" } }
                .map { info -> info.name to ActiveAttribute(gl.getAttribLocation(program, info.name), info.type) }
                .toMap()
        }

        private fun getActiveUniforms(gl: WebGL2RenderingContext, program: WebGLProgram): Map<String, ActiveUniform> {
            val uniformCount = gl.getProgramParameter(program, WebGL2RenderingContext.ACTIVE_UNIFORMS) as Int
            return (0..<uniformCount).asSequence()
                .map { i -> requireNotNull(gl.getActiveUniform(program, i)) { "Cannot get info for uniform #$i" } }
                .map { info -> info.name to ActiveUniform(gl.getUniformLocation(program, info.name), info.type) }
                .toMap()
        }

        private fun decorateVertexShaderSource(source: String): String =
            """
                #version 300 es
                
                $source
            """.trimIndent()

        private fun decorateFragmentShaderSource(source: String): String =
            """
                #version 300 es
                precision mediump float;
        
                $source
            """.trimIndent()

        private fun insertLineNumbers(source: String): String {
            return source.lineSequence()
                .mapIndexed { i, line -> "${i + 1}. $line" }
                .joinToString(separator = "\n")
        }
    }
}
