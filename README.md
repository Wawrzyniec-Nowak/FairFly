# FairFly

The idea of this project was to implement optimized way of working with 3rd party API. 
Currently, the sessions are stored in the memory store which is destroyed when application shuts down. Another approach would be
to run the Store as the separate application or even better to store these sessions in some persistent, reliable storage. 
All the sessions are initialized in parallel. The SessionManager is just a proxy that calls SessionStore methods
but the idea is to be able to inject different types of Stores into the Manager and in that case extend Manager API if
needed. The Store returns Optional<Session> and moves responsibility of the lacking session to the Worker. In the current
implementation Workers abort job when no session is available but such case may be handled differently (retry operation or
keep calling the Store till the session is free). Session object is a basic one and does not contain any extraordinary logic.

#### 1. Sessions are limited to a pool of X where X is passed as a property <i>session.store.size</i>
#### 2. Sessions are reused between worker jobs that is shown within com.nowak.wawrzyniec.fairflyapi.worker.WorkerTest.shouldHandleMultipleWorkersRunningInParallel test
#### 3. Do not lose sessions. Both SessionManager and SessionStore implements Autocloseable which will close all the resources even when they are not closed gracefully
#### 4. A worker needs to always have a fresh session. When a session is retrieved from a pool it is verified if it is valid using API call com.nowak.wawrzyniec.fairflyapi.store.InMemorySessionStore.getSession#48

### Bonus
#### 5.  
In order not to lose idle sessions it would be good to implement some kind of background task that would naively call keepAlive API method on all the pool's sessions every < 15 minutes. 
Better approach would be to have as many deamon threads as sessions. Each session would have its own thread that would call keepAlive every 15 minutes if it is idle.
Otherwise, whenever the session is used it would reset its deamon timer. Thanks to that architecture there would be less API calls yet more background threads running.

#### 6.
If understood correctly current implementation allows injection of different stores types because of using abstraction. This version uses in memory store but
there is possibility to inject any store (caching, persistent, virtual in cloud) as soon as it implements all required methods.
In case when multiple stores have to be used within the same manager then the Manager requires change to accept the collection of stores.
Secondly, the session retrieval would require credentials to choose the correct store. Finally, on close all the stores will have to be closed. 
