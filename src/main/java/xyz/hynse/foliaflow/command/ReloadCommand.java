package xyz.hynse.foliaflow.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import xyz.hynse.foliaflow.FoliaFlow;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("foliaflow.reload")) return true;
        try {
            FoliaFlow.instance.reload();
            sender.sendMessage(ChatColor.YELLOW + "FoliaFlow reloaded.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Something went wrong reloading FoliaFlow, see the console for more.");
            e.printStackTrace();
        }
        return true;
    }
}
