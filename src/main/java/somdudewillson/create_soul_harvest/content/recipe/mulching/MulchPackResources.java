package somdudewillson.create_soul_harvest.content.recipe.mulching;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import somdudewillson.create_soul_harvest.SoulHarvestMod;

//? >= 1.21 {
import net.minecraft.server.packs.PackLocationInfo;
//?}

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MulchPackResources implements PackResources {

	//? >= 1.21 {
	private final PackLocationInfo location;

	public MulchPackResources(PackLocationInfo location) {
		this.location = location;
	}

	@Override
	public PackLocationInfo location() {
		return this.location;
	}
	//?} else {
	
	/*private final String packId;

	public MulchPackResources(String packId) {
		this.packId = packId;
	}

	@Override
	public String packId() {
		return this.packId;
	}
	*///?}

	private Map<String, byte[]> data;

	private synchronized Map<String, byte[]> getData() {
		if (data == null) {
			data = MulchDynamicData.generate();
		}
		return data;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... segments) {
		String path = String.join("/", segments);
		byte[] bytes = getData().get(path);
		if (bytes == null) {
			return null;
		}
		return () -> new ByteArrayInputStream(bytes);
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		if (type != PackType.SERVER_DATA) {
			return null;
		}
		String path = "data/" + location.getNamespace() + "/" + location.getPath();
		byte[] bytes = getData().get(path);
		if (bytes == null) {
			return null;
		}
		return () -> new ByteArrayInputStream(bytes);
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
		if (type != PackType.SERVER_DATA || !SoulHarvestMod.MOD_ID.equals(namespace)) {
			return;
		}
		String prefix = "data/" + namespace + "/" + path;
		if (!path.isEmpty() && !prefix.endsWith("/")) {
			prefix += "/";
		}
		for (Map.Entry<String, byte[]> entry : getData().entrySet()) {
			String filePath = entry.getKey();
			if (filePath.startsWith(prefix)) {
				String subPath = filePath.substring(("data/" + namespace + "/").length());
				ResourceLocation loc = SoulHarvestMod.id(namespace, subPath);
				byte[] bytes = entry.getValue();
				output.accept(loc, () -> new ByteArrayInputStream(bytes));
			}
		}
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		if (type == PackType.SERVER_DATA) {
			return Set.of(SoulHarvestMod.MOD_ID);
		}
		return Collections.emptySet();
	}

	@Nullable
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
		IoSupplier<InputStream> root = getRootResource("pack.mcmeta");
		if (root == null) {
			return null;
		}
		try (InputStream is = root.get()) {
			JsonObject json = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
			if (!json.has(deserializer.getMetadataSectionName())) {
				return null;
			}
			return deserializer.fromJson(json.getAsJsonObject(deserializer.getMetadataSectionName()));
		}
	}

	@Override
	public void close() {
		data = null;
	}
}
