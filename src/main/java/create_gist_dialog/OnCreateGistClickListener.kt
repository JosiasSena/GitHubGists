package create_gist_dialog

import CreateGistDialog

/**
 * @author Josias Sena
 * @since v1.0
 */
interface OnCreateGistClickListener {

    fun onOkClicked(createGistDialog: CreateGistDialog)

    fun onCancelClicked(createGistDialog: CreateGistDialog)

}