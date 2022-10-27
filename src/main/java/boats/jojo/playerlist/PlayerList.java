package boats.jojo.playerlist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
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

    int secondsPerCheckPlayers = 1;

    float textScale = 2/4f;

    int stringEdgeOffset = 4;
    int stringSpacingX = 16;
    int stringSpacingY = 9;

    double lastCheckedPlayers = 0;
    int longestNotableUsernameWidth = 0;
    int longestEnchantWidth = 0;

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

        double curTime = System.currentTimeMillis();
        if (curTime - lastCheckedPlayers > 1000 * secondsPerCheckPlayers) {
            checkPlayers();
            lastCheckedPlayers = curTime;
        }
    }

    @SubscribeEvent
    public void overlayEvent(RenderGameOverlayEvent.Post event) {
        if (!event.type.equals(ElementType.TEXT)) {
            return;
        }

        for (int i = 0; i < playersList.size(); i++) {
            String curEntry[] = playersList.get(i);

            String curPlayerName = curEntry[0];
            String curPlayerEnch = curEntry[1];
            String curPlayerDist = curEntry[2];

            GlStateManager.pushMatrix();

            GlStateManager.scale((float) textScale, (float) textScale, 1); // don't know what last parameter is for

            mcInstance.fontRendererObj.drawStringWithShadow(curPlayerName, stringEdgeOffset, stringEdgeOffset + i * stringSpacingY, 0xffffff);
            mcInstance.fontRendererObj.drawStringWithShadow(curPlayerEnch, stringEdgeOffset + longestNotableUsernameWidth + stringSpacingX, stringEdgeOffset + i * stringSpacingY, 0xffffff);
            mcInstance.fontRendererObj.drawStringWithShadow(curPlayerDist, stringEdgeOffset + longestNotableUsernameWidth + stringSpacingX + longestEnchantWidth + stringSpacingX, stringEdgeOffset + i * stringSpacingY, 0xffffff);

            GlStateManager.popMatrix();
        }
    }

    public void checkPlayers() {
        playersList.clear();

        // for testing

        /*
		playersList.add(new String[] {"�f�l" + "JojoQ", "�5" + "�l" + "Venom" + " " + "II", "�f�l259m"});
		playersList.add(new String[] {"�f�l" + "qiaodou", "�c" + "�l" + "Regularity" + " " + "I", "�4�l7m"});
		playersList.add(new String[] {"�f�l" + "xTomCat", "�c" + "�l" + "Regularity" + " " + "III", "�e�l41m"});

		longestNotableUsernameWidth = mcInstance.fontRendererObj.getStringWidth("�f�lxTomCat");
		longestEnchantWidth = mcInstance.fontRendererObj.getStringWidth("�c�lRegularity III");

		if (true) { // return during testing to avoid using actual data
			return;
		}
         */

        BlockPos clientPos = mcInstance.thePlayer.getPosition();
        int clientPosX = clientPos.getX();
        int clientPosY = clientPos.getY();
        int clientPosZ = clientPos.getZ();

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

            if (!curPlayerPantsExtraAttributes.hasKey("CustomEnchants") || !curPlayerPantsExtraAttributes.hasKey("Nonce")) {
                continue;
            }

            int curPlayerPantsNonce = curPlayerPantsExtraAttributes.getInteger("Nonce");

            if (curPlayerPantsNonce != 6 && curPlayerPantsNonce != 9) { // darks nonce and rages nonce
                continue;
            }

            NBTTagList curItemNbtCustomEnchants = curPlayerPantsExtraAttributes.getTagList("CustomEnchants", 10); // don't know what the 10 does but i had it in some previous code, it says "type"?

            for (int p = 0; p < curItemNbtCustomEnchants.tagCount(); p++) {

                NBTTagCompound curEnchant = (NBTTagCompound) curItemNbtCustomEnchants.get(p);

                String curEnchantKey = "enchant";
                if (curEnchant.hasKey("Key")) { // just for sanity...
                    curEnchantKey = curEnchant.getString("Key");
                }

                for (String[] curNotableEnchant : notableEnchants) {
                    if (curNotableEnchant[0].equals(curEnchantKey)) {

                        String curEnchantLevel = "?";
                        if (curEnchant.hasKey("Level")) { // just for sanity also
                            curEnchantLevel = String.valueOf(curEnchant.getInteger("Level"));
                        }

                        if (curEnchantLevel.equals("1")) {
                            curEnchantLevel = "I";
                        }
                        else if (curEnchantLevel.equals("2")) {
                            curEnchantLevel = "II";
                        }
                        else if (curEnchantLevel.equals("3")) {
                            curEnchantLevel = "III";
                        }

                        String pantsColorCode = "";
                        if (curPlayerPantsNonce == 6) {
                            pantsColorCode = "�5";
                        }
                        else if (curPlayerPantsNonce == 9){
                            pantsColorCode = "�c";
                        }

                        String curUsernameStr = "�f�l" + curPlayerName;

                        int curPlayerNameDrawWidth = mcInstance.fontRendererObj.getStringWidth(curUsernameStr);
                        if (curPlayerNameDrawWidth > longestNotableUsernameWidth) {
                            longestNotableUsernameWidth = curPlayerNameDrawWidth;
                        }

                        String curEnchantStr = pantsColorCode + "�l" + curNotableEnchant[1] + " " + curEnchantLevel;

                        int curEnchantDrawWidth = mcInstance.fontRendererObj.getStringWidth(curEnchantStr);
                        if (curEnchantDrawWidth > longestEnchantWidth) {
                            longestEnchantWidth = curEnchantDrawWidth;
                        }

                        BlockPos curPlayerPos = curPlayer.getPosition();
                        int curPlayerPosX = curPlayerPos.getX();
                        int curPlayerPosY = curPlayerPos.getY();
                        int curPlayerPosZ = curPlayerPos.getZ();

                        int playerDist = (int) Math.round(getDist(curPlayerPosX, curPlayerPosY, curPlayerPosZ, clientPosX, clientPosY, clientPosZ));

                        String playerDistColorCode = "";
                        if (playerDist < 8) {
                            playerDistColorCode = "�4";
                        }
                        else if (playerDist < 16) {
                            playerDistColorCode = "�c";
                        }
                        else if (playerDist < 32) {
                            playerDistColorCode = "�6";
                        }
                        else if (playerDist < 64) {
                            playerDistColorCode = "�e";
                        }
                        else {
                            playerDistColorCode = "�f";
                        }

                        playersList.add(new String[] {curUsernameStr, curEnchantStr, playerDistColorCode + playerDist + "m"});

                        break;
                    }
                }
            }
        }
    }

    public boolean isInPit() {
        return true; // do smth here idk check for something idk what's best
    }

    public double getDist(double fx, double fy, double fz, double tx, double ty, double tz) {
        return Math.sqrt(Math.pow(tx - fx, 2) + Math.pow(ty - fy, 2) + Math.pow(tz - fz, 2));
    }
}
