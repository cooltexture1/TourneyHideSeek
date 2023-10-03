package org.cooltetxure.tourneyhideseek;

import org.bukkit.Location;
import org.bukkit.World;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class SoundAndLocationBuilder {

  public static Location getLocation(World w, String loc) {
    if (TourneyHideSeek.map == Map.FANTASY) {
      switch (loc) {
        case "lobby_spawn":
          return new Location(w, 652, -13, 470);
        case "map_center":
          return new Location(w, 590, 80, 540);
        case "seeker_spawn":
          return new Location(w, 600, 69, 347);
        case "hider_spawn":
          return new Location(w, 600, 69, 378);
        case "podium_room":
          return new Location(w, 467, 86, 1268);
        default:
          return null;
      }
    } else if (TourneyHideSeek.map == Map.ZERO) {
      switch (loc) {
        case "lobby_spawn":
          return new Location(w, 17, 110, 7);
        case "map_center":
          return new Location(w, 46, 181, -32);
        case "seeker_spawn":
          return new Location(w, 46, 172, 26);
        case "hider_spawn":
          return new Location(w, 46, 172, -81);
        case "podium_room":
          return new Location(w, 467, 86, 1268);
        default:
          return null;
      }
    } else {
      return null;
    }
  }

  public static Sound getSound(String name) {
    switch (name) {
      case "countdown":
        return Sound.sound(Key.key("game.countdown"), Sound.Source.AMBIENT, 1f, 1f);
      case "game_start":
        return Sound.sound(Key.key("game.final"), Sound.Source.AMBIENT, 1f, 1f);
      case "game_over":
        return Sound.sound(Key.key("game.game_win"), Sound.Source.AMBIENT, 1f, 1f);
      case "death_sound":
        return Sound.sound(Key.key("game.player_death"), Sound.Source.AMBIENT, 1f, 1f);
      case "kill_sound":
        return Sound.sound(Key.key("game.ally_kill"), Sound.Source.AMBIENT, 1f, 1f);
      case "end_sound":
        return Sound.sound(Key.key("game.game_win"), Sound.Source.AMBIENT, 1f, 1f);
      case "border_close":
        return Sound.sound(Key.key("block.note_block.banjo"), Sound.Source.AMBIENT, 1f, 1f);
      case "craftmaster_speed":
        return Sound.sound(Key.key("craftmaster.speed_boost"), Sound.Source.AMBIENT, 1f, 1f);
      case "meow":
        return Sound.sound(Key.key("entity.cat.ambient"), Sound.Source.AMBIENT, 40f, 1f);
      case "hazard":
        return Sound.sound(Key.key("block.beacon.power_select"), Sound.Source.AMBIENT, 1f, 1f);
      case "jump_pad":
        return Sound.sound(Key.key("game.hazard-positive"), Sound.Source.AMBIENT, 1f, 1f);
      default:
        return null;
    }
  }
}
