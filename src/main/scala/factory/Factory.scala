package factory

import cellular.language.Compile.compile
import com.github.ahnfelt.react4s._
import factory.component.MainComponent

object Factory {

    def main(args : Array[String]) : Unit = {
        val component = Component(MainComponent)
        ReactBridge.renderToDomById(component, "main")
    }

}
