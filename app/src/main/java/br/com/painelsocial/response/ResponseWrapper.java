package br.com.painelsocial.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by I837119 on 19/05/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseWrapper<E> {

    private E data;

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    private E demand;

    public E getDemand() {
        return demand;
    }

    public void setDemand(E demand) {
        this.demand = demand;
    }
}
