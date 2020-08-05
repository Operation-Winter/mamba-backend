package za.co.armandkamffer.mamba.Models.Planning;

import java.util.UUID;

public class User {
    public UUID identifier;
    public String name;

    public User(String name) {
        this.name = name;
        identifier = UUID.randomUUID();
    }
}