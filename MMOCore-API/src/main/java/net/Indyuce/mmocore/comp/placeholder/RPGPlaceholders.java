package net.Indyuce.mmocore.comp.placeholder;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.util.AltChar;
import io.lumine.mythic.lib.manager.StatManager;
import io.lumine.mythic.lib.version.Attributes;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttributes;
import net.Indyuce.mmocore.api.quest.PlayerQuests;
import net.Indyuce.mmocore.experience.PlayerProfessions;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.skill.CastableSkill;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;
import net.Indyuce.mmocore.skill.binding.BoundSkillInfo;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RPGPlaceholders extends PlaceholderExpansion {
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return "Indyuce";
    }

    @Override
    public String getIdentifier() {
        return "mmocore";
    }

    @Override
    public String getVersion() {
        return MMOCore.plugin.getDescription().getVersion();
    }

    private static final String ERROR_PLACEHOLDER = " ";

    @SuppressWarnings("DuplicateExpressions")
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (!PlayerData.has(player.getUniqueId()))
            return null;
        final PlayerData playerData = PlayerData.get(player);

        if (identifier.equals("mana_icon"))
            return playerData.getProfess().getManaDisplay().getIcon();

        if (identifier.equals("mana_name"))
            return playerData.getProfess().getManaDisplay().getName();

        if (identifier.equals("level"))
            return String.valueOf(playerData.getLevel());

        else if (identifier.startsWith("skill_level_")) {
            String id = identifier.substring(12);
            RegisteredSkill skill = MMOCore.plugin.skillManager.getSkillOrThrow(id);
            return String.valueOf(playerData.getSkillLevel(skill));
        }

        else if (identifier.startsWith("skill_tree_points_")) {
            int length = "skill_tree_points_".length();
            String id = identifier.substring(length);
            return String.valueOf(PlayerData.get(player).getSkillTreePoints(id));
        }

        /*
         * Given a skill slot number (integer) and a parameter name,
         * return the player's value of that skill parameter from that
         * specific skill slot.
         */
        else if (identifier.startsWith("bound_skill_parameter_")) {
            final String[] ids = identifier.substring(22).split(":");
            final String parameterId = ids[0];
            final int skillSlot = Integer.parseInt(ids[1]);
            final ClassSkill found = playerData.getBoundSkill(skillSlot);
            if (found == null) return "";
            final CastableSkill castable = found.toCastable(playerData);
            final double value = playerData.getMMOPlayerData().getSkillModifierMap().calculateValue(castable, parameterId);
            return MythicLib.plugin.getMMOConfig().decimal.format(value);
        }

        // Returns a player's value of a skill parameter.
        else if (identifier.startsWith("skill_modifier_") || identifier.startsWith("skill_parameter_")) {
            final String[] ids = identifier.substring(identifier.startsWith("skill_modifier_") ? 15 : 16).split(":");
            final String parameterId = ids[0];
            final String skillId = ids[1];
            final RegisteredSkill skill = Objects.requireNonNull(MMOCore.plugin.skillManager.getSkill(skillId), "Could not find skill with ID '" + skillId + "'");
            final CastableSkill castable = playerData.getProfess().getSkill(skill).toCastable(playerData);
            final double value = playerData.getMMOPlayerData().getSkillModifierMap().calculateValue(castable, parameterId);
            return MythicLib.plugin.getMMOConfig().decimal.format(value);
        } else if (identifier.startsWith("attribute_points_spent_")) {
            final String attributeId = identifier.substring(23);
            final PlayerAttributes.AttributeInstance attributeInstance = Objects.requireNonNull(playerData.getAttributes().getInstance(attributeId), "Could not find attribute with ID '" + attributeId + "'");
            return String.valueOf(attributeInstance.getBase());
        } else if (identifier.equals("level_percent")) {
            double current = playerData.getExperience(), next = playerData.getLevelUpExperience();
            return MythicLib.plugin.getMMOConfig().decimal.format(current / next * 100);
        } else if (identifier.equals("health"))
            return StatManager.format("MAX_HEALTH", player.getPlayer().getHealth());

        else if (identifier.equals("max_health"))
            return StatManager.format("MAX_HEALTH", player.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue());

        else if (identifier.equals("health_bar") && player.isOnline()) {
            StringBuilder format = new StringBuilder();
            double ratio = 20 * player.getPlayer().getHealth() / player.getPlayer().getAttribute(Attributes.MAX_HEALTH).getValue();
            for (double j = 1; j < 20; j++)
                format.append(ratio >= j ? ChatColor.RED : ratio >= j - .5 ? ChatColor.DARK_RED : ChatColor.DARK_GRAY).append(AltChar.listSquare);
            return format.toString();
        }

        else if (identifier.equals("class_id"))
            return playerData.getProfess().getId();
         else if (identifier.equals("class"))
            return playerData.getProfess().getName();

        else if (identifier.startsWith("profession_percent_")) {
            PlayerProfessions professions = playerData.getCollectionSkills();
            String name = identifier.substring(19).replace(" ", "-").replace("_", "-").toLowerCase();
            Profession profession = MMOCore.plugin.professionManager.get(name);
            double current = professions.getExperience(profession), next = professions.getLevelUpExperience(profession);
            return MythicLib.plugin.getMMOConfig().decimal.format(current / next * 100);

        } else if (identifier.equals("is_casting"))
            return String.valueOf(playerData.isCasting());

        else if (identifier.equals("in_combat"))
            return String.valueOf(playerData.isInCombat());

        else if (identifier.equals("pvp_mode"))
            return String.valueOf(playerData.getCombat().isInPvpMode());

        else if (identifier.startsWith("since_enter_combat"))
            return playerData.isInCombat() ? MythicLib.plugin.getMMOConfig().decimal.format((System.currentTimeMillis() - playerData.getCombat().getLastEntry()) / 1000.) : "-1";

        else if (identifier.startsWith("invulnerability_left"))
            return MythicLib.plugin.getMMOConfig().decimal.format(Math.max(0, (playerData.getCombat().getInvulnerableTill() - System.currentTimeMillis()) / 1000.));

        else if (identifier.startsWith("since_last_hit"))
            return playerData.isInCombat() ? MythicLib.plugin.getMMOConfig().decimal.format((System.currentTimeMillis() - playerData.getCombat().getLastHit()) / 1000.) : "-1";

            // Returns the bound skill ID
        else if (identifier.startsWith("id_bound_")) {
            final int slot = Math.max(1, Integer.parseInt(identifier.substring(9)));
            final ClassSkill info = playerData.getBoundSkill(slot);
            return info == null ? "" : info.getSkill().getHandler().getId();
        }

        // Returns the key that needs to be pressed to cast slot in slot N
        else if (identifier.startsWith("cast_slot_offset_")) {
            final Player online = player.getPlayer();
            Validate.notNull(online, "Player is offline");
            final int query = Integer.parseInt(identifier.substring(17));

            BoundSkillInfo bound = playerData.getBoundSkills().get(query);
            if (bound == null || bound.isPassive()) return String.valueOf(0);

            int slot = bound.skillBarCastSlot;
            // Offset due to player's hotbar location
            if (online.getInventory().getHeldItemSlot() < slot) slot++;
            return String.valueOf(slot);
        }

        // Is there a passive skill bound to given slot
        else if (identifier.startsWith("passive_bound_")) {
            final int slot = Integer.parseInt(identifier.substring(14));
            final ClassSkill skill = playerData.getBoundSkill(slot);
            return String.valueOf(skill != null && skill.getSkill().getTrigger().isPassive());
        }

        // Returns the bound skill name
        else if (identifier.startsWith("bound_")) {
            final int slot = Math.max(1, Integer.parseInt(identifier.substring(6)));
            final ClassSkill skill = playerData.getBoundSkill(slot);
            if (skill == null) return MMOCore.plugin.configManager.noSkillBoundPlaceholder;
            return (playerData.getCooldownMap().isOnCooldown(skill) ? ChatColor.RED : ChatColor.GREEN) + skill.getSkill().getName();
        }

        // Returns cooldown of skill bound at given slot
        else if (identifier.startsWith("cooldown_bound_")) {
            int slot = Math.max(0, Integer.parseInt(identifier.substring(15)));
            if (playerData.hasSkillBound(slot))
                return Double.toString(playerData.getCooldownMap().getCooldown(playerData.getBoundSkill(slot)));
            else return MMOCore.plugin.configManager.noSkillBoundPlaceholder;
        } else if (identifier.startsWith("profession_experience_"))
            return MythicLib.plugin.getMMOConfig().decimal.format(
                    playerData.getCollectionSkills().getExperience(identifier.substring(22).replace(" ", "-").replace("_", "-").toLowerCase()));

        else if (identifier.startsWith("profession_next_level_"))
            return String.valueOf(PlayerData.get(player).getCollectionSkills()
                    .getLevelUpExperience(identifier.substring(22).replace(" ", "-").replace("_", "-").toLowerCase()));

        else if (identifier.startsWith("party_count")) {
            final @Nullable AbstractParty party = playerData.getParty();
            return party == null ? "0" : String.valueOf(party.countMembers());
        } else if (identifier.startsWith("party_member_")) {
            final int n = Integer.parseInt(identifier.substring(13)) - 1;
            final @Nullable AbstractParty party = playerData.getParty();
            if (party == null) return ERROR_PLACEHOLDER;
            if (n >= party.countMembers()) return ERROR_PLACEHOLDER;
            final @Nullable PlayerData member = party.getMember(n);
            if (member == null) return ERROR_PLACEHOLDER;
            return member.getPlayer().getName();
        } else if (identifier.equals("online_friends")) {
            int count = 0;
            for (UUID friendId : playerData.getFriends())
                if (Bukkit.getPlayer(friendId) != null) count++;
            return String.valueOf(count);
        } else if (identifier.startsWith("online_friend_")) {
            final int n = Integer.parseInt(identifier.substring(14)) - 1;
            if (n >= playerData.getFriends().size()) return ERROR_PLACEHOLDER;
            final @Nullable Player friend = Bukkit.getPlayer(playerData.getFriends().get(n));
            if (friend == null) return ERROR_PLACEHOLDER;
            return friend.getName();
        } else if (identifier.startsWith("profession_"))
            return String
                    .valueOf(playerData.getCollectionSkills().getLevel(identifier.substring(11).replace(" ", "-").replace("_", "-").toLowerCase()));
        else if (identifier.startsWith("skilltree_")) {
                String regex = "skilltree_(.+?)\\[(.+?)\\]";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(identifier);
        
                if (matcher.find()) {
                    // Return an array with skill tree name and node name
                    try {
                        SkillTree tree = MMOCore.plugin.skillTreeManager.get(matcher.group(1));
                        SkillTreeNode node = tree.getNode(matcher.group(2));
                        return String.valueOf(playerData.getNodeLevel(node));
                    } catch (Exception e) {
                        return ERROR_PLACEHOLDER;
                    }
                }
        
                // Return "Not found" if not matching the expected format
                return "Could not find a match.";
            
        }
        
        else if (identifier.equals("experience"))
            return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getExperience());

        else if (identifier.equals("next_level"))
            return String.valueOf(playerData.getLevelUpExperience());

        else if (identifier.equals("class_points"))
            return String.valueOf(playerData.getClassPoints());

        else if (identifier.equals("skill_points"))
            return String.valueOf(playerData.getSkillPoints());

        else if (identifier.equals("attribute_points"))
            return String.valueOf(playerData.getAttributePoints());

        else if (identifier.equals("attribute_reallocation_points"))
            return String.valueOf(playerData.getAttributeReallocationPoints());

        else if (identifier.startsWith("attribute_"))
            return String.valueOf(playerData.getAttributes()
                    .getAttribute(MMOCore.plugin.attributeManager.get(identifier.substring(10).toLowerCase().replace("_", "-"))));

        else if (identifier.equals("mana"))
            return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getMana());

        else if (identifier.equals("mana_bar"))
            return playerData.getProfess().getManaDisplay().generateBar(playerData.getMana(), playerData.getStats().getStat("MAX_MANA"));

        else if (identifier.startsWith("exp_multiplier_")) {
            String format = identifier.substring(15).toLowerCase().replace("_", "-").replace(" ", "-");
            Profession profession = format.equals("main") ? null : MMOCore.plugin.professionManager.get(format);
            return MythicLib.plugin.getMMOConfig().decimal.format(MMOCore.plugin.boosterManager.getMultiplier(profession) * 100);
        } else if (identifier.startsWith("exp_boost_")) {
            String format = identifier.substring(10).toLowerCase().replace("_", "-").replace(" ", "-");
            Profession profession = format.equals("main") ? null : MMOCore.plugin.professionManager.get(format);
            return MythicLib.plugin.getMMOConfig().decimal.format((MMOCore.plugin.boosterManager.getMultiplier(profession) - 1) * 100);
        } else if (identifier.equals("stamina"))
            return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getStamina());

        else if (identifier.equals("stamina_bar")) {
            StringBuilder format = new StringBuilder();
            double ratio = 20 * playerData.getStamina() / playerData.getStats().getStat("MAX_STAMINA");
            for (double j = 1; j < 20; j++)
                format.append(ratio >= j ? MMOCore.plugin.configManager.staminaFull
                                : ratio >= j - .5 ? MMOCore.plugin.configManager.staminaHalf : MMOCore.plugin.configManager.staminaEmpty)
                        .append(AltChar.listSquare);
            return format.toString();
        } else if (identifier.startsWith("stat_")) {
            final String stat = UtilityMethods.enumName(identifier.substring(5));
            return StatManager.format(stat, playerData.getMMOPlayerData());
        } else if (identifier.equals("stellium"))
            return MythicLib.plugin.getMMOConfig().decimal.format(playerData.getStellium());

        else if (identifier.equals("stellium_bar")) {
            StringBuilder format = new StringBuilder();
            double ratio = 20 * playerData.getStellium() / playerData.getStats().getStat("MAX_STELLIUM");
            for (double j = 1; j < 20; j++)
                format.append(ratio >= j ? ChatColor.BLUE : ratio >= j - .5 ? ChatColor.AQUA : ChatColor.WHITE).append(AltChar.listSquare);
            return format.toString();
        } else if (identifier.equals("quest")) {
            PlayerQuests data = playerData.getQuestData();
            return data.hasCurrent() ? data.getCurrent().getQuest().getName() : "None";
        } else if (identifier.equals("quest_progress")) {
            PlayerQuests data = playerData.getQuestData();
            return data.hasCurrent() ? MythicLib.plugin.getMMOConfig().decimal
                    .format((double) data.getCurrent().getObjectiveNumber() / data.getCurrent().getQuest().getObjectives().size() * 100L) : "0";
        } else if (identifier.equals("quest_objective")) {
            PlayerQuests data = playerData.getQuestData();
            return data.hasCurrent() ? data.getCurrent().getFormattedLore() : "None";
        } else if (identifier.startsWith("guild_")) {
            String placeholder = identifier.substring(6);
            if (playerData.getGuild() == null)
                return "";

            if (placeholder.equalsIgnoreCase("name"))
                return playerData.getGuild().getName();
            else if (placeholder.equalsIgnoreCase("tag"))
                return playerData.getGuild().getTag();
            else if (placeholder.equalsIgnoreCase("leader"))
                return Bukkit.getOfflinePlayer(playerData.getGuild().getOwner()).getName();
            else if (placeholder.equalsIgnoreCase("members"))
                return String.valueOf(playerData.getGuild().countMembers());
            else if (placeholder.equalsIgnoreCase("online_members"))
                return String.valueOf(playerData.getGuild().countOnlineMembers());
        }

        return null;
    }
}
