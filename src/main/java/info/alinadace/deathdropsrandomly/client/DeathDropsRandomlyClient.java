package info.alinadace.deathdropsrandomly.client;

import info.alinadace.deathdropsrandomly.constant.RequestConstant;
import info.alinadace.deathdropsrandomly.utils.RequestResultUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * @author Kane
 * @date 2022/10/28 10:32
 */
@Environment(EnvType.CLIENT)
public class DeathDropsRandomlyClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(RequestConstant.TRANSLATE, (client, handler, buf, responseSender) -> {
			NbtCompound nbt = buf.readNbt();
			if (nbt != null && !nbt.isEmpty()) {
				NbtCompound respNbt = new NbtCompound();
				respNbt.put(RequestResultUtil.PLAYER_UUID, nbt.get(RequestResultUtil.REQUEST_UUID));
				respNbt.put(RequestResultUtil.REQUEST_UUID, nbt.get(RequestResultUtil.REQUEST_UUID));
				NbtList elements = nbt.getList("translation_key", NbtElement.STRING_TYPE);
				NbtList list = new NbtList();
				for (int i = 0; i < elements.size(); i++) {
					list.add(NbtString.of(I18n.translate(elements.getString(i))));
				}
				respNbt.put("translation_key", list);
				PacketByteBuf bufs = PacketByteBufs.create();
				bufs.writeNbt(respNbt);
				ClientPlayNetworking.send(RequestConstant.TRANSLATE, bufs);
			}
		});
	}
}
