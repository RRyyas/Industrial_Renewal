package cassiokf.industrialrenewal.world.generation;

import cassiokf.industrialrenewal.IndustrialRenewal;
import cassiokf.industrialrenewal.References;
import cassiokf.industrialrenewal.config.IRConfig;
import cassiokf.industrialrenewal.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class OreGeneration implements IWorldGenerator
{
    public static final List<Item> MINERABLE_ORES = new ArrayList<>();
    public static final List<Item> DEEP_VEIN_ORES = new ArrayList<>();
    public static Map<ChunkPos, ItemStack> CHUNKS_VEIN = new ConcurrentHashMap<>();

    private static void generateOverworld(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        //if (world.isRemote || world.provider.getDimension() != 0)
        //    return;
        //if (IRConfig.MainConfig.Generation.regenerateDeepVein) OreGeneration.tryRetrogenDeepVein(world);
        //generateDeepVein(random, chunkX, chunkZ, world, false);
    }

    public static void init()
    {
        final List<String> names = new ArrayList<>();
        for (String name : OreDictionary.getOreNames())
        {
            if (name.startsWith("ore")) names.add(name);
        }
        for (String name : names)
        {
            NonNullList<ItemStack> ores = OreDictionary.getOres(name);
            for (ItemStack ore : ores)
            {
                if (!ore.isEmpty()) MINERABLE_ORES.add(ore.getItem());
            }
        }
        populateDeepVeinOres();
    }

    public static ItemStack getChunkVein(World world, BlockPos pos)
    {
        ChunkPos cPos = world.getChunk(pos).getPos();
        ItemStack stack = CHUNKS_VEIN.get(cPos);
        return stack != null ? stack : ItemStack.EMPTY;
    }

    public static ItemStack generateNewVein(World world)
    {
        if (!IRConfig.MainConfig.Generation.spawnDeepVein || DEEP_VEIN_ORES.size() <= 0) return ItemStack.EMPTY;
        int chance = IRConfig.MainConfig.Generation.deepVeinSpawnRate;
        if (world.rand.nextInt(100) < chance)
        {
            ItemStack stack = ItemStack.EMPTY;
            while (stack.isEmpty())
            {
                stack = getNewStack(world);
            }
            return stack;
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack getNewStack(World world)
    {
        int min = IRConfig.MainConfig.Generation.deepVeinMinOre;
        int oreQuantity = world.rand.nextInt(IRConfig.MainConfig.Generation.deepVeinMaxOre - min) + min;
        Item item = DEEP_VEIN_ORES.get(world.rand.nextInt(DEEP_VEIN_ORES.size() - 1));
        Block block = Block.getBlockFromItem(item);
        ItemStack stack = new ItemStack(block.getItemDropped(block.getDefaultState(), world.rand, 0), 1, block.damageDropped(block.getDefaultState()));
        stack.setCount(oreQuantity);
        return stack;
    }

    private static void populateDeepVeinOres()
    {
        Map<String, Integer> map = IRConfig.MainConfig.Generation.deepVeinOres;
        if (map.isEmpty()) return;
        int i = 0;
        for (String str : map.keySet())
        {
            if (OreDictionary.doesOreNameExist(str))
            {
                List<ItemStack> list = OreDictionary.getOres(str);
                if (list.isEmpty())
                {
                    IndustrialRenewal.LOGGER.warn(TextFormatting.RED + "Oredict not found for: " + str + " , this ore will not be generate in Deep Veins");
                    continue;
                }
                ItemStack stack = list.get(0).copy();
                if (str.equals("oreIron")) stack = new ItemStack(ModBlocks.veinHematite);
                if (!stack.isEmpty() && !DEEP_VEIN_ORES.contains(stack.getItem()))
                {
                    placeItemXTimes(stack.getItem(), map.get(str));
                    i++;
                }
            }
            else
            {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str));
                if (item != null && !DEEP_VEIN_ORES.contains(item) && item != Items.AIR)
                {
                    placeItemXTimes(item, map.get(str));
                    i++;
                }
            }
        }
        IndustrialRenewal.LOGGER.info(TextFormatting.GREEN + References.NAME + " Registered " + i + " DeepVein Variants");
    }

    private static void placeItemXTimes(Item item, int t)
    {
        IndustrialRenewal.LOGGER.info(TextFormatting.YELLOW + References.NAME + " Registered " + item.getRegistryName());
        for (int i = 0; i < t; i++)
        {
            DEEP_VEIN_ORES.add(item);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.getDimension() == 0)
        {
            //generateOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }
}
