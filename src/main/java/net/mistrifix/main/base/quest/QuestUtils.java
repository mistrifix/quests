package net.mistrifix.main.base.quest;

import net.mistrifix.main.base.user.User;
import net.mistrifix.main.base.user.UserUtils;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class QuestUtils {

    private static final List<Quest> quests = new ArrayList<>();

    public static void addQuest(Quest quest)
    {
        quests.add(quest);
    }

    public static void removeQuest(Quest quest)
    {
        quests.remove(quest);
    }

    public static Quest getQuest(String name)
    {
        for(Quest all : quests)
        {
            if(all.getId().equalsIgnoreCase(name))
            {
                return all;
            }
        }
        return null;
    }

    public static Quest getQuest(int order)
    {
        for(Quest all : quests)
        {
            if(all.getOrder() == order)
            {
                return all;
            }
        }
        return null;
    }

    public static List<Quest> getQuests()
    {
        return quests;
    }

    public static List<String> getQuestsNames(List<Quest> quests)
    {
        return quests.stream().map(Quest::getId).collect(Collectors.toList());
    }

    public static List<Quest> getQuests(List<String> questNames)
    {
        return questNames.stream().map(Quest::get).collect(Collectors.toList());
    }

    public static int calculateOrder()
    {
        if(quests.isEmpty()) return 1;
        int lastOrder = quests.get(quests.size() - 1).getOrder();
        return lastOrder + 1;
    }

    public static List<User> mostQuestsCompleted(int count)
    {
        int i = 0;
        final List<User> leaderboard = new ArrayList<>(UserUtils.getUsers());
        for(User user : UserUtils.getUsers())
        {
            if(user.getCompletedQuests().size() > 0)
            {
                leaderboard.add(user);
                i++;
                if(i == count) {
                    break;
                }
            }
        }
        return leaderboard;
    }

    public static final String defaultInstruction = "W tym zadaniu masz do zdobycia {AMOUNT}x «{ITEM}».";

}
