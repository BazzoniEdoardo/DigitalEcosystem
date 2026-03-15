package managers;

public class EntityManager {

    private static int id = 0;

    public static int getCurrentId() {
        return id;
    }

    public static int nextId() {
        id++;
        return id;
    }

}
