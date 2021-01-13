package net.mistrifix.main.data.handler;


import net.mistrifix.main.QuestPlugin;
import net.mistrifix.main.base.quest.Quest;
import net.mistrifix.main.base.quest.QuestUtils;
import net.mistrifix.main.base.user.User;
import net.mistrifix.main.data.Database;
import net.mistrifix.main.data.MySQL;
import net.mistrifix.main.utils.ChatUtils;
import net.mistrifix.main.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DataLoader {

    public static final MySQL mysql = QuestPlugin.getInstance().mysql;

    public static void checkDatabase()
    {
        questFile();
        userTable(Database.getInstance());
    }

    private static void userTable(Database db)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists `" + mysql.usertable + "` (");
        sb.append("username text not null,");
        sb.append("uuid varchar(100) not null,");
        sb.append("quest text,");
        sb.append("progress int not null,");
        sb.append("completedQuests text,");
        sb.append("primary key (uuid));");
        db.executeUpdate(sb.toString());
    }

    private static void questFile()
    {
        File questPath = new File(QuestPlugin.getInstance().getDataFolder() + File.separator + "quests");
        if(!questPath.exists()) questPath.mkdirs();
    }

    public static void loadUsers()
    {
        try
        {
            ResultSet userResult = Database.getInstance().openConnection()
                    .prepareStatement("SELECT * FROM `" + mysql.usertable + "`").executeQuery();
            while(userResult.next())
            {
                String username =        userResult.getString("username");
                String uniqueId =        userResult.getString("uuid");
                String quest =           userResult.getString("quest");
                int progress =           userResult.getInt("progress");
                String completedQuests = userResult.getString("completedQuests");

                User user = User.create(username, UUID.fromString(uniqueId));
                user.setProgress(progress);
                user.applyChange();
            }
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public static void loadQuests()
    {
        File questPath = new File(QuestPlugin.getInstance().getDataFolder() + File.separator + "quests");
        if(!questPath.exists())
        {
            return;
        }
        for(File allFiles : Objects.requireNonNull(questPath.listFiles()))
        {
            FileConfiguration config = YamlConfiguration.loadConfiguration(allFiles);
            String id = config.getString("id");
            int order = config.getInt("order");
            String instruction = config.getString("instruction");
            String objective = config.getString("objective");
            List<String> rewards = config.getStringList("rewards");

            Quest quest = Quest.create(id, order);
            quest.setInstruction(instruction);
            quest.setObjectiveAsString(objective);
            quest.setRewards(ItemUtils.loadItemStackList(rewards));
            try
            {
                quest.setObjective(ItemUtils.parseItem(quest.getObjectiveAsString()));
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static void loadAll()
    {
        loadQuests();
        loadUsers();
    }
}
