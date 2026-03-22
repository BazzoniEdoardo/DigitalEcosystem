package managers;

import java.util.concurrent.atomic.AtomicInteger;

public class EntityManager {

    private static final AtomicInteger id = new AtomicInteger(0);

    public static int getCurrentId() {
        return id.get();
    }

    public static int nextId() {
        return id.incrementAndGet();
    }

}
