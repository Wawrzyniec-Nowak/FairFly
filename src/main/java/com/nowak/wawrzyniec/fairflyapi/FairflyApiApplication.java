package com.nowak.wawrzyniec.fairflyapi;

import com.nowak.wawrzyniec.fairflyapi.worker.TestCaseWorker;
import com.nowak.wawrzyniec.fairflyapi.worker.Worker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FairflyApiApplication implements CommandLineRunner {

    private final SessionManager sessionManager;

    public FairflyApiApplication(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public static void main(String[] args) {
        SpringApplication.run(FairflyApiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try (Worker worker = new TestCaseWorker(sessionManager)) {
            worker.doTheJob();
        }
    }
}
