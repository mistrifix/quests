package net.mistrifix.main.base.user;

import net.mistrifix.main.QuestPlugin;
import net.mistrifix.main.base.quest.Quest;
import net.mistrifix.main.base.quest.QuestUtils;
import net.mistrifix.main.data.Database;
import net.mistrifix.main.data.MySQL;
import net.mistrifix.main.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

public class User implements Comparable<User>{


    private static final String INSERT_DB = "INSERT INTO `" + QuestPlugin.getInstance().mysql.usertable + "` (uuid, username, quest, progress, completedQuests) VALUES (?,?,?,?,?)";
    private static final String UPDATE_DB = "UPDATE " + QuestPlugin.getInstance().mysql.usertable + " SET username=?, quest=?, progress=?, completedQuests=? WHERE uuid=?";

    private String name;
    private UUID uuid;
    private Quest quest;
    private int progress;

    private List<Quest> completedQuests = new ArrayList<>();
    private List<ItemStack> temporaryInbox = new ArrayList<>();

    private boolean changed;

    User(String name, UUID uuid)
    {
        this.name = name;
        this.uuid = uuid;
        UserUtils.addUser(this);
    }

    public String getName()
    {
        return name;
    }

    public UUID getUniqueId()
    {
        return uuid;
    }

    public void sendMessage(String message)
    {
        Player player = Bukkit.getPlayer(this.uuid);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(this.uuid);
    }

    public Quest getQuest()
    {
        return quest;
    }

    public void setQuest(Quest quest)
    {
        this.quest = quest;
        applyChange();
    }

    public boolean hasQuest()
    {
        return this.quest != null;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
        applyChange();
    }

    public List<Quest> getCompletedQuests()
    {
        return completedQuests;
    }

    public void setCompletedQuests(List<Quest> quests)
    {
        this.completedQuests = quests;
        applyChange();
    }

    public boolean meetsRequirements(Quest quest)
    {
        if(this.quest != null)
        {
            return false; // user is during another quest
        }
        if(!completedQuests.isEmpty())
        {
            Quest last = this.completedQuests.get(this.completedQuests.size() - 1);
            if(quest.getOrder() + 1 != last.getOrder())
            {
                return false; // user must complete previous quest to take this one
            }
            if(quest.equals(last))
            {
                return false; // user cannot take the same quest twice
            }
        }
        return true;
    }

    public boolean hasInbox()
    {
        return !temporaryInbox.isEmpty();
    }

    public void addItem(ItemStack is)
    {
        temporaryInbox.add(is);
    }

    public void removeItem(ItemStack is)
    {
        temporaryInbox.remove(is);
    }

    public void clearInbox()
    {
        temporaryInbox.clear();
    }

    public List<ItemStack> getTemporaryInbox()
    {
        return temporaryInbox;
    }

    public String progressPercentage()
    {
        DecimalFormat dFormat = new DecimalFormat("##.##");
        double percent = (progress * 100.0f) / this.quest.getRequiredAmount();
        double output = (progress > 0 ? percent : 0);
        return dFormat.format(output);
    }

    public static User create(String name, UUID uuid)
    {
        return new User(name, uuid);

    }

    public static User get(UUID uuid)
    {
        return UserUtils.getUser(uuid);
    }

    public void applyQuest(Quest quest)
    {
        this.quest = quest;
        this.progress = 0;
    }

    public void cancelQuest()
    {
        this.quest = null;
        this.progress = 0;
    }

    public void finishQuest()
    {
        this.quest.getRewards().forEach(itemStack -> temporaryInbox.add(itemStack));
        this.completedQuests.add(this.quest);
        this.quest = null;
        this.progress = 0;
        applyChange();
    }

    public Scoreboard setupScoreboard()
    {
        Player player = Bukkit.getPlayer(this.uuid);
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("title", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatUtils.colored(" &8 »» :|: &eQuests &8:|: «« "));

        //Team id = board.registerNewTeam("questId");
        //id.addEntry(ChatUtils.colored("&bAktualne zadanie"));

        //id.setPrefix(ChatUtils.colored(" &bAktualne"));
        //id.setSuffix(ChatUtils.colored(" zadanie:"));

        Score blank = objective.getScore(ChatUtils.colored(" "));
        Score questId = objective.getScore(ChatUtils.colored(" &eAktualne zadanie:    "));
        Score questNameBelow = objective.getScore(ChatUtils.colored("  &6» &fhehe_xd"));

        //Team progress = board.registerNewTeam("questProgress");
        //progress.addEntry(ChatUtils.colored("&bPostep w zadaniu"));

        //progress.setPrefix(ChatUtils.colored(" &bPostep w"));
        //progress.setSuffix(ChatUtils.colored(" zadaniu:"));

        Score blank2 = objective.getScore(ChatUtils.colored("&c"));
        Score questProgress = objective.getScore(ChatUtils.colored(" &ePostep w zadaniu:"));
        Score questProgressBelow = objective.getScore(ChatUtils.colored("  &6» &f18/20 (90%)"));

        Score blank3 = objective.getScore(ChatUtils.colored("&c"));

        blank.setScore(6);
        questId.setScore(5);
        questNameBelow.setScore(4);

        blank2.setScore(3);
        questProgress.setScore(2);
        questProgressBelow.setScore(1);

        blank3.setScore(0);

        return board;
    }

    public void save()
    {
        Database db = Database.getInstance();
        if(!db.isConnected()) db.openConnection();
        if(!exists()) {
            insert();
        } else {
            update();
        }
    }

    private void insert()
    {
        try
        {
            Connection connection = Database.getInstance().openConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_DB);

            statement.setString(1, this.uuid.toString());
            statement.setString(2, this.name);
            if(this.quest != null) {
                statement.setString(3, this.quest.getId());
                statement.setInt(   4, this.progress);
            } else {
                statement.setObject(3, null);
                statement.setInt(   4, 0);
            }
            if(!this.completedQuests.isEmpty())
            {
                statement.setString(5, ChatUtils.toString(QuestUtils.getQuestsNames(this.completedQuests), false));
            } else {
                statement.setObject(5, null);
            }
            statement.executeUpdate();
            connection.commit();
            statement.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void update()
    {
        try
        {
            Connection connection = Database.getInstance().openConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_DB);

            statement.setString(1, this.name);
            if(this.quest != null) {
                statement.setString(2, this.quest.getId());
                statement.setInt(   3, this.progress);
            } else {
                statement.setString(2, null);
                statement.setInt(   3, 0);
            }
            if(!this.completedQuests.isEmpty())
            {
                statement.setString(4, ChatUtils.toString(QuestUtils.getQuestsNames(this.completedQuests), false));
            } else {
                statement.setObject(4, null);
            }
            statement.setString(    5, this.uuid.toString());
            statement.executeUpdate();
            connection.commit();
            statement.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private boolean exists() {
        MySQL m = QuestPlugin.getInstance().mysql;
        try {
            PreparedStatement statement = Database.getInstance().openConnection().prepareStatement(
                    "SELECT username FROM `" + m.usertable + "` WHERE uuid='" + this.uuid.toString() + "'");
            ResultSet rs = statement.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void applyChange()
    {
        this.changed = true;
    }

    public boolean changed()
    {
        boolean changed = this.changed;
        if(changed)
        {
            this.changed = false;
        }
        return changed;
    }

    @Override
    public int compareTo(User user) {
        return user.getCompletedQuests().size() - this.completedQuests.size();
    }
}
