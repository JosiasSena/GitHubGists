package create_gist_action.presenter

import api.GistApi
import api.OnGistCreatedListener
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import create_gist_action.view.CreateGistAction
import org.jetbrains.plugins.github.api.data.request.GithubGistRequest.FileContent
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.util.GithubNotifications
import org.jetbrains.plugins.github.util.GithubSettings
import java.awt.datatransfer.StringSelection
import java.io.IOException

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

            val file = anActionEvent.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)
            val files = anActionEvent.dataContext.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)

            if (file == null && files == null) {
                return
            }

            file?.let {
                if (file.isDirectory || files?.size != 1) {
                    // Let the default github plugin do its thing
                    anActionEvent.actionManager.getAction("Github.Create.Gist").actionPerformed(anActionEvent)
                } else {
                    if (file.fileType.isBinary) {
                        anActionEvent.project?.let {
                            GithubNotifications.showWarning(it, "Can't create Gist", "Can't upload binary file: $file")
                        }
                    }

                    // We handle it
                    with(githubSettings) {
                        val gist = getGist(anActionEvent, file)

                        view.showCreateGistDialog(
                            anActionEvent,
                            file.name,
                            gist,
                            isPrivateGist,
                            isOpenInBrowserGist,
                            isCopyURLGist
                        )
                    }
                }
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

    private fun getGist(anActionEvent: AnActionEvent, file: VirtualFile): String {
        val editor = anActionEvent.dataContext.getData(PlatformDataKeys.EDITOR)

        val content = ReadAction.compute<String, RuntimeException> {
            try {
                return@compute FileDocumentManager.getInstance().getDocument(file)?.text
                    ?: String(file.contentsToByteArray(), file.charset)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
                return@compute null
            }
        }

        return (editor?.selectionModel?.selectedText ?: content).orEmpty()
    }
}