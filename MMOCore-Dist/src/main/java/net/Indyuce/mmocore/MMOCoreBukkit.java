package net.Indyuce.mmocore;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmocore.comp.profile.ForceClassProfileDataModule;
import net.Indyuce.mmocore.listener.*;
import net.Indyuce.mmocore.listener.event.PlayerPressKeyListener;
import net.Indyuce.mmocore.listener.option.*;
import net.Indyuce.mmocore.listener.profession.FishingListener;
import net.Indyuce.mmocore.listener.profession.PlayerCollectStats;
import org.bukkit.Bukkit;

/**
 * MMOCore - Advanced RPG plugin for Spigot servers
 * 
 * Originally developed by Indyuce
 * 
 * Modified and extended by Lythrilla (2025)
 * Website: https://lythrilla.cn
 * QQ: 3824670178
 * Last Update: 2025/05/12
 * 
 * This version includes Chemdah integration and various performance optimizations
 */
public class MMOCoreBukkit {
    // 控制台颜色常量
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    private static final String ANSI_BRIGHT_CYAN = "\u001B[96m";

    /**
     * Called when MMOCore enables. This registers
     * all the listeners required for MMOCore to run
     */
    public MMOCoreBukkit(MMOCore plugin) {
        // 检测Chemdah插件是否存在
        if (Bukkit.getPluginManager().getPlugin("Chemdah") != null) {
            plugin.getLogger().info("Hooked Chemdah onto MMOCore");
        }
        
        // 输出开发者信息
        plugin.getLogger().info(ANSI_BOLD + ANSI_BRIGHT_CYAN + "MMOCore" + ANSI_RESET + " - " + ANSI_BRIGHT_GREEN + "Modified Lythrilla" + ANSI_RESET);
        plugin.getLogger().info("QQ: 3824670178");
        plugin.getLogger().info("Version: 1.13.1-SNAPSHOT-Lythrilla (2025/05/12)");
        
        if (plugin.configManager.overrideVanillaExp)
            Bukkit.getPluginManager().registerEvents(new VanillaExperienceOverride(), plugin);

        if (plugin.getConfig().getBoolean("prevent-spawner-xp"))
            Bukkit.getPluginManager().registerEvents(new NoSpawnerEXP(), plugin);

        if (plugin.getConfig().getBoolean("death-exp-loss.enabled"))
            Bukkit.getPluginManager().registerEvents(new DeathExperienceLoss(), plugin);

        if (plugin.getConfig().getBoolean("shift-click-player-profile-check"))
            Bukkit.getPluginManager().registerEvents(new PlayerProfileCheck(), plugin);

        if (plugin.getConfig().getBoolean("vanilla-exp-redirection.enabled"))
            Bukkit.getPluginManager().registerEvents(new RedirectVanillaExp(plugin.getConfig().getDouble("vanilla-exp-redirection.ratio")), plugin);

        if (plugin.getConfig().getBoolean("force-class-selection") && MythicLib.plugin.hasProfiles())
            new ForceClassProfileDataModule();

        Bukkit.getPluginManager().registerEvents(new WaypointsListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GoldPouchesListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new LootableChestsListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GuildListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new FishingListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerCollectStats(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerPressKeyListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new MMOCoreSkillTriggers(), plugin);
        // Bukkit.getPluginManager().registerEvents(new ClassTriggers(), plugin);
    }
}
