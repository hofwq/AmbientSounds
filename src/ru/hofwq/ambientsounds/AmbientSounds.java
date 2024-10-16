package ru.hofwq.ambientsounds;

import org.bukkit.plugin.java.JavaPlugin;

public class AmbientSounds extends JavaPlugin{
	
	private static AmbientSounds plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
	
	@Override
	public void onDisable() {
		plugin = null;
	}

	public static AmbientSounds getPlugin() {
		return plugin;
	}
}
