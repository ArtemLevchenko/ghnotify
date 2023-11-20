package com.ffoggy.service;

import com.ffoggy.model.GHConfiguration;
import com.ffoggy.model.GHNotificationEntity;
import com.ffoggy.model.GHSystemEntity;
import com.ffoggy.ui.GitHubUI;
import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class GitHubService {
    private final GHConfiguration gitHubConfiguration;
    private final GitHubUI gui;

    public GitHubService() {
        try {
            GitHub gitHub = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();

            Yaml yaml = new Yaml();
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.yaml");
            GHSystemEntity systemEntity = yaml.loadAs(inputStream, GHSystemEntity.class);

            this.gitHubConfiguration = GHConfiguration
                    .builder()
                    .gitHub(gitHub)
                    .login(gitHub.getMyself().getLogin())
                    .systemEntity(systemEntity)
                    .build();
            this.gui = new GitHubUI(systemEntity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initAndRun() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    GHMyself myself = gitHubConfiguration.getGitHub().getMyself();
                    List<GHNotificationEntity> notificationEntities = getNotificationEntities(myself);

                    gui.setMenu(gitHubConfiguration.getLogin(), notificationEntities);
                    notifyUser(notificationEntities);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }, gitHubConfiguration.getSystemEntity().getDelay(), gitHubConfiguration.getSystemEntity().getPeriod());
    }

    private List<GHNotificationEntity> getNotificationEntities(GHMyself myself) throws IOException {

        return myself.getAllRepositories()
                .values()
                .stream()
                .map(repository -> {
                    try {
                        List<GHPullRequest> allPullRequests = repository.queryPullRequests()
                                .list()
                                .toList();

                        List<GHPullRequest> filteredByReviewIdList = allPullRequests
                                .stream()
                                .filter(pr -> {
                                    try {
                                        return pr.getRequestedReviewers()
                                                .stream()
                                                .anyMatch(ghUser -> ghUser.getLogin().equals(myself.getLogin()));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .toList();

                        return GHNotificationEntity
                                .builder()
                                .pullRequests(filteredByReviewIdList)
                                .repository(repository)
                                .build();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private void notifyUser(List<GHNotificationEntity> notificationEntities) {
        for (GHNotificationEntity notificationEntity : notificationEntities) {
            notificationEntity.getPullRequests().forEach(ghPullRequest -> gui.showNotification(
                    "New PR in " + ghPullRequest.getRepository().getFullName(),
                    ghPullRequest.getTitle()));
        }
    }
}
