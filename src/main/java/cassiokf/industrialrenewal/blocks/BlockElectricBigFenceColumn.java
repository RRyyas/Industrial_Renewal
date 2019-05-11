package cassiokf.industrialrenewal.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockElectricBigFenceColumn extends BlockBasicElectricFence {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyInteger INDEX = PropertyInteger.create("index", 0, 2);

    public static final PropertyBool ACTIVE_LEFT = PropertyBool.create("active_left");
    public static final PropertyBool ACTIVE_RIGHT = PropertyBool.create("active_right");
    public static final PropertyBool ACTIVE_LEFT_TOP = PropertyBool.create("active_left_top");
    public static final PropertyBool ACTIVE_RIGHT_TOP = PropertyBool.create("active_right_top");
    public static final PropertyBool ACTIVE_LEFT_DOWN = PropertyBool.create("active_left_down");
    public static final PropertyBool ACTIVE_RIGHT_DOWN = PropertyBool.create("active_right_down");


    public BlockElectricBigFenceColumn(String name, CreativeTabs tab) {
        super(name, tab);
        setSoundType(SoundType.METAL);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(INDEX) == 0) {
            worldIn.setBlockState(pos.up(), state.withProperty(INDEX, 1));
            worldIn.setBlockState(pos.up(2), state.withProperty(INDEX, 2));
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        switch (state.getValue(INDEX)) {
            case 0:
                if (IsBigFence(worldIn, pos.up())) worldIn.setBlockToAir(pos.up());
                if (IsBigFence(worldIn, pos.up(2))) worldIn.setBlockToAir(pos.up(2));
                break;
            case 1:
                if (IsBigFence(worldIn, pos.down())) worldIn.setBlockToAir(pos.down());
                if (IsBigFence(worldIn, pos.up())) worldIn.setBlockToAir(pos.up());
                break;
            case 2:
                if (IsBigFence(worldIn, pos.down())) worldIn.setBlockToAir(pos.down());
                if (IsBigFence(worldIn, pos.down(2))) worldIn.setBlockToAir(pos.down(2));
                break;
        }
        super.breakBlock(worldIn, pos, state);

    }

    private boolean IsBigFence(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() instanceof BlockElectricBigFenceColumn;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)
                && worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn, pos.up())
                && worldIn.getBlockState(pos.up(2)).getBlock().isReplaceable(worldIn, pos.up(2));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, INDEX, ACTIVE_LEFT, ACTIVE_RIGHT, ACTIVE_LEFT_TOP, ACTIVE_RIGHT_TOP, ACTIVE_LEFT_DOWN, ACTIVE_RIGHT_DOWN);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(INDEX, 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        int directionIndex = meta;
        if (meta > 3 && meta < 8) directionIndex -= 4;
        if (meta > 7) directionIndex -= 8;
        int index = 0;
        if (meta > 3 && meta < 8) index = 1;
        if (meta > 7) index = 2;
        return getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(directionIndex)).withProperty(INDEX, index);
    }

    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = state.getValue(FACING).getHorizontalIndex();
        if (state.getValue(INDEX) == 1) i += 4;
        if (state.getValue(INDEX) == 2) i += 8;
        return i;
    }

    private boolean ActiveSide(IBlockAccess world, BlockPos pos, boolean left, boolean top, boolean down) {
        IBlockState state = world.getBlockState(pos);
        if (!top && state.getValue(INDEX) == 2) return false;
        if (top && state.getValue(INDEX) != 2) return false;
        if (!down && state.getValue(INDEX) == 0) return false;
        if (down && state.getValue(INDEX) != 0) return false;
        EnumFacing facing = state.getValue(FACING);
        for (final EnumFacing face : EnumFacing.HORIZONTALS) {
            if ((left && face == facing.rotateYCCW()) || (!left && face == facing.rotateY())) {
                IBlockState sideState = world.getBlockState(pos.offset(face));
                Block block = sideState.getBlock();
                return sideState.isFullBlock() || block instanceof BlockElectricGate || block instanceof BlockBasicElectricFence;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, final IBlockAccess world, final BlockPos pos) {
        state = state.withProperty(ACTIVE_LEFT, ActiveSide(world, pos, true, false, false)).withProperty(ACTIVE_RIGHT, ActiveSide(world, pos, false, false, false))
                .withProperty(ACTIVE_LEFT_TOP, ActiveSide(world, pos, true, true, false)).withProperty(ACTIVE_RIGHT_TOP, ActiveSide(world, pos, false, true, false))
                .withProperty(ACTIVE_LEFT_DOWN, ActiveSide(world, pos, true, false, true)).withProperty(ACTIVE_RIGHT_DOWN, ActiveSide(world, pos, false, false, true));
        return state;
    }
}