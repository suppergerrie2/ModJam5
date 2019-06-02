package com.suppergerrie2.sdrones.networking;

import com.suppergerrie2.sdrones.Reference;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class DronesPacketHandler {

    public static SimpleChannel channel;
    private static ResourceLocation networkName = new ResourceLocation(Reference.MODID, "net");

    public static void registerChannel() {
        channel = NetworkRegistry.ChannelBuilder.named(networkName).
            clientAcceptedVersions(s -> Objects.equals(s, "1")).
            serverAcceptedVersions(s -> Objects.equals(s, "1")).
            networkProtocolVersion(() -> "1").
            simpleChannel();

        channel.messageBuilder(UpdateDroneInventoryMessage.class, 0).
            decoder(UpdateDroneInventoryMessage::fromBytes).
            encoder(UpdateDroneInventoryMessage::toBytes).
            consumer(UpdateDroneInventoryMessage::onMessage).
            add();
    }
}
