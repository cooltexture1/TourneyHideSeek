package org.cooltetxure.tourneyhideseek;

import net.kyori.adventure.key.Key;

import java.time.Duration;
import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;

public class ItemAndMsgBuilder {
  static Key font_key = Key.key("tubtext-thin");

  public static ItemStack itembuilder(String item_name) {

    if (item_name.equals("trophy")) {
      ItemStack trophy = new ItemStack(Material.COAL, 1);
      ItemMeta tmeta = trophy.getItemMeta();
      tmeta.setCustomModelData(1);
      tmeta.displayName(Component.text("You Won!").font(font_key));
      ArrayList<Component> lore = new ArrayList<>();
      lore.add(Component.text("A true Collectable"));
      tmeta.lore(lore);
      trophy.setItemMeta(tmeta);
      return trophy;

    } else if (item_name.equals("speed_orb")) {
      ItemStack speed_item = new ItemStack(Material.EMERALD, 1);
      ItemMeta smeta = speed_item.getItemMeta();
      smeta.setCustomModelData(1);
      smeta.displayName(Component.text("Speed Orb"));
      ArrayList<Component> lore = new ArrayList<>();
      lore.add(Component.text("Right click to gain speed!"));
      smeta.lore(lore);
      speed_item.setItemMeta(smeta);
      return speed_item;
    } else {
      return null;
    }
  }

  public static Component getPlayerTeam(Player p) {
    Scoreboard s = p.getScoreboard();
    Component role_component;

    if (s.getTeam("seeker_team").hasPlayer(p)) {
      role_component = Component.text("Seeker").color(NamedTextColor.DARK_RED);
    } else if (s.getTeam("hider_team").hasPlayer(p)) {
      role_component = Component.text("Hider").color(NamedTextColor.AQUA);
    } else if (s.getTeam("admin_team").hasPlayer(p)) {
      role_component = Component.text("Admin").color(NamedTextColor.RED);
    } else {
      role_component = Component.text("Spectator").color(NamedTextColor.GRAY);
    }

    role_component.font(font_key);
    return role_component;
  }

  public static Component borderShrinkAnnouncment(int minutes, int new_size) {
    return gameAnnouncement().append(
        Component.text(minutes + " minutes have passed: The border will now shrink to " + new_size + "x" + new_size)
            .color(NamedTextColor.WHITE).font(font_key));
  }

