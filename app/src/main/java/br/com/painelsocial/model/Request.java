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
    private int minusCount;
    private int plusCount;
    private Integer currentVote;

    private String[] images;
    private Comment[] comments;

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

    public int getMinusCount() {
        return minusCount;
    }

    public void setMinusCount(int minusCount) {
        this.minusCount = minusCount;
    }

    public int getPlusCount() {
        return plusCount;
    }

    public void setPlusCount(int plusCount) {
        this.plusCount = plusCount;
    }

    public Integer getCurrentVote() {
        return currentVote;
    }

    public void setCurrentVote(Integer currentVote) {
        this.currentVote = currentVote;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }
}
