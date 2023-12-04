package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MainCF implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("ClientFilter");
	ConfigCF configCF = ConfigCF.getInstance();
	boolean badwordsaid;
	Pair<String, String> blockedword;
	String sentMessage;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		AutoConfig.register(ConfigCF.class, GsonConfigSerializer::new);
		LOGGER.info("ClientFilter has loaded | Sootysplash was here!");

		ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> Filter(message.getString(), "Server"));
		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> Filter(message.getString(), "Game"));
		ClientSendMessageEvents.ALLOW_CHAT.register((message) -> Filter(message, "Client"));
		ClientSendMessageEvents.ALLOW_COMMAND.register((command) -> Filter(command, "Command"));

	}
	public boolean Filter(String message, String inputType){

		boolean theReturn = true;
		int total = getslur().length + getswear().length + gettoxic().length;
			for(int b = 0; b < total; b++) {
				for (int t = 0; t < MainCF.getslur().length; t++) {
					if (message.toLowerCase().contains(MainCF.getslur()[t].getLeft().toString())) {
						badwordsaid = true;
						blockedword = MainCF.getslur()[t];
						t = MainCF.getslur().length;
					}
				}

				for (int t = 0; t < MainCF.getswear().length; t++) {
					if (message.toLowerCase().contains(MainCF.getswear()[t].getLeft().toString())) {
						badwordsaid = true;
						blockedword = MainCF.getswear()[t];
						t = MainCF.getswear().length;
					}
				}

				for (int t = 0; t < MainCF.gettoxic().length; t++) {
					if (message.toLowerCase().contains(MainCF.gettoxic()[t].getLeft().toString())) {
						badwordsaid = true;
						blockedword = MainCF.gettoxic()[t];
						t = MainCF.gettoxic().length;
					}
				}

				if (badwordsaid) {

					if (configCF.debug) {
						try {
							MainCF.LOGGER.info("[DEBUG] Message: " + message + " Flagged word: " + blockedword.getLeft() + " Replacement word: " + blockedword.getRight());
						} catch (Exception e) {
							MainCF.LOGGER.error("Error while outputting debug message: " + e);
						}
					}

					if(configCF.outgoing && !message.equals(sentMessage) && inputType.equals("Command") || inputType.equals("Client")) {

						if (configCF.warn) {
							MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("[ChatFilter] Hey buddy, offensive words aren't the right way to release your anger."));
						}

						if (configCF.outResponse.equals("Replace")) {
							message = message.replace(blockedword.getLeft(), blockedword.getRight());
						}

						if (configCF.outResponse.equals("Remove")) {
							message = message.replace(blockedword.getLeft(), " ");
						}

						if (!configCF.outResponse.equals("Off")) {
							theReturn = false;
						}
						badwordsaid = false;

						sentMessage = message;
						if(inputType.equals("Command")){
							Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatCommand(message);
						}else if (inputType.equals("Client")){
							Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatMessage(message);
						}
					}

					if(configCF.incoming && inputType.equals("Server") || inputType.equals("Game")){

						if (configCF.inResponse.equals("Replace")) {
							message = message.replace(blockedword.getLeft(), blockedword.getRight());
						}

						if (configCF.inResponse.equals("Remove")) {
							message = message.replace(blockedword.getLeft(), " ");
						}

						if (!configCF.inResponse.equals("Off")) {
							theReturn = false;
						}
						badwordsaid = false;
						MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(message));
					}
				}
			}
		System.out.println("The return is: " +theReturn);
        return theReturn;
	}

	public static Pair pair(String left, String right){
		return new Pair<>(left, right);
	}
	public static Pair[] getslur(){
		return new Pair[]{pair("tranny", "trans"), pair("nigg", "..."), pair("chink", "..."), pair("fag", "gay")};
	}
	public static Pair[] getswear(){
		return new Pair[]{pair("fuck", "screw"), pair("bitch", "jerk"), pair("cunt", "jerk"), pair("shit", "crud")};
	}
	public static Pair[] gettoxic(){
		return new Pair[]{pair("kys", "..."), pair("ur dogshit", "..."), pair("kill ur", "..."), pair("ez", "gg"), pair("testword123", "this is a test")};
	}
}