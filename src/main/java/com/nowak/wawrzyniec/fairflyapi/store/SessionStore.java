package com.nowak.wawrzyniec.fairflyapi.store;

import java.util.Optional;

public interface SessionStore {

    Optional<Session> getSession();

    boolean isValid(Session session);

    void releaseSession(Session session);

    void close();
}
