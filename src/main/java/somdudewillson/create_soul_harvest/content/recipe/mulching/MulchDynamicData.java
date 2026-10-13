package somdudewillson.create_soul_harvest.content.recipe.mulching;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class MulchDynamicData {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static float getMulchAmount(int percent) {
		return percent / 12.5f;
	}
	
	public static float getMulchAmount(int count, int percent) {
		return count * getMulchAmount(percent);
	}

	@SuppressWarnings("deprecation")
	public static Map<String, byte[]> generate() {
		Map<String, byte[]> files = new HashMap<>();

		// Generate pack.mcmeta
		JsonObject packMeta = new JsonObject();
		JsonObject packObj = new JsonObject();
		packObj.addProperty("description", "Create: Soul Harvest Mulching Recipes");
		packObj.addProperty("pack_format", 48);
		JsonArray supported = new JsonArray();
		supported.add(0);
		supported.add(999);
		packObj.add("supported_formats", supported);
		packMeta.add("pack", packObj);
		files.put("pack.mcmeta", GSON.toJson(packMeta).getBytes(StandardCharsets.UTF_8));

		// Group compostables by percent
		Map<Integer, Set<String>> byPercent = new TreeMap<>();
		for (Object2FloatMap.Entry<ItemLike> entry : ComposterBlock.COMPOSTABLES.object2FloatEntrySet()) {
			ItemLike itemLike = entry.getKey();
			if (itemLike == null) { continue; }
			
			Item item = itemLike.asItem();
			if (item == null || item == Items.AIR) { continue; }
			
			ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
			if (id == null) { continue; }
			
			float chance = entry.getFloatValue();
			int percent = Math.round(chance * 100.0f);
			byPercent.computeIfAbsent(percent, k -> new TreeSet<>()).add(id.toString());
		}

		SoulHarvestMod.LOGGER.info("Generating mulching recipes and compostable item tags for {} compost chance tiers", byPercent.size());

		JsonObject parentTagJson = new JsonObject();
		parentTagJson.addProperty("replace", false);
		JsonArray parentValues = new JsonArray();
		
		// Generate item tags and mixing recipes for each chance tier
		for (Map.Entry<Integer, Set<String>> entry : byPercent.entrySet()) {
			int percent = entry.getKey();
			Set<String> itemIds = entry.getValue();

			String tagId = makeCompostableGroupTag(files, percent, itemIds);
			parentValues.add("#"+tagId);

			makeMulchingRecipe(files, percent, tagId);
		}

		parentTagJson.add("values", parentValues);
		byte[] parentTagBytes = GSON.toJson(parentTagJson).getBytes(StandardCharsets.UTF_8);
		files.put("data/" + SoulHarvestMod.MOD_ID + "/tags/item/compostables.json", parentTagBytes);
		files.put("data/" + SoulHarvestMod.MOD_ID + "/tags/items/compostables.json", parentTagBytes);

		return files;
	}

	private static void makeMulchingRecipe(Map<String, byte[]> files, int percent, String tagId) {
		JsonObject recipeJson = new JsonObject();
		recipeJson.addProperty("type", "create:mixing");
		recipeJson.addProperty("group", "mulching");
		
		int inputCount = Math.max(1, Math.round(100f / percent));

		JsonArray ingredients = new JsonArray();
		for (int i = 0; i < inputCount; i++) {
			JsonObject ing = new JsonObject();
			ing.addProperty("tag", tagId);
			ingredients.add(ing);
		}
		
		JsonObject gravelIng = new JsonObject();
		gravelIng.addProperty("item", "minecraft:gravel");
		ingredients.add(gravelIng);
		recipeJson.add("ingredients", ingredients);

		JsonArray results = new JsonArray();
		float mulchResultAmount = getMulchAmount(inputCount, percent);
		int mulchWholeAmount = (int) Math.floor(mulchResultAmount);
		float mulchFracAmount = mulchResultAmount - mulchWholeAmount;
		if (mulchWholeAmount > 0) {
			JsonObject wholeMulchResult = new JsonObject();
			wholeMulchResult.addProperty("count", mulchWholeAmount);
			wholeMulchResult.addProperty("id", SoulHarvestMod.MOD_ID + ":organic_mulch");
			wholeMulchResult.addProperty("item", SoulHarvestMod.MOD_ID + ":organic_mulch");
			results.add(wholeMulchResult);
		}
		if (mulchFracAmount > 0) {
			JsonObject fracMulchResult = new JsonObject();
			fracMulchResult.addProperty("count", 1);
			fracMulchResult.addProperty("id", SoulHarvestMod.MOD_ID + ":organic_mulch");
			fracMulchResult.addProperty("item", SoulHarvestMod.MOD_ID + ":organic_mulch");
			fracMulchResult.addProperty("chance", mulchFracAmount);
			results.add(fracMulchResult);
		}

		JsonObject gravelResult = new JsonObject();
		gravelResult.addProperty("count", 1);
		gravelResult.addProperty("id", "minecraft:gravel");
		gravelResult.addProperty("item", "minecraft:gravel");
		results.add(gravelResult);
		recipeJson.add("results", results);

		byte[] recipeBytes = GSON.toJson(recipeJson).getBytes(StandardCharsets.UTF_8);

		// Provide recipe in both recipe/ (1.21) and recipes/ (1.20) paths
		files.put("data/" + SoulHarvestMod.MOD_ID + "/recipe/mulching_" + percent + ".json", recipeBytes);
		files.put("data/" + SoulHarvestMod.MOD_ID + "/recipes/mulching_" + percent + ".json", recipeBytes);
	}

	private static String makeCompostableGroupTag(Map<String, byte[]> files, int percent, Set<String> itemIds) {
		JsonObject tagJson = new JsonObject();
		tagJson.addProperty("replace", false);
		JsonArray values = new JsonArray();
		for (String id : itemIds) {
			values.add(id);
		}
		tagJson.add("values", values);
		byte[] tagBytes = GSON.toJson(tagJson).getBytes(StandardCharsets.UTF_8);
		
		// Provide tag in both tags/item/ (1.21) and tags/items/ (1.20) paths
		files.put("data/" + SoulHarvestMod.MOD_ID + "/tags/item/compostables/" + percent + ".json", tagBytes);
		files.put("data/" + SoulHarvestMod.MOD_ID + "/tags/items/compostables/" + percent + ".json", tagBytes);
		
		return SoulHarvestMod.MOD_ID + ":compostables/" + percent;
	}
}
