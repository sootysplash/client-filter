package me.sootysplash;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainCF implements ModInitializer {
	//INFO: ChatGPT came up with 'Vitalium'
    public static final Logger LOGGER = LoggerFactory.getLogger("Vitalium");
	ConfigCF configCF = ConfigCF.getInstance();
	boolean badwordsaid, addIcon, warn, warnSound, cancel;
	Pair<String, String> blockedword;
	String sentMessage;
	MinecraftClient mc = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		AutoConfig.register(ConfigCF.class, GsonConfigSerializer::new);
		LOGGER.info("Vitalium has loaded | Sootysplash was here!");

		ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
				if(sender != null) {
					return Filter(message.getString(), "Server", sender.getName());
				}else{
					return Filter(message.getString(), "Server");
				}
		});
		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> Filter(message.getString(), "Game"));
		ClientSendMessageEvents.ALLOW_CHAT.register((message) -> Filter(message, "Client"));
		ClientSendMessageEvents.ALLOW_COMMAND.register((command) -> Filter(command, "Command"));
	}
	public boolean Filter(String message, String inputType, String sender){

		boolean theReturn = true;
		int total = getslur().length + getswear().length + gettoxic().length;
			for(int b = 0; b < total; b++) {

				if(configCF.slurs) {
					for (int t = 0; t < MainCF.getslur().length; t++) {
						Pattern pattern = Pattern.compile(MainCF.getslur()[t].getLeft(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), MainCF.getslur()[t].getRight());
							t = MainCF.getslur().length;
						}
					}
				}

				if(configCF.swears) {
					for (int t = 0; t < MainCF.getswear().length; t++) {
						Pattern pattern = Pattern.compile(MainCF.getswear()[t].getLeft(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), MainCF.getswear()[t].getRight());
							t = MainCF.getswear().length;
						}
					}
				}

				if(configCF.toxic) {
					for (int t = 0; t < MainCF.gettoxic().length; t++) {
						Pattern pattern = Pattern.compile(MainCF.gettoxic()[t].getLeft(), Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
						if (matcher.find()) {
							badwordsaid = true;
							blockedword = pair(matcher.group(), MainCF.gettoxic()[t].getRight());
							t = MainCF.gettoxic().length;
						}
					}
				}

				if(configCF.custom){
					for (int t = 0; t < configCF.customList.size(); t++) {
						String regex;
						String response;
						if(!(configCF.customList.get(t).charAt(0) == '~')) {
						try {
							regex = configCF.customList.get(t).substring(0, configCF.customList.get(t).indexOf("/") - 1);
							response = configCF.customList.get(t).substring(configCF.customList.get(t).indexOf("/") + 1);
						}catch (Exception e){
							if(configCF.debug) {
								LOGGER.error("Error loading custom list, are you sure you separated the regex and response with a '/'?");
							}
							regex = configCF.customList.get(t);
							response = "";
						}

						Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(message);
							if (matcher.find()) {
								badwordsaid = true;
								blockedword = pair(matcher.group(), response);
								t = configCF.customList.size();
							}
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

					if(configCF.outgoing && inputType.equals("Command") || inputType.equals("Client")) {

						if (!configCF.warn.isEmpty()) {
							warn = true;
						}

						if(configCF.warnSound && mc.player != null){
							warnSound = true;
						}
						if(!message.equals(sentMessage)) {

							if (configCF.outResponse.equals("Substitute")) {
								message = message.replace(blockedword.getLeft(), blockedword.getRight());
							}

							if (configCF.outResponse.equals("Remove")) {
								message = message.replace(blockedword.getLeft(), " ");
							}

							if (configCF.outResponse.equals("Cancel")) {
								cancel = true;
							}

							if(!configCF.outResponse.equals("Off")) {
								theReturn = false;
							}
							badwordsaid = false;
						}
					}

					if(configCF.incoming && mc.player != null && !sender.equals(mc.player.getEntityName()) && inputType.equals("Server") || inputType.equals("Game")){

						if (configCF.inResponse.equals("Substitute")) {
							message = message.replace(blockedword.getLeft(), blockedword.getRight());
							addIcon = true;
						}

						if (configCF.inResponse.equals("Remove")) {
							message = message.replace(blockedword.getLeft(), " ");
							addIcon = true;
						}

						if (configCF.inResponse.equals("Cancel")) {
							cancel = true;
						}

						if(!configCF.inResponse.equals("Off")) {
							theReturn = false;
						}
						badwordsaid = false;
					}
				}
			}
		if(warn){
			mc.inGameHud.getChatHud().addMessage(Text.of(configCF.warn));
			warn = false;
		}
		if(warnSound && mc.player != null){
			new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.ENTITY_GHAST_HURT), SoundCategory.MASTER, mc.player.getX(), mc.player.getY(), mc.player.getZ(), 1f, 1f, System.currentTimeMillis()).apply(mc.getNetworkHandler());
			warnSound = false;
		}
			if(!theReturn && !cancel) {
				switch (inputType) {
					case "Command" -> {
						sentMessage = message;
						Objects.requireNonNull(mc.getNetworkHandler()).sendChatCommand(message);
					}
					case "Client" -> {
						sentMessage = message;
						Objects.requireNonNull(mc.getNetworkHandler()).sendChatMessage(message);
					}
					case "Server", "Game" -> {
						if(!configCF.inIcon.isEmpty() && addIcon) {
							message = message.concat((" ").concat(configCF.inIcon));
							addIcon = false;
						}
						mc.inGameHud.getChatHud().addMessage(Text.of(message));
					}
				}
			}
		cancel = false;
		if(configCF.debug){
			LOGGER.info("Cancelled message: "+ theReturn);
		}
        return theReturn;
	}
	public boolean Filter(String message, String inputType){
		return Filter(message, inputType, "");
	}

	public static Pair<String, String> pair(String left, String right){
		return new Pair<>(left, right);
	}
	public static Pair<String, String>[] getslur(){
		// t slur, nword, c slur, f slur
		return new Pair[]{pair("t[r5].[nm]{1,2}[^s]y?", "trans"), pair("n[^a ][g6]+[e3a@]r?", "..."), pair("ch[i1l!]nk", ".."), pair("f *[a@] *[g6]{1,2}.?t?|bundle of stick", "gay")};
	}
	public static Pair<String, String>[] getswear(){
		// f word, b word, c word, s word, d word, a word/b word, p word, p word
		return new Pair[]{pair("f[u4oa]?c?k", "screw"), pair("b[il!1]?[tc]{1,2}h", "jerk"), pair("c[4u]?nt", "jerk"), pair("sh[^ou ]?r?t", "crud"), pair("d[i!l1]?c?k", "front"), pair("c[0o]ck", "front"), pair("[a@][s$6]{2}|butt", "tail"), pair("pu[s$6]{2}y?", "tail"), pair("p[1!li][s$6]{1,2}", "tee")};
	}
	public static Pair<String, String>[] gettoxic(){
		// kay why ess, keep yourself safe, your dog, ez, testword123
		return new Pair[]{pair("k+ *y+ *[s\\\\u0024]+", "love yourself"), pair("k.ll +[yu].{0,3}s.{1,2}f|keep yourself safe", "live a long life"), pair("y?[o0]?ure? ?d?[o0]?g?sh[^ou]?r?t|y?[o0]?ure? ?d?[o0]?g?crud", "you're good"),  pair("e+z+p?+z?+|easy", "gg"), pair("testword123", "this is a test")};
	}
}