package org.cooltetxure.tourneyhideseek;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Hazards {

  public static int GetHiderAmount() {
    int h_count = 0;
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (TourneyHideSeek.scoreboard.getTeam("hider_team").hasPlayer(player)) {
        h_count += 1;
      }
    }
    return h_count;
  }

  public static void LightningStorm() {
    int h_count = GetHiderAmount();
    if (h_count < 3) {
      for (Player p : Bukkit.getOnlinePlayers()) {
        if (p.isOp()) {
          p.sendMessage("[debug] cant start LightningStorm because less than 3 hiders exist");
          return;
        }
      }
    }
    List<Player> hiders = getHidersShuffled();
    hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.generateHazard());

    new BukkitRunnable() {
      @Override
      public void run() {
        hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.hazardText("lightning_storm"));
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 5));

    Random rand = new Random();
    int start_value = rand.nextInt(h_count - 2);

    new BukkitRunnable() {
      @Override
      public void run() {
        for (int i = start_value; i < 3; i++) {
          hiders.get(i).getWorld().strikeLightningEffect(hiders.get(i).getLocation());
        }
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 10));
  }

  public static void Nightvision() {
    List<Player> hiders = getHidersShuffled();
    hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.generateHazard());

    new BukkitRunnable() {
      public void run() {
        hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.hazardText("night_vision"));
        for (Player p : Bukkit.getOnlinePlayers()) {
          if (TourneyHideSeek.scoreboard.getTeam("seeker_team").hasPlayer(p)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, (20 * 30), 1));
          }
        }
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 5));
  }

  public static void glowing() {
    List<Player> hiders = getHidersShuffled();
    hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.generateHazard());

    int h_count = GetHiderAmount();
    Random rand = new Random();
    int select_player = rand.nextInt(h_count);
    Player selected_player = hiders.get(select_player);

    new BukkitRunnable() {
      @Override
      public void run() {
        hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.hazardText("glowing"));
        hiders.get(0).getWorld()
            .sendMessage(Component.text("The selected player is: ").append(selected_player.displayName()));
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (5 * 20));

    new BukkitRunnable() {
      int timer = 0;

      @Override
      public void run() {
        if (timer % 2 == 0) {
          selected_player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (20 * 30), 1));
        } else {
          selected_player.removePotionEffect(PotionEffectType.GLOWING);
        }
        if (timer == 7) {
          cancel();
        }
        timer++;
      }
    }.runTaskTimer(TourneyHideSeek.getInstance(), (20 * 8), 20);
  }

  public static void speedy() {
    int h_count = GetHiderAmount();

    List<Player> hiders = getHidersShuffled();
    hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.generateHazard());

    new BukkitRunnable() {
      @Override
      public void run() {
        hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.hazardText("speedy"));
        for (int i = 0; i < h_count; i++) {
          hiders.get(i).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * 15), 1));
        }
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 5));
  }

  public static void noisy() {
    List<Player> hiders = getHidersShuffled();
    hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.generateHazard());

    new BukkitRunnable() {
      @Override
      public void run() {
        hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.hazardText("noisy"));
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 5));

    new BukkitRunnable() {
      int times = 0;

      @Override
      public void run() {
        times++;
        for (Player p : hiders) {
          World w = p.getWorld();
          w.playSound(SoundAndLocationBuilder.getSound("meow"), p);
        }
        if (times >= 3) {
          cancel();
        }
      }
    }.runTaskTimer(TourneyHideSeek.getInstance(), (20 * 8), (20 * 2));
  }

  public static List<Player> getHidersShuffled() {
    List<Player> hider_buffer = new ArrayList<Player>();
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (TourneyHideSeek.scoreboard.getTeam("hider_team").hasPlayer(p)) {
        hider_buffer.add(p);
      }
    }
    Collections.shuffle(hider_buffer);
    return hider_buffer;
  }

  public static void clockWork(int x) {
    List<Player> hiders = getHidersShuffled();
    World w = hiders.get(0).getWorld();
    w.sendMessage(ItemAndMsgBuilder.generateHazard());

    new BukkitRunnable() {
      @Override
      public void run() {
        hiders.get(0).getWorld().sendMessage(ItemAndMsgBuilder.clockworkText(x));
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 5));

    new BukkitRunnable() {
      @Override
      public void run() {
        WorldBorder wb = w.getWorldBorder();
        wb.setSize(wb.getSize() - x, 30);
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 8));
  }

  public static void visualAid() {
    List<Player> hiders = getHidersShuffled();
    World w = hiders.get(0).getWorld();
    w.sendMessage(ItemAndMsgBuilder.generateHazard());

    Team team_h = TourneyHideSeek.scoreboard.getTeam("hider_team");

    new BukkitRunnable() {
      @Override
      public void run() {
        w.sendMessage(ItemAndMsgBuilder.hazardText("visual_aid"));
        team_h.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 5));

    new BukkitRunnable() {
      @Override
      public void run() {
        team_h.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
      }
    }.runTaskLater(TourneyHideSeek.getInstance(), (20 * 15));
  }

}
