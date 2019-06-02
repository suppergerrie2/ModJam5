package com.suppergerrie2.sdrones.networking;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Message to update the drone's inventory on the client so the client can display it
 */
public class UpdateDroneInventoryMessage {

    private ItemStack[] stacks;
    private int id;

    public UpdateDroneInventoryMessage() {
    }

    public UpdateDroneInventoryMessage(List<ItemStack> toSend, int id) {
        this(toSend.toArray(new ItemStack[0]), id);
    }

    public UpdateDroneInventoryMessage(ItemStack[] toSend, int id) {
        this.stacks = toSend;
        this.id = id;
    }

    public static UpdateDroneInventoryMessage fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer;
        if (buf instanceof PacketBuffer) {
            packetBuffer = (PacketBuffer) buf;
        } else {
            packetBuffer = new PacketBuffer(buf);
        }

        int id = packetBuffer.readInt();
        int size = packetBuffer.readInt();
        ItemStack[] stacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            stacks[i] = packetBuffer.readItemStack();
        }

        return new UpdateDroneInventoryMessage(stacks, id);
    }

    public static void onMessage(UpdateDroneInventoryMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        int id = message.id;
        ItemStack[] stacks = message.stacks;

        contextSupplier.get().enqueueWork(() -> {
            EntityAbstractDrone drone = (EntityAbstractDrone) Minecraft.getInstance().world.getEntityByID(id);

            if (drone != null) {
                drone.setDroneInventory(stacks, false);
            }
        });

        contextSupplier.get().setPacketHandled(true);
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer;
        if (buf instanceof PacketBuffer) {
            packetBuffer = (PacketBuffer) buf;
        } else {
            packetBuffer = new PacketBuffer(buf);
        }

        packetBuffer.writeInt(id);
        packetBuffer.writeInt(stacks.length);
        for (ItemStack stack : stacks) {
            packetBuffer.writeItemStack(stack);
        }

    }

}
