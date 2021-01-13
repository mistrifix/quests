package net.mistrifix.main;

import net.mistrifix.main.base.quest.QuestUtils;
import net.mistrifix.main.base.user.User;
import net.mistrifix.main.base.user.UserUtils;
import net.mistrifix.main.command.QuestCommand;
import net.mistrifix.main.data.MySQL;
import net.mistrifix.main.data.PluginConfiguration;
import net.mistrifix.main.data.handler.DataHandler;
import net.mistrifix.main.data.handler.DataLoader;
import net.mistrifix.main.listener.PlayerFish;
import net.mistrifix.main.listener.PlayerInteract;
import net.mistrifix.main.listener.PlayerJoin;
import net.mistrifix.main.listener.PlayerQuit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuestPlugin extends JavaPlugin {

    private static QuestPlugin instance;

    public PluginConfiguration configuration;
    public MySQL mysql;

    public void onLoad() {
        instance = this;
    }

    public void onEnable() {
        this.configuration = PluginConfiguration.getInstance();
        this.configuration.loadConfiguration();
        this.mysql = new MySQL();
        this.saveDefaultConfig();

        this.getCommand("quests").setExecutor(new QuestCommand());

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new PlayerJoin(), this);
        pm.registerEvents(new PlayerQuit(), this);
        pm.registerEvents(new PlayerFish(), this);
        pm.registerEvents(new PlayerInteract(), this);

        DataLoader.checkDatabase();
        DataLoader.loadAll();

       Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DataHandler(),
                configuration.data_saveDelay * 20 * 60, configuration.data_saveDelay * 20 * 60);

        System.out.println(" ");
        System.out.println(" QUESTS (v" + this.getDescription().getVersion() + ")");
        System.out.println("  Wczytano dane |(  " + UserUtils.getUsers().size() + "  )| graczy");
        System.out.println("  Wczytano dane |(  " + QuestUtils.getQuests().size() + "  )| zadan");
        System.out.println(" ");


    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        for (User user : UserUtils.getUsers()) {
            user.save();
        }
    }

    public static QuestPlugin getInstance() {
        return (instance != null ? instance : new QuestPlugin());
    }
}