package com.peffern.bees;

import com.bioxx.tfc.Items.ItemBlocks.ItemTerraBlock;
import com.bioxx.tfc.api.Enums.EnumWeight;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

/**
 * ItemBlock for the BeeBox to change its weight properties
 * @author peffern
 *
 */
public class ItemBlockBeeBox extends ItemTerraBlock
{

	public ItemBlockBeeBox(Block b) {
		super(b);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public EnumWeight getWeight(ItemStack is)
	{
		return EnumWeight.HEAVY;
	}
}
