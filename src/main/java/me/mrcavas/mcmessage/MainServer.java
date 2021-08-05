package me.mrcavas.mcmessage;

import me.mrcavas.mcmessage.command.MsgCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class MainServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new MsgCommand().register(dispatcher);
        });
    }
}
