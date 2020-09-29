package factory.component

import com.github.ahnfelt.react4s._
import org.scalajs.dom
import dom.raw.{WebGLRenderingContext => GL}

case class CanvasComponent() extends Component[NoEmit] {

    override def render(get : Get) : Node = {
        val canvas = E.canvas().withRef(withCanvas)
        canvas
    }

    def withCanvas(e : Any) : Unit = if(e != null) {
        val canvas = e.asInstanceOf[dom.html.Canvas]
        val gl = canvas.getContext("webgl").asInstanceOf[dom.raw.WebGLRenderingContext]
        gl.clearColor(0.5, 0.0, 0.5, 1.0);
        gl.clear(GL.COLOR_BUFFER_BIT);
    }
}
