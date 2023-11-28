package me.sootysplash.mixin.client;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ExampleClientMixin {

	@Unique
	boolean badwordsaid = false;
	@Unique
	String[] words = new String[]{"tranny", "nigger", "chink", "nigga", "faggot", "fag", "kys", "kill yourself", "kill urself", "you're dogshit", "your dogshit", "ur dogshit", "testword123"};

	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_allowSendChatMessage(String content, CallbackInfo ci) {

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
			ClientSendMessageEvents.CHAT_CANCELED.invoker().onSendChatMessageCanceled(content);
			ci.cancel();
		}

	}
	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void fabric_allowSendCommandMessage(String command, CallbackInfo ci) {

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
			ClientSendMessageEvents.COMMAND_CANCELED.invoker().onSendCommandMessageCanceled(command);
			ci.cancel();
		}

	}

}