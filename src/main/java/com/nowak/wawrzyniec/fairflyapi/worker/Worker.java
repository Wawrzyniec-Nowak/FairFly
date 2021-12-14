package com.nowak.wawrzyniec.fairflyapi.worker;

public interface Worker extends AutoCloseable {

    void doTheJob();

    void close();
}
