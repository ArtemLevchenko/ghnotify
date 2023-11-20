package com.ffoggy.ui;

import com.ffoggy.model.GHNotificationEntity;
import com.ffoggy.model.GHSystemEntity;
import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import com.sshtools.twoslices.ToasterFactory;
import com.sshtools.twoslices.ToasterSettings;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class GitHubUI {
    private final TrayIcon trayIcon;
    private final GHSystemEntity systemEntity;

    public GitHubUI(GHSystemEntity systemEntity) {
        this.systemEntity = systemEntity;
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit()
                    .createImage(getClass().getResource(this.systemEntity.getIconUIPath()));

            trayIcon = new TrayIcon(image, this.systemEntity.getApplicationName());
            trayIcon.setImageAutoSize(true);
            ToasterFactory.setSettings(new ToasterSettings().setParent(trayIcon));
            tray.add(trayIcon);

        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMenu(String login, List<GHNotificationEntity> repos) {
        PopupMenu popup = new PopupMenu();

        MenuItem accountMI = new MenuItem(login);
        accountMI.addActionListener(e -> openInBrowser(this.systemEntity + login));

        Menu repositoriesMI = new Menu("repositories");
        repos
                .forEach(repo -> {
                    String name = !repo.getPullRequests().isEmpty()
                            ? String.format("(%d) %s", repo.getPullRequests().size(), repo.getRepository().getFullName())
                            : repo.getRepository().getFullName();
                    Menu repoSM = new Menu(name);

                    MenuItem openInBrowser = new MenuItem("Open in browser");
                    openInBrowser.addActionListener(e ->
                            openInBrowser(repo.getRepository().getHtmlUrl().toString())
                    );

                    repoSM.add(openInBrowser);

                    if (!repo.getPullRequests().isEmpty()) {
                        repoSM.addSeparator();
                    }

                    repo.getPullRequests()
                            .forEach(pr -> {
                                MenuItem prMI = new MenuItem(pr.getTitle());
                                prMI.addActionListener(e ->
                                        openInBrowser(pr.getHtmlUrl().toString())
                                );
                                repoSM.add(prMI);
                            });

                    repositoriesMI.add(repoSM);
                });

        popup.add(accountMI);
        popup.addSeparator();
        popup.add(repositoriesMI);

        trayIcon.setPopupMenu(popup);
    }

    private void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void showNotification(String title, String text) {
        //trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
        Toast.toast(ToastType.INFO, title, text);
    }
}
