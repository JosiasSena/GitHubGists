package gists_tool_window.view

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import org.jetbrains.plugins.github.authentication.GithubAuthenticationManager

/**
 * @author Josias Sena
 * @since v1.0
 */
class GistsToolWindowCondition : Condition<Project> {

    override fun value(t: Project?): Boolean = GithubAuthenticationManager.getInstance().hasAccounts()
}