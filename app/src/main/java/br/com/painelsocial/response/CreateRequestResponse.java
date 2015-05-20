package br.com.painelsocial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by I837119 on 19/05/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRequestResponse {

    private Request[] data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Request {

        private Double latitude;
        private Double longitude;
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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
    }

    public Request[] getData() {
        return data;
    }

    public void setData(Request[] data) {
        this.data = data;
    }
}
