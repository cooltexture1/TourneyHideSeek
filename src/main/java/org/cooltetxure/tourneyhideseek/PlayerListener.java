package org.cooltetxure.tourneyhideseek;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerListener implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    World w = player.getWorld();

    updateTabList(w);
    event.joinMessage(Component.text(""));

    Scoreboard scoreboard = TourneyHideSeek.scoreboard;
    player.setScoreboard(scoreboard);

    // joining while game is running is different
    if (TourneyHideSeek.game_running) {
      if (scoreboard.getTeam("spectator_team").hasPlayer(player)) {
        player.setGameMode(GameMode.CREATIVE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
      } else if (scoreboard.getTeam("seeker_team").hasPlayer(player)) {
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
      } else if (scoreboard.getTeam("hider_team").hasPlayer(player)) {
        TourneyHideSeek.switchPlayerTeam(player, "seeker_team");
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        tagPlayer(player, null);
      }
      player.teleport(SoundAndLocationBuilder.getLocation(w, "map_center"));
    } else {
      player.teleport(SoundAndLocationBuilder.getLocation(w, "lobby_spawn"));
    }

    Team player_team = null;
    for (Team t : scoreboard.getTeams()) {
      if (t.hasPlayer(player)) {
        player_team = t;
      }
    }
    if (player_team == null) {
      TourneyHideSeek.switchPlayerTeam(player, "hider_team");
    } else {
      TourneyHideSeek.switchPlayerTeam(player, player_team);
    }
  }

  @EventHandler
  public void onPlayerHit(EntityDamageByEntityEvent event) {
    event.setCancelled(true);
    if (!TourneyHideSeek.game_running) {
      return;
    }

    if ((event.getEntity() instanceof Player) && (event.getDamager() instanceof Player)) {
      Scoreboard scoreboard = TourneyHideSeek.scoreboard;
      Player damager = (Player) event.getDamager();
      Player damagee = (Player) event.getEntity();

      if (!(scoreboard.getTeam("seeker_team").hasPlayer(damager) &&
          scoreboard.getTeam("hider_team").hasPlayer(damagee))) {
        // not a relevant hit
        return;
      }

      tagPlayer(damagee, damager);
    }
  }

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent e) {
    Player p = e.getPlayer();
    Block block_under = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
    if (block_under.getType() == Material.PURPUR_BLOCK && !(p.hasPotionEffect(PotionEffectType.LEVITATION))) {
      p.playSound(SoundAndLocationBuilder.getSound("jump_pad"));
      p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (20 * 3), 2));
    }
  }

  static public void finishGameifOver() {
    World world = Bukkit.getWorlds().get(0);
    Scoreboard scoreboard = TourneyHideSeek.scoreboard;

    int players_online = 0;
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (scoreboard.getTeam("hider_team").hasPlayer(p)) {
        players_online += 1;
      }
    }

    if (players_online <= 1) {
      TourneyHideSeek.game_running = false;
      world.hideBossBar(TourneyHideSeek.boss_bar);

      world.playSound(SoundAndLocationBuilder.getSound("game_over"));
      world.showTitle(ItemAndMsgBuilder.gameEnded());

      Player last_survivor = null;
      for (Player player : Bukkit.getOnlinePlayers()) {
        if (scoreboard.getTeam("hider_team").hasPlayer(player)) {
          last_survivor = player;
        }
      }
      TourneyHideSeek.caught_list.add(last_survivor);

      new BukkitRunnable() {
        @Override
        public void run() {
          for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(SoundAndLocationBuilder.getLocation(world, "lobby_spawn"));
            if (player.isOp()) {
              try {
                player.sendMessage(ItemAndMsgBuilder.debugPlacements());
              } catch (Exception e) {
              }
            }
          }
          cancel();
        }
      }.runTaskLater(TourneyHideSeek.getInstance(), 20 * 5);
    }
  }

  static public void tagPlayer(Player p, Player catcher) {
    World world = Bukkit.getWorlds().get(0);
    Key font_key = Key.key("tubtext-thin");

    world.sendMessage(ItemAndMsgBuilder.playerCaughtMsg(p, catcher));
    world.playSound(SoundAndLocationBuilder.getSound("kill_sound"));

    p.getInventory().addItem(ItemAndMsgBuilder.itembuilder("speed_orb"));

    TourneyHideSeek.caught_list.add(p);
    TourneyHideSeek.switchPlayerTeam(p, "seeker_team");

    Component title = Component.text("You got caught!").font(font_key);
    Component subtitle = Component.text("You are now a Seeker").font(font_key);
    p.showTitle(Title.title(title, subtitle));
    finishGameifOver();
  }

  @EventHandler
  public void onHunger(FoodLevelChangeEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    if (event.getPlayer().isOp()) {
      event.setCancelled(false);
    } else {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if (e.getCause() == DamageCause.FALL) {
      e.setCancelled(true);
    } else {
      e.setCancelled(false);
    }
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    if (e.getMaterial().equals(Material.EMERALD)) {
      if (player.getCooldown(Material.EMERALD) > 0) {
        player.sendMessage(Component.text("The item is on cooldown").color(TextColor.color(255, 85, 85)));
      } else {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (20 * 10), 1));
        player.setCooldown(Material.EMERALD, (20 * 60));
        player.playSound(SoundAndLocationBuilder.getSound("craftmaster_speed"));
      }
    } else if (e.getMaterial().equals(Material.COAL)) {
      if (player.getCooldown(Material.COAL) > 0) {
        player.sendMessage(Component.text("The item is on cooldown").color(TextColor.color(255, 85, 85)));
      } else {
        Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        ItemAndMsgBuilder.trophyFirework(fw);
        player.setCooldown(Material.COAL, (5 * 20));
      }
    }

    if (player.isOp()) {
      e.setCancelled(false);
    } else {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent event) {
    if (TourneyHideSeek.game_running) {
      if (TourneyHideSeek.scoreboard.getTeam("hider_team").hasPlayer(event.getPlayer())) {
        tagPlayer(event.getPlayer(), null);
      }
    }
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    if (TourneyHideSeek.game_running) {
      event.setRespawnLocation(SoundAndLocationBuilder.getLocation(event.getPlayer().getWorld(), "map_center"));
    } else {
      event.setRespawnLocation(SoundAndLocationBuilder.getLocation(event.getPlayer().getWorld(), "lobby_spawn"));
    }
  }

  @EventHandler
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (TourneyHideSeek.game_running) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    if (TourneyHideSeek.scoreboard.getTeam("hider_team").hasPlayer(e.getPlayer()) && TourneyHideSeek.game_running) {
      tagPlayer(e.getPlayer(), null);
    }
    e.quitMessage(Component.text(""));

    World w = Bukkit.getWorlds().get(0);
    updateTabList(w);
  }

  public void updateTabList(World w) {
    Key font_key = Key.key("tubtext-thin");
    int on_players = Bukkit.getOnlinePlayers().size();
    Component header = Component.text("\n\n\n").append(Component.text("\n"))
        .append(Component.text("Hide ").font(font_key).color(NamedTextColor.BLUE))
        .append(Component.text("&").color(NamedTextColor.BLUE))
        .append(Component.text(" Seek\n").font(font_key).color(NamedTextColor.BLUE))
        .append(Component.text("Online Players: " + on_players + "\n").font(font_key));
    Component footer = Component.text("\nThank you for playing the event!").font(font_key).color(NamedTextColor.GOLD);
    w.sendPlayerListHeaderAndFooter(header, footer);
  }

}
