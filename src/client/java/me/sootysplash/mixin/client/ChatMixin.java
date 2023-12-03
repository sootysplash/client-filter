package me.sootysplash.mixin.client;

import me.sootysplash.ConfigCF;
import me.sootysplash.MainCF;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMixin {

	@Unique
	boolean badwordsaid = false;
	@Unique
	Pair<String, String> blockedword;
	@Unique
	ConfigCF configCF = ConfigCF.getInstance();

	@ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String fabric_modifySendChatMessage(String content) {
		content = ClientSendMessageEvents.MODIFY_CHAT.invoker().modifySendChatMessage(content);
		ClientSendMessageEvents.CHAT.invoker().onSendChatMessage(content);

		if(configCF.outgoing) {
			for (int i = 0; i < MainCF.getslur().length; i++) {
				if (content.toLowerCase().contains(MainCF.getslur()[i].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getslur()[i];
					i = MainCF.getslur().length;
				}
			}

			for (int i = 0; i < MainCF.getswear().length; i++) {
				if (content.toLowerCase().contains(MainCF.getswear()[i].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getswear()[i];
					i = MainCF.getswear().length;
				}
			}

			for (int i = 0; i < MainCF.gettoxic().length; i++) {
				if (content.toLowerCase().contains(MainCF.gettoxic()[i].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.gettoxic()[i];
					i = MainCF.gettoxic().length;
				}
			}

			if (badwordsaid) {

				if (configCF.outResponse.equals("Replace")) {
					content = content.replace(blockedword.getLeft(), blockedword.getRight());
					ClientSendMessageEvents.MODIFY_CHAT.invoker().modifySendChatMessage(content);
				}

				if (configCF.outResponse.equals("Remove")) {
					content = content.replace(blockedword.getLeft(), " ");
					ClientSendMessageEvents.MODIFY_CHAT.invoker().modifySendChatMessage(content);
				}
			}
		}
		return content;

	}


		@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_allowSendChatMessage(String content, CallbackInfo ci) {

		if(configCF.outgoing) {
			for (int t = 0; t < MainCF.getslur().length; t++) {
				if (content.toLowerCase().contains(MainCF.getslur()[t].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getslur()[t];
					t = MainCF.getslur().length;
				}
			}

			for (int t = 0; t < MainCF.getswear().length; t++) {
				if (content.toLowerCase().contains(MainCF.getswear()[t].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getswear()[t];
					t = MainCF.getswear().length;
				}
			}

			for (int t = 0; t < MainCF.gettoxic().length; t++) {
				if (content.toLowerCase().contains(MainCF.gettoxic()[t].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.gettoxic()[t];
					t = MainCF.gettoxic().length;
				}
			}

			if (badwordsaid) {

				if(configCF.debug && blockedword != null){
					try{
						MainCF.LOGGER.debug("Message: " + content + " Flagged word: " + blockedword.getLeft() + " Replacement word: " + blockedword.getRight());
					}catch (Exception e){
						MainCF.LOGGER.error("Error while outputting debug message: " + e);
					}
				}

				if (configCF.warn) {
					MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("[ChatFilter] Hey buddy, offensive words aren't the right way to release your anger."));
				}

				if (configCF.outResponse.equals("Cancel")) {
					ClientSendMessageEvents.CHAT_CANCELED.invoker().onSendChatMessageCanceled(content);
					ci.cancel();
				}
				badwordsaid = false;
			}
		}

	}
	@ModifyVariable(method = "sendChatCommand", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String fabric_modifySendCommandMessage(String command) {
		command = ClientSendMessageEvents.MODIFY_COMMAND.invoker().modifySendCommandMessage(command);
		ClientSendMessageEvents.COMMAND.invoker().onSendCommandMessage(command);

		if(configCF.outgoing) {
			for (int j = 0; j < MainCF.getslur().length; j++) {
				if (command.toLowerCase().contains(MainCF.getslur()[j].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getslur()[j];
					j = MainCF.getslur().length;
				}
			}

			for (int j = 0; j < MainCF.getswear().length; j++) {
				if (command.toLowerCase().contains(MainCF.getswear()[j].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getswear()[j];
					j = MainCF.getswear().length;
				}
			}

			for (int j = 0; j < MainCF.gettoxic().length; j++) {
				if (command.toLowerCase().contains(MainCF.gettoxic()[j].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.gettoxic()[j];
					j = MainCF.gettoxic().length;
				}
			}

			if (badwordsaid) {
				if (configCF.outResponse.equals("Replace")) {
					command = command.replace(blockedword.getLeft(), blockedword.getRight());
					ClientSendMessageEvents.MODIFY_COMMAND.invoker().modifySendCommandMessage(command);
				}

				if (configCF.outResponse.equals("Remove")) {
					command = command.replace(blockedword.getLeft(), " ");
					ClientSendMessageEvents.MODIFY_COMMAND.invoker().modifySendCommandMessage(command);

				}
			}
		}
		return command;

	}
	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void fabric_allowSendCommandMessage(String command, CallbackInfo ci) {

		if(configCF.outgoing) {
			for (int h = 0; h < MainCF.getslur().length; h++) {
				if (command.toLowerCase().contains(MainCF.getslur()[h].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getslur()[h];
					h = MainCF.getslur().length;
				}
			}

			for (int h = 0; h < MainCF.getswear().length; h++) {
				if (command.toLowerCase().contains(MainCF.getswear()[h].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.getswear()[h];
					h = MainCF.getswear().length;
				}
			}

			for (int h = 0; h < MainCF.gettoxic().length; h++) {
				if (command.toLowerCase().contains(MainCF.gettoxic()[h].getLeft().toString())) {
					badwordsaid = true;
					blockedword = MainCF.gettoxic()[h];
					h = MainCF.gettoxic().length;
				}
			}

			if (badwordsaid) {

				if(configCF.debug && blockedword != null){
					try {
						MainCF.LOGGER.debug("Command: " + command + " Flagged word: " + blockedword.getLeft() + " Replacement word: " + blockedword.getRight());
					}catch (Exception e){
						MainCF.LOGGER.error("Error while outputting debug message: " + e);
					}
				}

				if (configCF.warn) {
					MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("[ChatFilter] Hey buddy, offensive words aren't the right way to release your anger."));
				}

				if (configCF.outResponse.equals("Cancel")) {
					ClientSendMessageEvents.COMMAND_CANCELED.invoker().onSendCommandMessageCanceled(command);
					ci.cancel();
				}
				badwordsaid = false;
			}
		}

	}

}