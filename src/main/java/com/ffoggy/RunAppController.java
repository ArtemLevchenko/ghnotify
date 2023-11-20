package com.ffoggy;

import com.ffoggy.service.GitHubService;

public class RunAppController {
    public static void main(String[] args) {
        GitHubService service = new GitHubService();
        service.initAndRun();
    }
}