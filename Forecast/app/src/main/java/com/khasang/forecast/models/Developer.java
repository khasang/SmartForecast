package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.List;

public class Developer {

    private String name;
    private Image image;
    private int descriptionResId;
    private List<Link> links;

    public Developer(String name, Image image, int descriptionResId) {
        this.name = name;
        this.image = image;
        this.descriptionResId = descriptionResId;
        this.links = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public int getDescriptionResId() {
        return descriptionResId;
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
