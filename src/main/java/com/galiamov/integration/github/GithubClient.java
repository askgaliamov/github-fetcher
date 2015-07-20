package com.galiamov.integration.github;

import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedSearchIterable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.kohsuke.github.GHUserSearchBuilder.Sort.REPOSITORIES;

public class GithubClient {

    private static final int USERS_TO_GET = 10;
    private static final int THREADS = 10;
    private static final String GITHUB_SEARCH_TYPE_USER = "user";
    private GitHub github;

    public List<MostUser> getMostUsers(final String city) {
        try {

            github = GitHub.connectAnonymously();

            List<String> users = getUsersLogins(city);

            List<CompletableFuture<MostUser>> relevanceFutures = users.stream().
                    map(site -> supplyAsync(() -> getRepositories(site), newFixedThreadPool(THREADS))).
                    collect(Collectors.<CompletableFuture<MostUser>> toList());

            CompletableFuture<List<MostUser>> allDone = sequence(relevanceFutures);

            return allDone.get();
        } catch (Exception e) {
            //log
        }
        return Collections.emptyList();
    }

    private static CompletableFuture<List<MostUser>> sequence(List<CompletableFuture<MostUser>> futures) {
        CompletableFuture<Void> allDoneFuture = allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(
                v -> futures.stream().map(CompletableFuture::join).collect(Collectors.<MostUser> toList())
        );
    }

    private List<String> getUsersLogins(String city) throws IOException {
        return github
                .searchUsers()
                .location(city)
                .type(GITHUB_SEARCH_TYPE_USER)
                .sort(REPOSITORIES)
                .list().asList().stream()
                .limit(USERS_TO_GET)
                .map(GHPerson::getLogin).collect(Collectors.toList());
    }

    private MostUser getRepositories(String login) {
        PagedSearchIterable<GHRepository> repositories = github.searchRepositories().user(login).list();
        MostUser user = new MostUser(login);
        List<String> repositoryNames =
                repositories.asList().stream().map(GHRepository::getName).collect(Collectors.toList());
        user.setRepositories(repositoryNames);
        return user;
    }

}
