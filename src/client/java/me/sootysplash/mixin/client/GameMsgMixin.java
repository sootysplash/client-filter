package me.sootysplash.mixin.client;

import me.sootysplash.ConfigCF;
import me.sootysplash.MainCF;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MessageHandler.class)
public class GameMsgMixin {
    @Unique
    boolean badwordsaid = false;
    @Unique
    Pair<String, String> blockedword;
    @Unique
    ConfigCF configCF = ConfigCF.getInstance();
    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void fabric_allowGameMessage(Text message, boolean overlay, CallbackInfo ci) {

        if(configCF.incoming) {
            for (int t = 0; t < MainCF.getslur().length; t++) {
                if (message.toString().toLowerCase().contains(MainCF.getslur()[t].getLeft().toString())) {
                    badwordsaid = true;
                    blockedword = MainCF.getslur()[t];
                    t = MainCF.getslur().length;
                }
            }

            for (int t = 0; t < MainCF.getswear().length; t++) {
                if (message.toString().toLowerCase().contains(MainCF.getswear()[t].getLeft().toString())) {
                    badwordsaid = true;
                    blockedword = MainCF.getswear()[t];
                    t = MainCF.getswear().length;
                }
            }

            for (int t = 0; t < MainCF.gettoxic().length; t++) {
                if (message.toString().toLowerCase().contains(MainCF.gettoxic()[t].getLeft().toString())) {
                    badwordsaid = true;
                    blockedword = MainCF.gettoxic()[t];
                    t = MainCF.gettoxic().length;
                }
            }

            if (badwordsaid) {

                if(configCF.debug && blockedword != null){
                    try{
                        MainCF.LOGGER.debug("Message: " + message + " Flagged word: " + blockedword.getLeft() + " Replacement word: " + blockedword.getRight());
                    }catch (Exception e){
                        MainCF.LOGGER.error("Error while outputting debug message: " + e);
                    }
                }

                if (configCF.inResponse.equals("Cancel")) {
                    ClientReceiveMessageEvents.GAME_CANCELED.invoker().onReceiveGameMessageCanceled(message, overlay);
                    ci.cancel();
                }
                badwordsaid = false;
            }
        }

    }

    @ModifyVariable(method = "onGameMessage", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
    private Text fabric_modifyGameMessage(Text message, Text message1, boolean overlay) {
        message = ClientReceiveMessageEvents.MODIFY_GAME.invoker().modifyReceivedGameMessage(message, overlay);
        ClientReceiveMessageEvents.GAME.invoker().onReceiveGameMessage(message, overlay);

        if(configCF.incoming) {
            for (int j = 0; j < MainCF.getslur().length; j++) {
                if (message.toString().toLowerCase().contains(MainCF.getslur()[j].getLeft().toString())) {
                    badwordsaid = true;
                    blockedword = MainCF.getslur()[j];
                    j = MainCF.getslur().length;
                }
            }

            for (int j = 0; j < MainCF.getswear().length; j++) {
                if (message.toString().toLowerCase().contains(MainCF.getswear()[j].getLeft().toString())) {
                    badwordsaid = true;
                    blockedword = MainCF.getswear()[j];
                    j = MainCF.getswear().length;
                }
            }

            for (int j = 0; j < MainCF.gettoxic().length; j++) {
                if (message.toString().toLowerCase().contains(MainCF.gettoxic()[j].getLeft().toString())) {
                    badwordsaid = true;
                    blockedword = MainCF.gettoxic()[j];
                    j = MainCF.gettoxic().length;
                }
            }

            if (badwordsaid) {
                if (configCF.inResponse.equals("Replace")) {
                    message = Text.literal(message.toString().replace(blockedword.getLeft(), blockedword.getRight()));
                }

                if (configCF.inResponse.equals("Remove")) {
                    message = Text.literal(message.toString().replace(blockedword.getLeft(), " "));

                }
            }
        }
        return message;

    }
}
