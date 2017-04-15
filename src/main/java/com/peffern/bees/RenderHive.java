package com.peffern.bees;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * Hive rendering ISBRH
 * @author peffern
 *
 */
public class RenderHive implements ISimpleBlockRenderingHandler
{
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		//just use the itemIcon
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
			RenderBlocks renderer) 
	{
		Tessellator tess = Tessellator.instance;
		GL11.glColor3f(1, 1, 1);
		int brightness = block.getMixedBrightnessForBlock(renderer.blockAccess, x,y,z);
		tess.setBrightness(brightness);
		tess.setColorOpaque_F(1, 1, 1);
		
		IIcon icon = block.getIcon(renderer.blockAccess,x,y,z,renderer.blockAccess.getBlockMetadata(x,y,z));
		
		//each array index is a ring - hbufs are the space between the ring width and the edge
		//vbufs are the height of the ring above the bottom
		//heights are the size of the ring vertically
		int[] hbufs = new int[]		{6,4,2,0,2,	4,	7};
		int[] vbufs = new int[]		{0,2,4,6,10,12,14,  16};
		int[] heights = new int[]	{2,2,2,4,2,	2,	2};
		
		for(int i = 0; i < hbufs.length; i++)
		{
			drawRing(tess,icon,x,y,z,hbufs[i],vbufs[i],heights[i]);
		}
		
		//cross horizontal texture
		IIcon plateIcon = ((BlockHive)block).plateIcon;
		
		int[] plates = new int[]	{4,8,12,16,16,12,8,  2};
		
		//draw the cross rectangles
		for(int i = 0; i < plates.length; i++)
		{
			drawPlate(tess,plateIcon,x,y,z,plates[i],vbufs[i]);
		}
		
				
		return true;
	}
	
	/**
	 * draws a square ring at specified coordinates
	 * @param tess tessellator for drawing
	 * @param icon icon to draw
	 * @param x world x coord
	 * @param y world y coord
	 * @param z world z coord
	 * @param hbuf buffer space to put on either side of the ring
	 * @param vbuf buffer space to put below the ring
	 * @param height size of ring vertical
	 */
	public void drawRing(Tessellator tess, IIcon icon, double x, double y, double z, double hbuf, double vbuf, double height)
	{
		//get draw coordinates
		double minX = x + hbuf/16d;
		double maxX = x + 1 - hbuf/16d;
		double minZ = z + hbuf/16d;
		double maxZ = z + 1 - hbuf/16d;
		double minY = y + vbuf/16d;
		double maxY = y + (vbuf+height)/16d;
		
		//get texel coordinates
		double minU = icon.getInterpolatedU(hbuf);
		double maxU = icon.getInterpolatedU(16-hbuf);
		double minV = icon.getInterpolatedV(16-vbuf-height);
		double maxV = icon.getInterpolatedV(16-vbuf);
		
		//4 sides of ring
		tess.addVertexWithUV(maxX,minY, maxZ, maxU, maxV);
		tess.addVertexWithUV(maxX,maxY,maxZ,maxU,minV);
		tess.addVertexWithUV(minX,maxY,maxZ,minU,minV);
		tess.addVertexWithUV(minX,minY,maxZ,minU,maxV);
		tess.addVertexWithUV(minX,minY, maxZ, maxU, maxV);
		tess.addVertexWithUV(minX,maxY,maxZ,maxU,minV);
		tess.addVertexWithUV(maxX,maxY,maxZ,minU,minV);
		tess.addVertexWithUV(maxX,minY,maxZ,minU,maxV);
		
		tess.addVertexWithUV(maxX,minY, minZ, maxU, maxV);
		tess.addVertexWithUV(maxX,maxY,minZ,maxU,minV);
		tess.addVertexWithUV(minX,maxY,minZ,minU,minV);
		tess.addVertexWithUV(minX,minY,minZ,minU,maxV);
		tess.addVertexWithUV(minX,minY, minZ, maxU, maxV);
		tess.addVertexWithUV(minX,maxY,minZ,maxU,minV);
		tess.addVertexWithUV(maxX,maxY,minZ,minU,minV);
		tess.addVertexWithUV(maxX,minY,minZ,minU,maxV);
		
		tess.addVertexWithUV(maxX,minY, maxZ, maxU, maxV);
		tess.addVertexWithUV(maxX,maxY,maxZ,maxU,minV);
		tess.addVertexWithUV(maxX,maxY,minZ,minU,minV);
		tess.addVertexWithUV(maxX,minY,minZ,minU,maxV);
		tess.addVertexWithUV(maxX,minY, minZ, maxU, maxV);
		tess.addVertexWithUV(maxX,maxY,minZ,maxU,minV);
		tess.addVertexWithUV(maxX,maxY,maxZ,minU,minV);
		tess.addVertexWithUV(maxX,minY,maxZ,minU,maxV);
		
		tess.addVertexWithUV(minX,minY, maxZ, maxU, maxV);
		tess.addVertexWithUV(minX,maxY,maxZ,maxU,minV);
		tess.addVertexWithUV(minX,maxY,minZ,minU,minV);
		tess.addVertexWithUV(minX,minY,minZ,minU,maxV);
		tess.addVertexWithUV(minX,minY, minZ, maxU, maxV);
		tess.addVertexWithUV(minX,maxY,minZ,maxU,minV);
		tess.addVertexWithUV(minX,maxY,maxZ,minU,minV);
		tess.addVertexWithUV(minX,minY,maxZ,minU,maxV);
	}
	
	/**
	 * Draws a horizontal square
	 * @param tess tessellator for drawing
	 * @param icon icon to draw
	 * @param x world x coord
	 * @param y world y coord
	 * @param z world z coord
	 * @param plate side length of square
	 * @param vbuf vertical height of square
	 */
	public void drawPlate(Tessellator tess, IIcon icon, double x, double y, double z, double plate, double vbuf)
	{
		//determine buffer size
		double hbuf = (16d-plate)/2d;
		
		//world coords
		double minX = x + hbuf/16d;
		double maxX = x + 1 - hbuf/16d;
		double minZ = z + hbuf/16d;
		double maxZ = z + 1 - hbuf/16d;
		double valY = y + vbuf/16d;
		
		//texel coords
		double minU = icon.getInterpolatedU(hbuf);
		double maxU = icon.getInterpolatedU(16d-hbuf);
		double minV = icon.getInterpolatedV(hbuf);
		double maxV = icon.getInterpolatedV(16d-hbuf);
		
		//draw square
		tess.addVertexWithUV(maxX, valY, maxZ, maxU, maxV);
		tess.addVertexWithUV(maxX, valY, minZ, maxU, minV);
		tess.addVertexWithUV(minX, valY, minZ, minU, minV);
		tess.addVertexWithUV(minX, valY, maxZ, minU, maxV);
		tess.addVertexWithUV(minX, valY, maxZ, maxU, maxV);
		tess.addVertexWithUV(minX, valY, minZ, maxU, minV);
		tess.addVertexWithUV(maxX, valY, minZ, minU, minV);
		tess.addVertexWithUV(maxX, valY, maxZ, minU, maxV);
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return Bees.hiveRenderID;
	}

}
