package com.peffern.bees;

import com.bioxx.tfc.Core.Player.PlayerInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Inventory container for the bee box gui
 * @author peffern
 *
 */
public class ContainerBeeBox extends Container
{		
	
	private TEBeeBox te = null;
	public ContainerBeeBox(InventoryPlayer playerInventory, World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof IInventory)
		{
			IInventory blockInventory = (IInventory)te;
			//input slot
			this.addSlotToContainer(new SlotBeeBox(blockInventory, 0, 35, 34, true));
			//output slots
			this.addSlotToContainer(new SlotBeeBox(blockInventory, 1, 71, 25, false));
			this.addSlotToContainer(new SlotBeeBox(blockInventory, 2, 89, 25, false));
			this.addSlotToContainer(new SlotBeeBox(blockInventory, 3, 71, 43, false));
			this.addSlotToContainer(new SlotBeeBox(blockInventory, 4, 89, 43, false));
		}
		if(te instanceof TEBeeBox)
			this.te = (TEBeeBox)te;
		

		//player inventory
		PlayerInventory.buildInventoryLayout(this, playerInventory, 8, 90, false, true);
	}
	
	/**
	 * Override slot click to handle the bee comb -> honey comb transformation for various player interactions
	 */
	@Override
	public ItemStack slotClick(int slotNum, int button, int flag, EntityPlayer player)
	{
		if(flag == 6) //double click
		{
			
				warnSlots(); //lie about stack size so the double click won't grab the comb slots
				//perform action
				ItemStack clickResult = super.slotClick(slotNum, button, flag, player);
				unWarnSlots(); //stop lying about stack size
				return clickResult;
		}
		if(flag == 1) // shift click
		{
			if(slotNum >= 5) //from player inventory
			{
				Slot slot = (Slot)this.inventorySlots.get(slotNum);
				if(slot != null && slot.getHasStack())
				{
					ItemStack stack = slot.getStack();
					Slot combInputSlot = ((Slot)this.inventorySlots.get(0));
					if(stack != null)
					{
						//adding comb
						if(stack.getItem() == Bees.beeComb && !combInputSlot.getHasStack())
						{
							if(stack.stackSize == 1)
							{
								combInputSlot.putStack(stack);
								slot.decrStackSize(1);
							}
							else
							{
								ItemStack newStack = stack.splitStack(1);
								combInputSlot.putStack(newStack);
							}	
						}
						else //shift click within player inventory
						{
							if(slotNum >= 32) //from hotbar
							{
								transferToSlots(slot,stack,5,31);
							}
							else //into hotbar
							{
								transferToSlots(slot,stack,32,40);
							}
						}
					}
				}
			}
			else //shift-clicked one of the bee slots
			{
				Slot slot = (Slot)this.inventorySlots.get(slotNum);
				if(slot != null && slot.getHasStack())
				{
					ItemStack stack = slot.getStack();
					if(stack != null)
					{
						if(stack.getItem() != Bees.beeComb)
						{
							transferToSlots(slot,stack,5,40);
						}
						else
						{
							//handle transformation
							ItemStack combStack = transformBeeComb(stack);
							transferToSlots(slot,combStack,5,40);
						}
					}
				}
			}
			return null;
		}
		if(flag == 0) //regular click
		{
			if(slotNum < 5) //from bee slot
			{
				Slot slot = null;;
				if(slotNum >= 0 && slotNum < inventorySlots.size())
					slot = (Slot)inventorySlots.get(slotNum);
				if(slot != null && slot.getHasStack())
				{
					ItemStack stack = slot.getStack();
					if(stack != null)
					{
						if(stack.getItem() != Bees.beeComb) //not a bee comb, handle normally
							return super.slotClick(slotNum, button, flag, player);
						else //change the stack contents before the slotClick hits
						{
							slot.putStack(transformBeeComb(stack));
							return super.slotClick(slotNum, button, flag, player);
						}
					}
				}
			}
			else //normal slot normal behavior
				return super.slotClick(slotNum, button, flag, player);
		}
		return super.slotClick(slotNum, button, flag, player);
	}
	
	/**
	 * Handles the transformation of a bee comb based on current container properties
	 * @param stack stack to transform
	 * @return transformed stack
	 */
	public ItemStack transformBeeComb(ItemStack stack)
	{
		//not bee combs
		if(stack == null)
			return null;
		if(stack.getItem() != Bees.beeComb)
			return stack;
		//bee comb
		else
		{
			//with smoke allow bee comb
			if(te.hasSmoke())
				return stack;
			else
			{
				ItemStack combStack = new ItemStack(Bees.honeyComb,stack.stackSize);
				//do the NBT stuff here - currently only damage values and honey combs have no damage 
				return combStack;
			}
			
		}
	}
	
	/**
	 * distribute an itemstack from a slot to some other slots
	 * @param slot the slot to transfer from
	 * @param stack the stack to transfer
	 * @param first starting index
	 * @param last ending index
	 */
	public void transferToSlots(Slot slot, ItemStack stack, int first, int last)
	{
		for(int num = first; num <= last; num++)
		{
			Slot toSlot = (Slot)this.inventorySlots.get(num);
			if(!toSlot.getHasStack())
			{
				toSlot.putStack(stack.copy());
				slot.decrStackSize(stack.stackSize);
				break;
			}
			else if(stack.isItemEqual(toSlot.getStack()) && ItemStack.areItemStackTagsEqual(stack, toSlot.getStack()))
			{
				int maxAdd = toSlot.getStack().getMaxStackSize() - toSlot.getStack().stackSize;
				if(stack.stackSize <= maxAdd)
				{
					toSlot.getStack().stackSize += stack.stackSize;
					slot.decrStackSize(stack.stackSize);
					break;
				}
				else
				{
					toSlot.getStack().stackSize += maxAdd;
					slot.decrStackSize(maxAdd);
				}
			}
		}
	}
	
	/**
	 * tells the slots to lie about their stack size
	 */
	public void warnSlots()
	{
		for(int i = 0; i <= 4; i++)
		{
			SlotBeeBox slot = (SlotBeeBox)this.inventorySlots.get(i);
			slot.warnDoubleClick();
		}
	}
	
	/**
	 * tells the slots to stop lying about their stack size
	 */
	public void unWarnSlots()
	{
		for(int i = 0; i <= 4; i++)
		{
			SlotBeeBox slot = (SlotBeeBox)this.inventorySlots.get(i);
			slot.unWarnDoubleClick();
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}
	
	
}
