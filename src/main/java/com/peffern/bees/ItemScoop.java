package com.peffern.bees;

import java.util.List;

import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.api.Enums.EnumItemReach;
import com.bioxx.tfc.api.Enums.EnumSize;
import com.bioxx.tfc.api.Enums.EnumWeight;
import com.bioxx.tfc.api.Interfaces.ISize;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * Bee scoop item
 * @author peffern
 *
 */
public class ItemScoop extends ItemTool implements ISize
{

	protected ItemScoop(ToolMaterial p_i45333_2_) 
	{
		super(2.0F, p_i45333_2_, Sets.newHashSet(new Block[]{Bees.hive}));
		this.setCreativeTab(TFCTabs.TFC_TOOLS);
	}
	
	@Override
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(Bees.MODID + ":" + "beeScoop");
	}

	@Override
	public EnumSize getSize(ItemStack is) {
		// TODO Auto-generated method stub
		return EnumSize.LARGE;
	}

	@Override
	public EnumWeight getWeight(ItemStack is) {
		// TODO Auto-generated method stub
		return EnumWeight.LIGHT;
	}

	@Override
	public EnumItemReach getReach(ItemStack is) {
		// TODO Auto-generated method stub
		return EnumItemReach.FAR;
	}

	@Override
	public boolean canStack() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public int getMaxDamage()
	{
		return 200;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		ItemTerra.addSizeInformation(is, arraylist);
	}

}
