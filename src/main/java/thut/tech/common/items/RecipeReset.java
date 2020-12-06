package thut.tech.common.items;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thut.tech.common.TechCore;
import thut.tech.common.util.RecipeSerializers;

public class RecipeReset extends SpecialRecipe
{
    public RecipeReset(final ResourceLocation idIn)
    {
        super(idIn);
    }

    @Override
    public ItemStack getCraftingResult(final CraftingInventory inv)
    {
        int n = 0;
        boolean matched = false;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() == TechCore.LINKER.get()) matched = true;
            n++;
        }
        if (n != 1) matched = false;
        if (matched) return new ItemStack(TechCore.LINKER.get());
        return ItemStack.EMPTY;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return RecipeSerializers.RECIPE_RESET_SERIALIZER.get();
    }

    @Override
    public boolean matches(final CraftingInventory inv, final World worldIn)
    {
        return !this.getCraftingResult(inv).isEmpty();
    }

    @Override
    public boolean canFit(final int width, final int height)
    {
        return width * height > 0;
    }

}