  public static Title startSoonMsg(Player p) {
    Component game_start = Component.text("The game will begin soon").font(font_key);
    Component role_component = ItemAndMsgBuilder.getPlayerTeam(p);
    Component subtitle = Component.text("you are a ").append(role_component).font(font_key);
    Title.Times times = Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1));
    return Title.title(game_start, subtitle, times);
  }

  public static Component broadcast(String s) {
    return Component.text("[\uA0CC]").color(NamedTextColor.GRAY)
        .append(Component.text("\uA168 \n\n").color(NamedTextColor.WHITE))
        .append(Component.text(s).color(NamedTextColor.WHITE).font(font_key));
  }

  public static Component hazardText(String s) {
    Bukkit.getWorlds().get(0).playSound(SoundAndLocationBuilder.getSound("hazard"));
    Component hazard = Component.text(" [\uA0CC]").color(NamedTextColor.GRAY)
        .append(Component.text(" Hazard").color(NamedTextColor.RED).font(font_key))
        .append(Component.text("\n\n"));
    switch (s) {
      case "lightning_storm":
        return hazard
            .append(Component.text("Ligtning Storm!\n3 random hiders will be struck by lightning.\n").font(font_key));
      case "night_vision":
        return hazard.append(Component.text("Night Vision!\nSeekers get 30 secs of night vision.\n").font(font_key));
      case "glowing":
        return hazard
            .append(Component.text("Glowing!\nOne Hider will start blinking for a few seconds.\n").font(font_key));
      case "speedy":
        return hazard.append(Component.text("Speedy!\nHiders get Speed.\n").font(font_key));
      case "noisy":
        return hazard.append(Component.text("Noisy!\nHiders will meow.\n").font(font_key));
      case "visual_aid":
        return hazard.append(
            Component.text("Visual Aid!\nHiders Nametags will become visible for 10 seconds.\n").font(font_key));
      default:
        return null;
    }
  }

  public static Component clockworkText(int x) {
    Bukkit.getWorlds().get(0).playSound(SoundAndLocationBuilder.getSound("hazard"));
    Component hazard = Component.text(" [\uA0CC]").color(NamedTextColor.GRAY)
        .append(Component.text(" Hazard").color(NamedTextColor.RED).font(font_key))
        .append(Component.text("\n\n"));

    return hazard
        .append(Component.text("Clockwork!\nThe border will decrease by " + x + " blocks over the next 30 seconds\n"));
  }

  public static Title seekersHaveBeenReleased() {
    Component title = Component.text("The Seekers have been released!").color(NamedTextColor.DARK_RED)
        .font(font_key);
    Title.Times times = Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3),
        Duration.ofSeconds(1));
    return Title.title(title, Component.empty(), times);
  }

  public static Component playerCaughtMsg(Player p, Player catcher) {
    if (catcher == null) {
      return gameAnnouncement().append(Component.text(p.getName() + " has been eliminated")
          .color(NamedTextColor.GREEN).font(font_key));
    } else {
      return gameAnnouncement().append(Component.text(p.getName() + " has been caught by " + catcher.getName())
          .color(NamedTextColor.GREEN).font(font_key));
    }
  }

  private static Component gameAnnouncement() {
    return Component.text(" [\uA0CC]").color(NamedTextColor.GRAY)
        .append(Component.text(" Game Announcement").color(NamedTextColor.RED).font(font_key))
        .append(Component.text("\n\n"));
  }

  public static Title showWinnerWon(Player p, int placement) {
    Component winner_title = p.displayName().append(Component.text("").font(font_key));
    Component placement_text = null;
    if (placement == 3) {
      placement_text = Component.text("comes in third!").font(font_key);
    } else if (placement == 2) {
      placement_text = Component.text("comes in second!").font(font_key);
    } else if (placement == 1) {
      placement_text = Component.text("comes in first!").font(font_key);
    }
    return Title.title(winner_title, placement_text);
  }

  public static Title gameEnded() {
    Component game_finish_title = Component.text("The Game has finished").font(font_key);
    Component podiumt = Component.text("Podium will begin shortly!").font(font_key);
    return Title.title(game_finish_title, podiumt);
  }

  public static void trophyFirework(Firework fw) {
    FireworkMeta fwm = fw.getFireworkMeta();
    fwm.setPower(1);
    FireworkEffect effect1 = FireworkEffect.builder().with(FireworkEffect.Type.BALL).trail(true)
        .withColor(Color.RED)
        .withColor(Color.BLUE).build();
    FireworkEffect effect2 = FireworkEffect.builder().trail(true).withColor(Color.TEAL).withColor(Color.AQUA)
        .build();
    fwm.addEffects(effect1, effect2);
    fw.setFireworkMeta(fwm);
  }

  public static void podiumFirework(Firework fw) {
    FireworkMeta fwm = fw.getFireworkMeta();
    fwm.setPower(1);
    FireworkEffect effect1 = FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withTrail()
        .withColor(Color.RED)
        .withColor(Color.BLUE).build();
    FireworkEffect effect2 = FireworkEffect.builder().with(FireworkEffect.Type.CREEPER).withFlicker().withTrail()
        .withColor(Color.GREEN)
        .withColor(Color.AQUA)
        .build();
    fwm.addEffects(effect1, effect2);
    fw.setFireworkMeta(fwm);
  }

  public static Component debugPlacements() {
    int caught_amount = TourneyHideSeek.caught_list.size();
    Player first = TourneyHideSeek.caught_list.get(caught_amount - 1);
    Player second = TourneyHideSeek.caught_list.get(caught_amount - 2);
    Player third = TourneyHideSeek.caught_list.get(caught_amount - 3);

    Component comp = Component
        .text("[Debug] Players who won:\n1. ").append(first.displayName());
    try {
      comp.append(Component.text("\n2.")).append(second.displayName());
    } catch (Exception e) {
      comp.append(Component.text("\n2.")).append(Component.text("error no player"));
    }
    try {
      comp.append(Component.text("\n3.")).append(third.displayName());
    } catch (Exception e) {
      comp.append(Component.text("\n3.")).append(Component.text("error no player"));

    }
    return comp;
  }

  public static Component generateHazard() {
    Bukkit.getWorlds().get(0).playSound(SoundAndLocationBuilder.getSound("hazard"));
    Component hazard = Component.text(" [\uA0CC]").color(NamedTextColor.GRAY)
        .append(Component.text(" Hazard").color(NamedTextColor.RED).font(font_key))
        .append(Component.text("\n\n"))
        .append(
            Component.text("A Hazard is generating please stand by...\n").color(NamedTextColor.WHITE).font(font_key));
    return hazard;
  }
}
