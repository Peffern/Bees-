package com.peffern.bees;

import com.bioxx.tfc.Items.ItemTerra;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraftforge.client.MinecraftForgeClient;

/**
 * Item for bee comb to have custom properties - currently none :/
 * @author peffern
 *
 */
public class ItemBeeComb extends ItemTerra
{
	@Override
	public void registerIcons(IIconRegister register)
	{
		this.itemIcon = register.registerIcon(Bees.MODID + ":" + "beeComb");
		MinecraftForgeClient.registerItemRenderer(this, new CombRenderer());
	}
}
