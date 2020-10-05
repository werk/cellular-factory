package factory.webgl

import factory.webgl.FactoryGl.UniformReference
import org.scalajs.dom
import dom.raw.{HTMLCanvasElement, HTMLImageElement, WebGLBuffer, WebGLFramebuffer, WebGLProgram, WebGLTexture, WebGLRenderingContext => GL}
import factory.IVec2
import factory.webgl.WebGlFunctions.{DataTextureSource, ImageTextureSource}

import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js

class FactoryGl(
    gl : GL,
    simulateFragmentCode : String,
    drawFragmentCode : String,
    uniforms : List[(String, UniformReference)],
    materialsImage : HTMLImageElement,
    stateSize : IVec2,
) {

    private object programs {
        val simulate = WebGlFunctions.initProgram(gl, FactoryGl.vertexCode, simulateFragmentCode)
        val draw = WebGlFunctions.initProgram(gl, FactoryGl.vertexCode, drawFragmentCode)
    }

    private object textures {
        val front = WebGlFunctions.bindDataTexture(gl, DataTextureSource(stateSize))
        val back = WebGlFunctions.bindDataTexture(gl, DataTextureSource(stateSize))
        //val materials = WebGlFunctions.bindDataTexture(gl, ImageTextureSource(materialsImage))
        //val inventory = WebGlFunctions.bindDataTexture(gl, DataTextureSource(inventorySize))
    }

    private val positionBuffer : WebGLBuffer = {
        val buffer = gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, buffer)
        gl.bufferData(
            GL.ARRAY_BUFFER,
            new Float32Array(js.Array[Float](
                -1, -1,
                1, -1,
                -1, 1,
                1, 1)),
            GL.STATIC_DRAW
        )
        buffer
    }

    private val framebuffer : WebGLFramebuffer = gl.createFramebuffer()

    def simulate() = {
        FactoryGl.renderSimulation(
            gl = gl,
            program = programs.draw,
            positionBuffer = positionBuffer,
            uniforms = uniforms,
            framebuffer = framebuffer,
            textureIn = textures.back,
            textureOut = textures.front,
            stateSize = stateSize,
        )
    }

    def draw() = FactoryGl.renderDraw(
        gl = gl,
        program = programs.draw,
        positionBuffer = positionBuffer,
        uniforms = uniforms,
        canvas = gl.canvas,
    )

}

object FactoryGl {

    def renderSimulation(
        gl : GL,
        program : WebGLProgram,
        positionBuffer : WebGLBuffer,
        uniforms : List[(String, UniformReference)],
        framebuffer : WebGLFramebuffer,
        textureIn : WebGLTexture,
        textureOut : WebGLTexture,
        stateSize : IVec2
    ): Unit = {
        gl.bindFramebuffer(GL.FRAMEBUFFER, framebuffer)
        gl.framebufferTexture2D(
            target = GL.FRAMEBUFFER,
            attachment = GL.COLOR_ATTACHMENT0,
            textarget = GL.TEXTURE_2D,
            texture = textureOut,
            level = 0
        )
        gl.activeTexture(GL.TEXTURE0 + 0)
        gl.bindTexture(GL.TEXTURE_2D, textureIn)
        gl.viewport(0, 0, stateSize.x, stateSize.y)

        {
            val location = gl.getUniformLocation(program, "state")
            gl.uniform1i(location, 0)
        }
        renderCommon(gl, program, positionBuffer, uniforms)
    }

    def renderDraw(
        gl : GL,
        program : WebGLProgram,
        positionBuffer : WebGLBuffer,
        uniforms : List[(String, UniformReference)],
        canvas : HTMLCanvasElement,
    ) : Unit = {
        WebGlFunctions.resize(canvas)
        gl.viewport(0, 0, canvas.width, canvas.height)
        renderCommon(gl, program, positionBuffer, uniforms)
    }

    private def renderCommon(
        gl : GL,
        program : WebGLProgram,
        positionBuffer : WebGLBuffer,
        uniforms : List[(String, UniformReference)]
    ) = {
        gl.useProgram(program)

        uniforms.foreach { case (name, u) =>
            val location = gl.getUniformLocation(program, name)
            u match {
                case u : FactoryGl.UniformFloat => gl.uniform1f(location, u.value)
                case u : FactoryGl.UniformVec2 => gl.uniform2f(location, u.x, u.y)
                case u : FactoryGl.UniformVec3 => gl.uniform3f(location, u.x, u.y, u.z)
                case u : FactoryGl.UniformVec4 => gl.uniform4f(location, u.x, u.y, u.z, u.w)
            }
        }

        val positionAttributeLocation = gl.getAttribLocation(program, "position")
        gl.enableVertexAttribArray(positionAttributeLocation)
        gl.bindBuffer(GL.ARRAY_BUFFER, positionBuffer)

        gl.vertexAttribPointer(
            indx = positionAttributeLocation,
            size = 2,
            `type` = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )

        gl.drawArrays(
            mode = GL.TRIANGLE_STRIP,
            first = 0,
            count = 4
        )
    }

    sealed trait UniformReference

    class UniformInt extends UniformReference {
        var value : Int = 0
    }

    class UniformFloat extends UniformReference {
        var value : Float = 0
    }

    class UniformVec2 extends UniformReference {
        var x : Float = 0
        var y : Float = 0
    }

    class UniformVec3 extends UniformReference {
        var x : Float = 0
        var y : Float = 0
        var z : Float = 0
    }

    class UniformVec4 extends UniformReference {
        var x : Float = 0
        var y : Float = 0
        var z : Float = 0
        var w : Float = 0
    }

    val vertexCode = s"""
precision mediump float;
attribute vec2 position;

void main() {
    gl_Position = vec4(position, 0, 1.0);
}
    """

}
