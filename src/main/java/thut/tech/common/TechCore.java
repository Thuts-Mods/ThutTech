package thut.tech.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thut.core.common.ThutCore;
import thut.core.common.config.Config;
import thut.core.common.network.PacketHandler;
import thut.tech.Reference;
import thut.tech.client.ClientProxy;
import thut.tech.common.blocks.lift.ControllerBlock;
import thut.tech.common.blocks.lift.ControllerTile;
import thut.tech.common.entity.EntityLift;
import thut.tech.common.handlers.ConfigHandler;
import thut.tech.common.items.ItemLinker;
import thut.tech.common.util.RecipeSerializers;

@Mod(value = Reference.MOD_ID)
public class TechCore
{
    // You can use EventBusSubscriber to automatically subscribe events on the
    // contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {

        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(TechCore.LIFTCONTROLLER);
        }

        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event)
        {
            EntityLift.TYPE.setRegistryName(Reference.MOD_ID, "lift");
            event.getRegistry().register(EntityLift.TYPE);
        }

        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(TechCore.LIFT);
            event.getRegistry().register(TechCore.LINKER);
            final BlockItem controller = new BlockItem(TechCore.LIFTCONTROLLER, new Item.Properties().group(
                    ThutCore.THUTITEMS));
            controller.setRegistryName(TechCore.LIFTCONTROLLER.getRegistryName());
            event.getRegistry().register(controller);
            ThutCore.THUTICON = new ItemStack(TechCore.LINKER);
        }

        @SubscribeEvent
        public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event)
        {
            ControllerTile.TYPE = TileEntityType.Builder.create(ControllerTile::new, TechCore.LIFTCONTROLLER).build(
                    null);
            ControllerTile.TYPE.setRegistryName(Reference.MOD_ID, "controller");
            event.getRegistry().register(ControllerTile.TYPE);
        }
    }

    public final static PacketHandler packets = new PacketHandler(new ResourceLocation(Reference.MOD_ID, "comms"),
            Reference.NETVERSION);

    public static final CommonProxy proxy = DistExecutor.safeRunForDist(
            () -> () -> new ClientProxy(),
            () -> () -> new CommonProxy());

    public static Block LIFTCONTROLLER;
    public static Item  LIFT;
    public static Item  LINKER;

    public static final ConfigHandler config = new ConfigHandler(Reference.MOD_ID);

    static void init()
    {
        TechCore.LIFTCONTROLLER = new ControllerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(3.5f)
                .variableOpacity().notSolid()).setRegistryName(Reference.MOD_ID, "controller");
        TechCore.LIFT = new Item(new Item.Properties().group(ThutCore.THUTITEMS)).setRegistryName(Reference.MOD_ID,
                "lift");
        TechCore.LINKER = new ItemLinker(new Item.Properties().group(ThutCore.THUTITEMS)).setRegistryName(
                Reference.MOD_ID, "linker");
    }

    public TechCore()
    {
        MinecraftForge.EVENT_BUS.register(this);

        TechCore.init();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TechCore.proxy::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(TechCore.proxy::setupClient);

        // Register recipe serializers
        RecipeSerializers.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register Config stuff
        Config.setupConfigs(TechCore.config, Reference.MOD_ID, Reference.MOD_ID);
    }
}
