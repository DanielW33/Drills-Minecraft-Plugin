import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class onCommandDrills implements CommandExecutor {
    private Drills plugin;
    public DrillDataManager DrillData;
    public BalanceManager BalData;

    public onCommandDrills(Drills plugin){
        this.plugin = plugin;
        this.DrillData = this.plugin.getDrillData();
        this.BalData = this.plugin.getBalData();
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args){
        if(label.equalsIgnoreCase("Drills")){
            if(args.length < 1){
                Sender.sendMessage("Please input arguments");
                return false;
            }
            else if(args[0].equalsIgnoreCase("give")){
                if(args.length < 2){
                    Sender.sendMessage("Please use command /Drills give [User] [Drill]");
                    return false;
                }
                Player target = Bukkit.getPlayerExact(args[1]);
                if(target == null){
                    Sender.sendMessage("Please enter a valid user.");
                    return false;
                }
                else if(args.length < 3){
                    Sender.sendMessage("Please enter a drill  .");
                    return false;
                }
                else if(this.DrillData.getConfig().getString("Drills." + args[2]) == null){
                    Sender.sendMessage("Please enter a valid drill.");
                    return false;
                }

                Item myDrill = new Item();

                myDrill.setName(ChatColor.translateAlternateColorCodes('&', this.DrillData.getConfig().getString("Drills." + args[2] + ".Name")));

                myDrill.setDMaterial(Material.matchMaterial(this.DrillData.getConfig().getString("Drills." + args[2] + ".Material")));

                List<String> DrillAttributes = this.DrillData.getConfig().getStringList("Drills." + args[2] + ".Lore");
                String[] DrillAttributesArray = new String[DrillAttributes.size()];
                DrillAttributes.toArray(DrillAttributesArray);
                ArrayList<String> ItemLore = new ArrayList<>();

                for(int i = 0; i < DrillAttributesArray.length; i++){
                    DrillAttributesArray[i] = ChatColor.translateAlternateColorCodes('&', DrillAttributesArray[i]);
                }
                ItemLore.addAll(Arrays.asList(DrillAttributesArray));
                myDrill.setItemLore(ItemLore);

                ItemStack FinalDrill = new ItemStack(myDrill.getDMaterial());
                ItemMeta DrillMeta = FinalDrill.getItemMeta();

                if(myDrill.isEnchant()){
                    DrillMeta.addEnchant(Enchantment.MENDING, 0, true);
                    DrillMeta.addItemFlags((ItemFlag.HIDE_ENCHANTS));
                }
                DrillMeta.addItemFlags((ItemFlag.HIDE_ATTRIBUTES));

                DrillMeta.setDisplayName(myDrill.getName());
                DrillMeta.setLore(myDrill.getItemLore());

                FinalDrill.setItemMeta(DrillMeta);

                Player player = Bukkit.getPlayerExact(args[1]);
                if (player.getInventory().firstEmpty() == -1) {
                    World world = player.getWorld();
                    world.dropItem(player.getLocation(), FinalDrill);
                }
                else {
                    player.getInventory().setItem(player.getInventory().firstEmpty(), FinalDrill);
                }
                return true;
            }
            else if(args[0].equalsIgnoreCase("List")){
                Sender.sendMessage(ChatColor.BOLD + "Available Drill List: ");
                this.DrillData.getConfig().getConfigurationSection("Drills").getKeys(false).forEach(Drill ->{
                    Sender.sendMessage(ChatColor.GREEN + Drill);
                });
            }
            else if(args[0].equalsIgnoreCase("Reload")){
                this.DrillData.reloadConfig();
                Sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cReloading &bDrills &cConfig!"));
            }
            else{
                Sender.sendMessage("Incorrect arguments. Please use command /Drills list");
                return false;
            }
        }
        else if(label.equalsIgnoreCase("Drill")) {
            if (Sender instanceof Player) {
                if (args.length < 1) {
                    Sender.sendMessage(ChatColor.GREEN + "[Drills] To see balance acquired from using drills use the command /Drill Balance");
                    return false;
                }
                else if (args[0].equalsIgnoreCase("Balance") || args[0].equalsIgnoreCase("Bal")) {
                    Player player = (Player) Sender;
                    BigDecimal Assignment = new BigDecimal(0);
                    Assignment = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".Balance"));
                    Sender.sendMessage(ChatColor.BOLD + "Drill Balance and Experience:");
                    Sender.sendMessage(ChatColor.GREEN + "Balance: " + ChatColor.BLUE + "$" + Assignment.setScale(2, RoundingMode.CEILING));
                    Assignment = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".MinerExp"));
                    Sender.sendMessage(ChatColor.GREEN + "Miner Experience: " + ChatColor.BLUE + Assignment.setScale(2, RoundingMode.CEILING));
                    Assignment = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".DiggerExp"));
                    Sender.sendMessage(ChatColor.GREEN + "Digger Experience: " + ChatColor.BLUE + Assignment.setScale(2, RoundingMode.CEILING));
                    Assignment = BigDecimal.valueOf(this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".WoodCutterExp"));
                    Sender.sendMessage(ChatColor.GREEN + "WoodCutter Experience: " + ChatColor.BLUE + Assignment.setScale(2, RoundingMode.CEILING));
                    return true;
                }
                else{
                    Sender.sendMessage("Please use the command /Drill Bal");
                    return false;
                }
            }
            else{
                Sender.sendMessage(ChatColor.RED + "[Drills] If trying to view player data from console, Please view the Playerdata config file.");
                return false;
            }
        }
        return false;
    }
}
