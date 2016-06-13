package com.khasang.forecast.models;

import java.util.ArrayList;
import java.util.List;

public class Developer {

    private int nameResId;
    private Image image;
    private int descriptionResId;
    private List<Link> links;

    public Developer(int nameResId, Image image, int descriptionResId) {
        this.nameResId = nameResId;
        this.image = image;
        this.descriptionResId = descriptionResId;
        this.links = new ArrayList<>();
    }

    public int getNameResId() {
        return nameResId;
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
