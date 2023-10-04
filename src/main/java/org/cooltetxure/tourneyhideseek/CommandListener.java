package org.cooltetxure.tourneyhideseek;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class CommandListener implements TabExecutor {
  @Override
  public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
      @NotNull String[] args) {
    Player cmd_sender = (Player) commandSender;

    switch (args[0]) {
      case "addteam": {
        Player player = Bukkit.getServer().getPlayer(args[1]);

        if (player == null) {
          commandSender.sendMessage("the provided player doesnt exist");
          return false;
        }
        try {
          TourneyHideSeek.switchPlayerTeam(player, args[2]);
        } catch (Exception e) {
          return false;
        }
        return true;
      }
      case "start": {
        Key font_key = Key.key("tubtext-thin");
        Scoreboard scoreboard = TourneyHideSeek.scoreboard;
        World world = cmd_sender.getWorld();
        TourneyHideSeek.game_running = true;
        // scoreboard.getTeam("hider_team").setOption(Team.Option.NAME_TAG_VISIBILITY,
        // Team.OptionStatus.FOR_OTHER_TEAMS);

        // show the title and subtitle for each player
        for (Player player : Bukkit.getOnlinePlayers()) {
          player.showTitle(ItemAndMsgBuilder.startSoonMsg(player));
        }

        // game start task before teleporting
        new BukkitRunnable() {
          public Integer game_start_timer = 40;

          @Override
          public void run() {
            game_start_timer -= 1;
            Component start_text_main = Component.text("Starting in...").font(font_key);

            if (game_start_timer <= 35 && game_start_timer >= 31) {
              world.playSound(SoundAndLocationBuilder.getSound("countdown"));
              world.showTitle(Title.title(start_text_main, Component.text(game_start_timer - 30).font(font_key)));
            }
            if (game_start_timer == 30) {
              world.playSound(SoundAndLocationBuilder.getSound("game_start"));
              world.clearTitle();

              ItemStack speed_item = ItemAndMsgBuilder.itembuilder("speed_orb");

              for (Player player : Bukkit.getOnlinePlayers()) {
                if (scoreboard.getTeam("seeker_team").hasPlayer(player)) {
                  player.teleport(SoundAndLocationBuilder.getLocation(world, "seeker_spawn"));
                  player.setGameMode(GameMode.SURVIVAL);
                  player.getInventory().addItem(speed_item);
                } else if (scoreboard.getTeam("hider_team").hasPlayer(player)) {
                  player.teleport(SoundAndLocationBuilder.getLocation(world, "hider_spawn"));
                  player.setGameMode(GameMode.SURVIVAL);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, (20 * 30), 1));
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * 30), 1));
                } else {
                  player.teleport(SoundAndLocationBuilder.getLocation(world, "hider_spawn"));
                  player.setGameMode(GameMode.CREATIVE);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                }
              }

              BorderController.seekerSpawnBorder(world);
            }
            if (game_start_timer < 30) {
              // after teleporting

              if (((game_start_timer % 10) == 0) || (game_start_timer <= 5)) {
                Component sec_release = Component.text(game_start_timer + " seconds until seekers are released!")
                    .font(font_key);
                world.sendMessage(sec_release);
              }
              if (game_start_timer == 0) {
                world.playSound(SoundAndLocationBuilder.getSound("game_start"));
                world.showBossBar(TourneyHideSeek.boss_bar);
                world.showTitle(ItemAndMsgBuilder.seekersHaveBeenReleased());

                BorderController.startWorldborderMain(world);
                cancel();
              }
            }
          }
        }.runTaskTimer(TourneyHideSeek.getInstance(), 0, 20);

        // return from "start" command
        return true;
      }
      case "stop": {
        TourneyHideSeek.game_running = false;
        TourneyHideSeek.caught_list = new Stack<Player>();
        World world = cmd_sender.getWorld();
        world.hideBossBar(TourneyHideSeek.boss_bar);

        BorderController.setBorderToLobby(world);

        for (Player player : Bukkit.getOnlinePlayers()) {
          player.teleport(SoundAndLocationBuilder.getLocation(world, "lobby_spawn"));
          player.getInventory().clear();
        }
        return true;
      }
      case "podium": {
        World world = cmd_sender.getWorld();
        PodiumAnimation.gameFinishPodium(world);
        return true;
      }
      case "debug": {
        Scoreboard scoreboard = TourneyHideSeek.scoreboard;
        Team hider_team = scoreboard.getTeam("hider_team");
        Team seeker_team = scoreboard.getTeam("seeker_team");
        commandSender.sendMessage("size of caught_list: " + TourneyHideSeek.caught_list.size());
        commandSender.sendMessage("size of hider_team: " + hider_team.getSize());
        commandSender.sendMessage("size of seeker_team: " + seeker_team.getSize());
        return true;
      }
      case "border_start_size": {
        if (args.length <= 1) {
          commandSender.sendMessage("current starting size: " + BorderController.border_start_size);
        } else {
          int size = Integer.parseInt(args[1]);
          BorderController.border_start_size = size;
          commandSender.sendMessage("the border starting size has been set to " + size);
        }
        return true;
      }
      case "get_item": {
        cmd_sender.getInventory().addItem(ItemAndMsgBuilder.itembuilder(args[1]));
        return true;
      }
      case "broadcast": {
        StringBuffer string_buffer = new StringBuffer();
        for (int i = 1; i < args.length; i++) {
          string_buffer.append(args[i] + " ");
        }
        cmd_sender.getWorld().sendMessage(ItemAndMsgBuilder.broadcast(string_buffer.toString()));
        return true;
      }
      case "hazard": {
        switch (args[1]) {
          case "lightning_storm":
            Hazards.LightningStorm();
            return true;
          case "night_vision":
            Hazards.Nightvision();
            return true;
          case "glowing":
            Hazards.glowing();
            return true;
          case "speedy":
            Hazards.speedy();
            return true;
          case "noisy":
            Hazards.noisy();
            return true;
          case "clockwork":
            Hazards.clockWork(Integer.parseInt(args[2]));
            return true;
        }
        return false;
      }
      case "set_all_hider": {
        Team hiders = TourneyHideSeek.scoreboard.getTeam("hider_team");
        for (Player p : Bukkit.getOnlinePlayers()) {
          TourneyHideSeek.switchPlayerTeam(p, hiders);
        }
        return true;
      }
      case "switch_world": {
        if (args[1].equals("zero")) {
          TourneyHideSeek.map = Map.ZERO;
          for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(SoundAndLocationBuilder.getLocation(cmd_sender.getWorld(), "lobby_spawn"));
          }
          return true;
        } else if (args[1].equals("fantasy")) {
          TourneyHideSeek.map = Map.FANTASY;
          for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(SoundAndLocationBuilder.getLocation(cmd_sender.getWorld(), "lobby_spawn"));
          }
          return true;
        } else if (args[1].equals("portside")) {
          TourneyHideSeek.map = Map.PORTSIDE;
          for (Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(SoundAndLocationBuilder.getLocation(cmd_sender.getWorld(), "lobby_spawn"));
          }
        } else {
          return false;
        }
      }
      default:
        return false;
    }
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (args.length == 1) {
      return Arrays.asList("start", "stop", "podium", "addteam", "border_start_size", "get_item", "broadcast",
          "hazard", "set_all_hider", "switch_world");
    }
    if (args.length == 2) {
      if (args[0].equals("get_item")) {
        return Arrays.asList("speed_orb", "trophy");
      } else if (args[0].equals("hazard")) {
        return Arrays.asList("lightning_storm", "night_vision", "glowing", "speedy", "noisy", "clockwork");
      } else if (args[0].equals("switch_world")) {
        return Arrays.asList("zero", "fantasy", "portside");
      } else {
        return null;
      }
    }
    if (args.length == 3) {
      if (args[1].equals("clockwork")) {
        return Arrays.asList("number");
      } else {
        return Arrays.asList("hider_team", "seeker_team", "spectator_team", "admin_team");
      }
    }
    return null;
  }
}
