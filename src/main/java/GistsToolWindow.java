import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import gists_tool_window.presenter.GistsToolWindowPresenter;
import gists_tool_window.view.list.GistCellRenderer;
import gists_tool_window.view.list.GithubGistModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.github.api.data.GithubAuthenticatedUser;
import org.jetbrains.plugins.github.api.data.GithubGist;
import utils.SimpleMouseListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author Josias Sena
 * @since v1.0
 */
public class GistsToolWindow {

    private final GistsToolWindowPresenter presenter;
    private GithubGistModel model;
    private JPanel content;
    private JList myGists;
    private JLabel labelName;
    private JLabel labelEmail;
    private JLabel labelLogin;
    private JLabel imagePlaceHolder;
    private JPanel profileInfoPanel;
    private JButton labelRefresh;

    public GistsToolWindow(final Project project, final ToolWindow toolWindow) {
        myGists.setCellRenderer(new GistCellRenderer());
        presenter = new GistsToolWindowPresenter(this, project);

        init(toolWindow);
    }

    private void init(final ToolWindow toolWindow) {
        myGists.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        myGists.addListSelectionListener(selectionEvent -> {
            if (!selectionEvent.getValueIsAdjusting()) {
                presenter.onItemClicked(((JList<GithubGist>) selectionEvent.getSource()).getSelectedValue());
            }
        });

        presenter.fetchProfileInfo();
        presenter.getGists();
    }

    public JComponent getContent() {
        return content;
    }

    public void displayUserData(@Nullable final Image image, @NotNull final GithubAuthenticatedUser user) {
        if (image != null) {
            imagePlaceHolder.setMaximumSize(new Dimension(100, 100));
            imagePlaceHolder.setIcon(new ImageIcon(image));
            imagePlaceHolder.setVisible(true);
        }

        labelName.setText(user.getName());
        labelLogin.setText(user.getLogin());
        labelEmail.setText(user.getEmail());

        profileInfoPanel.addMouseListener(new SimpleMouseListener() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                presenter.goToUsersGitHubPage(user);
            }
        });

        labelRefresh.addMouseListener(new SimpleMouseListener() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                presenter.getGists();
            }
        });
    }

    public void hideProfileInfoSection() {
        profileInfoPanel.setVisible(false);
    }

    public void showGists(@NotNull final List<GithubGist> gists) {
        model = new GithubGistModel(gists);
        myGists.setModel(model);

        gists.forEach(githubGist -> model.addGist(githubGist));
    }
}
