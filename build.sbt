lazy val root = (project in file(".")).
  settings(
    organization := "org.foundweekends",
    name := "foundweekends_website",
    TaskKey[Unit]("makeSite") := {
      val output = target.value / "site"
      IO.delete(output)
      val src     = (LocalRootProject / baseDirectory).value / "src" / "pamflet"
      val storage = pamflet.FileStorage(src, Nil)
      pamflet.Produce(storage.globalized, output)
      IO.delete(output / "offline")
    }
  )
