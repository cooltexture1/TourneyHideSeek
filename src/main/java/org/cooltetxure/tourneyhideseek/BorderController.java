package org.cooltetxure.tourneyhideseek;

import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.key.Key;

import org.bukkit.World;
import org.bukkit.WorldBorder;

public class BorderController {
  static public int border_start_size = 550;

  public static void setBorderToLobby(World world) {
    WorldBorder wb = world.getWorldBorder();

    wb.setCenter(SoundAndLocationBuilder.getLocation(world, "lobby_spawn"));
    wb.setSize(1000);
    wb.setWarningDistance(0);
    wb.setDamageAmount(0);
  }

  public static void setBorderPodium(World world) {
    WorldBorder wb = world.getWorldBorder();

    wb.setSize(1000);
    wb.setCenter(SoundAndLocationBuilder.getLocation(world, "podium_room"));
    wb.setWarningDistance(0);
    wb.setDamageAmount(0);
  }

  public static void seekerSpawnBorder(World world) {
    WorldBorder wb = world.getWorldBorder();

    wb.setCenter(SoundAndLocationBuilder.getLocation(world, "seeker_spawn"));
    wb.setSize(5);
    wb.setWarningDistance(0);
    wb.setDamageAmount(0);
  }

  public static void startWorldborderMain(World world) {
    WorldBorder wb = world.getWorldBorder();

    wb.setCenter(SoundAndLocationBuilder.getLocation(world, "map_center"));
    wb.setSize(BorderController.border_start_size);
    wb.setWarningDistance(10);
    wb.setDamageAmount(2);

    if (TourneyHideSeek.map == Map.ZERO) {
      return;
    }

    startBorderDecrease(world);
  }

  public static void startBorderDecrease(World world) {
    WorldBorder wb = world.getWorldBorder();
    Key font_key = Key.key("tubtext-thin");

    new BukkitRunnable() {
      public int game_timer = 0;
      public int border_cycles = 1;
      public int curr_periode_start = 0;

      @Override
      public void run() {
        if ((!TourneyHideSeek.game_running) || (wb.getSize() <= 150)) {
          cancel();
        }
        game_timer += 1;

        int curr_periode_len = border_cycles * 5 * 60;
        int secs_until_next_decrease = curr_periode_len - (game_timer - curr_periode_start);
        float progres = 1 - (((float) secs_until_next_decrease) / curr_periode_len);
        TourneyHideSeek.boss_bar.progress(progres);
        TourneyHideSeek.boss_bar.name(Component
            .text("Next Border Shrink in: " + secs_until_next_decrease)
            .font(font_key));

        if (game_timer == (curr_periode_start + curr_periode_len)) {
          curr_periode_start = game_timer;
          if (border_cycles <= 4) {
            // border increse every 20 mins
            border_cycles += 1;
          }
          long new_wb_size = Math.round(wb.getSize() - 50);
          wb.setSize(new_wb_size, 30);
          world.sendMessage(ItemAndMsgBuilder.borderShrinkAnnouncment(game_timer / 60, (int) new_wb_size));
          world.playSound(SoundAndLocationBuilder.getSound("border_close"));
        }

      }
    }.runTaskTimer(TourneyHideSeek.getInstance(), 0, 20);

  }
}
