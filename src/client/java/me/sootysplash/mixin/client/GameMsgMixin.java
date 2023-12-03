package me.sootysplash.mixin.client;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public class GameMsgMixin {

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void fabric_allowGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (!ClientReceiveMessageEvents.ALLOW_GAME.invoker().allowReceiveGameMessage(message, overlay)) {
            ClientReceiveMessageEvents.GAME_CANCELED.invoker().onReceiveGameMessageCanceled(message, overlay);
            ci.cancel();
        }
    }

    @ModifyVariable(method = "onGameMessage", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
    private Text fabric_modifyGameMessage(Text message, Text message1, boolean overlay) {
        message = ClientReceiveMessageEvents.MODIFY_GAME.invoker().modifyReceivedGameMessage(message, overlay);
        ClientReceiveMessageEvents.GAME.invoker().onReceiveGameMessage(message, overlay);
        return message;
    }
}
