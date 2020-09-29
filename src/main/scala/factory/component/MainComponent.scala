package factory.component

import cellular.language.Compile.compile
import com.github.ahnfelt.react4s._
import factory.SandAndWater

case class MainComponent() extends Component[NoEmit] {

    override def render(get : Get) : Element = {
        val glsl = compile(SandAndWater.declarations)
        E.pre(Text(glsl))
    }

}