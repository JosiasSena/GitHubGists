package utils

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.ex.WindowManagerEx
import com.intellij.openapi.wm.impl.IdeFrameImpl
import java.awt.Window

/**
 * @see <a href="https://stackoverflow.com/a/50643996/10113150">See Stack overflow</a>
 */
fun Project.getParentWindow(): Window? {
    val windowManager = WindowManager.getInstance() as WindowManagerEx
    var window = windowManager.suggestParentWindow(this)

    window?.let {
        val focusedWindow: Window = windowManager.mostRecentFocusedWindow
        if (focusedWindow is IdeFrameImpl) {
            window = focusedWindow
        }
    }

    return window
}