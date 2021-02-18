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

        // Try to match a device linker
        ItemStack linker = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            link:
            if (stack.getItem() == TechCore.LINKER.get())
            {
                if (!stack.hasTag()) break link;
                if (!stack.getTag().contains("lift")) break link;
                matched = true;
                linker = stack;
            }
            n++;
        }
        if (n != 1) matched = false;
        if (matched)
        {
            final ItemStack ret = linker.copy();
            ret.getTag().remove("lift");
            return ret;
        }

        // Try to match an elevator item
        n = 0;
        linker = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            link:
            if (stack.getItem() == TechCore.LIFT.get())
            {
                if (!stack.hasTag()) break link;
                if (!stack.getTag().contains("min")) break link;
                matched = true;
                linker = stack;
            }
            n++;
        }
        if (n != 1) matched = false;
        if (matched)
        {
            final ItemStack ret = linker.copy();
            ret.getTag().remove("min");
            ret.getTag().remove("time");
            return ret;
        }

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
