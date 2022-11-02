package info.alinadace.deathdropsrandomly.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * @author Kane
 * @date 2022/10/28 16:47
 */
public class PlayerEvent {
	public static final Event<onDropInventory> onDropInventory = EventFactory.createArrayBacked(onDropInventory.class, listeners -> (player) -> {
		for (onDropInventory listener : listeners) {
			ActionResult result = listener.interact(player);

			if(result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.PASS;
	});

	@FunctionalInterface
	public interface onDropInventory{
		/**
		 * 在玩家受到即将死亡时调用
		 * @param player 玩家实体
		 * @return 完成调用流程
		 */
		ActionResult interact(PlayerEntity player);
	}
}
