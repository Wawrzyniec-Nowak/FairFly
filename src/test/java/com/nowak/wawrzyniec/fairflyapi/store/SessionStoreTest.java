package com.nowak.wawrzyniec.fairflyapi.store;

import com.nowak.wawrzyniec.fairflyapi.api.ThirdPartyApi;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SessionStoreTest {

    private SessionStore store;

    private ThirdPartyApi api;

    @BeforeEach
    void init() {
        Properties properties = new Properties();
        this.api = spy(new TestThirdPartyApi());
        this.store = new InMemorySessionStore(api, properties);
    }

    @Test
    void shouldReturnValidSession() {
        Optional<Session> session = store.getSession();

        assertTrue(session.isPresent());
        verify(api, times(1)).keepAlive(session.get());
    }

    @Test
    void shouldReleaseAndCloseInvalidSessionAndCreateNewOne() throws InterruptedException {
        Thread.sleep(1500);
        Optional<Session> session = store.getSession();

        assertTrue(session.isPresent());
        InOrder inOrder = inOrder(api);
        inOrder.verify(api, times(10)).createSession();
        inOrder.verify(api, times(1)).ping(any());
        inOrder.verify(api, times(1)).closeSession(any());
        inOrder.verify(api, times(1)).createSession();
    }

    @Test
    void shouldReturnOnlySessionsFromPool() {
        for (int i = 0; i < 20; i++) {
            if (i < 10) {
                assertTrue(store.getSession().isPresent());
            } else {
                assertFalse(store.getSession().isPresent());
            }
        }
    }

    @Test
    void shouldCallApiToCheckIfSessionIsValid() {
        Session session = new Session("id");
        store.isValid(session);

        verify(api, times(1)).ping(session);
    }

    @Test
    void shouldNotCloseSessionAfterReleasingIt() {
        Session session = store.getSession().orElseThrow();
        store.releaseSession(session);

        verify(api, times(0)).closeSession(session);
    }

    @Test
    void shouldReturnAllSessionsToThePool() {
        store.close();
        for (int i = 0; i < 10; i++) {
            assertNotNull(store.getSession());
        }
    }

    static class TestThirdPartyApi implements ThirdPartyApi {

        @Override
        public Session createSession() {
            return new Session(RandomStringUtils.random(10));
        }

        @Override
        public void closeSession(Session session) {

        }

        @Override
        public void keepAlive(Session session) {
            session.keepAlive();
        }

        @Override
        public boolean ping(Session session) {
            return session.getCreationTime().plus(1, ChronoUnit.SECONDS).isAfter(Instant.now());
        }
    }
}