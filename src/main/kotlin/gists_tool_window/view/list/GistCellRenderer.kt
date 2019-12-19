package gists_tool_window.view.list

import org.jetbrains.plugins.github.api.data.GithubGist
import java.awt.Color
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.JEditorPane
import javax.swing.JList
import javax.swing.ListCellRenderer

/**
 * @author Josias Sena
 * @since v1.0
 */
class GistCellRenderer : JEditorPane(), ListCellRenderer<GithubGist> {

    override fun getListCellRendererComponent(
        list: JList<out GithubGist>?,
        githubGist: GithubGist?,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        contentType = "text/html"
        border = BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1, true),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
            )
        )

        val file = githubGist?.files?.first()

        val description = if (!githubGist?.description.isNullOrEmpty()) {
            "<p style=\"color:#586069; font-size:9px;\"> - ${githubGist?.description}</p>"
        } else {
            ""
        }

        text = """
                <p>${githubGist?.user?.login} / <b style="color:#0366d6; font-size:12px;">${file?.filename}</b> </p>
                </br>
                $description
                </br>
                <p style="color:#0366d6;">See more...</p>
            """.trimIndent()

        super.validate()
        return this
    }
}