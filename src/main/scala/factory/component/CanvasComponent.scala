package factory.component

import com.github.ahnfelt.react4s._
import org.scalajs.dom
import dom.raw.{WebGLRenderingContext => GL}
import factory.webgl.WebGlRunner
import factory.webgl.WebGlRunner.UniformFloat

case class CanvasComponent() extends Component[NoEmit] {

    override def render(get : Get) : Node = {
        val canvas = E.canvas().withRef(withCanvas)
        canvas
    }

    def withCanvas(e : Any) : Unit = if(e != null) {
        val canvas = e.asInstanceOf[dom.html.Canvas]
        val gl = canvas.getContext("webgl").asInstanceOf[GL]
        val timeUniform = new UniformFloat()
        val renderer = new WebGlRunner(gl, fragmentCode, List("t" -> timeUniform))
        start(renderer, timeUniform)
    }

    def start(renderer : WebGlRunner, timeUniform : UniformFloat) {
        val t0 = System.currentTimeMillis()

        def loop(x : Double) {
            val t = (System.currentTimeMillis() - t0) / 1000f
            timeUniform.value = t
            renderer.render()
            dom.window.requestAnimationFrame(loop)
        }
        dom.window.requestAnimationFrame(loop);
    }

    val fragmentCode = """
  precision mediump float;

  uniform float t;

  void main() {
    gl_FragColor = vec4(0.5, sin(t) * 0.5 + 0.5, 1, 1);
  }
    """
}
