package gists_tool_window.presenter

import GistsToolWindow
import api.GistApi
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.intellij.util.ui.ImageUtil
import org.jetbrains.plugins.github.api.GithubApiRequest
import org.jetbrains.plugins.github.api.GithubApiRequests
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.api.data.GithubAuthenticatedUser
import org.jetbrains.plugins.github.api.data.GithubGist
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.util.GithubNotifications
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * @author Josias Sena
 * @since v1.0
 */
fun GithubApiRequests.Gists.getPersonalGists(server: GithubServerPath): GithubApiRequest<Array<GithubGist>?> {
    return GithubApiRequest.Get.Optional.json<Array<GithubGist>>(
        GithubApiRequests.getUrl(server, urlSuffix, "")
    ).withOperationName("get gists")
}

/**
 * @author Josias Sena
 * @since v1.0
 */
class GistsToolWindowPresenter @JvmOverloads constructor(
    private val view: GistsToolWindow,
    private val project: Project,
    private val authenticationManager: GithubAuthenticationManager = GithubAuthenticationManager.getInstance(),
    private val gistApi: GistApi = GistApi()
) {

    fun getGists() {
        authenticationManager.getSingleOrDefaultAccount(project)?.let { githubAccount ->

            gistApi.getGists(project, githubAccount, object : OnGotGistsListener {

                override fun onSuccess(results: List<GithubGist>) {
                    view.showGists(results)
                }

                override fun onError(error: Throwable) {
                    GithubNotifications.showError(project, "Error getting gists!", error)
                }
            })
        }
    }

    fun fetchProfileInfo() {
        authenticationManager.getSingleOrDefaultAccount(project)?.let { githubAccount ->

            gistApi.fetchProfileInfo(project, githubAccount, object : OnGotUserProfileDataListener {

                override fun onSuccess(image: Image?, user: GithubAuthenticatedUser) {
                    view.displayUserData(
                        image?.let { image.getScaledInstance(100, 100, Image.SCALE_AREA_AVERAGING) },
                        user
                    )
                }

                override fun onError(error: Throwable) {
                    error.printStackTrace()
                    view.hideProfileInfoSection()
                }
            })
        }
    }

    fun onItemClicked(githubGist: GithubGist) {
        BrowserUtil.browse("https://gist.github.com/${githubGist.user?.login}/${githubGist.id}")
    }

    private fun getScaledImage(srcImg: Image, width: Int, height: Int): Image? {
        val resizedImg = ImageUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics2D = resizedImg.createGraphics()
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics2D.drawImage(srcImg, 0, 0, width, height, null)
        graphics2D.dispose()
        return resizedImg
    }

    fun goToUsersGitHubPage(user: GithubAuthenticatedUser) {
        BrowserUtil.browse(user.htmlUrl)
    }
}