package create_gist_action.view

import CreateGistDialog
import api.GistApi
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import create_gist_action.presenter.CreateGistParams
import create_gist_action.presenter.CreateGistPresenter
import create_gist_dialog.OnCreateGistClickListener
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager
import org.jetbrains.plugins.github.util.GithubSettings
import utils.getParentWindow

/**
 * @author Josias Sena
 * @since v1.0
 */
class CreateGistAction : AnAction() {

    private val githubSettings = GithubSettings.getInstance()
    private val githubAuthenticationManager = GithubAuthenticationManager.getInstance()
    private val githubApiRequestExecutorManager = GithubApiRequestExecutorManager.getInstance()

    private var presenter: CreateGistPresenter = CreateGistPresenter(
        this,
        githubSettings,
        githubAuthenticationManager,
        CopyPasteManager.getInstance(),
        GistApi(githubApiRequestExecutorManager)
    )

    override fun beforeActionPerformedUpdate(anActionEvent: AnActionEvent) {
        super.beforeActionPerformedUpdate(anActionEvent)
        anActionEvent.presentation.isEnabledAndVisible = true
    }

    override fun update(anActionEvent: AnActionEvent) {
        anActionEvent.presentation.isEnabledAndVisible = true
    }

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        presenter.handleAction(anActionEvent)
    }

    internal fun showCreateGistDialog(
        anActionEvent: AnActionEvent,
        isPrivate: Boolean,
        isOpenInBrowser: Boolean,
        isCopyUrl: Boolean
    ) {
        with(
            CreateGistDialog(
                presenter.getCurrentFileName(anActionEvent),
                presenter.getGist(anActionEvent),
                isPrivate,
                isOpenInBrowser,
                isCopyUrl,
                object : OnCreateGistClickListener {

                    override fun onOkClicked(createGistDialog: CreateGistDialog) {

                        with(createGistDialog) {
                            presenter.createGist(
                                anActionEvent,
                                CreateGistParams(
                                    filenameTextField.text,
                                    descriptionTextField.text,
                                    gistTextArea.text,
                                    privateGistCheckBox.isSelected,
                                    openInBrowserCheckBox.isSelected,
                                    copyURLCheckBox.isSelected
                                )
                            )

                            dispose()
                        }
                    }

                    override fun onCancelClicked(createGistDialog: CreateGistDialog) {
                        createGistDialog.dispose()
                    }
                })
        ) {
            pack()
            setLocationRelativeTo(anActionEvent.project?.getParentWindow())
            isVisible = true
        }
    }
}