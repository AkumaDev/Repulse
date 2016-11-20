package pw.mcspook.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import lombok.Getter;
import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;
import pw.mcspook.main.lobby.ServerSelector;
import pw.mcspook.main.managers.PlayerEvents;
import pw.mcspook.main.scoreboard.ScoreboardHelper;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Repulse extends JavaPlugin implements Listener
{
	@Getter
	public static Repulse instance;
	@Getter
	public static Plugin plugin;
   PluginManager pm;
   public static Plugin pl;
   private DecimalFormat formatter;
   private List<String> base;
   private String bars;
   private String title;
   private Map<Player, ScoreboardHelper> scoreboardHelperMap;
   
   public Repulse() {
       this.pm = Bukkit.getServer().getPluginManager();
       this.formatter = new DecimalFormat("0.0");
       this.scoreboardHelperMap = new HashMap<Player, ScoreboardHelper>();
   }
   
   public void onEnable() {
	   instance = this;
		 getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
		 Bukkit.getPluginManager().registerEvents(new ServerSelector(), this);
		 Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
       Player[] onlinePlayers;
       for (int length = (onlinePlayers = Bukkit.getServer().getOnlinePlayers()).length, i = 0; i < length; ++i) {
           final Player player = onlinePlayers[i];
       }
       this.saveDefaultConfig();
       this.getConfig().options().copyDefaults();
       this.base = this.translate(this.getConfig().getStringList("SCOREBOARD.LINES"));
       this.bars = this.translate(this.getConfig().getString("SCOREBOARD.SETTINGS.BARS"));
       this.title = this.translate(this.getConfig().getString("SCOREBOARD.TITLE"));
       this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
       new BukkitRunnable() {
           public void run() {
               for (final Map.Entry<Player, ScoreboardHelper> entry : Repulse.this.scoreboardHelperMap.entrySet()) {
                   final Player player = entry.getKey();
                   final ScoreboardHelper helper = entry.getValue();
                   helper.clear();
                   boolean addedSomethingButBars = false;
                   for (String string : Repulse.this.base) {
                       boolean skip = false;
                       if (string.contains("%bars%")) {
                           string = string.replace("%bars%", Repulse.this.bars);
                       }
                       else {
                            if (string.contains("%online%")) {
                            	string = string.replace("%online%", String.valueOf(Bukkit.getServer().getOnlinePlayers().length));
                            } else {
                            	if(string.contains("%rank%")) {
                            		Player p = (Player) Bukkit.getPlayer(getName());
                            		PermissionUser user = PermissionsEx.getUser(player);
                            		string = string.replace("%rank%", String.valueOf(user.getRank(getName())));
                            	}
                            }
                           if (!skip) {
                               addedSomethingButBars = true;
                           }
                       }
                       if (!skip) {
                           helper.add(string);
                       }
                   }
                   if (!addedSomethingButBars) {
                       helper.clear();
                   }
                   helper.update(player);
               }
           }
       }.runTaskTimer((Plugin)this, 1L, 1L);
       Player[] onlinePlayers2;
       for (int length2 = (onlinePlayers2 = Bukkit.getOnlinePlayers()).length, j = 0; j < length2; ++j) {
           final Player player2 = onlinePlayers2[j];
           this.onJoin(player2);
       }
   }
   
   @EventHandler
   public void onPlayerJoin(final PlayerJoinEvent event) {
       this.onJoin(event.getPlayer());
   }
   
   public void onJoin(final Player player) {
       new BukkitRunnable() {
           public void run() {
               if (player.isOnline()) {
                   final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                   final ScoreboardHelper scoreboardHelper = new ScoreboardHelper(scoreboard, Repulse.this.title);
                   Repulse.this.scoreboardHelperMap.put(player, scoreboardHelper);
               }
           }
       }.runTaskLater((Plugin)this, 20L);
   }
   
   @EventHandler
   public void onPlayerQuit(final PlayerQuitEvent event) {
       final Player player = event.getPlayer();
       this.scoreboardHelperMap.remove(player);
   }
   
   public DecimalFormat getDecimalFormatter() {
       return this.formatter;
   }
   
   public String translate(final String input) {
       return StringEscapeUtils.unescapeJava(ChatColor.translateAlternateColorCodes('&', input));
   }
   
   public List<String> translate(final List<String> input) {
       final List<String> text = new ArrayList<String>();
       for (final String string : input) {
           text.add(this.translate(string));
       }
       return text;
   }
}
