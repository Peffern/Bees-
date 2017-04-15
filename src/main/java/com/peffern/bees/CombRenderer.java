package com.peffern.bees;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc.Render.Item.FoodItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

/**
 * awful hack i put together to render the bee combs in the bee box
 * @author peffern
 *
 */
public class CombRenderer implements IItemRenderer
{

	// reflect on the renderer things in FoodItemRenderer so I can use them
	static Method renderQuad = null;
	static Method renderIcon = null;
	static
	{
		try
		{
		renderQuad = FoodItemRenderer.class.getDeclaredMethod("renderQuad", double.class, double.class, double.class, double.class, int.class);
		renderQuad.setAccessible(true);
		renderIcon = FoodItemRenderer.class.getDeclaredMethod("renderIcon", int.class, int.class, IIcon.class, int.class, int.class);
		renderIcon.setAccessible(true);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
	}
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack is, Object... data) 
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if(is != null)
		{			
			renderIcon(0, 0, is.getItem().getIconIndex(is), 16, 16);
			
			if(type == ItemRenderType.INVENTORY)
			{
				
				int damage = is.getItemDamage();
				if(damage > 0)
				{
					float perc = (8f - damage) / 8f;
					if (perc <= 1) //only draw bar if theres some damage
					{
						
						//draw black bar
						renderQuad(1, 14, 13, 1, 0);
						float top = perc * 13.0F;
		
						//draw white bar on top of it
						renderQuad(1, 14, top, 1, 0xffffff);
						
					}
				}
				
			}

		}

		GL11.glPopAttrib();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	public static void renderQuad(double x, double y, double sizeX, double sizeY, int color)
	{
		if(renderQuad != null)
		{
			try
			{
				renderQuad.invoke(FoodItemRenderer.class, x,y,sizeX,sizeY,color);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public static void renderIcon(int x, int y, IIcon icon, int sizeX, int sizeY)
	{
		if(renderIcon != null)
		{
			try
			{
				renderIcon.invoke(FoodItemRenderer.class, x,y,icon,sizeX,sizeY);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

}
