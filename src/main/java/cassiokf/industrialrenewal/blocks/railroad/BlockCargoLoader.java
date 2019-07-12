package cassiokf.industrialrenewal.blocks.railroad;

import cassiokf.industrialrenewal.IndustrialRenewal;
import cassiokf.industrialrenewal.blocks.BlockBasicContainer;
import cassiokf.industrialrenewal.init.GUIHandler;
import cassiokf.industrialrenewal.tileentity.railroad.TileEntityCargoLoader;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCargoLoader extends BlockBasicContainer<TileEntityCargoLoader>
{

    public static final PropertyBool LOADING = PropertyBool.create("loading");
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool UNLOAD = PropertyBool.create("unload");
    protected static final AxisAlignedBB BLOCK_AABB = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1D, 1D, 1D);
    protected static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, 1D);

    public BlockCargoLoader(String name, CreativeTabs tab) {
        super(name, tab, Material.IRON);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (!worldIn.isRemote)
        {
            if (tileentity instanceof TileEntityCargoLoader) {
                OpenGUI(worldIn, pos, playerIn);
                playerIn.addStat(StatList.HOPPER_INSPECTED);
            }
        }
        return true;
    }

    private void OpenGUI(World world, BlockPos pos, EntityPlayer player) {
        player.openGui(IndustrialRenewal.instance, GUIHandler.CARGOLOADER, world, pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean isUnload(IBlockAccess world, BlockPos pos) {
        IBlockState downState = world.getBlockState(pos.down(2));
        return !(downState.getBlock() instanceof BlockLoaderRail);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, final IBlockAccess world, final BlockPos pos) {
        return state.withProperty(UNLOAD, isUnload(world, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(LOADING, false);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityHopper) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityHopper) tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, LOADING, UNLOAD);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(LOADING, false);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = state.getActualState(source, pos);
        if (state.getValue(UNLOAD)) {
            return FULL_AABB;
        }
        return BLOCK_AABB;
    }

    public Class<TileEntityCargoLoader> getTileEntityClass() {
        return TileEntityCargoLoader.class;
    }

    @Nullable
    @Override
    public TileEntityCargoLoader createTileEntity(World world, IBlockState state) {
        return new TileEntityCargoLoader();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCargoLoader();
    }
}