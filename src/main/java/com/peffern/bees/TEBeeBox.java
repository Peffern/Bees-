package com.peffern.bees;

import com.bioxx.tfc.Blocks.Devices.BlockFirepit;
import com.bioxx.tfc.TileEntities.NetworkTileEntity;
import com.bioxx.tfc.TileEntities.TEFirepit;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;

/**
 * Tile Entity for Bee Box
 * @author peffern
 *
 */
public class TEBeeBox extends NetworkTileEntity implements IUpdatePlayerListBox, ISidedInventory


{
	ItemStack[] slots = new ItemStack[5]; 
	private boolean hasSmoke = false;
	int counter = 60;
	int nextCombProgress = 50;
	int attackTimer = 60;
	
	@Override
	public void handleInitPacket(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createInitNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return slots.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return slots[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (slots[index] != null)
        {
            if (slots[index].stackSize <= count)
            {
                ItemStack stack = slots[index];
                slots[index] = null;
                return stack;
            }
            else
            {
                ItemStack split = slots[index].splitStack(count);

                if (slots[index].stackSize == 0)
                {
                    slots[index] = null;
                }

                return split;
            }
        }
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		slots[index] = stack;
		
	}

	@Override
	public String getInventoryName() {
		return "container.beebox";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	@Override
	public void openInventory() {
		
	}

	@Override
	public void closeInventory() {
		
	}

	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return new int[]{0,1,2,3,4};
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void updateEntity() 
	{
		if(!worldObj.isRemote)
		{
			if(slots[0] != null && slots[0].getItem() == Bees.beeComb)
			{
				counter--;
				if(counter <= 0)
				{
					counter = 300;
					nextCombProgress--;
					if(nextCombProgress <= 0)
					{
						nextCombProgress = 50 + worldObj.rand.nextInt(10)-5;
						//damage the bee comb - counts comb production
						slots[0].setItemDamage(slots[0].getItemDamage() + 1);
						//always die after 8
						if(slots[0].getItemDamage() >= 8)
						{
							killComb();
						}
						else
						{
							//increasing chance to kill comb after producing 4 combs
							int r = worldObj.rand.nextInt(slots[0].getItemDamage());
							if(r > 3)
							{
								killComb();
							}
							else
							{
								produceComb();
							}
						}
					}
				}
			}
			else
			{
				counter = 6;
			}
		}
		
		if(slots[0] != null && slots[0].getItem() == Bees.beeComb && !hasSmoke())
			attackTimer--;
		if(attackTimer <= 0)
		{
			attackTimer = 60;
			//get player
			EntityPlayer p = worldObj.getClosestPlayer(xCoord, yCoord, zCoord, Math.sqrt(Math.pow(2,2) + Math.pow(3, 2)));
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
					p.attackEntityFrom(Bees.beesDamage, 20);
			}
		}
		
		
		for(int k = 0; k >= -2; k--)
		{
			for(int j = -1; j <= 1; j++)
			{
				for(int i = -1; i <= 1; i++)
				{
					int x = xCoord + i;
					int z = zCoord + j;
					int y = yCoord + k;
					
					Block block = worldObj.getBlock(x, y, z);
					
					if(block instanceof BlockFirepit)
					{
						TileEntity entity = worldObj.getTileEntity(x, y, z);
						if(entity instanceof TEFirepit)
						{
							TEFirepit te = (TEFirepit)entity;
							if(te.fireTemp == 350)
							{
								//firepits don't sync their data with the server properly so clientside firepits alwyas have 350 temp
								//we ignore this case and don't do anything, and we let the server side one handle it
								return;
							}
							if(te.fireTemp > 350)
							{
								//ok we found one so stop
								hasSmoke = true;
								return;
							}
						}
					}
				}
			}
		}
		//none found
		hasSmoke = false;
	}
	
	public void produceHoneyComb()
	{
		for(int i = 1; i < slots.length; i++)
		{
			if(slots[i] == null)
			{
				slots[i] = new ItemStack(Bees.honeyComb,1);
				return;
			}
		}
	}
	
	public void produceBeeComb()
	{
		for(int i = 1; i < slots.length; i++)
		{
			if(slots[i] == null)
			{
				slots[i] = new ItemStack(Bees.beeComb,1);
				return;
			}
		}
		for(int i = 1; i < slots.length; i++)
		{
			if(slots[i].getItem() == Bees.honeyComb)
			{
				slots[i] = new ItemStack(Bees.beeComb,1);
				return;
			}
		}
	}
	
	public void produceComb()
	{
		int s = worldObj.rand.nextInt(8);
		if(s == 0)
			produceBeeComb();
		else
			produceHoneyComb();
	}
	
	public void killComb()
	{
		slots[0] = new ItemStack(Bees.honeyComb,1);
		produceBeeComb();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		slots = new ItemStack[slots.length];
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if(byte0 >= 0 && byte0 < slots.length)
				slots[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
		}
	}
	
	private boolean inRange(EntityPlayer p)
	{
		double px = p.posX;
		double py = p.posY;
		double pz = p.posZ;
		double mx = xCoord;
		double my = yCoord;
		double mz = zCoord;
		double dist = Math.sqrt(Math.pow(px-mx, 2)+ Math.pow(pz-mz, 2));
		double distH = Math.abs(py-my);
		return (dist <= 2 && distH <= 3);
	
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < slots.length; i++)
		{
			if(slots[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				slots[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbt.setTag("Items", nbttaglist);
	}

	public boolean hasSmoke() 
	{		
		
		return hasSmoke;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
