package net.tgoroshek.subscriptionsdemo.controller;

public interface Router {
    interface Users {
        String ROOT = "/users";
        String PASSWORD = ROOT + "/password";
        String REGISTER = ROOT + "/register";
        String BY_USERNAME = ROOT + "/{username}";
    }

    interface Subscriptions {
        String ROOT = Users.ROOT + "/subscriptions";
        String TOP = ROOT + "/top";
        String USER_WISE = Users.BY_USERNAME + "/subscriptions";
        String BY_ID = USER_WISE + "/{id}";
    }

}
