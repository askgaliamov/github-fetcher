package com.galiamov.integration.github;

import java.util.List;

public class MostUser {

    private String login;
    private List<String> repositories;

    public MostUser(String login) {
        this.login = login;
    }

    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public String getName() {
        return login;
    }

    public List<String> getRepositories() {
        return repositories;
    }
}
