package cassiokf.industrialrenewal.item.carts;

import cassiokf.industrialrenewal.entity.EntityPassengerCar;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;

public class ItemMineCartPassengerCar extends ItemCartBase
{
    public ItemMineCartPassengerCar(String name, CreativeTabs tab)
    {
        super(name, tab);
    }

    @Override
    public EntityMinecart getEntity(World world, double x, double y, double z)
    {
        return new EntityPassengerCar(world, x, y, z);
    }
}
