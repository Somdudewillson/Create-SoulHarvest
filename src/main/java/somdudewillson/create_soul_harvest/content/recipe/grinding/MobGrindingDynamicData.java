package somdudewillson.create_soul_harvest.content.recipe.grinding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import somdudewillson.create_soul_harvest.SoulHarvestMod;
import somdudewillson.create_soul_harvest.content.item.PolyclayItem;
import somdudewillson.create_soul_harvest.content.soul.SoulData;
import somdudewillson.create_soul_harvest.content.soul.SoulDataManager;
import somdudewillson.create_soul_harvest.content.soul.SoulDropEntry;
import somdudewillson.create_soul_harvest.content.soul.SoulDropPool;
import somdudewillson.create_soul_harvest.content.soul.DropRestriction;

//? >= 1.21 {
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.RecipeHolder;
//?}

import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.annotation.Nullable;

public class MobGrindingDynamicData {
	private static final float ENHANCED_ONLY_MULT = 0.25f;
	
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

		Map<ResourceLocation, JsonObject> recipes = generateRecipeJsons(null);
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

	public static Map<ResourceLocation, JsonObject> generateRecipeJsons(@Nullable MinecraftServer server) {
		Optional<MinecraftServer> optMinecraftServer = Optional.ofNullable(server);
		RegistryAccess nullableRegistryAccess = optMinecraftServer.map(MinecraftServer::registryAccess).orElse(null);
		
		Map<ResourceLocation, JsonObject> recipes = new HashMap<>();
		Map<ResourceLocation, SoulData> knownSouls = SoulDataManager.getKnownSoulData();

		List<PolyclayItem> polyclays = new ArrayList<>();
		for (Item item : BuiltInRegistries.ITEM) {
			if (item instanceof PolyclayItem polyclay) {
				polyclays.add(polyclay);
			}
		}

		for (PolyclayItem polyclay : polyclays) {
			ResourceLocation polyclayId = BuiltInRegistries.ITEM.getKey(polyclay);
			if (polyclayId == null) continue;
			int polyclayTier = polyclay.getTier();
			Item transItem = polyclay.getTransitionalItem();
			Item attunedItem = polyclay.getAttunedItem();
			if (attunedItem == null) continue;

			ResourceLocation attunedId = BuiltInRegistries.ITEM.getKey(attunedItem);
			if (attunedId == null) continue;

			for (Map.Entry<ResourceLocation, SoulData> entry : knownSouls.entrySet()) {
				ResourceLocation entityId = entry.getKey();
				SoulData soulData = entry.getValue();

				int mobTier = soulData.tier();
				if (mobTier <= 0 || mobTier > polyclayTier + 1) {
					continue;
				}

				if (!soulData.hasDrops(nullableRegistryAccess, true)) {
					continue;
				}
				

				String recipeSuffix = polyclayId.getPath() + "_" + entityId.getNamespace() + "_" + entityId.getPath();
				String washSuffix = attunedId.getPath() + "_" + entityId.getNamespace() + "_" + entityId.getPath();

				// Attuned processing
				float multiplier = PolyclayItem.getDropMultiplier(polyclayTier, mobTier);
				JsonObject splashing = makeSplashingRecipe(attunedItem, entityId, soulData, multiplier, false, nullableRegistryAccess);
				if (splashing != null) {
					recipes.put(SoulHarvestMod.id("splashing/wash_" + washSuffix), splashing);
				}

				if (soulData.hasEnhancedSpecificDrops(nullableRegistryAccess)) {
					JsonObject enhancedSplashing = makeSplashingRecipe(attunedItem, entityId, soulData, multiplier, true, nullableRegistryAccess);
					if (enhancedSplashing != null) {
						recipes.put(SoulHarvestMod.id("splashing/wash_enhanced_" + washSuffix), enhancedSplashing);
						recipes.put(SoulHarvestMod.id("deploying/enhance_" + washSuffix),
								makeDeployEnhanceRecipe(attunedItem, entityId));
					}
				}
				
				JsonObject grinding = makeGrindingRecipe(entityId, soulData, nullableRegistryAccess);
				if (grinding != null) {
					recipes.put(SoulHarvestMod.id("splashing/grind_" + washSuffix), grinding);
				}

				// Attuning
				recipes.put(SoulHarvestMod.id("sequenced_assembly/attune_" + recipeSuffix),
						makeSequencedAssemblyRecipe(polyclay, transItem, attunedItem, entityId, mobTier));
			}
		}

		return recipes;
	}

