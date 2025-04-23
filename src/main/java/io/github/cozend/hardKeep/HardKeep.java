package io.github.cozend.hardKeep;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HardKeep extends JavaPlugin implements Listener {

    private final Map<UUID, List<ItemStack>> savedInventories = new HashMap<>();
    private final Map<UUID, ItemStack[]> pendingRespawnInventories = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("HardKeep enabled.");
    }

    @Override
    public void onDisable() {
        savedInventories.clear();
    }

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Save the inventory
        List<ItemStack> save = new ArrayList<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                save.add(item.clone());
            }
        }
        savedInventories.put(uuid, save);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUniqueId();

        List<ItemStack> save = savedInventories.get(uuid);
        if (save == null) return;

        event.getDrops().clear();

        ItemStack[] currentContents = player.getInventory().getContents();
        ItemStack[] filtered = new ItemStack[currentContents.length];
        List<ItemStack> postSleepDrops = new ArrayList<>();

        for (int i = 0; i < currentContents.length; i++) {
            ItemStack current = currentContents[i];
            if (current == null || current.getType() == Material.AIR) continue;

            boolean matched = false;
            for (ItemStack saved : save) {
                if (saved.isSimilar(current) && current.getAmount() <= saved.getAmount()) {
                    matched = true;
                    filtered[i] = current.clone();
                    break;
                }
            }

            if (!matched) {
                postSleepDrops.add(current.clone());
            }
        }

        // Store filtered inventory to be restored on respawn
        pendingRespawnInventories.put(uuid, filtered);

        // Drop items that were not part of the sleep save
        event.getDrops().addAll(postSleepDrops);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!pendingRespawnInventories.containsKey(uuid)) return;

        ItemStack[] filtered = pendingRespawnInventories.remove(uuid);

        // Schedule 1 tick later, I don't feel comfortable doing it right when the event happens
        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.getInventory().clear();
            player.getInventory().setContents(filtered);
        }, 1L);
    }

}
