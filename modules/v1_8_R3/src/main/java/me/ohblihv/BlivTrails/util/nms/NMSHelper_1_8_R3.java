package me.ohblihv.BlivTrails.util.nms;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Chris Brown (OhBlihv) on 8/08/2016.
 */
public class NMSHelper_1_8_R3 implements INMSHelper
{
	
	@Override
	public ItemStack addEnchantmentEffect(ItemStack item)
	{
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag())
		{
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
		{
			tag = nmsStack.getTag();
		}
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}
	
	@Override
	public ItemStack removeEnchantmentEffect(ItemStack item)
	{
		net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag())
		{
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
		{
			tag = nmsStack.getTag();
		}
		tag.remove("ench");
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}
	
}
