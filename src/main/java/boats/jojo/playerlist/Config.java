package boats.jojo.playerlist;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;

public class Config {

	public static final File configDir = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "playerlist");
	public static  File config;

	// cannot be fucked to make non static even though i should

	public static void loadConfig() {
		if(!configDir.isDirectory()) {
			configDir.mkdir();
        }
		config = new File(configDir, "config.json");

		if (!config.exists()) {
			try {
				config.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }
		else {
			JsonParser jsonParser = new JsonParser();
			try (FileReader reader = new FileReader(config)) {
				JsonElement obj = jsonParser.parse(reader);
				JsonArray jarr = obj.getAsJsonArray();
				for(int i = 0; i < jarr.size(); i++) {
					PermCommand.permList.add(jarr.get(i).getAsString());
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateConfig() {
		JsonArray data = new JsonArray();
		for(String name :PermCommand.permList) {
			JsonPrimitive n = new JsonPrimitive(name);
			data.add(n);
		}
		try (PrintWriter out = new PrintWriter(new FileWriter(config))) {
			out.write(data.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
