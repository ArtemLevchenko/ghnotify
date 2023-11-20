package com.ffoggy.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GHSystemEntity {
    private String applicationName;
    private String iconUIPath;
    private String serviceURL;
    private int delay;
    private int period;

    public GHSystemEntity() {
    }

    public GHSystemEntity(String applicationName, String iconUIPath, String serviceURL, int delay, int period) {
        this.applicationName = applicationName;
        this.iconUIPath = iconUIPath;
        this.serviceURL = serviceURL;
        this.delay = delay;
        this.period = period;
    }


}
