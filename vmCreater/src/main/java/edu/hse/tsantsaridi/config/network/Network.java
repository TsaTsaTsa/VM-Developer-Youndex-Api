package edu.hse.tsantsaridi.config.network;

import org.ini4j.Profile;

public class Network {
    private String name;
    private String id;

    public Network(Profile.Section section) {
        setName(section.get("name"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
