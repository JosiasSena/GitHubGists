package create_gist_action.presenter

/**
 * @author Josias Sena
 * @since v1.0
 */
data class CreateGistParams(
    val fileName: String,
    val description: String,
    val gist: String,
    val isPrivate: Boolean,
    val isShouldOpenInBrowser: Boolean,
    val isCopyUrl: Boolean
)