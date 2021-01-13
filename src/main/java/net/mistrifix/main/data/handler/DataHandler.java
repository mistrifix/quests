package net.mistrifix.main.data.handler;

import net.mistrifix.main.QuestPlugin;
import net.mistrifix.main.base.quest.Quest;
import net.mistrifix.main.base.quest.QuestUtils;
import net.mistrifix.main.base.user.User;
import net.mistrifix.main.base.user.UserUtils;
import net.mistrifix.main.utils.ChatUtils;
import org.bukkit.Bukkit;

public class DataHandler implements Runnable{

    public void run()
    {
        for(Quest quest : QuestUtils.getQuests())
        {
            quest.save();
        }
        for(User user : UserUtils.getUsers())
        {
            user.save();
        }
        System.out.println(" ");
        System.out.println("        QUESTS (v" + QuestPlugin.getInstance().getDescription().getVersion() + ")");
        System.out.println("          Wczytano dane |(  " + UserUtils.getUsers().size() + "  )| graczy");
        System.out.println("          Wczytano dane |(  " + QuestUtils.getQuests().size() + "  )| zadan");
        System.out.println(" ");
    }
}
