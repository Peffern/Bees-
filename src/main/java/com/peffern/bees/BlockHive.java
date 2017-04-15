package com.peffern.bees;

import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.Blocks.Devices.BlockFirepit;
import com.bioxx.tfc.TileEntities.TEFirepit;
import com.bioxx.tfc.api.TFCBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 * Wild behive block
 * @author peffern
 *
 */
public class BlockHive extends BlockTerraContainer
{
	/** custom icon for the horizontal parts of the hive to render */
	public IIcon plateIcon;
	
	public BlockHive()
	{
		super(Material.sponge);
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegisterer)
	{
		this.blockIcon = iconRegisterer.registerIcon(Bees.MODID + ":" + "Hive");
		this.plateIcon = iconRegisterer.registerIcon(Bees.MODID + ":" + "HivePlate");
	}
	
	@Override
	public void onBlockHarvested(World world, int i, int j, int k, int l, EntityPlayer player)
	{
		ItemStack equip = player.getCurrentEquippedItem();
		//only drop if scoop equipped
		if(equip != null && equip.getItem() == Bees.beeScoop)
		{
			ItemStack stack = new ItemStack(Bees.honeyComb);
			//check blocks below
			for(int d = 1; d <= 7; d++)
			{
				if(!world.isAirBlock(i, j-d, k))
				{
					Block block = world.getBlock(i, j-d, k);
					//find firepit
					if(block instanceof BlockFirepit)
					{
						TEFirepit pit = (TEFirepit)world.getTileEntity(i, j-d, k);
						//firepit is on
						if(pit.fuelTimeLeft > 0)
						{
							//change to bee comb
							stack = new ItemStack(Bees.beeComb);
						}
						else
							break;
					}
					else
						break;
				}
				
			}
			//create item
			world.spawnEntityInWorld(new EntityItem(world,i,j,k,stack));
		}
		
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TEHive();
	}
	
	@Override
	public void dropBlockAsItem(World world, int x, int y, int z, ItemStack is)
	{
		//do nothing
	}
	
	@Override
	public int getRenderType()
	{
		return Bees.hiveRenderID;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	public boolean isWood(World world, int x, int y, int z)
	{
		return isWood(world.getBlock(x, y, z));
	}
	
	public boolean isWood(Block b)
	{
		return b == TFCBlocks.logNatural || b == TFCBlocks.logNatural2;
	}
	
	public boolean isLeaves(Block b)
	{
		return b == TFCBlocks.leaves || b == TFCBlocks.leaves2;
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		//stay next to tree
		return isWood(world,x-1,y,z) || isWood(world,x+1,y,z) || isWood(world,x,y,z-1) || isWood(world,x,y,z+1);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b)
	{
		//allows the hive to be placed wherever, but delete itself if its block updates and its no longer valid.
		//this way people can build with beehives in creative mode, but trees cut down will take the hives with them.
		if((isWood(b) || isLeaves(b)))
		{
			TEHive hive = (TEHive)world.getTileEntity(x, y, z);
			hive.blockRemoved();
		}
	}
	
}
