package com.nowak.wawrzyniec.fairflyapi.worker;

import com.nowak.wawrzyniec.fairflyapi.SessionManager;
import com.nowak.wawrzyniec.fairflyapi.api.MockThirdPartyApi;
import com.nowak.wawrzyniec.fairflyapi.api.ThirdPartyApi;
import com.nowak.wawrzyniec.fairflyapi.store.InMemorySessionStore;
import com.nowak.wawrzyniec.fairflyapi.store.SessionStore;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

class WorkerTest {

    private final SessionManager sessionManager = mock(SessionManager.class);

    private final Worker worker = new TestCaseWorker(sessionManager);

    @Test
    void shouldCloseSessionManager() {
        worker.close();

        verify(sessionManager, times(1)).close();
    }

    @Test
    void shouldHandleMultipleWorkersRunningInParallel() throws InterruptedException {
        ThirdPartyApi api = spy(new MockThirdPartyApi());
        Properties properties = new Properties();
        properties.setProperty("session.store.size", "20");
        SessionStore sessionStore = new InMemorySessionStore(api, properties);
        SessionManager sessionManager = new SessionManager(sessionStore);

        ExecutorService executorService = Executors.newFixedThreadPool(40);
        List<Callable<Void>> tasks = new LinkedList<>();
        for (int i = 0; i < 40; i++) {
            int id = i;
            Callable<Void> task = () -> {
                try (Worker worker = new WorkerImpl(sessionManager, id)) {
                    worker.doTheJob();
                }
                return null;
            };
            tasks.add(task);
        }
        executorService.invokeAll(tasks);

        executorService.shutdown();
        verify(api, times(20)).createSession();
    }

    static class WorkerImpl implements Worker {

        private static final Logger log = LoggerFactory.getLogger(WorkerImpl.class);
        private final SessionManager sessionManager;
        private final int id;

        WorkerImpl(SessionManager sessionManager, int id) {
            this.sessionManager = sessionManager;
            this.id = id;
        }

        @Override
        public void doTheJob() {
            log.info("Worker {}: Retrieved session {}", this.id, sessionManager.getSession());
        }

        @Override
        public void close() {
            sessionManager.close();
        }
    }
}