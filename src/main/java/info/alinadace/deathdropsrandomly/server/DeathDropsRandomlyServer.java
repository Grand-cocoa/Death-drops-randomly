package info.alinadace.deathdropsrandomly.server;

import info.alinadace.deathdropsrandomly.constant.RequestConstant;
import info.alinadace.deathdropsrandomly.utils.RequestResultUtil;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

/**
 * @author Kane
 * @date 2022/10/28 10:41
 */
public class DeathDropsRandomlyServer implements DedicatedServerModInitializer {
	/**
	 * Runs the mod initializer on the server environment.
	 */
	@Override
	public void onInitializeServer() {
		ServerPlayNetworking.registerGlobalReceiver(RequestConstant.TRANSLATE,
				(server, client, handler, buf, responseSender) -> RequestResultUtil.onResult(buf));
	}
}
