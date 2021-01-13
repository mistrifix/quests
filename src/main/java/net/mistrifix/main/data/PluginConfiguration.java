package net.mistrifix.main.data;

import net.mistrifix.main.QuestPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfiguration {

    private static PluginConfiguration instance;

    public PluginConfiguration()
    {
        instance = this;
    }

    public String mysql_host;
    public String mysql_username;
    public String mysql_password;
    public String mysql_dbname;
    public String mysql_usertable;
    public int   mysql_port;

    public String npc_type;
    public String npc_name;
    public String npc_uuid;
    public String npc_world;
    public int npc_x;
    public int npc_y;
    public int npc_z;

    public int data_saveDelay;

    public String questDisplayMode;

    public void loadConfiguration()
    {
        FileConfiguration config = QuestPlugin.getInstance().getConfig();
        mysql_host      = config.getString("database.host");
        mysql_username  = config.getString("database.username");
        mysql_password  = config.getString("database.password");
        mysql_dbname    = config.getString("database.dbname");
        mysql_usertable = config.getString("database.usertable");
        mysql_port      = config.getInt("database.port");

        npc_type        = config.getString("npc.type");
        npc_name        = config.getString("npc.name");
        npc_uuid        = config.getString("npc.uuid");
        npc_world       = config.getString("npc.world");
        npc_x           = config.getInt("npc.x");
        npc_y           = config.getInt("npc.y");
        npc_z           = config.getInt("npc.z");

        data_saveDelay  = config.getInt("scheduler.saveDelay");
        questDisplayMode = config.getString("questDisplayMode");
    }

    public static PluginConfiguration getInstance()
    {
        return (instance != null ? instance : new PluginConfiguration());
    }

}
