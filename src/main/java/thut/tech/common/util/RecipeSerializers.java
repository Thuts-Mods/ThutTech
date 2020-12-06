package thut.tech.common.util;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import thut.tech.Reference;
import thut.tech.common.items.RecipeReset;

public class RecipeSerializers
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID
    );

    public static final RegistryObject<SpecialRecipeSerializer<RecipeReset>> RECIPE_RESET_SERIALIZER = RECIPE_SERIALIZERS.register(
            "resetlinker", special(RecipeReset::new)
    );

    private static <T extends IRecipe<?>> Supplier<SpecialRecipeSerializer<T>> special(Function<ResourceLocation, T> create)
    {
        return () -> new SpecialRecipeSerializer<>(create);
    }
}