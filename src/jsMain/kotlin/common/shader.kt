package common

import web.gl.*

fun buildProgram(gl: WebGL2RenderingContext, vertexSource: String, fragmentSource: String): WebGLProgram {
    val vertexShader = compileShader(gl, vertexSource, WebGL2RenderingContext.VERTEX_SHADER)
    val fragmentShader = compileShader(gl, fragmentSource, WebGL2RenderingContext.FRAGMENT_SHADER)
    return linkProgram(gl, vertexShader, fragmentShader)
}

fun compileShader(gl: WebGL2RenderingContext, source: String, type: GLenum): WebGLShader {
    val shader = checkNotNull(gl.createShader(type)) { "Cannot create shader." }
    gl.shaderSource(shader, source)
    gl.compileShader(shader)
    if (!(gl.getShaderParameter(shader, WebGL2RenderingContext.COMPILE_STATUS) as Boolean)) {
        console.error(gl.getShaderInfoLog(shader))
    }
    return shader
}

fun linkProgram(gl: WebGL2RenderingContext, vertex: WebGLShader, fragment: WebGLShader): WebGLProgram {
    val program = checkNotNull(gl.createProgram()) { "Cannot create program." }
    gl.attachShader(program, vertex)
    gl.attachShader(program, fragment)
    gl.linkProgram(program)
    if (!(gl.getProgramParameter(program, WebGL2RenderingContext.LINK_STATUS) as Boolean)) {
        console.error(gl.getProgramInfoLog(program))
    }
    return program
}

data class ActiveLocation(val location: GLuint, val type: GLenum)

fun getActiveAttributes(gl: WebGL2RenderingContext, program: WebGLProgram): Map<String, ActiveLocation> {
    val attributeCount = gl.getProgramParameter(program, WebGL2RenderingContext.ACTIVE_ATTRIBUTES) as Int
    return (0..<attributeCount).asSequence()
        .map { i -> requireNotNull(gl.getActiveAttrib(program, i)) { "Cannot get info for attribute #$i" } }
        .map {  info -> info.name to ActiveLocation(gl.getAttribLocation(program, info.name), info.type) }
        .toMap()
}