package com.atacankullabci.immovin.common;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("InProgressMap")
public class InProgressMap implements Serializable {

    @Id
    private String userId;
    private Boolean isInProgress;

    public InProgressMap() {
    }

    public InProgressMap(String userId, Boolean isInProgress) {
        this.userId = userId;
        this.isInProgress = isInProgress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getInProgress() {
        return isInProgress;
    }

    public void setInProgress(Boolean inProgress) {
        isInProgress = inProgress;
    }

    @Override
    public String toString() {
        return "InProgressMap{" +
                "userId='" + userId + '\'' +
                ", isInProgress=" + isInProgress +
                '}';
    }
}
