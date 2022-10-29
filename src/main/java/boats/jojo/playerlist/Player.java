package boats.jojo.playerlist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;

public class Player {
	
	public int longestStringLength;
	public int longestEnchantLength;
	private String name;
	private List<String> enchants = new ArrayList<String>();
	private boolean permed;
	private String distance;
	private Minecraft mc = Minecraft.getMinecraft();
	
	public void doCalc() { //bad code
		longestStringLength = mc.fontRendererObj.getStringWidth(name);
		enchants.forEach(enchant -> longestEnchantLength += mc.fontRendererObj.getStringWidth(enchant));
	}
	
	public void addEnchant(String ench) {
		enchants.add(ench);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getEnchants() {
		return enchants;
	}
	public void setEnchants(List<String> enchants) {
		this.enchants = enchants;
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
		return permed || !enchants.isEmpty();
	}
	
}
