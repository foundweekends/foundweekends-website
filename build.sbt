import com.github.sbt.git.SbtGit.{git, GitKeys}
import com.github.sbt.git.GitRunner

lazy val root = (project in file(".")).
  enablePlugins(PamfletPlugin, GhpagesPlugin).
  settings(
    organization := "org.foundweekends",
    name := "foundweekends_website",
    git.remoteRepo := "git@github.com:foundweekends/foundweekends.github.io.git",
    ghpagesUpdatedRepository / GitKeys.gitBranch := Some("master"),
    // This task is responsible for updating the master branch on some temp dir.
    // On the branch there are files that was generated in some other ways such as:
    // - CNAME file
    //
    // This task's job is to call "git rm" on files and directories that this project owns
    // and then copy over the newly generated files.
    ghpagesSynchLocal := {
      // sync the generated site
      val repo = ghpagesUpdatedRepository.value
      val s = streams.value
      val r = GitKeys.gitRunner.value
      gitRemoveFiles(repo, (repo ** "*.html").get.toList, r, s)
      val mappings =  for {
        (file, target) <- (Pamflet / Keys.mappings).value if siteInclude(file)
      } yield (file, repo / target)
      assert(mappings.nonEmpty)
      IO.copy(mappings)
      repo
    }
  )

def siteInclude(f: File) = true
def gitRemoveFiles(dir: File, files: List[File], git: GitRunner, s: TaskStreams): Unit = {
  if(!files.isEmpty)
    git(("rm" :: "-r" :: "-f" :: "--ignore-unmatch" :: files.map(_.getAbsolutePath)) :_*)(dir, s.log)
  ()
}
