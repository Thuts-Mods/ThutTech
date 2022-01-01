package thut.tech.compat.cct;

import java.util.Arrays;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public static class ElevatorPeripheral implements IDynamicPeripheral
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
                if (axis.equalsIgnoreCase("x")) this.tile.getLift().setDestX((float) (this.tile.getLift().getX()
                        + amount));
                if (axis.equalsIgnoreCase("y")) this.tile.getLift().setDestY((float) (this.tile.getLift().getY()
                        + amount));
                if (axis.equalsIgnoreCase("z")) this.tile.getLift().setDestZ((float) (this.tile.getLift().getZ()
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
                return new double[] { this.tile.getLift().getX(), this.tile.getLift().getY(), this.tile.getLift()
                        .getZ() };

            }
            public boolean has()
            {
                return this.tile.liftID != null;
            }

          public boolean isReady() throws LuaException {
              if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");

              return this.tile.getLift().getCurrentFloor() == this.tile.getLift().getDestinationFloor();
            }

          public boolean setFloor(final int floor) throws LuaException {
              if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");
              if(this.tile.floor != floor) {
                    this.tile.floor = floor;
                    return true;
                }
                return false;

          }
          public double getCoord(final String axis) throws LuaException {
              if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");
              if (axis.equalsIgnoreCase("x")) return this.tile.getLift().getX();
              if (axis.equalsIgnoreCase("y")) return this.tile.getLift().getY();
              if (axis.equalsIgnoreCase("z")) return this.tile.getLift().getZ();
              return -1;
          }
          public double getFloorYValue(final int floor) throws LuaException {
              if (this.tile.getLift() == null) throw new LuaException("No Elevator Linked!");
              return this.tile.getLift().getFloorPos(floor);
          }
        }
        @Override
        public String[] getMethodNames() {
            return new String[]{ "move_by", "goto_floor", "find", "has", "isReady", "callFloor",
                    "callXValue", "callYValue", "callZValue", "setFloor", "getXValue", "getYValue", "getZValue", "getFloorYValue" };
        }

        @Override
        public MethodResult callMethod(final IComputerAccess iComputerAccess, final ILuaContext iLuaContext, final int method, final IArguments iArguments) throws LuaException {
            switch (method) {
                case 0:
                    return MethodResult.of(this.provider.moveBy(iArguments.getString(0), iArguments.getInt(1)));
                case 1:
                case 5:
                    return MethodResult.of(this.provider.goTo(iArguments.getInt(0)));
                case 2:
                    return MethodResult.of(Arrays.toString(this.provider.find()));
                case 3:
                    return MethodResult.of(this.provider.has());
                case 4:
                    return MethodResult.of(this.provider.isReady());
                case 6:
                    return MethodResult.of(this.provider.moveBy("x", iArguments.getInt(0)));
                case 7:
                    return MethodResult.of(this.provider.moveBy("y", iArguments.getInt(0)));
                case 8:
                    return MethodResult.of(this.provider.moveBy("Z", iArguments.getInt(0)));
                case 9:
                    return MethodResult.of(this.provider.setFloor(iArguments.getInt(0)));
                case 10:
                    return MethodResult.of(this.provider.getCoord("x"));
                case 11:
                    return MethodResult.of(this.provider.getCoord("y"));
                case 12:
                    return MethodResult.of(this.provider.getCoord("z"));
                case 13:
                    return MethodResult.of(this.provider.getFloorYValue(iArguments.getInt(0)));
            }

            return null;
        }

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
        public LazyOptional<IPeripheral> getPeripheral(final Level world, final BlockPos pos, final Direction side)
        {
            final BlockEntity tile = world.getBlockEntity(pos);
            if (tile instanceof ControllerTile) return LazyOptional.of(() -> new ElevatorPeripheral(
                    (ControllerTile) tile));
            return LazyOptional.empty();
        }
    }

}
