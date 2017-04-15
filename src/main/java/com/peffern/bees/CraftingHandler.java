package com.peffern.bees;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.Items.Tools.ItemCustomSaw;
import com.bioxx.tfc.Items.Tools.ItemHammer;
import com.bioxx.tfc.Items.Tools.ItemKnife;
import com.bioxx.tfc.api.TFCItems;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Handle various recipe things
 * @author peffern
 *
 */
public class CraftingHandler 
{
	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent e)
	{
		ItemStack result = e.crafting;
		IInventory grid = e.craftMatrix;
		EntityPlayer player = e.player;
		World world = player.worldObj;
		
		
		if(result != null && grid != null)
		{
			//crafting beeswax - spawn honey and damage the knife
			if(result.getItem() == Bees.beeswax)
			{
				//make sure there's a comb
				boolean comb = false;
				//make sure there's a knife
				ItemStack knife = null;
				for(int i = 0, l = grid.getSizeInventory(); i < l; i++)
				{
					ItemStack s = grid.getStackInSlot(i);
					if(s != null)
					{
						if(s.getItem() == Bees.honeyComb)
						{
							comb = true;
						}
						else if(s.getItem() instanceof ItemKnife)
						{
							knife = s;
						}
	
					}
				}
				if(comb && knife != null)
				{
					//damage the knife
					if (knife.getItemDamage() < knife.getMaxDamage() - 1)
					{
						knife.damageItem(1 , e.player);
						knife.stackSize++;
					}
					//spawn the honey
					if(!world.isRemote)
					{
						TFC_Core.giveItemToPlayer(ItemFoodTFC.createTag(new ItemStack(Bees.honey),4), player);
					}
				}
			}
			//crafting bee box so damage the saw
			else if(result.getItem() == Item.getItemFromBlock(Bees.beeBox))
			{
				//make sure there's a saw
				ItemStack saw = null;
				for(int i = 0, l = grid.getSizeInventory(); i < l; i++)
				{
					ItemStack s = grid.getStackInSlot(i);
					if(s != null)
					{
						if(s.getItem() instanceof ItemCustomSaw)
						{
							saw = s;
							break;
						}
					}
				}
				if(saw != null)
				{
					//damage the saw
					if (saw.getItemDamage() < saw.getMaxDamage() - 1)
					{
						saw.damageItem(1 , e.player);
						saw.stackSize++;
					}
				}
			}
			//breaking open the wax mold constructions
			else if(result.getItem() == TFCItems.ceramicMold)
			{
				//make sure the wax mold is there
				boolean wax = false;
				//make sure there's a hammer
				ItemStack hammer = null;
				for(int i = 0, l = grid.getSizeInventory(); i < l; i++)
				{
					ItemStack s = grid.getStackInSlot(i);
					if(s != null)
					{
						if(s.getItem() == Bees.waxMold)
						{
							wax = true;
						}
						else if(s.getItem() instanceof ItemHammer)
						{
							hammer = s;
						}
					}
				}
				if(wax && hammer != null)
				{
					//damage the hammer
					if (hammer.getItemDamage() < hammer.getMaxDamage() - 1)
					{
						hammer.damageItem(1 , e.player);
						hammer.stackSize++;
					}
				}
			}
		}
		
		
		
	}
}
