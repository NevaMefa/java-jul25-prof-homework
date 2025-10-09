package ru.otus.crm.model;

public class User {
    private long id;
    private String name;
    private String login;
    private String password;
    private boolean isAdmin;


    public User(long id, String name, String login, String password, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
