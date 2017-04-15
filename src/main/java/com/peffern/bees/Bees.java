package com.peffern.bees;

import com.bioxx.tfc.TerraFirmaCraft;
import com.bioxx.tfc.Blocks.BlockTerra;
import com.bioxx.tfc.Core.FluidBaseTFC;
import com.bioxx.tfc.Core.TFCTabs;
import com.bioxx.tfc.Food.ItemFoodTFC;
import com.bioxx.tfc.Items.ItemAlcohol;
import com.bioxx.tfc.Items.ItemTFCArmor;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Items.ItemBlocks.ItemTerraBlock;
import com.bioxx.tfc.Items.Pottery.ItemPotteryBase;
import com.bioxx.tfc.Render.Item.FoodItemRenderer;
import com.bioxx.tfc.api.Armor;
import com.bioxx.tfc.api.TFCFluids;
import com.bioxx.tfc.api.TFCItems;
import com.bioxx.tfc.api.TFCOptions;
import com.bioxx.tfc.api.Crafting.BarrelAlcoholRecipe;
import com.bioxx.tfc.api.Crafting.BarrelManager;
import com.bioxx.tfc.api.Crafting.BarrelVinegarRecipe;
import com.bioxx.tfc.api.Crafting.KilnCraftingManager;
import com.bioxx.tfc.api.Crafting.KilnRecipe;
import com.bioxx.tfc.api.Enums.EnumFoodGroup;
import com.bioxx.tfc.api.Enums.EnumWeight;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * This mod makes bees
 * @author peffern
 *
 */
@Mod(modid = Bees.MODID, name = Bees.MODNAME, version = Bees.VERSION, dependencies = "required-after:" + "terrafirmacraft" + ";")
public class Bees 
{
	/** Mod instance Singleton */
	@Instance(Bees.MODID)
	public static Bees instance;

	/** Mod ID String */
	public static final String MODID = "bees";
	
	/** Mod Name */
	public static final String MODNAME = "Bees?";
	
	/** Mod Version */
	public static final String VERSION = "1.0";
	
	/** The wild beehive Block*/
	public static Block hive;
	
	/** The bee box block */
	public static Block beeBox;
	
	/** block of wax */
	public static Block waxBlock;
	
	/** candle */
	public static Block candle;
	
	/** Beekeeping Veil armor item */
	public static Item beeVeil;
	
	/** regular honey comb */
	public static Item honeyComb;

	/** honey comb with bees*/
	public static Item beeComb;
	
	/** bee scoop */
	public static Item beeScoop;
	
	/** wax product */
	public static Item beeswax;
	
	/** honey product */
	public static Item honey;
	
	/** wax mold construction */
	public static Item waxMold;
	
	/** mead alcohol item */
	public static Item mead;
	
	/** custom renderer for hives */
	public static int hiveRenderID;
	
	/** gui id number for bee box */
	public static int beeBoxGuiId;
	
	/** armor item for the bee veil */
	public static Armor beeVeilArmorIndex;
	
	/** damage source */
	public static DamageSource beesDamage;
	
	/** mead alcohol */
	public static Fluid MEAD = new FluidBaseTFC("bees_mead").setBaseColor(0xad770c);
	
	/** configuration of candles */
	public static int candleBurnTime;
	
	/**
	 * Mod setup and item registration
	 * @param e initialization event
	 */
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		
		//do it relative to torches so I can avoid learning how config works
		candleBurnTime = TFCOptions.torchBurnTime * 3;
		
		//same nutrition as sugar
		honey = new ItemFoodTFC(EnumFoodGroup.None, 30, 0, 0, 0, 0, true)
		{
			@Override
			public void registerIcons(IIconRegister register)
			{
				this.itemIcon = register.registerIcon(Bees.MODID + ":" + "honey");
				MinecraftForgeClient.registerItemRenderer(this, new FoodItemRenderer());
			}
		}.setDecayRate(0.01f).setUnlocalizedName("honey");
		GameRegistry.registerItem(honey, honey.getUnlocalizedName());
		
