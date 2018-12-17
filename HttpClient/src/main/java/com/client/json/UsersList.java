package com.client.json;

import java.util.List;

public class UsersList {
    private List<UserInfo> users;

    public UsersList(List<UserInfo> users) {
        this.users = users;
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}
