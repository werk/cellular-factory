enablePlugins(ScalaJSPlugin)

name := "cellular-factory"
version := "0.1"
scalaVersion := "2.13.3"

scalaJSUseMainModuleInitializer := true

resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies += "com.github.ahnfelt" %%% "react4s" % "0.10.0-SNAPSHOT"
libraryDependencies += "cellular" %%% "cellular" % "0.1.0-SNAPSHOT"