		//do mead fluid setup
		mead = new ItemAlcohol().setUnlocalizedName("mead").setCreativeTab(TFCTabs.TFC_FOODS);
		GameRegistry.registerItem(mead,mead.getUnlocalizedName());
		FluidRegistry.registerFluid(MEAD);
		FluidContainerRegistry.registerFluidContainer(new FluidStack(MEAD, 250), new ItemStack(mead), new ItemStack(TFCItems.glassBottle));
		
		//mead fermentation recipes
		BarrelManager.getInstance().addRecipe(new BarrelAlcoholRecipe(ItemFoodTFC.createTag(new ItemStack(honey), 160), new FluidStack(TFCFluids.FRESHWATER, 10000), null, new FluidStack(MEAD, 10000)));
		BarrelManager.getInstance().addRecipe(new BarrelVinegarRecipe(new FluidStack(MEAD, 100), new FluidStack(TFCFluids.VINEGAR, 100)));

		//damage source
		beesDamage = new DamageSource("bees");
		
		//wild hives TE and worldgen
		hive = new BlockHive().setBlockName("Hive").setHardness(0.5f);
		GameRegistry.registerBlock(hive, hive.getUnlocalizedName());	
		GameRegistry.registerTileEntity(TEHive.class, "Hive");	
		GameRegistry.registerWorldGenerator(new WorldGenHives(),9);
		hiveRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(hiveRenderID, new RenderHive());
		
		//armor
		beeVeilArmorIndex = new Armor(100,0,0,0,"Bee Veil");
		beeVeil = new ItemTFCArmor(beeVeilArmorIndex, TerraFirmaCraft.proxy.getArmorRenderID("bees"), 0, 50,3)
		{
			
			@Override
			public void registerIcons(IIconRegister registerer)
			{
				this.itemIcon = registerer.registerIcon(Bees.MODID + ":" + "armor/beeVeil");
			}
			@Override
			public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
			{
				return Bees.MODID + ":" + "textures/models/armor/beeveil.png";
			}
			@Override
			public EnumWeight getWeight(ItemStack is)
			{
				return EnumWeight.LIGHT;
			}
		}.setUnlocalizedName("beeVeil");
		GameRegistry.registerItem(beeVeil, beeVeil.getUnlocalizedName());
		GameRegistry.addShapedRecipe(new ItemStack(beeVeil,1), "ABA","CBC","C C", 'A', Items.string, 'B', TFCItems.burlapCloth, 'C', TFCItems.juteFiber);
		
		//honeycomb items
		honeyComb = new ItemTerra()
		{
			@Override
			public void registerIcons(IIconRegister register)
			{
				this.itemIcon = register.registerIcon(Bees.MODID + ":" + "honeyComb");
			}
		}.setUnlocalizedName("plainComb").setCreativeTab(TFCTabs.TFC_MISC);
		GameRegistry.registerItem(honeyComb, honeyComb.getUnlocalizedName());
		beeComb = new ItemBeeComb().setUnlocalizedName("beeComb").setCreativeTab(TFCTabs.TFC_MISC);
		GameRegistry.registerItem(beeComb, beeComb.getUnlocalizedName());
		GameRegistry.addShapelessRecipe(new ItemStack(honeyComb), new ItemStack(beeComb));

		//scoop
		beeScoop = new ItemScoop(ToolMaterial.WOOD).setUnlocalizedName("beeScoop");
		GameRegistry.registerItem(beeScoop, beeScoop.getUnlocalizedName());
		GameRegistry.addShapedRecipe(new ItemStack(beeScoop,1), "AB ", "BC ", "  C", 'A', TFCItems.burlapCloth, 'B', Items.string, 'C', TFCItems.stick);
		
