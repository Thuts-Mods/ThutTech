package thut.tech.client;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thut.api.maths.Vector3;
import thut.tech.Reference;
import thut.tech.client.render.ControllerRenderer;
import thut.tech.client.render.RenderLift;
import thut.tech.common.TechCore;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Reference.MOD_ID, value = Dist.CLIENT)
public class ClientHandler
{
    public static class BoundRenderer
    {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void RenderBounds(final RenderLevelStageEvent event)
        {
        	if (event.getStage() != Stage.AFTER_SOLID_BLOCKS) return;
            ItemStack held;
            final Player player = Minecraft.getInstance().player;
            if (!(held = player.getMainHandItem()).isEmpty() || !(held = player.getOffhandItem()).isEmpty())
            {
                if (held.getItem() != TechCore.LIFT.get()) return;
                if (held.getTag() != null && held.getTag().contains("min"))
                {
                    final Minecraft mc = Minecraft.getInstance();
                    final Vec3 projectedView = mc.gameRenderer.getMainCamera().getPosition();
                    Vec3 pointed = new Vec3(projectedView.x, projectedView.y, projectedView.z).add(mc.player
                            .getViewVector(event.getPartialTick()));
                    if (mc.hitResult != null && mc.hitResult.getType() == Type.BLOCK)
                    {
                        final BlockHitResult result = (BlockHitResult) mc.hitResult;
                        pointed = new Vec3(result.getBlockPos().getX(), result.getBlockPos().getY(), result
                                .getBlockPos().getZ());
                        //
                    }
                    final Vector3 v = Vector3.readFromNBT(held.getTag().getCompound("min"), "");

                    final AABB one = new AABB(v.getPos());
                    final AABB two = new AABB(new BlockPos(pointed));

                    final double minX = Math.min(one.minX, two.minX);
                    final double minY = Math.min(one.minY, two.minY);
                    final double minZ = Math.min(one.minZ, two.minZ);
                    final double maxX = Math.max(one.maxX, two.maxX);
                    final double maxY = Math.max(one.maxY, two.maxY);
                    final double maxZ = Math.max(one.maxZ, two.maxZ);

                    final PoseStack mat = event.getPoseStack();
                    mat.translate(-projectedView.x, -projectedView.y, -projectedView.z);

                    final List<Pair<Vector3f, Vector3f>> lines = Lists.newArrayList();

                    lines.add(Pair.of(new Vector3f((float) minX, (float) minY, (float) minZ), new Vector3f((float) maxX,
                            (float) minY, (float) minZ)));
                    lines.add(Pair.of(new Vector3f((float) minX, (float) maxY, (float) minZ), new Vector3f((float) maxX,
                            (float) maxY, (float) minZ)));
                    lines.add(Pair.of(new Vector3f((float) minX, (float) minY, (float) maxZ), new Vector3f((float) maxX,
                            (float) minY, (float) maxZ)));
                    lines.add(Pair.of(new Vector3f((float) minX, (float) maxY, (float) maxZ), new Vector3f((float) maxX,
                            (float) maxY, (float) maxZ)));

                    lines.add(Pair.of(new Vector3f((float) minX, (float) minY, (float) minZ), new Vector3f((float) minX,
                            (float) minY, (float) maxZ)));
                    lines.add(Pair.of(new Vector3f((float) maxX, (float) minY, (float) minZ), new Vector3f((float) maxX,
                            (float) minY, (float) maxZ)));
                    lines.add(Pair.of(new Vector3f((float) minX, (float) maxY, (float) minZ), new Vector3f((float) minX,
                            (float) maxY, (float) maxZ)));
                    lines.add(Pair.of(new Vector3f((float) maxX, (float) maxY, (float) minZ), new Vector3f((float) maxX,
                            (float) maxY, (float) maxZ)));

                    lines.add(Pair.of(new Vector3f((float) minX, (float) minY, (float) minZ), new Vector3f((float) minX,
                            (float) maxY, (float) minZ)));
                    lines.add(Pair.of(new Vector3f((float) maxX, (float) minY, (float) minZ), new Vector3f((float) maxX,
                            (float) maxY, (float) minZ)));
                    lines.add(Pair.of(new Vector3f((float) minX, (float) minY, (float) maxZ), new Vector3f((float) minX,
                            (float) maxY, (float) maxZ)));
                    lines.add(Pair.of(new Vector3f((float) maxX, (float) minY, (float) maxZ), new Vector3f((float) maxX,
                            (float) maxY, (float) maxZ)));

                    mat.pushPose();

                    final Matrix4f positionMatrix = mat.last().pose();

                    final MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers()
                            .bufferSource();
                    final VertexConsumer builder = buffer.getBuffer(RenderType.LINES);
                    for (final Pair<Vector3f, Vector3f> line : lines)
                        thut.core.init.ClientInit.line(builder, positionMatrix, line.getLeft(), line.getRight(), 1, 0,
                                0, 1f);
                    mat.popPose();
                }
            }
        }
    }

    @SubscribeEvent
    public static void registerRenderers(final RegisterRenderers event)
    {
        event.registerEntityRenderer(TechCore.LIFTTYPE.get(), RenderLift::new);
        event.registerBlockEntityRenderer(TechCore.CONTROLTYPE.get(), ControllerRenderer::new);
    }

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(BoundRenderer.class);

        ItemBlockRenderTypes.setRenderLayer(TechCore.LIFTCONTROLLER.get(), t -> (t == RenderType.translucent()
                || t == RenderType.cutoutMipped()));
    }
}
