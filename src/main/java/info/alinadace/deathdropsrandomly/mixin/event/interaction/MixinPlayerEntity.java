package info.alinadace.deathdropsrandomly.mixin.event.interaction;

import info.alinadace.deathdropsrandomly.event.PlayerEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Kane
 * @date 2022/10/28 17:04
 */
@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
	@Inject(method = "dropInventory", at = @At("HEAD"), cancellable = true)
	public void onDropInventory(CallbackInfo info){
		PlayerEntity player = (PlayerEntity) (Object) this;
		if (player.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)){
			return;
		}
		ActionResult result = PlayerEvent.onDropInventory.invoker().interact(player);
		if (result != ActionResult.PASS){
			info.cancel();
		}
	}
}
