package cassiokf.industrialrenewal.blocks;

import cassiokf.industrialrenewal.blocks.abstracts.BlockHorizontalFacing;
import cassiokf.industrialrenewal.tileentity.TileEntityElectricPump;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockElectricPump extends BlockHorizontalFacing
{
    public static final PropertyInteger INDEX = PropertyInteger.create("index", 0, 1);

    public BlockElectricPump(String name, CreativeTabs tab)
    {
        super(name, tab, Material.IRON);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (state.getValue(INDEX) == 0)
        {
            worldIn.setBlockState(pos.offset(state.getValue(FACING)), state.withProperty(INDEX, 1));
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        switch (state.getValue(INDEX))
        {
            case 0:
                if (IsPump(worldIn, pos.offset(state.getValue(FACING))))
                    worldIn.setBlockToAir(pos.offset(state.getValue(FACING)));
                break;
            case 1:
                if (IsPump(worldIn, pos.offset(state.getValue(FACING).getOpposite())))
                    worldIn.setBlockToAir(pos.offset(state.getValue(FACING).getOpposite()));
                break;
        }
        super.breakBlock(worldIn, pos, state);

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(I18n.format("info.industrialrenewal.requires")
                + ": "
                + TileEntityElectricPump.energyPerTick
                + " FE/t");
        super.addInformation(stack, player, tooltip, advanced);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        return false;
    }

    private boolean IsPump(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getBlock() instanceof BlockElectricPump;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        EntityPlayer player = worldIn.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10D, false);
        if (player == null) return false;
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)
                && worldIn.getBlockState(pos.offset(player.getHorizontalFacing())).getBlock().isReplaceable(worldIn, pos.offset(player.getHorizontalFacing()));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, INDEX);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(INDEX, 0);
    }

    @Override
    public IBlockState getStateFromMeta(final int meta)
    {
        int directionIndex = meta;
        if (meta > 3) directionIndex -= 4;
        int index = 0;
        if (meta > 3) index = 1;
        return getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(directionIndex)).withProperty(INDEX, index);
    }

    @Override
    public int getMetaFromState(final IBlockState state)
    {
        int i = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(INDEX) == 1) i += 4;
        return i;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntityElectricPump createTileEntity(World world, IBlockState state)
    {
        return new TileEntityElectricPump();
    }
}
