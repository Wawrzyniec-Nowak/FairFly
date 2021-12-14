package com.nowak.wawrzyniec.fairflyapi;

import com.nowak.wawrzyniec.fairflyapi.store.SessionStore;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SessionManagerTest {

    private final SessionStore store = mock(SessionStore.class);

    private final SessionManager sessionManager = new SessionManager(store);

    @Test
    void shouldCallStoreToGetSession() {
        sessionManager.getSession();

        verify(store, times(1)).getSession();
    }

    @Test
    void shouldCloseStoreOnShutdown() {
        sessionManager.close();

        verify(store, times(1)).close();
    }
}