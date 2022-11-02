package info.alinadace.deathdropsrandomly.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kane
 * @date 2022/10/29 23:25
 */
public class RequestResultUtil {
	private static final Map<String, PacketByteBuf> REQUEST_RESULT = new HashMap<>();

	public static final String REQUEST_UUID = "request_uuid";
	public static final String PLAYER_UUID = "player_uuid";

	public static boolean onResult(PacketByteBuf buf){
		NbtCompound nbt = buf.readNbt();
		if (nbt != null){
			String uuid = nbt.getString(PLAYER_UUID);
			String request = nbt.getString(REQUEST_UUID);
			REQUEST_RESULT.putIfAbsent(uuid + '@' + request, buf);
			return true;
		}
		return false;
	}
	public static PacketByteBuf getRequest(String playerUuid, String requestUuid){
		for(int i = 0; i < 300; i++){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
			PacketByteBuf buf = REQUEST_RESULT.get(playerUuid + '@' + requestUuid);
			if (buf != null){
				return buf;
			}
		}
		return null;
	}
}
