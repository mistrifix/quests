package net.mistrifix.main.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChatUtils {

    public static String colored(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String toString(Collection<String> list, boolean display)
    {
        StringBuilder sb = new StringBuilder();
        for(String strings : list)
        {
            sb.append(strings);
            sb.append(',');
            if(display)
            {
                sb.append(' ');
            }
        }
        String strings = sb.toString();
        if (display) {
            if (strings.length() > 2) {
                strings=  strings.substring(0, strings.length() - 2);
            } else if (strings.length() > 1) {
                strings = strings.substring(0, strings.length() - 1);
            }
        }
        return strings;
    }

    public static List<String> fromString(String formatted) {
        List<String> list = new ArrayList<>();
        if (formatted == null || formatted.isEmpty()) {
            return list;
        }

        list = Arrays.asList(formatted.split(","));
        return list;
    }



}
