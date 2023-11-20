package com.ffoggy.model;

import lombok.Builder;
import lombok.Getter;
import org.kohsuke.github.GitHub;

@Builder(builderClassName = "Builder")
@Getter
public class GHConfiguration {
    private GitHub gitHub;
    private String login;
    private GHSystemEntity systemEntity;
}
