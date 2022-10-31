package boats.jojo.playerlist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

public class Player {
	
	public int longestStringLength;
	public int enchantLength;
	public int distanceLength;
	private String name;
	private String enchant;
	private boolean permed;
	private String distance;
	private Minecraft mc = Minecraft.getMinecraft();
	
	public void doCalc() { //bad code
		longestStringLength = mc.fontRendererObj.getStringWidth(name);
		distanceLength = mc.fontRendererObj.getStringWidth(distance);
		enchantLength = mc.fontRendererObj.getStringWidth(enchant);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEnchant() {
		return enchant;
	}
	public void setEnchant(String ench) {
		this.enchant = ench;
	}

	public boolean isPermed() {
		return permed;
	}
	public void setPermed(boolean permed) {
		this.permed = permed;
	}

	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	
	public boolean isNoticable() {
		return permed || enchant != null;
	}
	
}
