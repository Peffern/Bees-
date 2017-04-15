package com.peffern.bees;


import com.bioxx.tfc.TileEntities.NetworkTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Ticking tile entity for the hive block
 * @author peffern
 *
 */
public class TEHive extends NetworkTileEntity
{
	//bee attack stats
	private static final float BEE_STRIKE_DISTANCE_HORIZONTAL = 3.8f;
	private static final float BEE_STRIKE_DISTANCE_VERTICAL = 7f;
	private static final int BEE_STRIKE_DAMAGE = 20;
		
	//other
	private int counter = 60;
	private boolean tryingToDisappear = false;
	
	/**
	 * Handle refilling and validation
	 */
	@Override
	public void updateEntity()
	{
		//fix to make hive go away when you cut down trees
		//TE has to be told to disappear by the block when it updates so detect this flag first
		if(tryingToDisappear)
		{
			//detect that we actually can't stay and if so remove the block
			BlockHive block = (BlockHive)worldObj.getBlock(xCoord, yCoord, zCoord);
			if(!block.canBlockStay(worldObj, xCoord, yCoord, zCoord))
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			else
				tryingToDisappear = false;
		}
		counter--;
		
		//attack time
		if(counter <= 0)
		{
			counter = 60;
			
			//get player
			EntityPlayer p = worldObj.getClosestPlayer(xCoord, yCoord, zCoord, Math.sqrt(Math.pow(BEE_STRIKE_DISTANCE_HORIZONTAL,2) + Math.pow(BEE_STRIKE_DISTANCE_VERTICAL, 2)));
			if(p != null && inRange(p))
			{
				boolean veil = false;
				ItemStack helmet = p.inventory.armorInventory[3];
				if(helmet != null)
				{
					Item item = helmet.getItem();
					veil = item == Bees.beeVeil;
				}
				//player is not wearing bee veil
				if(!veil)
					p.attackEntityFrom(Bees.beesDamage, BEE_STRIKE_DAMAGE);
			}
			
		
		}
		
		
		
	}
	
	/**
	 * Flag the block to be destroyed
	 */
	public void blockRemoved()
	{
		tryingToDisappear = true;
	}
	
	public boolean inRange(EntityPlayer p)
	{
		double px = p.posX;
		double py = p.posY;
		double pz = p.posZ;
		double mx = xCoord;
		double my = yCoord;
		double mz = zCoord;
		double dist = Math.sqrt(Math.pow(px-mx, 2)+ Math.pow(pz-mz, 2));
		double distH = Math.abs(py-my);
		return (dist <= BEE_STRIKE_DISTANCE_HORIZONTAL && distH <= BEE_STRIKE_DISTANCE_VERTICAL);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
	}
	
	@Override
	public void handleInitPacket(NBTTagCompound nbttagcompound) 
	{
	}

	@Override
	public void handleDataPacket(NBTTagCompound nbt) 
	{
		
	}

	@Override
	public void createInitNBT(NBTTagCompound nbt) 
	{
		
	}
}
