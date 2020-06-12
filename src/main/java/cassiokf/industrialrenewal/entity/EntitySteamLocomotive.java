package cassiokf.industrialrenewal.entity;

import cassiokf.industrialrenewal.IndustrialRenewal;
import cassiokf.industrialrenewal.handlers.SteamBoiler;
import cassiokf.industrialrenewal.init.GUIHandler;
import cassiokf.industrialrenewal.init.ModItems;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntitySteamLocomotive extends LocomotiveBase
{
    private final SteamBoiler boiler = new SteamBoiler(this, SteamBoiler.BoilerType.Solid, 1)
            .setWaterTankCapacity(64000);
    boolean active = false;

    public EntitySteamLocomotive(World worldIn)
    {
        super(worldIn);
        this.setSize(1F, 1.4F);
    }

    public EntitySteamLocomotive(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        //if (!player.getHeldItem(hand).isEmpty() && player.getHeldItem(hand).getItem() instanceof ItemCartLinkable)
        //{
        //    //send to server to rotate the cart
        //    return true;
        //}
        if (!player.isSneaking())
        {
            if (!this.world.isRemote)
                player.openGui(IndustrialRenewal.instance, GUIHandler.STEAMLOCOMOTIVE, this.world, this.getEntityId(), 0, 0);
            return true;
        }
        active = !active;
        return super.processInitialInteract(player, hand);
    }

    @Override
    public void onLocomotiveUpdate()
    {
        if (!inventory.getStackInSlot(0).isEmpty()) moveForward();
    }

    @Override
    public ItemStack getCartItem() {
        return new ItemStack(ModItems.steamLocomotive);
    }

    @Override
    public float getMaxCouplingDistance(EntityMinecart cart)
    {
        return 2.0f;
    }

    @Override
    public float getFixedDistance(EntityMinecart cart)
    {
        return 1.6f;
    }
}
