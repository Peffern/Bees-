package com.peffern.bees;

import com.bioxx.tfc.Blocks.BlockTerraContainer;
import com.bioxx.tfc.Core.TFCTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Bee box block. faces the player and opens the bee box gui
 * @author peffern
 *
 */
public class BlockBeeBox extends BlockTerraContainer
{
	/** icon bee box front */
	private IIcon frontIcon;
	/** default icon bee box */
	private IIcon sidesIcon;
	/** icon bee box top */
	private IIcon topIcon;
	/** icon bee box bottom */
	private IIcon bottomIcon;
	
	public BlockBeeBox()
	{
		super(Material.wood);
		this.setCreativeTab(TFCTabs.TFC_DEVICES);
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register)
	{
		frontIcon 	= register.registerIcon(Bees.MODID + ":" + "beeBoxFront");
		sidesIcon 	= register.registerIcon(Bees.MODID + ":" + "beeBoxSide");
		topIcon 	= register.registerIcon(Bees.MODID + ":" + "beeBoxTop");
		bottomIcon 	= register.registerIcon(Bees.MODID + ":" + "beeBoxBottom");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TEBeeBox();
	}
	
	@Override
	public IIcon getIcon(int side, int meta)
	{
		//top
		if (side == 1) return this.topIcon;
		//bottom
		else if (side == 0) return this.bottomIcon;
		//find the front from meta
		else if (meta == 2 && side == 2) return this.frontIcon;
		else if (meta == 3 && side == 5) return this.frontIcon;
		else if (meta == 0 && side == 3) return this.frontIcon;
		else if (meta == 1 && side == 4) return this.frontIcon;
		//default
		else return this.sidesIcon;
	}
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack stack)
	{
		super.onBlockPlacedBy(world, x,y,z, placer, stack);
		//set meta to facing direction
		int dir = MathHelper.floor_double((double)(placer.rotationYaw * 4.0F / 360.0F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x,y,z,dir,2);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
	{
		if(!world.isRemote)
		{
			//gui
			entityplayer.openGui(Bees.instance, Bees.beeBoxGuiId, world, x, y, z);
			return true;
		}
		return super.onBlockActivated(world, x, y, z, entityplayer, par6, par7, par8, par9);
		
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TEBeeBox te = (TEBeeBox)world.getTileEntity(x, y, z);
		//if there's no smoke when the block breaks, replace all bee combs with honey combs
		if(!te.hasSmoke())
		{
			for(int i = 0; i < te.slots.length; i++)
			{
				if(te.slots[i] != null && te.slots[i].getItem() == Bees.beeComb)
				{
					te.slots[i] = new ItemStack(Bees.honeyComb,1);
				}
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}
	
}
