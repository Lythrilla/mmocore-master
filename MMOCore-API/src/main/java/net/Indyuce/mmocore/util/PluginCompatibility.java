package net.Indyuce.mmocore.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * 插件兼容性工具类，处理与其他插件的集成
 */
public class PluginCompatibility {

    /**
     * 检查玩家是否在Chemdah对话中
     * 
     * @param player 要检查的玩家
     * @return 如果玩家在Chemdah对话中则返回true，否则返回false
     */
    public static boolean isInChemdahConversation(Player player) {
        return isInChemdahConversation(player.getName());
    }
    
    /**
     * 检查玩家是否在Chemdah对话中
     * 
     * @param playerName 玩家名称
     * @return 如果玩家在Chemdah对话中则返回true，否则返回false
     */
    public static boolean isInChemdahConversation(String playerName) {
        // 使用Bukkit的插件管理器检查Chemdah是否启用
        Plugin chemdah = Bukkit.getPluginManager().getPlugin("Chemdah");
        if (chemdah == null || !chemdah.isEnabled()) {
            return false;
        }
        
        try {
            // 获取ConversationManager类
            Class<?> conversationManagerClass = Class.forName("ink.ptms.chemdah.core.conversation.ConversationManager");
            
            // 获取sessions字段
            java.lang.reflect.Field sessionsField = conversationManagerClass.getDeclaredField("sessions");
            sessionsField.setAccessible(true);
            
            // 获取sessions Map
            Object sessions = sessionsField.get(null);
            
            // 检查玩家是否在会话中
            return ((java.util.Map<?, ?>)sessions).containsKey(playerName);
        } catch (Exception e) {
            // 出现异常时，记录错误并返回false
            Bukkit.getLogger().warning("Error checking Chemdah conversation status: " + e.getMessage());
            return false;
        }
    }
} 