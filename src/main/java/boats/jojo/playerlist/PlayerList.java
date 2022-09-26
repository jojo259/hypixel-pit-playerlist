package boats.jojo.playerlist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = PlayerList.MODID, version = PlayerList.VERSION)
public class PlayerList
{
    public static final String MODID = "playerlist";
    public static final String VERSION = "1.0";
    
    Minecraft mcInstance = Minecraft.getMinecraft();
    
    ArrayList<String[]> playersList = new ArrayList<String[]>();
    
    double lastCheckedPlayers = 0;
    int longestNotableUsername = 0;
    
    // all dark enchants (besides somber) + regularity
    String notableEnchants[][] = {
		{"regularity", "Regularity"},
		{"venom", "Venom"},
		{"misery", "Misery"},
		{"spite", "Spite"},
		{"mind_assault", "Mind Assault"},
		{"grim_reaper", "Grim Reaper"},
		{"hedge_fund", "Hedge Fund"},
		{"heartripper", "Heartripper"},
		{"needless_suffering", "Needless Suffering"},
		{"lycanthropy", "Lycanthropy"},
		{"sanguisuge", "Sanguisuge"},
		{"nostalgia", "Nostalgia"},
		{"golden_handcuffs", "Golden Handcuffs"},
	};
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
	public void tickEvent(TickEvent.PlayerTickEvent event) {
    	if (!isInPit()) {
    		return;
    	}
    	
    	if (System.currentTimeMillis() - lastCheckedPlayers > 1000) {
    		checkPlayers();
    	}
    }
    
    @SubscribeEvent
	public void overlayEvent(RenderGameOverlayEvent.Post event) {
    	if (mcInstance.currentScreen != null) {
    		return; // only renders if no GUI open
    	}
    	
    	for (int i = 0; i < playersList.size(); i++) {
    		String curEntry[] = playersList.get(i);
    		
    		String curPlayerName = curEntry[0];
    		String curPlayerRest = curEntry[1];
    		
    		int widthForEnchant = longestNotableUsername * 6 + 6;
    		
    		mcInstance.fontRendererObj.drawStringWithShadow(curPlayerName, 4, 4 + i * 8, 0xffffff);
    		mcInstance.fontRendererObj.drawStringWithShadow(curPlayerRest, 4 + widthForEnchant, 4 + i * 8, 0xffffff);
    	}
    }
    
    public void checkPlayers() {
    	playersList.clear();

    	List<EntityPlayer> allPlayers = mcInstance.theWorld.playerEntities;
    	
    	for (EntityPlayer curPlayer : allPlayers) {
    		String curPlayerName = curPlayer.getName();
    		if (curPlayerName.equals(mcInstance.thePlayer.getName())) {
    			continue;
    		}
    		
    		ItemStack curPlayerPants = curPlayer.getCurrentArmor(1); // 1 = pants
    		
    		if (curPlayerPants == null) {
    			continue;
    		}
    		
    		NBTTagCompound curPlayerPantsNbt = curPlayerPants.getTagCompound();
			
			if (!curPlayerPantsNbt.hasKey("ExtraAttributes")) {
				continue;
			}
			
			NBTTagCompound curPlayerPantsExtraAttributes = (NBTTagCompound) curPlayerPantsNbt.getTag("ExtraAttributes");
			
			if (!curPlayerPantsExtraAttributes.hasKey("CustomEnchants")) {
				continue;
			}
			
			if (!curPlayerPantsExtraAttributes.hasKey("Nonce")) {
				continue;
			}
			
			int curPlayerPantsNonce = curPlayerPantsExtraAttributes.getInteger("Nonce");
			
			if (curPlayerPantsNonce != 6 && curPlayerPantsNonce != 9) { // darks nonce and rages nonce
				continue;
			}
			
			NBTTagList curItemNbtCustomEnchants = curPlayerPantsExtraAttributes.getTagList("CustomEnchants", 10); // don't know what the 10 does but i had it in some previous code, it says "type"?
			
			for (int p = 0; p < curItemNbtCustomEnchants.tagCount(); p++) {
				
				NBTTagCompound curEnchant = (NBTTagCompound) curItemNbtCustomEnchants.get(p);
				
				String curEnchantKey = curEnchant.getString("Key");
				
				for (int k = 0; k < notableEnchants.length; k++) {
					String[] curNotableEnchant = notableEnchants[k];
					if (curNotableEnchant[0].equals(curEnchantKey)) {
						if (curPlayerName.length() > longestNotableUsername) {
							longestNotableUsername = curPlayerName.length();
						}
						
						String curEnchantLevel = String.valueOf(curEnchant.getInteger("Level"));
						
						String pantsColorCode = "";
						if (curPlayerPantsNonce == 6) {
							pantsColorCode = "§8";
						}
						else if (curPlayerPantsNonce == 9){
							pantsColorCode = "§c";
						}
						
						playersList.add(new String[] {curPlayerName, pantsColorCode + curNotableEnchant[1] + " " + curEnchantLevel});
						
						break;
					}
				}
			}
    	}
    }
    
    public boolean isInPit() {
    	return true; // do smth here idk check for something idk what's best
    }
}
