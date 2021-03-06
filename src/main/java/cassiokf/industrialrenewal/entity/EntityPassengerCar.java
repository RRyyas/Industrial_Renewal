package cassiokf.industrialrenewal.entity;

import cassiokf.industrialrenewal.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityPassengerCar extends RotatableBase
{

    public EntityPassengerCar(World worldIn)
    {
        super(worldIn);
        this.setSize(1.0F, 1.0F);
    }

    public EntityPassengerCar(World worldIn, double x, double y, double z)
    {
        super(worldIn, x, y, z);
    }

    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (super.processInitialInteract(player, hand)) return true;

        if (player.isSneaking())
        {
            return false;
        }
        else if (this.isBeingRidden())
        {
            return true;
        }
        else
        {
            if (!this.world.isRemote)
            {
                player.startRiding(this);
            }

            return true;
        }
    }

    @Override
    public void killMinecart(DamageSource source)
    {
        //super.killMinecart(source);
        this.setDead();

        if (!source.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops"))
        {
            this.entityDropItem(new ItemStack(ModItems.passengerCar, 1), 0.0F);
        }
    }

    @Override
    public Type getType()
    {
        return Type.RIDEABLE;
    }

    @Override
    public ItemStack getCartItem()
    {
        return new ItemStack(ModItems.passengerCar);
    }

}
