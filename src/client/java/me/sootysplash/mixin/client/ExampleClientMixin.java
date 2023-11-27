package me.sootysplash.mixin.client;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public class ExampleClientMixin {

	@Unique
	boolean badwordsaid = false;
	@Unique
	String[] words = new String[]{"tranny", "nigger", "chink", "nigga", "faggot", "fag", "kys", "kill yourself", "kill urself", "you're dogshit", "your dogshit", "ur dogshit", "testword123"};
	@ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String fabric_modifySendChatMessage(String content) {
		content = ClientSendMessageEvents.MODIFY_CHAT.invoker().modifySendChatMessage(content);
		ClientSendMessageEvents.CHAT.invoker().onSendChatMessage(content);

		for(int i = 0; i < words.length; i++) {
			if (content.contains(words[i])) {
				badwordsaid = true;
				i = words.length;
			}
			i++;
		}
		if(badwordsaid){
			content = "";
			MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("[ChatFilter] Hey buddy, offensive words aren't the right way to release your anger."));
			badwordsaid = false;
		}


		return content;
	}

	@ModifyVariable(method = "sendChatCommand", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String fabric_modifySendCommandMessage(String command) {
		command = ClientSendMessageEvents.MODIFY_COMMAND.invoker().modifySendCommandMessage(command);
		ClientSendMessageEvents.COMMAND.invoker().onSendCommandMessage(command);



		for(int i = 0; i < words.length; i++) {
			if (command.contains(words[i])) {
				badwordsaid = true;
				i = words.length;
			}
			i++;
		}
		if(badwordsaid){
			command = "";
			MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("[ChatFilter] Hey buddy, offensive words aren't the right way to release your anger."));
			badwordsaid = false;
		}

		return command;
	}
}