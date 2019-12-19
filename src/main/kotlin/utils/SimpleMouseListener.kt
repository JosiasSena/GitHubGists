package utils

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * A simple MouseListener class that overrides the default [MouseListener] methods by default.
 *
 * Allows for simple and cleaner implementation.
 *
 * @author Josias Sena
 * @since v1.0
 */
open class SimpleMouseListener : MouseListener {

    override fun mouseReleased(e: MouseEvent?) {
        // override
    }

    override fun mouseEntered(e: MouseEvent?) {
        // override
    }

    override fun mouseClicked(e: MouseEvent?) {
        // override
    }

    override fun mouseExited(e: MouseEvent?) {
        // override
    }

    override fun mousePressed(e: MouseEvent?) {
        // override
    }
}