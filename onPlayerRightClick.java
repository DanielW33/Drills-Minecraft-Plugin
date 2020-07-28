import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class onPlayerRightClick implements Listener {
    private Drills plugin;
    public DrillDataManager DrillData;
    public BalanceManager BalData;

    public onPlayerRightClick(Drills plugin) {
        this.plugin = plugin;
        this.DrillData = this.plugin.getDrillData();
        this.BalData = this.plugin.getBalData();
    }

    @EventHandler
    public void RightClick(PlayerInteractEvent Event) {

        if (Event.getItem() != null) {
            if (Event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                Player player = Event.getPlayer();
                if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
                    return;
                }

                this.DrillData.getConfig().getConfigurationSection("Drills.").getKeys(false).forEach(Drills -> {
                    if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                            this.DrillData.getConfig().getString("Drills." + Drills + ".Name")))) {
                        Material tool = Material.matchMaterial(this.DrillData.getConfig().getString("Drills." + Drills + ".Material"));

                        BigDecimal TotXp = new BigDecimal(0);
                        String Job;
                        BigDecimal Balance = new BigDecimal(0);
                        Balance = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".Balance"));

                        if (tool == Material.DIAMOND_PICKAXE) {
                            Job = "Miner";
                            TotXp = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".MinerExp"));
                            if (TotXp.doubleValue() != 0) {
                                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".MinerExp", 0);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "Jobs " + "grantxp " + player.getName() + " " + Job + " " + TotXp.setScale(2, RoundingMode.CEILING));
                            }
                        } else if (tool == Material.DIAMOND_SHOVEL) {
                            Job = "Digger";
                            TotXp = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".DiggerExp"));
                            if (TotXp.doubleValue() != 0) {
                                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".DiggerExp", 0);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "Jobs " + "grantxp " + player.getName() + " " + Job + " " + TotXp.setScale(2, RoundingMode.CEILING));
                            }
                        } else {
                            Job = "WoodCutter";
                            TotXp = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".WoodCutterExp"));
                            if (TotXp.doubleValue() != 0) {
                                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".WoodCutterExp", 0);
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "Jobs " + "grantxp " + player.getName() + " " + Job + " " + TotXp.setScale(2,RoundingMode.CEILING));
                            }
                        }
                        if (Balance.doubleValue() != 0) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco " + "give " + player.getName() + " " + Balance.setScale(2, RoundingMode.CEILING));
                            this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".Balance", 0);
                        }
                        this.BalData.saveConfig();
                    }
                });
            }
        }
    }
}
