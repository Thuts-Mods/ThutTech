package thut.tech.compat.cct;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import thut.core.common.ThutCore;
import thut.tech.common.blocks.lift.ControllerTile;

public class Peripherals
{
    private static boolean reged = false;

    public static void register()
    {
        if (!Peripherals.reged)
        {
            Peripherals.reged = true;
            ThutCore.LOGGER.info("Registering CC Peripheral!");
            ComputerCraftAPI.registerPeripheralProvider(new ElevatorPeripheralProvider());
        }
    }

    public static class ElevatorPeripheral implements IPeripheral
    {
        public static class Provider
        {
            private final ControllerTile tile;

            public Provider(final ControllerTile tile)
            {
                this.tile = tile;
            }

            public boolean moveBy(final String axis, final float amount) throws LuaException
            {
                if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");
                if (axis.equalsIgnoreCase("x")) this.tile.getLift().setDestX((float) (this.tile.getLift().getPosX()
                        + amount));
                if (axis.equalsIgnoreCase("y")) this.tile.getLift().setDestY((float) (this.tile.getLift().getPosY()
                        + amount));
                if (axis.equalsIgnoreCase("z")) this.tile.getLift().setDestZ((float) (this.tile.getLift().getPosZ()
                        + amount));
                return true;
            }

            public boolean goTo(int floor) throws LuaException
            {
                if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");
                if (floor < 0) floor = 64 - floor;
                if (floor - 1 >= this.tile.getLift().maxFloors()) throw new LuaException("Floor not in range");
                if (!this.tile.getLift().hasFloor(floor)) throw new LuaException("Floor not found.");
                this.tile.getLift().call(floor);
                return true;
            }

            public double[] find() throws LuaException
            {
                if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");
                return new double[] { this.tile.getLift().getPosX(), this.tile.getLift().getPosY(), this.tile.getLift()
                        .getPosZ() };

            }

            public boolean has()
            {
                return this.tile.liftID != null;
            }
        }

        public static String[] METHODS = { "move_by", "goto_floor", "find", "has" };

        private final Provider provider;

        public ElevatorPeripheral(final ControllerTile tile)
        {
            this.provider = new Provider(tile);
        }

        @Override
        public String getType()
        {
            return "lift";
        }

        @Override
        public Object getTarget()
        {
            return this.provider;
        }

        @Override
        public boolean equals(final IPeripheral other)
        {
            return other instanceof ElevatorPeripheral
                    && ((ElevatorPeripheral) other).provider.tile == this.provider.tile;
        }

    }

    public static class ElevatorPeripheralProvider implements IPeripheralProvider
    {
        @Override
        public LazyOptional<IPeripheral> getPeripheral(final World world, final BlockPos pos, final Direction side)
        {
            final TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof ControllerTile) return LazyOptional.of(() -> new ElevatorPeripheral(
                    (ControllerTile) tile));
            return LazyOptional.empty();
        }
    }

}