		//wax items
		beeswax = new ItemTerra()
		{
			@Override
			public void registerIcons(IIconRegister register)
			{
				this.itemIcon = register.registerIcon(Bees.MODID + ":" + "waxLump");
			}
		}.setUnlocalizedName("beeswax");
		GameRegistry.registerItem(beeswax, beeswax.getUnlocalizedName());
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(beeswax), new ItemStack(honeyComb),"itemKnife"));
		
		waxBlock = new BlockTerra()
		{
			@Override
			public void registerBlockIcons(IIconRegister register)
			{
				this.blockIcon = register.registerIcon(Bees.MODID + ":" + "wax");
			}
			@Override
			public void breakBlock(World world, int x, int y, int z, Block block, int meta)
			{
				world.spawnEntityInWorld(new EntityItem(world,x,y,z,new ItemStack(Item.getItemFromBlock(waxBlock),1)));
			}
		}.setBlockName("wax").setHardness(0.2f).setCreativeTab(TFCTabs.TFC_DECORATION);
		GameRegistry.registerBlock(waxBlock, ItemTerraBlock.class, waxBlock.getUnlocalizedName());
		GameRegistry.addShapedRecipe(new ItemStack(Item.getItemFromBlock(waxBlock),1), "AA", "AA", 'A', new ItemStack(beeswax,1));
		GameRegistry.addShapelessRecipe(new ItemStack(beeswax,4), new ItemStack(Item.getItemFromBlock(waxBlock),1));
		
		waxMold = new ItemPotteryBase()
		{
			@Override
			public void registerIcons(IIconRegister registerer)
			{
				this.clayIcon = registerer.registerIcon(Bees.MODID + ":" + metaNames[0]);
				this.ceramicIcon = registerer.registerIcon(Bees.MODID + ":" + metaNames[1]);
			}
		}.setMetaNames(new String[]{"waxMold","waxMoldCooked"}).setUnlocalizedName("Wax Mold");
		GameRegistry.registerItem(waxMold, waxMold.getUnlocalizedName());
		KilnCraftingManager.getInstance().addRecipe(new KilnRecipe(new ItemStack(waxMold,1,0),0,new ItemStack(waxMold,1,1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(waxMold,1,0), "AAA", "ABA", "AAA", 'A', new ItemStack(TFCItems.clayBall,1), 'B', new ItemStack(Item.getItemFromBlock(waxBlock),1)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TFCItems.ceramicMold,6,1), new ItemStack(waxMold,1,1), "itemHammer"));

		candle = new BlockCandle().setBlockName("candle");
		GameRegistry.registerBlock(candle, ItemTerraBlock.class, candle.getUnlocalizedName());
		Item wax = Item.getItemFromBlock(waxBlock);
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(candle),12), new ItemStack(wax), new ItemStack(wax), new ItemStack(wax), new ItemStack(Items.string)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Item.getItemFromBlock(candle),32), new ItemStack(wax), new ItemStack(wax), new ItemStack(wax),new ItemStack(wax), new ItemStack(wax), new ItemStack(wax),new ItemStack(wax), new ItemStack(wax), new ItemStack(Items.string)));
		
		
		//bee box
		beeBox = new BlockBeeBox().setBlockName("beeBox").setHardness(0.5f);
		GameRegistry.registerBlock(beeBox,ItemBlockBeeBox.class,beeBox.getUnlocalizedName());
		GameRegistry.registerTileEntity(TEBeeBox.class, "BeeBox");
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Item.getItemFromBlock(beeBox),1), "ABA", "CCC", "AAA", 'A', "plankWood", 'B', "itemSaw", 'C', Bees.honeyComb));
		beeBoxGuiId = 1;
		
		//Forge
		NetworkRegistry.INSTANCE.registerGuiHandler(Bees.instance, new GuiHandler());
		FMLCommonHandler.instance().bus().register(new CraftingHandler());

		
	}
	
	
	
	
}
