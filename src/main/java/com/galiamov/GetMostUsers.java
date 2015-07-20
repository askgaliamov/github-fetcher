package com.galiamov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galiamov.integration.github.GithubClient;
import com.galiamov.integration.github.MostUser;

import java.util.List;

public class GetMostUsers {

    public static void main(String[] args) throws JsonProcessingException {
        GithubClient githubClient = new GithubClient();
        String arg = args[0];
        List<MostUser> mostUsers = githubClient.getMostUsers(arg);
        System.out.print(toJSON(mostUsers));
    }

    private static String toJSON(List<MostUser> mostUsers) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(mostUsers);
    }

}
