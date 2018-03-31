package com.suppergerrie2.drones.networking;

import com.suppergerrie2.drones.entities.EntityBasicDrone;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ItemsInDroneMessage implements IMessage {

	public ItemsInDroneMessage(){}

	private ItemStack[] toSend;
	private int id;
	
	private ItemStack[] received;
	
	public ItemsInDroneMessage(ItemStack[] toSend, int id) {
		this.toSend = toSend;
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		id = buf.readInt();
		int size = buf.readInt();
		ItemStack[] stacks = new ItemStack[size];
		for(int i = 0; i < size; i++) {
			stacks[i] = ByteBufUtils.readItemStack(buf);
		}
		received = stacks;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(id);
		buf.writeInt(toSend.length);
		for(ItemStack stack : toSend) {
			ByteBufUtils.writeItemStack(buf, stack);
		}
		
	}
	
	public static class ItemsInDroneMessageHandler implements IMessageHandler<ItemsInDroneMessage, IMessage> {

		  @Override public IMessage onMessage(ItemsInDroneMessage message, MessageContext ctx) {
		    EntityBasicDrone drone = (EntityBasicDrone) Minecraft.getMinecraft().world.getEntityByID(message.id);
		    
		    ItemStack[] stacks = message.received;
		    Minecraft.getMinecraft().addScheduledTask(() -> {
		    	drone.setItemStacksInDrone(stacks);
		    });
		    return null;
		  }
		}

}
