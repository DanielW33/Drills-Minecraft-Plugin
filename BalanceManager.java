import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class BalanceManager {
    private Drills plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public BalanceManager(Drills plugin){
        this.plugin = plugin;
        saveDefaultConfig();
    }
    public void reloadConfig(){
        if (this.configFile == null){
            this.configFile = new File(this.plugin.getDataFolder(), "PlayerData.yml");
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource("PlayerData.yml");
        if(defaultStream != null){
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults((defaultConfig));
        }
    }

    public FileConfiguration getConfig(){
        if(this.dataConfig == null){
            reloadConfig();
        }
        return this.dataConfig;
    }

    public void saveConfig() {
        if(this.dataConfig == null || this.configFile == null){
            return;
        }
        try {
            this.getConfig().save(this.configFile);
        }catch(IOException e){
            plugin.getLogger().log(Level.SEVERE, "[Drills] Failed to save player rank data config.", e);
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig(){
        if(this.configFile == null){
            this.configFile = new File(this.plugin.getDataFolder(), "PlayerData.yml");
        }

        if(!this.configFile.exists()){
            this.plugin.saveResource("PlayerData.yml", false);
        }
    }
}
