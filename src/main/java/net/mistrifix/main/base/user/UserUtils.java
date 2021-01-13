package net.mistrifix.main.base.user;

import net.mistrifix.main.base.user.User;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserUtils {

    private static final Map<UUID, User> users = new HashMap<>();

    public static void addUser(User user)
    {
        users.put(user.getUniqueId(), user);
    }

    public static void removeUser(User user)
    {
        users.remove(user.getUniqueId(), user);
    }

    public static void refresh()
    {
        users.clear();
    }

    public static List<User> getUsers()
    {
        return new ArrayList<>(users.values());
    }

    public static User getUser(UUID uuid)
    {
        return users.get(uuid);
    }

    public static User getUser(Player player)
    {
        return getUser(player.getUniqueId());
    }

    public static User create(String name, UUID uuid)
    {
        return new User(name, uuid);
    }

    public static Set<String> getNames(Collection<User> users) {
        return users.stream().map(User::getName).collect(Collectors.toSet());
    }

    public static Set<User> getUsers(Collection<UUID> names) {
        return names.stream().map(User::get).collect(Collectors.toSet());
    }

    
}
