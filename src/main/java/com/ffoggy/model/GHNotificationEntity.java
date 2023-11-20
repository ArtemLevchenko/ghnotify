package com.ffoggy.model;

import lombok.Builder;
import lombok.Getter;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;

import java.util.List;

@Builder(builderClassName = "Builder")
@Getter
public class GHNotificationEntity {
    private GHRepository repository;
    private List<GHPullRequest> pullRequests;
}
