package com.nowak.wawrzyniec.fairflyapi.store;

import java.time.Instant;

public class Session {

    private final String id;

    private Instant creationTime;

    public Session(String id) {
        this.id = id;
        this.creationTime = Instant.now();
    }

    public void keepAlive() {
        this.creationTime = Instant.now();
    }

    public String getId() {
        return id;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", creationTime=" + creationTime +
                '}';
    }
}
