package factory.component

import com.github.ahnfelt.react4s._
import org.scalajs.dom
import dom.raw.{WebGLRenderingContext => GL}
import factory.IVec2
import factory.webgl.FactoryGl
import factory.webgl.FactoryGl.UniformFloat

case class CanvasComponent(stepCodeP : P[String], viewCodeP : P[String]) extends Component[NoEmit] {

    override def render(get : Get) : Node = {
        val stepCode = get(stepCodeP)
        val viewCode = get(viewCodeP)
        val canvas = E.canvas(
            S.width.percent(100),
            S.height.percent(100),
        ).withRef(withCanvas(stepCode, viewCode, _))
        canvas
    }

    def withCanvas(stepCode : String, viewCode : String, e : Any) : Unit = if(e != null) {
        val canvas = e.asInstanceOf[dom.html.Canvas]
        val gl = canvas.getContext("webgl2").asInstanceOf[GL]
        val timeUniform = new UniformFloat()
        val renderer = new FactoryGl(
            gl = gl,
            simulateCode = stepCode,
            viewCode = viewCode,
            uniforms = List("t" -> timeUniform),
            materialsImage = null,
            stateSize = IVec2(100, 100)
        )
        start(renderer, timeUniform)
    }

    def start(renderer : FactoryGl, timeUniform : UniformFloat) {
        val t0 = System.currentTimeMillis()
        var step = 0

        def loop(x : Double) {
            val t = (System.currentTimeMillis() - t0) / 1000f
            if(t.toInt > step) {
                step = t.toInt
                //println(s"Simulate $step")
                renderer.simulate()
            }

            timeUniform.value = t
            renderer.draw()
            dom.window.requestAnimationFrame(loop)
        }
        dom.window.requestAnimationFrame(loop)
    }
/*
    val viewCode = """
precision mediump float;
uniform sampler2D state;

uniform float t;

void main() {
    vec2 offset = vec2(0, 0);
    vec2 resolution = vec2(500, 500);
    float zoom = 1.0;
    float screenToMapRatio = zoom / resolution.x;
    vec2 xy = gl_FragCoord.xy * screenToMapRatio + offset;
    vec2 tile = floor(xy + 0.5);

    vec2 stateSize = vec2(100, 100);

    vec4 center = texture2D(state, tile / stateSize);

    gl_FragColor = vec4(center.x, sin(t) * 0.5 + 0.5, 1, 1);
}
    """

    val simulationCode = """
precision mediump float;

uniform float t;

void main() {
    gl_FragColor = vec4(sin(t * 13.37) * 0.5 + 0.5, 1, 1, 1);
}
    """
*/
}
