package api

/**
 * @author Josias Sena
 * @since v1.0
 */
interface OnGistCreatedListener {

    fun onSuccess(gistUrl: String?)

    fun onError(error: Throwable)
}