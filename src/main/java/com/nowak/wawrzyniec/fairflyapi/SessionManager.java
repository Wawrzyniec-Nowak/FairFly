package com.nowak.wawrzyniec.fairflyapi;

import com.nowak.wawrzyniec.fairflyapi.store.Session;
import com.nowak.wawrzyniec.fairflyapi.store.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionManager implements AutoCloseable {

    private final SessionStore store;

    @Autowired
    public SessionManager(SessionStore store) {
        this.store = store;
    }

    public Optional<Session> getSession() {
        return store.getSession();
    }

    @Override
    public void close() {
        store.close();
    }
}
