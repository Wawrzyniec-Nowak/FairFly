package com.nowak.wawrzyniec.fairflyapi.store;

import com.nowak.wawrzyniec.fairflyapi.api.ThirdPartyApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;

@Service
public class InMemorySessionStore implements SessionStore {

    private static final Logger log = LoggerFactory.getLogger(InMemorySessionStore.class);

    private final ThirdPartyApi api;
    private final ConcurrentLinkedDeque<Session> pool;
    private final ConcurrentLinkedDeque<Session> usedSessions = new ConcurrentLinkedDeque<>();

    @Autowired
    public InMemorySessionStore(ThirdPartyApi api, Properties properties) {
        this.api = api;
        this.pool = new ConcurrentLinkedDeque<>();
        int poolSize = Integer.parseInt(properties.getProperty("session.store.size", "10"));

        List<CompletableFuture<Void>> pendingSessions = new LinkedList<>();
        for (int i = 0; i < poolSize; i++) {
            CompletableFuture<Void> pendingSession = CompletableFuture.supplyAsync(api::createSession).thenAccept(pool::add);
            pendingSessions.add(pendingSession);
        }
        try {
            CompletableFuture.allOf(pendingSessions.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Cannot initialize sessions pool", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public synchronized Optional<Session> getSession() {
        Session session = pool.poll();
        if (session == null) {
            return Optional.empty();
        }
        if (isValid(session)) {
            usedSessions.add(session);
            return Optional.of(session);
        }
        api.closeSession(session);
        usedSessions.remove(session);
        pool.remove(session);

        session = api.createSession();
        pool.add(session);
        usedSessions.add(session);
        return Optional.of(session);
    }

    @Override
    public boolean isValid(Session session) {
        return api.ping(session);
    }

    @Override
    public synchronized void releaseSession(Session session) {
        usedSessions.remove(session);
        pool.add(session);
    }

    @Override
    public void close() {
        Iterator<Session> iterator = usedSessions.iterator();
        while (iterator.hasNext()) {
            releaseSession(iterator.next());
        }
    }
}
