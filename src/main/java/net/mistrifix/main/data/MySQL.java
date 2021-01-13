package net.mistrifix.main.data;

import net.mistrifix.main.QuestPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class MySQL {

    public String host, username, password, database, usertable;
    public int port;

    public MySQL()
    {
        PluginConfiguration config = QuestPlugin.getInstance().configuration;

        host      = config.mysql_host;
        username  = config.mysql_username;
        password  = config.mysql_password;
        database  = config.mysql_dbname;
        usertable = config.mysql_usertable;
        port      = config.mysql_port;
    }
}
