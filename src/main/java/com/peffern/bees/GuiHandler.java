package com.peffern.bees;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Gui manager
 * @author peffern
 *
 */
public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {		
		
		if(ID == Bees.beeBoxGuiId)
		{
			//server so get container
			return new ContainerBeeBox(player.inventory, world, x, y, z);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == Bees.beeBoxGuiId)
		{
			//client so get gui screen
			return new GuiBeeBox(player.inventory, world, x,y,z);
		}
		return null;
	}

}
