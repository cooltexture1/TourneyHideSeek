package org.cooltetxure.tourneyhideseek;

import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PodiumAnimation {
  // this expects the last surviving player to be put into the caught list befor
  // its run
  static public void gameFinishPodium(World world) {
    TourneyHideSeek.game_running = false;

    Location podium_first = new Location(world, 480, 92, 1268, -90, 0);
    Location podium_second = new Location(world, 480, 91, 1275);
    Location podium_third = new Location(world, 480, 90, 1261);
    world.hideBossBar(TourneyHideSeek.boss_bar);
    world.playSound(SoundAndLocationBuilder.getSound("end_sound"));

    for (Player player : Bukkit.getOnlinePlayers()) {
      player.teleport(SoundAndLocationBuilder.getLocation(world, "podium_room"));
    }
    BorderController.setBorderPodium(world);

    new BukkitRunnable() {
      int timer = 0;
      final int caught_amount = TourneyHideSeek.caught_list.size();

      @Override
      public void run() {
        if (timer == 5) {
          Firework fw = (Firework) world.spawnEntity(podium_third.clone().add(5, 0, 0), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
          try {
            Player player = TourneyHideSeek.caught_list.get(caught_amount - 3);
            player.getInventory().addItem(ItemAndMsgBuilder.itembuilder("trophy"));
            player.teleport(podium_third);
            world.showTitle(ItemAndMsgBuilder.showWinnerWon(player, 3));
          } catch (Exception e) {
            Bukkit.getOnlinePlayers().forEach(p -> {
              if (p.isOp()) {
                p.sendMessage("[debug] there was no player in third place: " + e);
              }
            });
          }
        } else if (timer == 6) {
          Firework fw = (Firework) world.spawnEntity(podium_third.clone().add(5, 0, -2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
        } else if (timer == 7) {
          Firework fw = (Firework) world.spawnEntity(podium_third.clone().add(5, 0, 2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
        } else if (timer == 15) {
          Firework fw = (Firework) world.spawnEntity(podium_second.clone().add(5, 0, 0), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
          try {
            Player player = TourneyHideSeek.caught_list.get(caught_amount - 2);
            player.getInventory().addItem(ItemAndMsgBuilder.itembuilder("trophy"));
            player.teleport(podium_second);
            world.showTitle(ItemAndMsgBuilder.showWinnerWon(player, 2));
          } catch (Exception e) {
            Bukkit.getOnlinePlayers().forEach(p -> {
              if (p.isOp()) {
                p.sendMessage("[debug] there was no player in second place: " + e);
              }
            });
          }
        } else if (timer == 16) {
          Firework fw = (Firework) world.spawnEntity(podium_second.clone().add(5, 0, 2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
        } else if (timer == 17) {
          Firework fw = (Firework) world.spawnEntity(podium_second.clone().add(5, 0, -2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
        } else if (timer == 25) {
          Firework fw = (Firework) world.spawnEntity(podium_first.clone().add(5, 0, 0), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
          try {
            Player player = TourneyHideSeek.caught_list.get(caught_amount - 1);
            player.getInventory().addItem(ItemAndMsgBuilder.itembuilder("trophy"));
            player.teleport(podium_first);
            world.showTitle(ItemAndMsgBuilder.showWinnerWon(player, 1));
          } catch (Exception e) {
            Bukkit.getOnlinePlayers().forEach(p -> {
              if (p.isOp()) {
                p.sendMessage("[debug] there was no player in first place: " + e);
              }
            });
          }
        } else if (timer == 26) {
          Firework fw = (Firework) world.spawnEntity(podium_first.clone().add(5, 0, 2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
          Firework fw2 = (Firework) world.spawnEntity(podium_second.clone().add(5, 0, 2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw2);
        } else if (timer == 27) {
          Firework fw = (Firework) world.spawnEntity(podium_first.clone().add(5, 0, -2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw);
          Firework fw2 = (Firework) world.spawnEntity(podium_third.clone().add(5, 0, 2), EntityType.FIREWORK);
          ItemAndMsgBuilder.podiumFirework(fw2);
        } else if (timer == 38) {
          cancel();
          Player third = TourneyHideSeek.caught_list.get(caught_amount - 3);
          Player second = TourneyHideSeek.caught_list.get(caught_amount - 2);
          Player first = TourneyHideSeek.caught_list.get(caught_amount - 1);
          third.teleport(SoundAndLocationBuilder.getLocation(world, "podium_room"));
          second.teleport(SoundAndLocationBuilder.getLocation(world, "podium_room"));
          first.teleport(SoundAndLocationBuilder.getLocation(world, "podium_room"));
          TourneyHideSeek.caught_list = new Stack<Player>();
        }

        timer += 1;
      }
    }.runTaskTimer(TourneyHideSeek.getInstance(), 0, 20);
  }
}
