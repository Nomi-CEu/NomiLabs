package com.nomiceu.nomilabs.mixin.topaddons;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.nomiceu.nomilabs.util.LabsTranslate;

import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import io.github.drmanganese.topaddons.addons.AddonForge;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;

/**
 * Fixes Localization of Fluid Names.
 */
@Mixin(value = AddonForge.class, remap = false)
public class AddonForgeMixin {

    @Redirect(method = "addTankElement(Lmcjty/theoneprobe/api/IProbeInfo;Ljava/lang/Class;Lnet/minecraftforge/fluids/FluidTank;ILmcjty/theoneprobe/api/ProbeMode;Lnet/minecraft/entity/player/EntityPlayer;)Lmcjty/theoneprobe/api/IProbeInfo;",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraftforge/fluids/FluidStack;getLocalizedName()Ljava/lang/String;"))
    private static String useFluidName1(FluidStack instance) {
        return FluidRegistry.getFluidName(instance);
    }

    @Redirect(method = "addTankElement(Lmcjty/theoneprobe/api/IProbeInfo;Ljava/lang/String;Lnet/minecraftforge/fluids/FluidTankInfo;Lmcjty/theoneprobe/api/ProbeMode;Lnet/minecraft/entity/player/EntityPlayer;)Lmcjty/theoneprobe/api/IProbeInfo;",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraftforge/fluids/FluidStack;getLocalizedName()Ljava/lang/String;"))
    private static String useUnlocalizedName2(FluidStack instance) {
        return FluidRegistry.getFluidName(instance);
    }

    @Redirect(method = "addProbeInfo",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraftforge/fluids/Fluid;getLocalizedName(Lnet/minecraftforge/fluids/FluidStack;)Ljava/lang/String;"))
    private String useUnlocalizedName3(Fluid instance, FluidStack stack) {
        return FluidRegistry.getFluidName(instance);
    }

    @ModifyConstant(method = "addProbeInfo", constant = @Constant(stringValue = "Tank"))
    private String localizedTank(String constant) {
        return LabsTranslate.translate("topaddons.fluid_display.tank.display.default");
    }

    @Inject(method = "addProbeInfo",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraftforge/fluids/capability/IFluidTankProperties;getContents()Lnet/minecraftforge/fluids/FluidStack;",
                     ordinal = 0),
            require = 1,
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void gtTankNames(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
                             IBlockState blockState, IProbeHitData data, CallbackInfo ci,
                             @Local int i,
                             @Local(ordinal = 1) LocalRef<String> tankName) {
        TileEntity tile = world.getTileEntity(data.getPos());

        if (tile instanceof IGregTechTileEntity gt &&
                gt.getMetaTileEntity() instanceof SimpleMachineMetaTileEntity simple) {
            int inputAmt = simple.getRecipeMap().getMaxFluidInputs();
            int outputAmt = simple.getRecipeMap().getMaxFluidOutputs();

            if (i > inputAmt - 1)
                tankName.set(LabsTranslate.translate("topaddons.fluid_display.tank.display.output"));
            else
                tankName.set(LabsTranslate.translate("topaddons.fluid_display.tank.display.input"));
        }
    }
}