	public static void updateRecipeManager(MinecraftServer server) {
		if (server == null) {
			return;
		}

		Map<ResourceLocation, JsonObject> recipeJsons = generateRecipeJsons(server);
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

	private static JsonObject makeSequencedAssemblyRecipe(Item polyclay, Item transItem, Item attunedItem, ResourceLocation entityId, int mobTier) {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "create:sequenced_assembly");

		ResourceLocation polyclayId = BuiltInRegistries.ITEM.getKey(polyclay);
		ResourceLocation transId = BuiltInRegistries.ITEM.getKey(transItem);
		ResourceLocation attunedId = BuiltInRegistries.ITEM.getKey(attunedItem);

		// Base input
		JsonObject baseIng = new JsonObject();
		baseIng.addProperty("item", polyclayId != null ? polyclayId.toString() : "create_soul_harvest:polyclay");
		recipe.add("ingredient", baseIng);

		// Transitional item
		JsonObject transItemJson = new JsonObject();
		String transStr = transId != null ? transId.toString() : "create_soul_harvest:polyclay_attuned_incomplete";
		transItemJson.addProperty("id", transStr);
		transItemJson.addProperty("item", transStr);
		recipe.add("transitional_item", transItemJson);

		recipe.addProperty("loops", 1);

		// Sequence steps
		JsonArray sequence = new JsonArray();

		// --- Deployer application of Soul Book
		JsonObject deployStep = new JsonObject();
		deployStep.addProperty("type", "create:deploying");

		JsonArray deployIngredients = new JsonArray();
		JsonObject ingTrans = new JsonObject();
		ingTrans.addProperty("id", transStr);
		ingTrans.addProperty("item", transStr);
		deployIngredients.add(ingTrans);

		JsonObject soulBookIng = new JsonObject();
		soulBookIng.addProperty("type", "create_soul_harvest:soul_book");
		soulBookIng.addProperty("soul", entityId.toString());
		soulBookIng.addProperty("min_tier", mobTier);
		deployIngredients.add(soulBookIng);
		deployStep.add("ingredients", deployIngredients);

		JsonArray deployResults = new JsonArray();
		JsonObject deployResult = new JsonObject();
		deployResult.addProperty("id", transStr);
		deployResult.addProperty("item", transStr);
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
		spoutItemIng.addProperty("item", transStr);
		spoutIngredients.add(spoutItemIng);

		int fluidAmount = switch (mobTier) {
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
		spoutResult.addProperty("id", transStr);
		spoutResult.addProperty("item", transStr);
		spoutResults.add(spoutResult);
		spoutStep.add("results", spoutResults);

		sequence.add(spoutStep);
		recipe.add("sequence", sequence);

		// --- Output Attuned Polyclay with soul component & nbt
		String attunedStr = attunedId != null ? attunedId.toString() : "create_soul_harvest:polyclay_attuned";
		JsonArray finalResults = new JsonArray();
		JsonObject finalResult = new JsonObject();
		finalResult.addProperty("id", attunedStr);
		finalResult.addProperty("item", attunedStr);

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

	private static JsonObject makeDeployEnhanceRecipe(Item attunedItem, ResourceLocation entityId) {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "create:deploying");

		ResourceLocation attunedId = BuiltInRegistries.ITEM.getKey(attunedItem);
		String attunedStr = attunedId != null ? attunedId.toString() : "create_soul_harvest:polyclay_attuned";

		JsonArray ingredients = new JsonArray();
		JsonObject polyclayIng = new JsonObject();
		polyclayIng.addProperty("type", "create_soul_harvest:attuned_polyclay");
		polyclayIng.addProperty("item", attunedStr);
		polyclayIng.addProperty("soul", entityId.toString());
		polyclayIng.addProperty("enhanced", false);
		ingredients.add(polyclayIng);

		JsonObject catalystIng = new JsonObject();
		catalystIng.addProperty("item", "create_soul_harvest:enchanted_inlaid_lapis");
		ingredients.add(catalystIng);
		recipe.add("ingredients", ingredients);

		JsonArray results = new JsonArray();
		JsonObject result = new JsonObject();
		result.addProperty("id", attunedStr);
		result.addProperty("item", attunedStr);

		JsonObject soulDataTag = new JsonObject();
		soulDataTag.addProperty("soul_harvest:held_soul", entityId.toString());
		soulDataTag.addProperty("soul_harvest:soul_portion", 100);
		soulDataTag.addProperty("soul_harvest:enhanced", true);

		JsonObject components = new JsonObject();
		components.add("minecraft:custom_data", soulDataTag);
		result.add("components", components);
		result.add("nbt", soulDataTag);

		results.add(result);
		recipe.add("results", results);

		return recipe;
	}

	private static JsonObject makeSplashingRecipe(Item attunedItem, ResourceLocation entityId, SoulData soulData, float multiplier, boolean enhanced, @Nullable RegistryAccess registryAccess) {
		JsonObject recipe = new JsonObject();
		recipe.addProperty("type", "create:splashing");

		ResourceLocation attunedId = BuiltInRegistries.ITEM.getKey(attunedItem);
		String attunedStr = attunedId != null ? attunedId.toString() : "create_soul_harvest:polyclay_attuned";

		// Ingredient
		JsonArray ingredients = new JsonArray();
		JsonObject ing = new JsonObject();
		ing.addProperty("type", "create_soul_harvest:attuned_polyclay");
		ing.addProperty("item", attunedStr);
		ing.addProperty("soul", entityId.toString());
		ing.addProperty("enhanced", enhanced);
		ingredients.add(ing);
		recipe.add("ingredients", ingredients);

		// Results from soul drop pools (representing 1 mob kill) scaled by drop multiplier
		JsonArray results = new JsonArray();
		int maxOutputs = 12; // Create's maximum output limit

		for (SoulDropPool pool : soulData.pools().values()) {
			for (SoulDropEntry entry : pool.entries()) {
				if (results.size() >= maxOutputs) {
					break;
				}

				if (entry.restriction() == DropRestriction.LOOT_TOKEN) {
					continue;
				}
				if (entry.restriction() == DropRestriction.ENHANCED && !enhanced) {
					continue;
				}

				if (!SoulData.isDropAllowed(entry.item(), registryAccess, enhanced)) {
					continue;
				}
				boolean enhancedOnly = enhanced && !SoulData.isDropAllowedStandard(entry.item(), registryAccess);

				float baseCount = (entry.min() + entry.max()) / 2.0f;
				float baseChance = Math.min(1.0f, Math.max(0.01f, entry.weight() / 100.0f));
				float enhanceFac = enhancedOnly ? ENHANCED_ONLY_MULT : 1;
				float expected = baseCount * baseChance * multiplier * enhanceFac;

				if (expected <= 0.001f) {
					if (enhancedOnly) {
						expected = 0.001f;
					} else {
						continue;
					}
				}

				JsonObject out = new JsonObject();
				out.addProperty("id", entry.item().toString());
				out.addProperty("item", entry.item().toString());

				int count = Math.max(1, (int) Math.ceil(expected));
				float chance = Math.min(1.0f, Math.max(0.01f, expected / count));
				chance = Math.round(chance * 1000.0f) / 1000.0f;

				if (count > 1) {
					out.addProperty("count", count);
				}
				if (chance < 1.0f) {
					out.addProperty("chance", chance);
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

	private static JsonObject makeGrindingRecipe(ResourceLocation entityId, SoulData soulData, @Nullable RegistryAccess registryAccess) {
		// TODO Is this even possible? Consider later.
		return null;
	}
}
