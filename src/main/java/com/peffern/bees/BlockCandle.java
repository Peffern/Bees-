package com.peffern.bees;

import java.util.ArrayList;
import java.util.Random;

import com.bioxx.tfc.Blocks.Vanilla.BlockTorch;
import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Core.TFC_Time;
import com.bioxx.tfc.TileEntities.TELightEmitter;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.TFCItems;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * like a torch but losts longer, is dimmer, and 1-use
 * @author peffern
 *
 */
public class BlockCandle extends BlockTorch
{
	public BlockCandle()
	{
		super();
		setLightLevel(0.875F);
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		this.blockIcon = register.registerIcon(Bees.MODID + ":" + "candle");
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote)
		{
			//make torches
			ItemStack is = player.inventory.getCurrentItem();
			Item item = is != null ? is.getItem() : null;

			if (item == TFCItems.stick)
			{	
				player.inventory.consumeInventoryItem(TFCItems.stick);
				TFC_Core.giveItemToPlayer(new ItemStack(TFCBlocks.torch), player);
			}
		}
		return true;
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 0)
		{
			this.onBlockAdded(world, x, y, z);
		}
		if (!world.isRemote)
		{
			if (Bees.candleBurnTime != 0 && world.getTileEntity(x, y, z) instanceof TELightEmitter)
			{
				//go out after time or in rain
				TELightEmitter te = (TELightEmitter) world.getTileEntity(x, y, z);
				if (TFC_Time.getTotalHours() > te.hourPlaced + Bees.candleBurnTime ||
					TFC_Core.isExposedToRain(world, x, y, z))
				{
					
					world.setBlockToAir(x, y, z);
				}
			}
		}
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
	{
		//drop nothing
		return new ArrayList<ItemStack>();
	}
}
