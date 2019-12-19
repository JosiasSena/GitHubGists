package gists_tool_window.presenter

import org.jetbrains.plugins.github.api.data.GithubGist

/**
 * @author Josias Sena
 * @since v1.0
 */
interface OnGotGistsListener {

    fun onSuccess(results: List<GithubGist>)

    fun onError(error: Throwable)
}