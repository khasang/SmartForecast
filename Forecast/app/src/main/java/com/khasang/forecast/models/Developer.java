package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.List;

public class Developer {

    private String name;
    private String description;
    private int resId;
    private List<Link> links;

    public Developer(String name, String description, int resId) {
        this.name = name;
        this.description = description;
        this.resId = resId;
        this.links = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getResId() {
        return resId;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void addLink(Link link) {
        this.links.add(link);
    }
}
