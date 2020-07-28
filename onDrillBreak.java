import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import net.minecraft.server.v1_16_R1.EnumDirection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;



public class onDrillBreak implements Listener {
    private Drills plugin;
    public DrillDataManager DrillData;
    public BalanceManager BalData;
    private double on_Jobs;
    private double on_Jobs_xp;
    private boolean isPickaxe;
    private boolean isShovel;
    private boolean isAxe;
    private Material tool;

    public onDrillBreak(Drills plugin) {
        this.plugin = plugin;
        this.DrillData = this.plugin.getDrillData();
        this.BalData = this.plugin.getBalData();
    }
    @EventHandler
    public void onDrillUse(BlockBreakEvent Event) {
        Player player = Event.getPlayer();
        boolean Truefalse = PlayerCacheUtil.getCachePermission(player, Event.getBlock().getLocation(), Event.getBlock().getType(), TownyPermission.ActionType.DESTROY);
        if(Event.isCancelled() || !Truefalse){
            return;
        }

        on_Jobs = 0;
        on_Jobs_xp = 0;
        if (player.getInventory().getItemInMainHand().getItemMeta() == null) {
            return;
        }
        this.DrillData.getConfig().getConfigurationSection("Drills").getKeys(false).forEach(Drills -> {
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',
                    this.DrillData.getConfig().getString("Drills." + Drills + ".Name")))) {

                isPickaxe = false; isShovel = false; isAxe = false;
                tool = Material.matchMaterial(this.DrillData.getConfig().getString("Drills." + Drills + ".Material"));
                if(tool == Material.DIAMOND_PICKAXE){
                    isPickaxe = true;
                }
                else if(tool == Material.DIAMOND_SHOVEL){
                    isShovel = true;
                }
                else{
                    isAxe = true;
                }

                Block myBlock = Event.getBlock();
                Location BlockLocation = myBlock.getLocation();

                CraftPlayer CPlayer = (CraftPlayer) player;
                EnumDirection Direction = CPlayer.getHandle().getDirection();
                int Size = this.DrillData.getConfig().getInt("Drills." + Drills + ".MineArea");
                if (player.isSneaking()) {
                    UpDown(player, BlockLocation, Size, Drills);
                }
                else {
                    if (Direction == EnumDirection.NORTH || Direction == EnumDirection.SOUTH) {
                        NorthSouth(player, BlockLocation, Size, Drills);
                    } else if (Direction == EnumDirection.EAST || Direction == EnumDirection.WEST) {
                        EastWest(player, BlockLocation, Size, Drills);
                    }
                }
                if(this.DrillData.getConfig().getBoolean("Job") && (isPickaxe || isShovel || isAxe)){
                    SavePlayerData(player);
                    return;
                }
            }
        });
    }

    public void NorthSouth(Player player, Location BlockLocation, int Size, String Drills) {
        int x = BlockLocation.getBlockX() - (Size / 2), y = BlockLocation.getBlockY() + (Size / 2);
        BlockLocation.setX(x);
        BlockLocation.setY(y);
        for (int i = 0; i < Size; i++) {
            BlockLocation.setY(y - i);
            for (int j = 0; j < Size; j++) {
                BlockLocation.setX(x + j);
                checkBlock(player, BlockLocation, Drills);
            }
        }
    }

    public void EastWest(Player player, Location BlockLocation, int Size, String Drills) {
        int y = BlockLocation.getBlockY() + (Size / 2), z = BlockLocation.getBlockZ() - (Size / 2);
        BlockLocation.setZ(z);
        BlockLocation.setY(y);
        for (int i = 0; i < Size; i++) {
            BlockLocation.setY(y - i);
            for (int j = 0; j < Size; j++) {
                BlockLocation.setZ(z + j);
                checkBlock(player, BlockLocation, Drills);
            }
        }
    }

    public void UpDown(Player player, Location BlockLocation, int Size, String Drills) {
        int x = BlockLocation.getBlockX() + (Size / 2), z = BlockLocation.getBlockZ() - (Size / 2);
        BlockLocation.setZ(z);
        BlockLocation.setX(x);
        for (int i = 0; i < Size; i++) {
            BlockLocation.setX(x - i);
            for (int j = 0; j < Size; j++) {
                BlockLocation.setZ(z + j);
                checkBlock(player ,BlockLocation, Drills);
            }
        }
    }
    //Added in per item pay rates and exp with the 1.16.1 Update.
    public void checkBlock(Player player,Location BlockLocation, String Drills) {
        if(PlayerCacheUtil.getCachePermission(player, BlockLocation.getBlock().getLocation(), BlockLocation.getBlock().getType(), TownyPermission.ActionType.DESTROY)) {
            List<String> AllowedBlocks = this.DrillData.getConfig().getStringList("Drills." + Drills + ".Block");
            for (int i = 0; i < AllowedBlocks.size(); i++) {
                //Added on String SPlit
                String[] GetData = AllowedBlocks.get(i).split(":", 4);
                //Changed if statement from AllowedBlocks.get(i) to GetData[0]
                if (BlockLocation.getBlock().getType() == Material.matchMaterial(GetData[0])) {
                    if (this.DrillData.getConfig().getBoolean("Job") && (isPickaxe || isShovel || isAxe)) {
                        ifJobMiner(player, GetData[1], GetData[2]);
                    }
                    BlockLocation.getBlock().setType(Material.AIR);
                    // ((CraftPlayer)player).getHandle().playerInteractManager.breakBlock(new BlockPosition(BlockLocation.getX(),BlockLocation.getY(),BlockLocation.getZ()));

                    World AffectWorld = player.getWorld();
                    ItemMeta Meta = player.getInventory().getItemInMainHand().getItemMeta();
                    Map<Enchantment, Integer> Enchant = Meta.getEnchants();

                    if (Enchant == null) {
                        return;
                    } else if (Enchant.containsKey(Enchantment.SILK_TOUCH)) {
                        AffectWorld.dropItem(BlockLocation, new ItemStack(Material.matchMaterial(GetData[0]), Integer.parseInt(GetData[3])));
                    } else {
                        List<String> Translation = this.DrillData.getConfig().getStringList("Drills." + Drills + ".Translation");
                        AffectWorld.dropItem(BlockLocation, new ItemStack(Material.matchMaterial(Translation.get(i)), Integer.parseInt(GetData[3])));
                    }
                    AffectWorld.playSound(BlockLocation, Sound.BLOCK_STONE_BREAK, 1f, 1f);
                }
            }
        }
    }

    public void ifJobMiner(Player player, String PayAmount, String ExpAmount) {
        List<JobProgression> jobs = Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression();
        double rate = Double.parseDouble(PayAmount);
        double xprate = Double.parseDouble(ExpAmount);
        ItemStack hand = player.getInventory().getItemInMainHand();
        for (JobProgression OneJob : jobs) {
            if (OneJob.getJob().getName().equalsIgnoreCase("Miner") && hand.getType() == Material.DIAMOND_PICKAXE) {
                on_Jobs = on_Jobs + rate;
                on_Jobs_xp = on_Jobs_xp + xprate;
            }
            else if(OneJob.getJob().getName().equalsIgnoreCase("WoodCutter") && hand.getType() == Material.DIAMOND_AXE){
                on_Jobs = on_Jobs + rate;
                on_Jobs_xp = on_Jobs_xp + xprate;
            }
            else if(OneJob.getJob().getName().equalsIgnoreCase("Digger") && hand.getType() == Material.DIAMOND_SHOVEL){
                on_Jobs = on_Jobs + rate;
                on_Jobs_xp = on_Jobs_xp + xprate;
            }
        }
    }
    public void SavePlayerData(Player player){
        if (!this.BalData.getConfig().contains("players." + player.getUniqueId().toString())) {
            this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".Player-Name", player.getName());
            this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".Balance", on_Jobs);
            if(Material.DIAMOND_PICKAXE == tool){
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".MinerExp", on_Jobs_xp);
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".DiggerExp", 0);
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".WoodCutterExp", 0);
            }
            else if(Material.DIAMOND_SHOVEL == tool){
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".DiggerExp", on_Jobs_xp);
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".MinerExp", 0);
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".WoodCutterExp", 0);
            }
            else{
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".WoodCutterExp", on_Jobs_xp);
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".DiggerExp", 0);
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".MinerExp", 0);
            }
            this.BalData.saveConfig();
        }
        else{
            double bal, exp;
            bal = this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".Balance");
            if(tool == Material.DIAMOND_PICKAXE){
                exp = this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".MinerExp");
                exp = exp + on_Jobs_xp;
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".MinerExp", exp);
                bal = bal + on_Jobs;
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".Balance", bal);
            }
            else if(tool == Material.DIAMOND_SHOVEL){
                exp = this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".DiggerExp");
                exp = exp + on_Jobs_xp;
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".DiggerExp", exp);
                bal = bal + on_Jobs;
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".Balance", bal);
            }
            else{
                exp = this.BalData.getConfig().getDouble("players." + player.getUniqueId().toString() + ".WoodCutterExp");
                exp = exp + on_Jobs_xp;
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".WoodCutterExp", exp);
                bal = bal + on_Jobs;
                this.BalData.getConfig().set("players." + player.getUniqueId().toString() + ".Balance", bal);
            }
            this.BalData.saveConfig();
        }
    }
}
