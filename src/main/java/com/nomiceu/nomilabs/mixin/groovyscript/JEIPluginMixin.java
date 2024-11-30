package com.nomiceu.nomilabs.mixin.groovyscript;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IShapedRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import com.cleanroommc.groovyscript.compat.mods.jei.ShapedRecipeWrapper;
import com.nomiceu.nomilabs.groovy.mixinhelper.StrictableItemRecipeWrappers;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;

/**
 * Registers recipes with the new 'strictable' wrappers.
 */
@Mixin(value = JeiPlugin.class, remap = false)
public class JEIPluginMixin {

    @Redirect(method = "lambda$register$0",
              at = @At(value = "NEW",
                       target = "(Lmezz/jei/api/IJeiHelpers;Lnet/minecraftforge/common/crafting/IShapedRecipe;)Lcom/cleanroommc/groovyscript/compat/mods/jei/ShapedRecipeWrapper;"))
    private static ShapedRecipeWrapper returnNewHandlerShaped(IJeiHelpers jeiHelpers, IShapedRecipe recipe) {
        return new StrictableItemRecipeWrappers.Shaped(jeiHelpers, recipe);
    }

    @Redirect(method = "lambda$register$1",
              at = @At(value = "NEW",
                       target = "(Lmezz/jei/api/IJeiHelpers;Lnet/minecraft/item/crafting/IRecipe;)Lmezz/jei/plugins/vanilla/crafting/ShapelessRecipeWrapper;"))
    private static ShapelessRecipeWrapper<?> returnNewHandlerShapeless(IJeiHelpers jeiHelpers, IRecipe recipe) {
        return new StrictableItemRecipeWrappers.Shapeless(jeiHelpers, recipe);
    }
}
