package somdudewillson.create_soul_harvest.content.recipe.grinding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.content.soul.SoulDropEntry;
import somdudewillson.create_soul_harvest.content.soul.SoulDropPool;

//? >= 1.21 {
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

import java.nio.charset.StandardCharsets;
import java.util.*;

public class MobGrindingDynamicData {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static Map<String, byte[]> generate() {
		Map<String, byte[]> files = new HashMap<>();

		// Generate pack.mcmeta
		JsonObject packMeta = new JsonObject();
		JsonObject packObj = new JsonObject();
		packObj.addProperty("description", "Create: Soul Harvest Mob Grinding Recipes");
		packObj.addProperty("pack_format", 48);
		JsonArray supported = new JsonArray();
		supported.add(0);
		supported.add(999);
		packObj.add("supported_formats", supported);
		packMeta.add("pack", packObj);
		files.put("pack.mcmeta", GSON.toJson(packMeta).getBytes(StandardCharsets.UTF_8));

		Map<ResourceLocation, JsonObject> recipes = generateRecipeJsons();
		SoulHarvestMod.LOGGER.info("Generating mob grinding recipes for {} capturable souls ({} recipes)",
				SoulDataManager.getKnownSoulData().size(), recipes.size());

		for (Map.Entry<ResourceLocation, JsonObject> entry : recipes.entrySet()) {
			ResourceLocation id = entry.getKey();
			byte[] bytes = GSON.toJson(entry.getValue()).getBytes(StandardCharsets.UTF_8);
			files.put("data/create_soul_harvest/recipe/" + id.getPath() + ".json", bytes);
			files.put("data/create_soul_harvest/recipes/" + id.getPath() + ".json", bytes);
		}

		return files;
	}

	public static Map<ResourceLocation, JsonObject> generateRecipeJsons() {
		Map<ResourceLocation, JsonObject> recipes = new HashMap<>();
		Map<ResourceLocation, SoulData> knownSouls = SoulDataManager.getKnownSoulData();

		for (Map.Entry<ResourceLocation, SoulData> entry : knownSouls.entrySet()) {
			ResourceLocation entityId = entry.getKey();
			SoulData soulData = entry.getValue();

			int tier = soulData.tier();
			if (tier <= 0) {
				continue;
			}

			String recipeName = entityId.getNamespace() + "_" + entityId.getPath();

			if (soulData.hasDrops()) {
				recipes.put(SoulHarvestMod.id("sequenced_assembly/attune_polyclay_" + recipeName),
						makeSequencedAssemblyRecipe(entityId, tier));

				JsonObject splashing = makeSplashingRecipe(entityId, soulData);
				if (splashing != null) {
					recipes.put(SoulHarvestMod.id("splashing/wash_attuned_polyclay_" + recipeName), splashing);
				}
			}
		}

		return recipes;
	}

	public static void updateRecipeManager(MinecraftServer server) {
		if (server == null) {
			return;
		}

		Map<ResourceLocation, JsonObject> recipeJsons = generateRecipeJsons();
		if (recipeJsons.isEmpty()) {
			return;
		}

		RecipeManager recipeManager = server.getRecipeManager();

		//? >= 1.21 {
		RegistryOps<com.google.gson.JsonElement> ops = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);
		Map<ResourceLocation, RecipeHolder<?>> map = new LinkedHashMap<>();
		for (RecipeHolder<?> existing : recipeManager.getRecipes()) {
			map.put(existing.id(), existing);
		}

		int added = 0;
		for (Map.Entry<ResourceLocation, JsonObject> entry : recipeJsons.entrySet()) {
			ResourceLocation recipeId = entry.getKey();
			JsonObject json = entry.getValue();
			try {
				Recipe<?> recipe = Recipe.CODEC.parse(ops, json).getOrThrow(JsonParseException::new);
				map.put(recipeId, new RecipeHolder<>(recipeId, recipe));
				added++;
			} catch (Exception e) {
				SoulHarvestMod.LOGGER.error("Failed to parse dynamic mob grinding recipe {}: {}", recipeId, e.getMessage());
			}
		}

