package ru.hofwq.ambientsounds;

import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public enum DayNightDurations {
	camp_center_day(137),
	camp_center_night(54);
	
	private final int duration;

	DayNightDurations(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
    
    public static void playSound(String soundName, Player player) {
    	DayNightDurations soundToPlay = null;

        for (DayNightDurations sound : DayNightDurations.values()) {
            if (sound.name().equals(soundName)) {
                soundToPlay = sound;
                break;
            }
        }

        if (soundToPlay == null) {
            return;
        }

        player.playSound(player.getLocation(), "minecraft:my_sounds." + soundToPlay.name().toLowerCase(), SoundCategory.AMBIENT, 1.0F, 1.0F);
    }
}
