package me.svr.raiders.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class Logger {

    private static final ConsoleCommandSender console = Bukkit.getConsoleSender();

    public static void log(LogLevel level, String message) {
        if (message == null) return;

        String prefix;
        ChatColor color;

        switch (level) {
            case ERROR:
                prefix = "[ERROR]";
                color = ChatColor.RED;
                break;
            case WARNING:
                prefix = "[WARNING]";
                color = ChatColor.YELLOW;
                break;
            case INFO:
                prefix = "[INFO]";
                color = ChatColor.BLUE;
                break;
            case SUCCESS:
                prefix = "[SUCCESS]";
                color = ChatColor.GREEN;
                break;
            case OUTLINE:
                prefix = "";
                color = ChatColor.GRAY;
                break;
            default:
                prefix = "[UNKNOWN]";
                color = ChatColor.WHITE;
                break;
        }

        console.sendMessage(color + prefix + ChatColor.RESET + " " + message);
    }

    public enum LogLevel {
        ERROR, WARNING, INFO, SUCCESS, OUTLINE
    }
}
