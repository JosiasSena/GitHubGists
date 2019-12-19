package gists_tool_window.view.list

import org.jetbrains.plugins.github.api.data.GithubGist
import javax.swing.AbstractListModel

/**
 * @author Josias Sena
 * @since v1.0
 */
class GithubGistModel(nonMutableModels: List<GithubGist>) : AbstractListModel<GithubGist>() {

    private val models = nonMutableModels.toMutableList()

    override fun getElementAt(index: Int): GithubGist = models[index]

    override fun getSize(): Int = models.size

    fun addGist(githubGist: GithubGist) {
        models.add(githubGist)
    }
}