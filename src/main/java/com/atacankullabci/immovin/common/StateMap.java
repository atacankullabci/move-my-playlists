package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("StateMap")
public class StateMap {
    @Id
    private String state;
    private Boolean validity;

    public StateMap() {
    }

    public StateMap(String state, Boolean validity) {
        this.state = state;
        this.validity = validity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getValidity() {
        return validity;
    }

    public void setValidity(Boolean validity) {
        this.validity = validity;
    }

    @Override
    public String toString() {
        return "StateMap{" +
                "state='" + state + '\'' +
                ", validity=" + validity +
                '}';
    }
}