		recipeManager.replaceRecipes(map.values());
		SoulHarvestMod.LOGGER.info("Registered {} dynamic mob grinding recipes to RecipeManager", added);

		if (server.getPlayerList() != null && !server.getPlayerList().getPlayers().isEmpty()) {
			ClientboundUpdateRecipesPacket packet = new ClientboundUpdateRecipesPacket(recipeManager.getOrderedRecipes());
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				player.connection.send(packet);
				player.getRecipeBook().sendInitialRecipeBook(player);
			}
		}
		//?} else {
		/*Map<ResourceLocation, Recipe<?>> map = new LinkedHashMap<>();
		for (Recipe<?> existing : recipeManager.getRecipes()) {
			map.put(existing.getId(), existing);
		}

		int added = 0;
		for (Map.Entry<ResourceLocation, JsonObject> entry : recipeJsons.entrySet()) {
			ResourceLocation recipeId = entry.getKey();
			JsonObject json = entry.getValue();
			try {
				Recipe<?> recipe = RecipeManager.fromJson(recipeId, json, net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
				map.put(recipeId, recipe);
				added++;
			} catch (Exception e) {
				SoulHarvestMod.LOGGER.error("Failed to parse dynamic mob grinding recipe {}: {}", recipeId, e.getMessage());
			}
		}

		recipeManager.replaceRecipes(map.values());
		SoulHarvestMod.LOGGER.info("Registered {} dynamic mob grinding recipes to RecipeManager", added);

		if (server.getPlayerList() != null && !server.getPlayerList().getPlayers().isEmpty()) {
			ClientboundUpdateRecipesPacket packet = new ClientboundUpdateRecipesPacket(recipeManager.getRecipes());
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				player.connection.send(packet);
				player.getRecipeBook().sendInitialRecipeBook(player);
			}
		}
		*///?}
	}

	private static JsonObject makeSequencedAssemblyRecipe(ResourceLocation entityId, int tier) {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "create:sequenced_assembly");

		// Base input
		JsonObject baseIng = new JsonObject();
		baseIng.addProperty("item", "create_soul_harvest:polyclay");
		recipe.add("ingredient", baseIng);

		// Transitional item
		JsonObject transItem = new JsonObject();
		transItem.addProperty("id", "create_soul_harvest:incomplete_polyclay");
		transItem.addProperty("item", "create_soul_harvest:incomplete_polyclay");
		recipe.add("transitional_item", transItem);

		recipe.addProperty("loops", 1);

		// Sequence steps
		JsonArray sequence = new JsonArray();

		// --- Deployer application of Soul Book
		JsonObject deployStep = new JsonObject();
		deployStep.addProperty("type", "create:deploying");

		JsonArray deployIngredients = new JsonArray();
		JsonObject ingTrans = new JsonObject();
		ingTrans.addProperty("id", "create_soul_harvest:incomplete_polyclay");
		ingTrans.addProperty("item", "create_soul_harvest:incomplete_polyclay");
		deployIngredients.add(ingTrans);

		JsonObject soulBookIng = new JsonObject();
		soulBookIng.addProperty("type", "create_soul_harvest:soul_book");
		soulBookIng.addProperty("soul", entityId.toString());
		soulBookIng.addProperty("min_tier", tier);
		deployIngredients.add(soulBookIng);
		deployStep.add("ingredients", deployIngredients);

		JsonArray deployResults = new JsonArray();
		JsonObject deployResult = new JsonObject();
		deployResult.addProperty("id", "create_soul_harvest:incomplete_polyclay");
		deployResult.addProperty("item", "create_soul_harvest:incomplete_polyclay");
		deployResults.add(deployResult);
		deployStep.add("results", deployResults);

		deployStep.addProperty("keep_held_item", true);
		deployStep.addProperty("keepHeldItem", true);
		sequence.add(deployStep);

		// --- Spouting liquid soul crystal
		JsonObject spoutStep = new JsonObject();
		spoutStep.addProperty("type", "create:filling");

		JsonArray spoutIngredients = new JsonArray();
		JsonObject spoutItemIng = new JsonObject();
		spoutItemIng.addProperty("item", "create_soul_harvest:incomplete_polyclay");
		spoutIngredients.add(spoutItemIng);

		int fluidAmount = switch (tier) {
			case 1 -> 250;
			case 2 -> 500;
			case 3 -> 1000;
			default -> 250;
		};

		JsonObject fluidIng = new JsonObject();
		//? >= 1.21 {
		fluidIng.addProperty("type", "neoforge:single");
		fluidIng.addProperty("fluid", "create_soul_harvest:liquid_soul_crystal");
		fluidIng.addProperty("amount", fluidAmount);
		//?} else {
		/*fluidIng.addProperty("fluid", "create_soul_harvest:liquid_soul_crystal");
		fluidIng.addProperty("amount", fluidAmount);
		fluidIng.add("nbt", new JsonObject());
		*///?}
		spoutIngredients.add(fluidIng);
		spoutStep.add("ingredients", spoutIngredients);

		JsonArray spoutResults = new JsonArray();
		JsonObject spoutResult = new JsonObject();
		spoutResult.addProperty("id", "create_soul_harvest:incomplete_polyclay");
		spoutResult.addProperty("item", "create_soul_harvest:incomplete_polyclay");
		spoutResults.add(spoutResult);
		spoutStep.add("results", spoutResults);

		sequence.add(spoutStep);
		recipe.add("sequence", sequence);

		// --- Output Attuned Polyclay with soul component & nbt
		JsonArray finalResults = new JsonArray();
		JsonObject finalResult = new JsonObject();
		finalResult.addProperty("id", "create_soul_harvest:attuned_polyclay");
		finalResult.addProperty("item", "create_soul_harvest:attuned_polyclay");

		JsonObject soulDataTag = new JsonObject();
		soulDataTag.addProperty("soul_harvest:held_soul", entityId.toString());
		soulDataTag.addProperty("soul_harvest:soul_portion", 100);

		// 1.21 NeoForge components
		JsonObject components = new JsonObject();
		components.add("minecraft:custom_data", soulDataTag);
		finalResult.add("components", components);

		// 1.20 Forge NBT
		finalResult.add("nbt", soulDataTag);

		finalResults.add(finalResult);
		recipe.add("results", finalResults);

		return recipe;
	}

	private static JsonObject makeSplashingRecipe(ResourceLocation entityId, SoulData soulData) {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "create:splashing");

		// Ingredient
		JsonArray ingredients = new JsonArray();
		JsonObject ing = new JsonObject();
		ing.addProperty("type", "create_soul_harvest:attuned_polyclay");
		ing.addProperty("soul", entityId.toString());
		ingredients.add(ing);
		recipe.add("ingredients", ingredients);

		// Results from soul drop pools (representing 1 mob kill)
		JsonArray results = new JsonArray();
		int maxOutputs = 12; // Create's maximum output limit

		for (SoulDropPool pool : soulData.pools().values()) {
			for (SoulDropEntry entry : pool.entries()) {
				if (results.size() >= maxOutputs) {
					break;
				}

				JsonObject out = new JsonObject();
				out.addProperty("id", entry.item().toString());
				out.addProperty("item", entry.item().toString());

				int count = Math.round((entry.min() + entry.max()) / 2.0f);
				if (count > 1) {
					out.addProperty("count", count);
				}

				float chance = Math.min(1.0f, Math.max(0.01f, entry.weight() / 100.0f));
				if (chance < 1.0f) {
					out.addProperty("chance", Math.round(chance * 100.0f) / 100.0f);
				}

				results.add(out);
			}
			if (results.size() >= maxOutputs) {
				break;
			}
		}

		if (results.isEmpty()) {
			return null;
		}

		recipe.add("results", results);
		return recipe;
	}
}
