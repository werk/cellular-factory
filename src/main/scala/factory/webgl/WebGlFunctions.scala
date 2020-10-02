package factory.webgl

import org.scalajs.dom
import org.scalajs.dom.raw.WebGLRenderingContext._
import org.scalajs.dom.raw._

object WebGlFunctions {


    def getContexts(canvas : HTMLCanvasElement) : WebGLRenderingContext = {
        val context = canvas.getContext("webgl") || canvas.getContext("experimental-webgl")
        if (context == null) throw new RuntimeException("This browser does not support WebGL.")
        context.asInstanceOf[WebGLRenderingContext]
    }

    private def initShader(gl : WebGLRenderingContext, code: String, shaderType : Int) : WebGLShader = {
        val shaderTypeName =
            if(shaderType == VERTEX_SHADER) "vertex shader"
            else if(shaderType == FRAGMENT_SHADER) "fragment shader"
            else throw new RuntimeException(s"Invalid shader type: $shaderType")

        val shader = gl.createShader(shaderType)
        gl.shaderSource(shader, code)
        gl.compileShader(shader)

        val status = gl.getShaderParameter(shader, COMPILE_STATUS).asInstanceOf[Boolean]
        if(!status) {
            val error = s"Failed to compile $shaderTypeName: ${gl.getShaderInfoLog(shader)}"
            println(error)
            println("Code")
            println(code)
            throw new RuntimeException(error)
        }

        shader
    }

    def initVertexShader(gl : WebGLRenderingContext, code: String) : WebGLShader = initShader(gl, code, VERTEX_SHADER)
    def initFragmentShader(gl : WebGLRenderingContext, code: String) : WebGLShader = initShader(gl, code, FRAGMENT_SHADER)

    def initProgram(gl : WebGLRenderingContext, vertexShader: WebGLShader, fragmentShader: WebGLShader) : WebGLProgram = {
        val shaderProgram = gl.createProgram()
        gl.attachShader(shaderProgram, vertexShader)
        gl.attachShader(shaderProgram, fragmentShader)
        gl.linkProgram(shaderProgram)
        shaderProgram
    }

    def initProgram(gl : WebGLRenderingContext, vertexShader: String, fragmentShader: String) : WebGLProgram = {
        initProgram(gl, initVertexShader(gl, vertexShader), initFragmentShader(gl, fragmentShader))
    }

    def activateTexture(gl : WebGLRenderingContext, texture : WebGLTexture, uniformLocation : WebGLUniformLocation): Unit = {
        gl.activeTexture(TEXTURE0)
        gl.bindTexture(TEXTURE_2D, texture)
        gl.uniform1i(uniformLocation, 0)
    }

    def bindTexture(gl : WebGLRenderingContext, loadedImage : HTMLImageElement) : WebGLTexture = {
        val texture = gl.createTexture()
        gl.bindTexture(TEXTURE_2D, texture)
        gl.texImage2D(TEXTURE_2D, 0, RGBA, RGBA, UNSIGNED_BYTE, loadedImage)
        //gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, LINEAR)
        //gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, LINEAR_MIPMAP_NEAREST)
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST)
        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST)
        gl.generateMipmap(TEXTURE_2D)
        gl.bindTexture(TEXTURE_2D, null)
        texture
    }

    def clear(gl : WebGLRenderingContext, clearColor : (Double, Double, Double, Double)): Unit = {

        // Blending
        //gl.blendFunc(ONE, ONE)
        gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
        gl.enable(BLEND)
        gl.disable(DEPTH_TEST)

        val (r, g, b, a) = clearColor
        gl.clearColor(r, g, b, a)
        gl.clear(COLOR_BUFFER_BIT)

        // Set the view port
        gl.viewport(0, 0, gl.canvas.width, gl.canvas.height)
    }

    def resize(gl : WebGLRenderingContext) {
        // Lookup the size the browser is displaying the canvas in CSS pixels
        // and compute a size needed to make our drawingbuffer match it in
        // device pixels.
        val realToCSSPixels = dom.window.devicePixelRatio
        val displayWidth = Math.floor(gl.canvas.clientWidth * realToCSSPixels).toInt
        val displayHeight = Math.floor(gl.canvas.clientHeight * realToCSSPixels).toInt

        if (gl.canvas.width != displayWidth || gl.canvas.height != displayHeight) {
            gl.canvas.width = displayWidth
            gl.canvas.height = displayHeight
        }
    }
}