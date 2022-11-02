package info.alinadace.deathdropsrandomly;

import info.alinadace.deathdropsrandomly.constant.RequestConstant;
import info.alinadace.deathdropsrandomly.event.PlayerEvent;
import info.alinadace.deathdropsrandomly.utils.BookPagesUtil;
import info.alinadace.deathdropsrandomly.utils.RequestResultUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

import java.util.*;

/**
 * @author Kane
 * @date 2022/10/28 10:32
 */
public class DeathDropsRandomly implements ModInitializer {
	@Override
	public void onInitialize() {
		PlayerEvent.onDropInventory.register((player) -> {
			Random random = new Random();
//			List<ItemStack> dropItem = new LinkedList<>();
			Map<String, Integer> dropList = new HashMap<>();
			for (int i = 0; i < player.getInventory().size(); ++i) {
				ItemStack itemStack = player.getInventory().getStack(i);
				// 非消失诅咒物品处理
				if (itemStack.isEmpty() || !EnchantmentHelper.hasVanishingCurse(itemStack)) {
					int dropCount = 0;
					int itemCount = itemStack.getCount();
					for (int i1 = 0; i1 < itemCount; i1++) {
						if (random.nextInt() % 10 < 2){
							dropCount++;
						}
					}
					if (dropCount < itemCount){
						if (dropCount != 0){
							itemStack.setCount(dropCount);
							player.dropItem(itemStack, true, false);
							itemStack.setCount(itemCount - dropCount);
							dropList.put(itemStack.getTranslationKey(), dropCount);
						}
					}else {
						dropList.put(itemStack.getTranslationKey(), dropCount);
						player.getInventory().removeStack(i);
					}
//					// 从配置文件获取概率设定
//					if (random.nextInt() % 10 < 2){
//						player.dropItem(itemStack, true,false);
//						if (!itemStack.isEmpty()){
//							dropItem.add(itemStack);
//						}
//						player.getInventory().removeStack(i);
//					}
					continue;
				}
				// 删除消失诅咒物品
				player.getInventory().removeStack(i);
			}
			if (dropList.size() > 0){
				// 尝试创建丢失物品清单
				ItemStack stack = new ItemStack(Registry.ITEM.get(new Identifier("minecraft:written_book")));
				NbtCompound nbt = new NbtCompound();
//				nbt.putInt("generation", 3);
				String s = player.getEntityName() + "的遗失清单";
				nbt.putString("title", s);
				nbt.putString("filtered_title", s);
				nbt.putString("author", player.getEntityName());
				NbtList pages = new NbtList();
				StringBuilder sb = new StringBuilder("""
						你似乎不太小心，没有关系
						，我帮你保留了一些物品，
						不必客气！但是有一些物品
						好像比较淘气，不过我偷偷
						记下了它们的名字：
						
						""");
				Map<String, Integer> bookList = new HashMap<>();
				if (!player.world.isClient()){
					PacketByteBuf buf = PacketByteBufs.create();
					String uuid = UUID.randomUUID().toString();
					NbtCompound compound = new NbtCompound();
					compound.putString(RequestResultUtil.PLAYER_UUID, player.getUuidAsString());
					compound.putString(RequestResultUtil.REQUEST_UUID, uuid);
					NbtList itemIdList = new NbtList();
					dropList.forEach((k, v) -> {
						itemIdList.add(NbtString.of(k));
					});
					compound.put("translation_key", itemIdList);
					buf.writeNbt(compound);
					ServerPlayNetworking.send((ServerPlayerEntity) player, RequestConstant.TRANSLATE, buf);
					PacketByteBuf request = RequestResultUtil.getRequest(player.getUuidAsString(), uuid);
					if (request != null){
						NbtCompound readNbt = request.readNbt();
						assert readNbt != null;
						NbtList translation = (NbtList) readNbt.get("translation_key");
						if (translation != null){
							for (int i = 0; i < translation.size(); i++) {
								bookList.merge(translation.getString(i), dropList.get(itemIdList.getString(i)), Integer::sum);
							}
						}else {
							dropList.forEach((k, v) -> {
								bookList.merge(I18n.translate(k), v, Integer::sum);
							});
						}
					}
				}else{
					dropList.forEach((k, v) -> {
						bookList.merge(I18n.translate(k), v, Integer::sum);
					});
				}
				Set<String> key = bookList.keySet();
				Iterator<String> iterator = key.iterator();
				for (int i = 0; i < 15 - 6; i++){
					if (iterator.hasNext()){
						String next = iterator.next();
						sb.append(next).append(" × ").append(bookList.get(next)).append('\n');
					}
				}
				pages.add(NbtString.of(BookPagesUtil.newPage(sb.toString())));
				while(iterator.hasNext()){
					sb = new StringBuilder();
					for (int i = 0; i < 15; i++){
						if (iterator.hasNext()){
							String next = iterator.next();
							sb.append(next).append(" × ").append(bookList.get(next)).append('\n');
						}
					}
					pages.add(NbtString.of(BookPagesUtil.newPage(sb.toString())));
				}
				nbt.put("pages", pages);
				stack.setNbt(nbt);
				player.getInventory().addPickBlock(stack);
			}
			return ActionResult.SUCCESS;
		});
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (oldPlayer.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)){
				return;
			}
			PlayerInventory inventory = oldPlayer.getInventory();
			PlayerInventory newPlayerInventory = newPlayer.getInventory();
			for(int i = 0; i < inventory.size(); ++i){
				newPlayerInventory.clone(inventory);
			}
		});
	}
}
