package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EventListener;

public class MainCF implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("ClientFilter");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		AutoConfig.register(ConfigCF.class, GsonConfigSerializer::new);
		LOGGER.info("ClientFilter has loaded | Sootysplash was here!");



	}
	public static Pair pair(String left, String right){
		return new Pair<>(left, right);
	}
	public static Pair[] getslur(){
		return new Pair[]{pair("tranny", "trans"), pair("nigger", "..."), pair("chink", "..."), pair("fag", "gay")};
	}
	public static Pair[] getswear(){
		return new Pair[]{pair("fuck", "screw"), pair("bitch", "jerk"), pair("cunt", "jerk"), pair("shit", "crud")};
	}
	public static Pair[] gettoxic(){
		return new Pair[]{pair("kys", "..."), pair("ur dogshit", "..."), pair("kill ur", "..."), pair("ez", "gg"), pair("testword123", "this is a test")};
	}
}