package com.peffern.bees;

import com.bioxx.tfc.GUI.GuiContainerTFC;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Gui bee box screen
 * @author peffern
 *
 */
public class GuiBeeBox extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Bees.MODID, "textures/gui/gui_beebox.png");

	public GuiBeeBox(InventoryPlayer inventoryplayer, World world, int i, int j, int k)
	{
		super(new ContainerBeeBox(inventoryplayer, world, i, j, k), 176, 85);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		this.drawGui(texture);
	}


}
