package api

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Ref
import create_gist_action.presenter.CreateGistParams
import gists_tool_window.presenter.OnGotGistsListener
import gists_tool_window.presenter.OnGotUserProfileDataListener
import gists_tool_window.presenter.getPersonalGists
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
import org.jetbrains.plugins.github.api.GithubApiRequests
import org.jetbrains.plugins.github.api.data.GithubAuthenticatedUser
import org.jetbrains.plugins.github.api.data.GithubGist
import org.jetbrains.plugins.github.api.data.request.GithubGistRequest
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import java.awt.Image
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

/**
 * @author Josias Sena
 * @since v1.0
 */
class GistApi(private val apiRequestExecutorManager: GithubApiRequestExecutorManager = GithubApiRequestExecutorManager.getInstance()) {

    fun createGist(
        project: Project,
        githubAccount: GithubAccount,
        contents: List<GithubGistRequest.FileContent>,
        gistParams: CreateGistParams,
        onGistCreatedListener: OnGistCreatedListener
    ) {
        val executor = apiRequestExecutorManager.getExecutor(githubAccount, project)
        val gistUrlRef = Ref<String>()

        executor?.let {
            val githubServerPath = githubAccount.server

            object : Task.Backgroundable(project, "Creating Gist...") {

                override fun run(indicator: ProgressIndicator) {
                    val githubApiRequest = GithubApiRequests.Gists.create(
                        githubServerPath,
                        contents,
                        gistParams.description,
                        !gistParams.isPrivate
                    )

                    gistUrlRef.set(executor.execute(indicator, githubApiRequest).htmlUrl)
                }

                override fun onSuccess() {
                    super.onSuccess()
                    onGistCreatedListener.onSuccess(gistUrlRef.get())
                }

                override fun onThrowable(error: Throwable) {
                    super.onThrowable(error)
                    onGistCreatedListener.onError(error)
                }
            }.queue()
        }
    }

    fun fetchProfileInfo(project: Project, githubAccount: GithubAccount, listener: OnGotUserProfileDataListener) {
        val executor = apiRequestExecutorManager.getExecutor(githubAccount, project)
        val gistUserRef = Ref<GithubAuthenticatedUser>()

        executor?.let {
            val githubServerPath = githubAccount.server

            object : Task.Backgroundable(project, "Fetching GitHub profile...") {

                override fun run(indicator: ProgressIndicator) {
                    gistUserRef.set(executor.execute(GithubApiRequests.CurrentUser.get(githubServerPath)))
                }

                override fun onSuccess() {
                    super.onSuccess()
                    val user = gistUserRef.get()

                    var image: Image? = null

                    try {
                        val url = URL(user.avatarUrl)
                        image = ImageIO.read(url)
                    } catch (ioException: IOException) {
                        ioException.printStackTrace()
                    }

                    listener.onSuccess(image, user)
                }

                override fun onThrowable(error: Throwable) {
                    super.onThrowable(error)
                    listener.onError(error)
                }
            }.queue()
        }
    }

    fun getGists(project: Project, githubAccount: GithubAccount, onGotGistsListener: OnGotGistsListener) {
        val githubServerPath = githubAccount.server
        val executor = apiRequestExecutorManager.getExecutor(githubAccount, project)
        val gistsRef = Ref<List<GithubGist>>()

        executor?.let {
            object : Task.Backgroundable(project, "Getting Gits...") {
                override fun run(indicator: ProgressIndicator) {
                    val githubApiRequest = GithubApiRequests.Gists.getPersonalGists(githubServerPath)

                    gistsRef.set(executor.execute(indicator, githubApiRequest)?.toList().orEmpty())
                }

                override fun onSuccess() {
                    onGotGistsListener.onSuccess(gistsRef.get().toMutableList())
                }

                override fun onThrowable(error: Throwable) {
                    super.onThrowable(error)
                    onGotGistsListener.onError(error)
                }
            }.queue()
        }
    }
}