package br.com.painelsocial.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by I837119 on 02/06/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request implements Serializable {

    private String _id;
    private Double latitude;
    private Double longitude;
    private String description;

    private RequestImage[] images;

    public static class RequestImage {

        private String _id;
        private String image;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestImage[] getImages() {
        return images;
    }

    public void setImages(RequestImage[] images) {
        this.images = images;
    }
}
