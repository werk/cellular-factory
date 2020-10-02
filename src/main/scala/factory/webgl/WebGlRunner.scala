package factory.webgl

import factory.webgl.WebGlRunner.UniformReference
import org.scalajs.dom
import dom.raw.{HTMLCanvasElement, WebGLRenderingContext => GL}

import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js

class WebGlRunner(gl : GL, fragmentCode : String, uniforms : List[(String, UniformReference)]) {

    private val program = WebGlFunctions.initProgram(gl, WebGlRunner.vertexCode, fragmentCode)

    private val positionAttributeLocation = gl.getAttribLocation(program, "position");

    private val positionBuffer = gl.createBuffer()
    gl.bindBuffer(GL.ARRAY_BUFFER, positionBuffer)

    gl.bufferData(
        GL.ARRAY_BUFFER,
        new Float32Array(js.Array[Float](
            -1, -1,
            1, -1,
            -1, 1,
            1, 1)),
        GL.STATIC_DRAW
    );

    def render(): Unit = {
        resize(gl.canvas)
        gl.viewport(0, 0, gl.canvas.width, gl.canvas.height);
        gl.clearColor(0, 0, 0, 0)
        gl.clear(GL.COLOR_BUFFER_BIT)
        gl.useProgram(program)

        uniforms.foreach { case (name, u) =>
            val location = gl.getUniformLocation(program, name)
            u match {
                case u : WebGlRunner.UniformFloat => gl.uniform1f(location, u.value);
                case u : WebGlRunner.UniformVec2 => gl.uniform2f(location, u.x, u.y);
                case u : WebGlRunner.UniformVec3 => gl.uniform3f(location, u.x, u.y, u.z);
                case u : WebGlRunner.UniformVec4 => gl.uniform4f(location, u.x, u.y, u.z, u.w);
            }
        }

        gl.enableVertexAttribArray(positionAttributeLocation)
        gl.bindBuffer(GL.ARRAY_BUFFER, positionBuffer);

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

    def resize(canvas : HTMLCanvasElement) {
        val displayWidth  = canvas.clientWidth;
        val displayHeight = canvas.clientHeight;

        if (canvas.width  != displayWidth || canvas.height != displayHeight) {
            canvas.width  = displayWidth;
            canvas.height = displayHeight;
        }
    }

}

object WebGlRunner {
    sealed trait UniformReference

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
