import create_gist_dialog.OnCreateGistClickListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Josias Sena
 * @since v1.0
 */
public class CreateGistDialog extends JDialog {

    private JPanel contentPane;
    private JTextField tfFilename;
    private JTextField tfDescription;
    private JTextArea taGist;
    private JCheckBox cbPrivateGist;
    private JCheckBox cbOpenInBrowser;
    private JCheckBox cbCopyURL;
    private JButton buttonCancel;
    private JButton buttonOk;

    public CreateGistDialog(final String currentFileName,
                            final String gist,
                            final Boolean isPrivateGist,
                            final Boolean isShouldOpenInBrowser,
                            final Boolean isCopyUrl,
                            final OnCreateGistClickListener onCreateGistClickListener) {

        setContentPane(contentPane);
        setModal(true);
        setTitle("Create Gist");

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCreateGistClickListener.onCancelClicked(CreateGistDialog.this);
            }
        });

        setMinimumSize(new Dimension(500, 500));
        setMaximumSize(new Dimension(900, 900));

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(actionEvent -> onCreateGistClickListener.onCancelClicked(this),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        tfFilename.setText(currentFileName);
        taGist.setText(gist);

        cbPrivateGist.setSelected(isPrivateGist);
        cbOpenInBrowser.setSelected(isShouldOpenInBrowser);
        cbCopyURL.setSelected(isCopyUrl);

        buttonOk.addActionListener(actionEvent -> onCreateGistClickListener.onOkClicked(this));
        buttonCancel.addActionListener(actionEvent -> onCreateGistClickListener.onCancelClicked(this));
    }

    public JTextField getFilenameTextField() {
        return tfFilename;
    }

    public JTextField getDescriptionTextField() {
        return tfDescription;
    }

    public JTextArea getGistTextArea() {
        return taGist;
    }

    public JCheckBox getPrivateGistCheckBox() {
        return cbPrivateGist;
    }

    public JCheckBox getOpenInBrowserCheckBox() {
        return cbOpenInBrowser;
    }

    public JCheckBox getCopyURLCheckBox() {
        return cbCopyURL;
    }
}
