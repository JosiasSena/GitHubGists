package gists_tool_window.presenter

import org.jetbrains.plugins.github.api.data.GithubAuthenticatedUser
import java.awt.Image

/**
 * @author Josias Sena
 * @since v1.0
 */
interface OnGotUserProfileDataListener {

    fun onSuccess(image: Image?, user: GithubAuthenticatedUser)

    fun onError(error: Throwable)
}