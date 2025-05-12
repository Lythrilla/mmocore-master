package net.Indyuce.mmocore.party.provided;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.event.social.PartyChatEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyModule;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class MMOCorePartyModule implements PartyModule, Listener {
    protected final Set<Party> parties = new HashSet<>();
    protected final Map<UUID, Party> playerParties = new HashMap<>();

    public MMOCorePartyModule() {
        Bukkit.getPluginManager().registerEvents(this, MMOCore.plugin);
    }

    @Deprecated
    public void registerParty(Party party) {
        parties.add(party);
    }

    /**
     * Creates and registers a new party with given owner
     */
    public Party newRegisteredParty(PlayerData owner) {
        final Party party = new Party(this, owner);
        parties.add(party);
        return party;
    }

    public boolean isRegistered(Party party) {
        return parties.contains(party);
    }

    public void unregisterParty(Party party) {
        // IMPORTANT: clears all party members before unregistering the party
        party.forEachMember(party::removeMember);
        Validate.isTrue(party.getMembers().isEmpty(), "Tried unregistering a non-empty party");
        parties.remove(party);
    }

    @Override
    public Party getParty(PlayerData playerData) {
        return this.playerParties.get(playerData.getUniqueId());
    }

    public void setParty(PlayerData playerData, Party party) {
        this.playerParties.put(playerData.getUniqueId(), party);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void partyChat(AsyncPlayerChatEvent event) {
        if (!event.getMessage().startsWith(MMOCore.plugin.configManager.partyChatPrefix)) return;

        PlayerData data = PlayerData.get(event.getPlayer());
        Party party = this.getParty(data);
        if (party == null) return;

        event.setCancelled(true);

        // Running it in a delayed task is recommended
        Bukkit.getScheduler().runTask(MMOCore.plugin, () -> {
            ConfigMessage message = ConfigMessage.fromKey("party-chat", "player", data.getPlayer().getName(), "message",
                    event.getMessage().substring(MMOCore.plugin.configManager.partyChatPrefix.length()));
            PartyChatEvent called = new PartyChatEvent(party, data, message.asLine());
            Bukkit.getPluginManager().callEvent(called);
            if (!called.isCancelled()) party.getOnlineMembers().forEach(member -> message.send(member.getPlayer()));
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void leavePartyOnQuit(PlayerQuitEvent event) {
        final PlayerData playerData = PlayerData.get(event.getPlayer());
        final AbstractParty party = playerData.getParty();
        if (party != null) ((Party) party).removeMember(playerData);
    }
}
