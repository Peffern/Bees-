package com.peffern.bees;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Inventory slot for bee box inventory
 * @author peffern
 *
 */
public class SlotBeeBox extends Slot
{
	/** if true pretend to be empty */
	private boolean lyingAboutStack;
	/** only allow input if true */
	private boolean acceptInput = false;
	
	public SlotBeeBox(IInventory inventory, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_, boolean p_i1824_5_) 
	{
		super(inventory, p_i1824_2_, p_i1824_3_, p_i1824_4_);
		acceptInput = p_i1824_5_;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if(acceptInput)
			return (stack != null && stack.getItem() == Bees.beeComb);
		else
			return false;
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
	
	/**
	 * Begin lying about state
	 */
	public void warnDoubleClick()
	{
		lyingAboutStack = true;
	}
	
	/**
	 * Stop lying about state
	 */
	public void unWarnDoubleClick()
	{
		lyingAboutStack = false;
	}
	
	/**
	 * If we are lying about state, pretend to be empty,
	 * otherwise report the real value.
	 */
	@Override
	public boolean getHasStack()
	{
		if(lyingAboutStack)
		{
			return false;
		}
		else
			return super.getHasStack();
	}

}
