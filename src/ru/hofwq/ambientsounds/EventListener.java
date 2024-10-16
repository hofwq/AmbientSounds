package ru.hofwq.ambientsounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

import ru.hofwq.storyline.utils.Utils;

public class EventListener implements Listener {
	private AmbientSounds plugin = AmbientSounds.getPlugin();
	public boolean isNight;
	private String daySound = "camp_center_day";
	private String nightSound = "camp_center_night";
	private static Map<Player, String> playingSounds = new HashMap<>();
	private static List<UUID> playersWithLoadedPack = new ArrayList<>();

	@EventHandler
	public void onPluginEnable(PluginEnableEvent e) {
	    new BukkitRunnable() {
	        @Override
	        public void run() {
	            World world = Bukkit.getWorld("world");
	            long time = world.getTime();
	            for (Player p : Bukkit.getOnlinePlayers()) {
	                if (playersWithLoadedPack.contains(p.getUniqueId())) {
	                	FileConfiguration playerConfig = Utils.getPlayerConfiguration(p);
	                	
	                	if(playerConfig.getInt("storylineLevel") >= 1) {
	                		playSoundsDependsOfTime(p, time);
	                	}
	                }
	            }
	        }
	    }.runTaskTimer(plugin, 0L, 20L);
	}

	@EventHandler
	public void onResourcePackLoad(PlayerResourcePackStatusEvent e) {
	    Player player = e.getPlayer();

	    if (e.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED || e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
	        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
	            @Override
	            public void run() {
	                playersWithLoadedPack.add(player.getUniqueId());
	            }
	        }, 20L);
	    }
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		stopCurrentSound(player);
		playersWithLoadedPack.remove(player.getUniqueId());
	}
	
	private void playSoundsDependsOfTime(Player player, long time) {
	    if (time > 13500 && time <= 23500) {
	        isNight = true;
	        if (!isPlaying(player, nightSound)) {
	            stopCurrentSound(player);
	            playNightSound(player);
	        }
	    } else {
	        isNight = false;
	        if (!isPlaying(player, daySound)) {
	            stopCurrentSound(player);
	            playDaySound(player);
	        }
	    }
	}

	private void playDaySound(Player player) {
	    playingSounds.put(player, daySound);

	    new BukkitRunnable() {
	        @Override
	        public void run() {
	            DayNightDurations.playSound(daySound, player);
	            new BukkitRunnable() {
	                @Override
	                public void run() {
	                    playingSounds.remove(player);
	                }
	            }.runTaskLater(plugin, 20L * DayNightDurations.valueOf(daySound).getDuration());
	        }
	    }.runTask(plugin);
	}

	private void playNightSound(Player player) {
	    playingSounds.put(player, nightSound);

	    new BukkitRunnable() {
	        @Override
	        public void run() {
	            DayNightDurations.playSound(nightSound, player);
	            new BukkitRunnable() {
	                @Override
	                public void run() {
	                    playingSounds.remove(player);
	                }
	            }.runTaskLater(plugin, 20L * DayNightDurations.valueOf(nightSound).getDuration());
	        }
	    }.runTask(plugin);
	}

	private void stopCurrentSound(Player player) {
	    if (playingSounds.containsKey(player)) {
	        String currentSound = playingSounds.get(player);
	        player.stopSound("minecraft:my_sounds." + currentSound, SoundCategory.AMBIENT);
	        playingSounds.remove(player);
	    }
	}

	private boolean isPlaying(Player player, String sound) {
	    return playingSounds.containsKey(player) && playingSounds.get(player).equals(sound);
	}
}