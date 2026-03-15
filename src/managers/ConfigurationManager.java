package managers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ConfigurationManager {

    public static final ConcurrentMap<String, ?> generalSettings = new ConcurrentHashMap<>();
}
