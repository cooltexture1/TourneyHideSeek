package org.cooltetxure.tourneyhideseek;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.key.Key;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

enum Map {
  FANTASY,
  ZERO,
  PORTSIDE,
}

public final class TourneyHideSeek extends JavaPlugin {
  static public boolean game_running = false;
  static public List<Player> caught_list = new ArrayList<Player>();
  static public Scoreboard scoreboard;
  static public BossBar boss_bar;
  static public Map map = Map.FANTASY;

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    getCommand("game").setExecutor(new CommandListener());

    setupScoreboard();
    setupBossBar();

    scoreboardUpdater();
  }

  @Override
  public void onDisable() {
  }

  public static TourneyHideSeek getInstance() {
    return getPlugin(TourneyHideSeek.class);
  }

  private void setupBossBar() {
    Key font_key = Key.key("tubtext-thin");
    Component bar_name = Component.text("Time until next border decrease").font(font_key);
    BossBar bb = BossBar.bossBar(bar_name, 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

    TourneyHideSeek.boss_bar = bb;
  }

  public static void switchPlayerTeam(Player p, String t) {
    switchPlayerTeam(p, TourneyHideSeek.scoreboard.getTeam(t));
  }

  public static void switchPlayerTeam(Player p, Team t) {
    t.addPlayer(p);
    Component chat_name;
    if (t.getName().equals("hider_team")) {
      chat_name = Component.text("\uE003 ").color(NamedTextColor.WHITE)
          .append(Component.text(p.getName()).color(NamedTextColor.AQUA));
    } else if (t.getName().equals("seeker_team")) {
      chat_name = Component.text("\uE002 ").color(NamedTextColor.WHITE)
          .append(Component.text(p.getName()).color(NamedTextColor.DARK_RED));
    } else if (t.getName().equals("admin_team")) {
      chat_name = Component.text("ꀪ ").color(NamedTextColor.WHITE)
          .append(Component.text(p.getName()).color(NamedTextColor.RED));
    } else {
      chat_name = Component.text(" ").color(NamedTextColor.WHITE)
          .append(Component.text(p.getName()).color(NamedTextColor.GRAY));
    }
    p.displayName(chat_name);

  }

  private void setupScoreboard() {
    Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    // if a team doesnt exist, we create it
    if (scoreboard.getTeam("hider_team") == null) {
      Team team_h = scoreboard.registerNewTeam("hider_team");
      team_h.setCanSeeFriendlyInvisibles(true);
      team_h.prefix(Component.text(" \uE003 ").color(TextColor.color(255, 255, 255)));
      team_h.color(NamedTextColor.AQUA);
    }
    if (scoreboard.getTeam("seeker_team") == null) {
      Team team_se = scoreboard.registerNewTeam("seeker_team");
      team_se.setCanSeeFriendlyInvisibles(true);
      team_se.prefix(Component.text(" \uE002 ").color(TextColor.color(255, 255, 255)));
      team_se.color(NamedTextColor.DARK_RED);
    }
    if (scoreboard.getTeam("spectator_team") == null) {
      Team team_sp = scoreboard.registerNewTeam("spectator_team");
      team_sp.setCanSeeFriendlyInvisibles(true);
      team_sp.prefix(Component.text("  ").color(TextColor.color(255, 255, 255)));
      team_sp.color(NamedTextColor.GRAY);
    }
    if (scoreboard.getTeam("admin_team") == null) {
      Team team_a = scoreboard.registerNewTeam("admin_team");
      team_a.setCanSeeFriendlyInvisibles(true);
      team_a.prefix(Component.text(" ꀪ ").color(TextColor.color(255, 255, 255)));
      team_a.color(NamedTextColor.RED);
    }

    // the sidebar
    Component title = Component.text(ChatColor.BOLD + "" + ChatColor.WHITE + "ᴛᴜʙɴᴇᴛ ᴛᴏᴜʀɴᴇʏѕ");
    Objective obj = scoreboard.registerNewObjective(TourneyHideSeek.getInstance().getName(), "dummy", title);

    obj.setDisplaySlot(DisplaySlot.SIDEBAR);

    obj.getScore(ChatColor.BOLD + "" + ChatColor.GOLD + "ᴇᴠᴇɴᴛ: " + ChatColor.DARK_GREEN + "9/24/23").setScore(13);
    obj.getScore(ChatColor.DARK_RED + "").setScore(12);
    // obj.getScore(ChatColor.GOLD + "").setScore(11);
    obj.getScore(ChatColor.DARK_BLUE + "").setScore(10);
    obj.getScore(ChatColor.WHITE + "ᴍᴀᴘ: " + ChatColor.WHITE + "ꜰᴀɴᴛᴀѕʏ").setScore(9);
    obj.getScore(ChatColor.BOLD + "").setScore(8);
    obj.getScore(ChatColor.DARK_GREEN + "").setScore(7);
    obj.getScore(ChatColor.DARK_AQUA + "").setScore(6);
    obj.getScore(ChatColor.DARK_GRAY + "").setScore(5);
    obj.getScore(ChatColor.WHITE + "    ᴛʜᴀɴᴋ ʏᴏᴜ").setScore(4);
    obj.getScore(ChatColor.WHITE + "  ꜰᴏʀ ᴄᴏᴍᴘᴇᴛɪɴɢ").setScore(3);
    obj.getScore(ChatColor.BLACK + "").setScore(2);
    obj.getScore(ChatColor.WHITE + "ꅮ").setScore(1);

    Key font_key = Key.key("tubtext-thin");

    // Team pteam_display = scoreboard.registerNewTeam("pteam_display");
    // String pteam_display_key = ChatColor.GOLD + "";
    // pteam_display.addEntry(pteam_display_key);
    // pteam_display.prefix(Component.text("Team:
    // ").color(NamedTextColor.GOLD).font(font_key));
    // pteam_display.suffix(Component.text("invalid_team").color(NamedTextColor.WHITE).font(font_key));
    // obj.getScore(pteam_display_key).setScore(11);

    Team player_count = scoreboard.registerNewTeam("player_count");
    String player_count_key = ChatColor.DARK_BLUE + "";
    player_count.addEntry(player_count_key);
    player_count.prefix(Component.text("Player Count: ").color(NamedTextColor.WHITE).font(font_key));
    player_count.suffix(Component.text("invalid_player_count").color(NamedTextColor.WHITE).font(font_key));
    obj.getScore(player_count_key).setScore(10);

    Team hider_count = scoreboard.registerNewTeam("hider_count");
    String hider_count_key = ChatColor.DARK_GREEN + "";
    hider_count.addEntry(hider_count_key);
    hider_count
        .prefix(Component.text(" \uE003: ").decorate(TextDecoration.BOLD));
    hider_count.suffix(
        Component.text("invalid_player_count").decorate(TextDecoration.BOLD).color(NamedTextColor.WHITE)
            .font(font_key));
    obj.getScore(hider_count_key).setScore(7);

    Team seeker_count = scoreboard.registerNewTeam("seeker_count");
    String seeker_count_key = ChatColor.DARK_AQUA + "";
    seeker_count.addEntry(seeker_count_key);
    seeker_count.prefix(
        Component.text(" \uE002: ").decorate(TextDecoration.BOLD));
    seeker_count.suffix(Component.text("invalid_player_count").decorate(TextDecoration.BOLD).color(NamedTextColor.WHITE)
        .font(font_key));
    obj.getScore(hider_count_key).setScore(6);

    TourneyHideSeek.scoreboard = scoreboard;
  }

  public void scoreboardUpdater() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
          Scoreboard s = p.getScoreboard();

          // Team pteam_display = s.getTeam("pteam_display");
          // pteam_display.suffix(ItemAndMsgBuilder.getPlayerTeam(p));

          Team player_count = s.getTeam("player_count");
          player_count.suffix(Component.text("" + Bukkit.getOnlinePlayers().size()));

          int h_count = 0;
          int s_count = 0;
          for (Player player : Bukkit.getOnlinePlayers()) {
            if (s.getTeam("hider_team").hasPlayer(player)) {
              h_count += 1;
            } else if (s.getTeam("seeker_team").hasPlayer(player)) {
              s_count += 1;
            }
          }
          Team hider_count = s.getTeam("hider_count");
          hider_count.suffix(Component.text("" + h_count));

          Team seeker_count = s.getTeam("seeker_count");
          seeker_count.suffix(Component.text("" + s_count));

        }
      }
    }.runTaskTimer(TourneyHideSeek.getInstance(), 20, 20);

  }
}
