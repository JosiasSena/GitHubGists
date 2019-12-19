package create_gist_action.presenter

import api.GistApi
import api.OnGistCreatedListener
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import create_gist_action.view.CreateGistAction
import org.jetbrains.plugins.github.api.data.request.GithubGistRequest.FileContent
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.util.GithubNotifications
import org.jetbrains.plugins.github.util.GithubSettings
import java.awt.datatransfer.StringSelection

/**
 * @author Josias Sena
 * @since v1.0
 */
class CreateGistPresenter(
    private val view: CreateGistAction,
    private val githubSettings: GithubSettings,
    private val githubAuthenticationManager: GithubAuthenticationManager,
    private val copyPasteManager: CopyPasteManager,
    private val gistApi: GistApi
) {

    fun createGist(anActionEvent: AnActionEvent, gistParams: CreateGistParams) {

        githubSettings.apply {
            isPrivateGist = gistParams.isPrivate
            isOpenInBrowserGist = gistParams.isShouldOpenInBrowser
            isCopyURLGist = gistParams.isCopyUrl
        }

        val githubAccount = githubAuthenticationManager.getAccounts().first()

        anActionEvent.project?.let { project ->
            val contents = listOf(FileContent(gistParams.fileName, gistParams.gist))

            gistApi
                .createGist(project, githubAccount, contents, gistParams, object :
                    OnGistCreatedListener {
                    override fun onSuccess(gistUrl: String?) {
                        onGistCreatedSuccessfully(
                            gistUrl,
                            gistParams.isCopyUrl,
                            gistParams.isShouldOpenInBrowser,
                            project
                        )
                    }

                    override fun onError(error: Throwable) {
                    }
                })
        }
    }

    private fun onGistCreatedSuccessfully(
        gistUrl: String?,
        isCopyUrl: Boolean,
        isShouldOpenInBrowser: Boolean,
        project: Project
    ) {
        gistUrl?.let {
            if (isCopyUrl) {
                val stringSelection = StringSelection(it)
                copyPasteManager.setContents(stringSelection)
            }

            if (isShouldOpenInBrowser) {
                BrowserUtil.browse(it)
            } else {
                GithubNotifications.showInfoURL(project, "Gist Created Successfully", "Your gist url", it)
            }
        }
    }

    fun handleAction(anActionEvent: AnActionEvent) {
        if (isLoggedInToGitHub()) {
            println("Logged in to GitHub!")

            with(githubSettings) {
                view.showCreateGistDialog(anActionEvent, isPrivateGist, isOpenInBrowserGist, isCopyURLGist)
            }

        } else {
            println("Not logged in to GitHub!")

            requestToLogIn(anActionEvent)
        }
    }

    private fun requestToLogIn(anActionEvent: AnActionEvent) {
        anActionEvent.project?.let {
            githubAuthenticationManager.requestNewAccount(it, null)
        }
    }

    private fun isLoggedInToGitHub(): Boolean = githubAuthenticationManager.hasAccounts()

    fun getGist(anActionEvent: AnActionEvent): String {
        val editor = anActionEvent.dataContext.getData(PlatformDataKeys.EDITOR)

        return (editor?.selectionModel?.selectedText ?: editor?.document?.text).orEmpty()
    }

    fun getCurrentFileName(anActionEvent: AnActionEvent): String? {
        val fileEditor = anActionEvent.dataContext.getData(PlatformDataKeys.FILE_EDITOR)
        val virtualFile = fileEditor?.file

        return virtualFile?.name
    }
}