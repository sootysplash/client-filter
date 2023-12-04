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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

				if(configCF.slurs) {
					for (int t = 0; t < MainCF.getslur().length; t++) {
						Pattern pattern = Pattern.compile(MainCF.getslur()[t].getLeft().toString(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), MainCF.getslur()[t].getRight().toString());
							t = MainCF.getslur().length;
						}
					}
				}

				if(configCF.swears) {
					for (int t = 0; t < MainCF.getswear().length; t++) {
						Pattern pattern = Pattern.compile(MainCF.getswear()[t].getLeft().toString(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), MainCF.getswear()[t].getRight().toString());
							t = MainCF.getswear().length;
						}
					}
				}

				if(configCF.toxic) {
					for (int t = 0; t < MainCF.gettoxic().length; t++) {
						Pattern pattern = Pattern.compile(MainCF.gettoxic()[t].getLeft().toString(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), MainCF.gettoxic()[t].getRight().toString());
							t = MainCF.gettoxic().length;
						}
					}
				}

				if(configCF.custom){
					for (int t = 0; t < configCF.customList.size(); t++) {
						Pattern pattern = Pattern.compile(configCF.customList.get(t), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), "");
							t = configCF.customList.size();
						}
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

						if (configCF.outResponse.equals("Substitute")) {
							message = message.replace(blockedword.getLeft(), blockedword.getRight());
						}

						if (configCF.outResponse.equals("Remove")) {
							message = message.replace(blockedword.getLeft(), " ");
						}

						if (!configCF.outResponse.equals("Off")) {
							theReturn = false;
						}
						badwordsaid = false;
					}

					if(configCF.incoming && inputType.equals("Server") || inputType.equals("Game")){

						if (configCF.inResponse.equals("Substitute")) {
							message = message.replace(blockedword.getLeft(), blockedword.getRight());
						}

						if (configCF.inResponse.equals("Remove")) {
							message = message.replace(blockedword.getLeft(), " ");
						}

						if (!configCF.inResponse.equals("Off")) {
							theReturn = false;
						}
						badwordsaid = false;
					}
				}
			}
			if(!theReturn) {
				switch (inputType) {
					case "Command" -> {
						sentMessage = message;
						Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatCommand(message);
					}
					case "Client" -> {
						sentMessage = message;
						Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendChatMessage(message);
					}
					case "Server", "Game" -> MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(message));
				}
			}

//		System.out.println("The return is: " +theReturn);
        return theReturn;
	}

	public static Pair pair(String left, String right){
		return new Pair<>(left, right);
	}
	public static Pair[] getslur(){
		// t slur, nword, c slur, f slur
		return new Pair[]{pair("t[r5].[nm]{1,2}[^s]y?", "trans"), pair("n[^a ][g6]+[e3a@]r?", "..."), pair("ch[i1l!]nk", ".."), pair("f +[a@] +[g6]{1,2}.?t?|bundle of stick", "gay")};
	}
	public static Pair[] getswear(){
		// f word, b word, c word, s word, d word, a word/b word, p word, p word
		return new Pair[]{pair("f[u4o]?c?k", "screw"), pair("b[il!1]?[tc]{1,2}h", "jerk"), pair("c[4u]?nt", "jerk"), pair("sh[^ou]?r?t", "crud"), pair("d[i!l1]?c?k", "front"), pair("[a@][s$6]{2}|butt", "tail"), pair("pu[s$6]{2}y?", "tail"), pair("p[1!li][s$6]{1,2}", "teed")};
	}
	public static Pair[] gettoxic(){
		// kay why ess, keep yourself safe, your dog, ez, testword123
		return new Pair[]{pair("k+ *y+ *[s\\\\u0024]+", "Love yourself"), pair("k.ll +[yu].{0,3}s.{1,2}f|keep yourself safe", "Live a long life"), pair("y?[o0]?ure? ?d?[o0]?g?sh[^ou]?r?t|y?[o0]?ure? ?d?[o0]?g?crud", "you're good"),  pair("e+z+p?+z?+|easy", "gg"), pair("testword123", "this is a test")};
	}
}