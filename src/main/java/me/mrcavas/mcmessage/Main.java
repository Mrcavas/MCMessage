package me.mrcavas.mcmessage;

import io.netty.buffer.Unpooled;
import me.mrcavas.mcmessage.command.MsgCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Main implements ModInitializer {

    private File data;
    public static ArrayList<UUID> withMod = new ArrayList<>();

    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new MsgCommand().register(dispatcher);
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("mcmessage", "ok"), ((server, player, handler, buf, responseSender) -> {
            UUID serverUUID = null;

            if (!server.isDedicated()) {
                data = new File(server.getSavePath(WorldSavePath.ROOT).toFile(), "mcmessage");

                try {
                    Scanner reader = new Scanner(data);

                    if (reader.hasNext()) {
                        serverUUID = UUID.fromString(reader.next());
                        reader.close();
                    } else {
                        reader.close();
                        throw new IllegalArgumentException();
                    }
                } catch (FileNotFoundException ignored) {
                    try {
                        if ((data.isDirectory() && data.delete()) || data.createNewFile()) {
                            FileWriter writer = new FileWriter(data);
                            serverUUID = UUID.randomUUID();

                            writer.write(serverUUID.toString());
                            writer.close();
                        } else {
                            server.close();
                        }
                    } catch (Exception e) {
                        server.close();
                    }
                } catch (IllegalArgumentException ignored) {
                    try {
                        FileWriter writer = new FileWriter(data);
                        serverUUID = UUID.randomUUID();

                        writer.write(serverUUID.toString());
                        writer.close();
                    } catch (IOException e) {
                        server.close();
                    }
                }
            } else {
                data = new File(server.getRunDirectory(), "mcmessage");

                try {
                    Scanner reader = new Scanner(data);

                    if (reader.hasNext()) {
                        serverUUID = UUID.fromString(reader.next());
                        reader.close();
                    } else {
                        reader.close();
                        throw new IllegalArgumentException();
                    }
                } catch (FileNotFoundException ignored) {
                    try {
                        if ((data.isDirectory() && data.delete()) || data.createNewFile()) {
                            FileWriter writer = new FileWriter(data);
                            serverUUID = UUID.randomUUID();

                            writer.write(serverUUID.toString());
                            writer.close();
                        } else {
                            server.close();
                        }
                    } catch (Exception e) {
                        server.close();
                    }
                } catch (IllegalArgumentException ignored) {
                    try {
                        FileWriter writer = new FileWriter(data);
                        serverUUID = UUID.randomUUID();

                        writer.write(serverUUID.toString());
                        writer.close();
                    } catch (IOException e) {
                        server.close();
                    }
                }
            }

            if (serverUUID == null) server.close();
            else {
                withMod.add(player.getUuid());
                ServerPlayNetworking.send(player, new Identifier("mcmessage", "ok"), new PacketByteBuf(Unpooled.copiedBuffer(serverUUID.toString().getBytes(StandardCharsets.UTF_8))));
            }
        }));
    }
}