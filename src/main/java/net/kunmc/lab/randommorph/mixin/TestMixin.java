package net.kunmc.lab.randommorph.mixin;

import draylar.identity.screen.widget.HelpWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HelpWidget.class)
public abstract class TestMixin {

    @ModifyArg(method = "<init>(IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;<init>(IIIILnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)V"), index = 4)
    private static Text injected(Text text) {
        return new LiteralText("!");
    }
}
