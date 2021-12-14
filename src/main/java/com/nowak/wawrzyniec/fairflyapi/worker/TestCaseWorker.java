package com.nowak.wawrzyniec.fairflyapi.worker;

import com.nowak.wawrzyniec.fairflyapi.SessionManager;
import com.nowak.wawrzyniec.fairflyapi.store.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class TestCaseWorker implements Worker {

    private static final Logger log = LoggerFactory.getLogger(TestCaseWorker.class);
    private final SessionManager sessionManager;

    public TestCaseWorker(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void doTheJob() {
        Optional<Session> session = sessionManager.getSession();
        session.ifPresentOrElse(this::job, () -> log.warn("Cannot obtain session - aborting"));
    }

    @Override
    public void close() {
        sessionManager.close();
    }

    private void job(Session session) {
        log.info("Doing anything needed with the session {}", session);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            log.error("Error occurred while worker was working", e);
        }
    }
}
