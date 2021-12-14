package com.nowak.wawrzyniec.fairflyapi.api;

import com.nowak.wawrzyniec.fairflyapi.store.Session;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class MockThirdPartyApi implements ThirdPartyApi {
    @Override
    public Session createSession() {
        try {
            Thread.sleep(1500); //simulate session creation
        } catch (InterruptedException ignored) {
        }
        return new Session(RandomStringUtils.random(10));
    }

    @Override
    public void closeSession(Session session) {
        try {
            Thread.sleep(1500); //simulate session closure
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void keepAlive(Session session) {

    }

    @Override
    public boolean ping(Session session) {
        return true;
    }
}
