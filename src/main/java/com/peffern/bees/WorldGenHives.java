package com.peffern.bees;

import java.util.Random;

import com.bioxx.tfc.Core.TFC_Climate;
import com.bioxx.tfc.api.TFCBlocks;
import com.bioxx.tfc.api.Constant.Global;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Generate hive blocks in the world
 * @author peffern
 *
 */
public class WorldGenHives implements IWorldGenerator
{

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider) 
	{
		//System.out.println("gen");
		
		//check for climate okay and RNG check
		int check = climateCheck(chunkX * 16, chunkZ * 16, world);
		//System.out.println("check: " + check);
		if(check > 0 && world.rand.nextInt(check) == 0)
		{
			//System.out.println("passed climate check");
			//search for a tree - starting 3 blocks into the chunk in the +x and +z directions so the spiral fully fills the chunk
			HivePointer ptr = new HivePointer(chunkX * 16 + 3,Global.SEALEVEL + 1, chunkZ * 16 + 3, world);
			ptr.iterate();
			if(ptr.found)
			{
				//System.out.println("tree");
				addToWoodBlock(ptr.x,ptr.y,ptr.z,world);
			}
		}
		
		
	}
	
	/**
	 * gets the chance of spawning a beehive in this chunk
	 * @param x real x coord
	 * @param z real z coord
	 * @param world world referebce
	 * @return the number N such that the chance of spawning a beehive here is 1 in N (0 for impossible)
	 */
	public int climateCheck(int x, int z, World world)
	{
		float temp = TFC_Climate.getBioTemperature(world, x, z);
		//System.out.println("temp: " + temp);
		if(temp < 7 || temp > 43)
			return 0;
		
		double dist = Math.abs(25-temp);
		
		return (int)((4d/9d)*dist + 4);
	}
	
	
	
	public static boolean isWood(int x, int y, int z, World world)
	{
		return world.getBlock(x, y, z) == TFCBlocks.logNatural || world.getBlock(x,y,z) == TFCBlocks.logNatural2;
	}
	
	public static boolean isLeaves(int x, int y, int z, World world)
	{
		return world.getBlock(x, y, z) == TFCBlocks.leaves || world.getBlock(x,y,z) == TFCBlocks.leaves2;
	}
	
	/**
	 * Gets a "code" for a block to determine where it is relative to the land/air boundary to find trees
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 * @param world world reference
	 * @return 0 if we found a tree, 1 if we're in the air (i.e. above possible trees), -1 if we're in the ground (i.e below possible trees)
	 */
	public static int genCode(int x, int y, int z, World world)
	{
		if(isWood(x,y,z,world))
			return 0;
		else if(world.isAirBlock(x, y, z) || isLeaves(x,y,z,world))
			return -1;
		else
			return 1;
	}
	
	/**
	 * add the hive to a wood block 
	 * @param x x coord of wood block
	 * @param y y coord of wood block
	 * @param z z coord of wood block
	 * @param world world
	 */
	public void addToWoodBlock(int x, int y, int z, World world)
	{
		//coords are gonna move
		while(isWood(x,y,z, world))
	{
			//find 4 different directions of (leaves above space next to wood)
			//if you find one make the hive
		if(isLeaves(x-1,y+1,z,world) && world.isAirBlock(x-1, y, z))
		{

			makeHive(x-1,y,z,world);
			return;
		}
		if(isLeaves(x+1,y+1,z,world) && world.isAirBlock(x+1, y, z))
		{

			makeHive(x+1,y,z,world);
			return;
		}
		if(isLeaves(x,y+1,z-1,world) && world.isAirBlock(x, y, z-1))
		{

			makeHive(x,y,z-1,world);
			return;
		}
		if(isLeaves(x,y+1,z+1,world) && world.isAirBlock(x, y, z+1))
		{

			makeHive(x,y,z+1,world);
			return;
		}
		//otherwise go up
		y++;
	}
		
	}
	
	/**
	 * actually sets the hive block
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 * @param world world
	 */
	public void makeHive(int x, int y, int z, World world)
	{
		if(world.setBlock(x, y, z, Bees.hive))
		{
			//debug here
			//System.out.println("(" + x + "," + y + "," + z + ")");
		}
	}
	
	/**
	 * Iteration over chunk helper class
	 * @author peffern
	 *
	 */
	class HivePointer
	{
		//current position
		protected int x;
		protected int y;
		protected int z;
		
		//whether we found a tree
		protected boolean found;
		
		//world reference
		private World world;
		
		//current motion distance
		private int dx;
		private int dz;
		
		//progress on current path
		private int c;
		//how many paths have been taken
		private int path;
		
		public HivePointer(int i, int j, int k, World w)
		{
			x = i;
			y = j;
			z = k;
			world = w;
			found = false;
			
			dx = 1;
			dz = 0;
			
			c = 0;
			path = 0;
		}
		
		//moves the pointer y to the wood location if there is one
		//otherwise moves to the boundary between air and ground
		protected void vertical()
		{
			//check for wood
			int code = genCode(x,y,z,world);
			if(code == 0)
			{
				found = true;
				return;
			}
			//look down for air, up for ground
			int code2 = genCode(x,y+code,z,world);
			//found wood again
			if(code2 == 0)
			{
				y = y + code;
				found = true;
			}
			//not at boundary so keep looking until we find the boundary or a tree
			else if(code == code2)
			{
				y = y + code;
				vertical();
			}
			//if at boundary stop
		}
		
		//rotates the direction the pointer moves horizontaly by 90 degrees
		private void nextD()
		{
			if(dx == 1)
			{
				dx = 0;
				dz = 1;
			}
			else if(dz == 1)
			{
				dx = -1;
				dz = 0;
			}
			else if(dx == -1)
			{
				dx = 0;
				dz = -1;
			}
			else
			{
				dx = 1;
				dz = 0;
			}
		}
		
		//determines the length of the current section of spiral
		//based on how many spiral lengths have passed
		private int pathCount()
		{
			//last one is shorter so we don't leave the chunk
			if(path == 14)
				return 7;
			
			//otherwise use this quasi-floor function to make the spiral
			return (path/2)+1;
		}
		
		//moves the pointer horizontally in the current direction
		//rotates at the end of paths
		protected void horizontal()
		{
			x += dx;
			z += dz;
			c++;
			//detect end of path
			if(c == pathCount())
			{
				c = 0;
				path++;
				nextD();
			}		
		}
		
		//searches in a square spiral that if we start at x+3,z+3 will fill a whole chunk. 
		//looks along the boundary and tries to find trees
		public void iterate()
		{
			
			while(true)
			{	
				//search for current block
				vertical();
				//detect trees
				if(found)
					return;
				//go to next block
				horizontal();
				//at the end of the chunk (15th path would be outside chunk)
				if(path > 14)
				{
					//do one last corner check
					vertical();
					return;
				}
			}
					
			
			
		}
		
		
	}

}
