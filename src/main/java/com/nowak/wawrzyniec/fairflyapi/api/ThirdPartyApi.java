package com.nowak.wawrzyniec.fairflyapi.api;

import com.nowak.wawrzyniec.fairflyapi.store.Session;

public interface ThirdPartyApi {

    Session createSession();

    void closeSession(Session session);

    void keepAlive(Session session);

    boolean ping(Session session);
}
