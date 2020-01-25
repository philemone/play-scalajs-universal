import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val circeVersion = "0.11.1"
val monocleVersion = "1.5.1-cats"

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

lazy val reactVerion = "16.7.0"
lazy val udashJqueryVersion = "3.0.2"
lazy val scalaJsVersion = "1.4.2"
lazy val macroParadiseVersion = "2.1.1"
lazy val scalaJsDOMVersion = "0.9.7"
lazy val playJsonVersion = "2.7.4"
lazy val autowireVersion = "0.2.6"
lazy val boopickleVersion = "1.2.6"
lazy val playMongoVersion = "0.3.0"
lazy val mongoScalaDriverVersion = "2.7.0"
lazy val airframeLogVersion = "19.11.1"
lazy val scalaJsScriptsVersion = "1.1.2"
lazy val jwtVersion = "0.19.0"
lazy val seleniumVersion = "3.141.59"
lazy val commonsIoVersion = "2.6"

def sharedSetting(pName: String) = Seq(
  name := pName,
  version := "0.1",
  scalaVersion := "2.12.10",
  organization := "Filip Michalski - UWr ISSP"
)

def frontEndSharedSetting = Seq(
  resolvers += Resolver.bintrayRepo("scalajs-react-interface", "maven"),
  libraryDependencies ++= Seq(
    "com.github.japgolly.scalajs-react" %%% "core" % scalaJsVersion,
    "com.github.japgolly.scalajs-react" %%% "extra" % scalaJsVersion,
    "com.github.japgolly.scalajs-react" %%% "ext-monocle" % scalaJsVersion,
    "org.scala-js" %%% "scalajs-dom" % scalaJsDOMVersion,
    "io.udash" %%% "udash-jquery" % udashJqueryVersion
  ),
  addCompilerPlugin("org.scalamacros" %% "paradise" % macroParadiseVersion cross CrossVersion.full),
  jsDependencies ++= Seq(
  "org.webjars.npm" % "react" % reactVerion / "umd/react.development.js" minified "umd/react.production.min.js" commonJSName "React",
  "org.webjars.npm" % "react-dom" % reactVerion / "umd/react-dom.development.js" minified  "umd/react-dom.production.min.js" dependsOn "umd/react.development.js" commonJSName "ReactDOM",
  "org.webjars.npm" % "react-dom" % reactVerion / "umd/react-dom-server.browser.development.js" minified  "umd/react-dom-server.browser.production.min.js" dependsOn "umd/react-dom.development.js" commonJSName "ReactDOMServer"),
)

lazy val appFrontEnd = (project in file("app-front-end"))
  .settings(
    sharedSetting("app-front-end"),
    scalaJSUseMainModuleInitializer := true,
  )
  .enablePlugins(ScalaJSPlugin, JSDependenciesPlugin)
  .dependsOn(frontEndShared)

lazy val loginFrontEnd = (project in file("login-front-end"))
  .settings(
    sharedSetting("login-front-end"),
    scalaJSUseMainModuleInitializer := true
  )
  .enablePlugins(ScalaJSPlugin, JSDependenciesPlugin)
  .dependsOn(frontEndShared)

lazy val frontEndShared = (project in file("front-end-shared"))
  .settings(sharedSetting("front-end-shared"))
  .settings(frontEndSharedSetting)
  .enablePlugins(ScalaJSPlugin, JSDependenciesPlugin)
  .dependsOn(sharedJS)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSetting("shared"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %%% "play-json" % playJsonVersion,
      "com.lihaoyi" %%% "autowire" % autowireVersion,
      "io.suzaku" %%% "boopickle" % boopickleVersion,
      "cn.playscala" % "play-mongo_2.12" % playMongoVersion,
      "org.mongodb.scala" %% "mongo-scala-driver" % mongoScalaDriverVersion,
      "org.wvlet.airframe" %%% "airframe-log" % airframeLogVersion,
      "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
      "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
      "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test"
    ),
    addCompilerPlugin("org.scalamacros" %% "paradise" % macroParadiseVersion cross CrossVersion.full),
    libraryDependencies ++= Seq("io.circe" %% "circe-core", "io.circe" %% "circe-generic", "io.circe" %% "circe-parser").map(_ % circeVersion),
  ).jsConfigure(_ enablePlugins ScalaJSWeb)


lazy val backEnd = (project in file("back-end"))
  .settings(sharedSetting("backEnd"))
  .settings(
    scalaJSProjects := Seq(appFrontEnd, loginFrontEnd),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.vmunier" %% "scalajs-scripts" % scalaJsScriptsVersion,
      guice,
      specs2 % Test,
      "com.pauldijou" %% "jwt-play" % jwtVersion,
      "com.pauldijou" %% "jwt-core" % jwtVersion,
      "org.seleniumhq.selenium" % "selenium-java" % seleniumVersion,
      "org.seleniumhq.selenium" % "selenium-remote-driver" % seleniumVersion,
      "org.seleniumhq.selenium" % "selenium-chrome-driver" % seleniumVersion,
      "commons-io" % "commons-io" % commonsIoVersion
    )
  )
  .enablePlugins(PlayScala)
  .dependsOn(sharedJVM)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project backEnd" :: s}