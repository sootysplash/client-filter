package me.sootysplash;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.*;
import net.minecraft.text.Text;

import java.util.List;


public class ModMenuCF implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigCF config = ConfigCF.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.of("Config"))
                    .setSavingRunnable(config::save);

            ConfigCategory handle = builder.getOrCreateCategory(Text.of("Handling"));
            ConfigEntryBuilder cfghandle =  builder.entryBuilder();



            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Outgoing"), config.outgoing)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Filter outgoing (Client to Server) messages?"))
                    .setSaveConsumer(newValue -> config.outgoing = newValue)
                    .build());

            handle.addEntry(cfghandle.startStringDropdownMenu(Text.of("Outgoing flagged message handling"), config.outResponse)
                    .setDefaultValue("Substitute")
                    .setSelections(List.of("Substitute", "Remove", "Cancel", "Off"))
                    .setTooltip(Text.of("Response to outgoing (Client to Server) flagged messages"))
                    .setSaveConsumer(newValue -> config.outResponse = newValue)
                    .build());

            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Outgoing warn"), config.warn)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Warn for flagged outgoing (Client to Server) messages"))
                    .setSaveConsumer(newValue -> config.warn = newValue)
                    .build());

            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Warning sound"), config.warnsound)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Adds an auditory warning for flagged (Client to Server) messages"))
                    .setSaveConsumer(newValue -> config.warnsound = newValue)
                    .build());

            handle.addEntry(cfghandle.startBooleanToggle(Text.of("Incoming"), config.incoming)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Filter incoming (Server to Client) messages?"))
                    .setSaveConsumer(newValue -> config.incoming = newValue)
                    .build());

            handle.addEntry(cfghandle.startStringDropdownMenu(Text.of("Incoming flagged message handling"), config.inResponse)
                    .setDefaultValue("Substitute")
                    .setSelections(List.of("Substitute", "Remove", "Cancel", "Off"))
                    .setTooltip(Text.of("Response to incoming (Server to Client) flagged messages"))
                    .setSaveConsumer(newValue -> config.inResponse = newValue)
                    .build());

            handle.addEntry(cfghandle.startStringDropdownMenu(Text.of("Incoming flagged icon"), config.inIcon)
                    .setDefaultValue("ɤ")
                    .setTooltip(Text.of("Adds a small icon to (Server to Client) flagged messages to indicate filtering, leave blank to disable"))
                    .setSaveConsumer(newValue -> config.inIcon = newValue)
                    .build());

            ConfigCategory filter = builder.getOrCreateCategory(Text.of("Filter"));
            ConfigEntryBuilder cfgfilter =  builder.entryBuilder();

            filter.addEntry(cfgfilter.startBooleanToggle(Text.of("Slurs"), config.slurs)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Filter slurs?"))
                    .setSaveConsumer(newValue -> config.slurs = newValue)
                    .build());

            filter.addEntry(cfgfilter.startBooleanToggle(Text.of("Swears"), config.swears)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Filter swears?"))
                    .setSaveConsumer(newValue -> config.swears = newValue)
                    .build());

            filter.addEntry(cfgfilter.startBooleanToggle(Text.of("Toxicity"), config.toxic)
                    .setDefaultValue(true)
                    .setTooltip(Text.of("Filter toxicity?"))
                    .setSaveConsumer(newValue -> config.toxic = newValue)
                    .build());

            filter.addEntry(cfgfilter.startBooleanToggle(Text.of("Debug"), config.debug)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Output flagged messages + logic to your log"))
                    .setSaveConsumer(newValue -> config.debug = newValue)
                    .build());

            filter.addEntry(cfgfilter.startBooleanToggle(Text.of("Custom"), config.custom)
                    .setDefaultValue(false)
                    .setTooltip(Text.of("Filter your custom words?"))
                    .setSaveConsumer(newValue -> config.custom = newValue)
                    .build());

            filter.addEntry(cfgfilter.startStrList(Text.of("Custom word list"), config.customList)
                    .setTooltip(Text.of("This list supports regex, separate patterns and response words with '/'"))
                    .setSaveConsumer(newValue -> config.customList = newValue)
                    .build());


            return builder.build();
        };
    }

}
