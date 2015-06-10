package br.com.painelsocial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by I837119 on 09/06/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    private String _id;
    private String description;
    private String[] images;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
