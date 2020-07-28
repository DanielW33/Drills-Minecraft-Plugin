import org.bukkit.plugin.java.JavaPlugin;

public class Drills extends JavaPlugin {
    public DrillDataManager DrillData;
    public BalanceManager BalData;
    public onCommandDrills DrillCmd;
    @Override
    public void onEnable(){
        this.DrillData = new DrillDataManager(this);
        this.BalData = new BalanceManager(this);

        this.DrillData.getConfig().options().copyDefaults(false);
        this.DrillData.saveDefaultConfig();

        this.BalData.getConfig().options().copyDefaults(false);
        this.BalData.saveDefaultConfig();

        this.DrillCmd = new onCommandDrills(this);
        this.getCommand("Drill").setExecutor(DrillCmd);
        this.getCommand("Drills").setExecutor(DrillCmd);


        this.getServer().getPluginManager().registerEvents(new onDrillBreak(this), this);
        this.getServer().getPluginManager().registerEvents(new onPlayerRightClick(this), this);
    }
    @Override
    public void onDisable(){
        this.DrillData.saveDefaultConfig();
        this.BalData.saveDefaultConfig();
    }
    public void setData(DrillDataManager Data){
        this.DrillData = Data;
        this.DrillData.saveDefaultConfig();
    }
    public DrillDataManager getDrillData(){
        return this.DrillData;
    }
    public void setBalData(BalanceManager Data){
        this.BalData = Data;
        this.BalData.saveDefaultConfig();
    }
    public BalanceManager getBalData(){
        return this.BalData;
    }
}
