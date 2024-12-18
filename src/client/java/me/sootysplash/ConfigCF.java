package me.sootysplash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


@me.shedaniel.autoconfig.annotation.Config(name = "Vitalium")
public class ConfigCF implements ConfigData {

    //Andy is the goat https://github.com/AndyRusso/pvplegacyutils/blob/main/src/main/java/io/github/andyrusso/pvplegacyutils/PvPLegacyUtilsConfig.java

    private static final Path file = FabricLoader.getInstance().getConfigDir().resolve("Vitalium.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigCF instance;

    public boolean outgoing = true;
    public boolean incoming = true;
    public String warn = "[§l§o§4Vitalium§r] Hey buddy, offensive words are not the right way to release your anger.";
    public String outResponse = "Substitute";
    public String inResponse = "Substitute";
    public boolean debug = false;
    public boolean slurs = true;
    public boolean swears = true;
    public boolean toxic = true;
    public boolean custom = false;
    public boolean warnSound = true;
    public String inIcon = "ɤ";
    public List<String> customList = List.of("~balls /spheres", "die /fall");

    public void save() {
        try {
            Files.writeString(file, GSON.toJson(this));
        } catch (IOException e) {
            MainCF.LOGGER.error("Vitalium could not save the config.");
            throw new RuntimeException(e);
        }
    }

    public static ConfigCF getInstance() {
        if (instance == null) {
            try {
                instance = GSON.fromJson(Files.readString(file), ConfigCF.class);
            } catch (IOException exception) {
                MainCF.LOGGER.warn("Vitalium couldn't load the config, using defaults.");
                instance = new ConfigCF();
            }
        }

        return instance;
    }

}
