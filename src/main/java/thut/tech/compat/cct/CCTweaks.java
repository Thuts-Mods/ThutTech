package thut.tech.compat.cct;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CCTweaks
{

    @SubscribeEvent
    public static void serverAboutToStart(final ServerAboutToStartEvent event)
    {
        if (ModList.get().isLoaded("computercraft")) Peripherals.register();
    }

}
