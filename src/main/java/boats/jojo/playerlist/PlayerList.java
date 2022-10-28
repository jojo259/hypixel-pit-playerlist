package boats.jojo.playerlist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.ClientCommandHandler;
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
    public static final String VERSION = "1.1";

    Minecraft mc = Minecraft.getMinecraft();

    ArrayList<String[]> playersList = new ArrayList<String[]>();
    ArrayList<EntityPlayer> permList = new ArrayList<EntityPlayer>();

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
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new PermCommand());
        Config.loadConfig();
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.PlayerTickEvent event) {
        if (!isInPit())
            return;

        double curTime = System.currentTimeMillis();
        if (curTime - lastCheckedPlayers > 1000 * secondsPerCheckPlayers) {
            checkPlayers();
            lastCheckedPlayers = curTime;
        }
    }

    @SubscribeEvent
    public void overlayEvent(RenderGameOverlayEvent.Post event) {
        if (!event.type.equals(ElementType.TEXT))
            return;

        int yOffSet = stringEdgeOffset;
        for (String[] cur : playersList) {

            GlStateManager.pushMatrix();
            GlStateManager.scale((float) textScale, (float) textScale, 1); // don't know what last parameter is for

            int xOffSet = stringEdgeOffset;
            for (String element : cur) {
                mc.fontRendererObj.drawStringWithShadow(element, xOffSet, yOffSet, 0xFFFFFFFF);
                xOffSet += mc.fontRendererObj.getStringWidth(element + " ");
            };
            yOffSet += mc.fontRendererObj.FONT_HEIGHT + 1;

            GlStateManager.popMatrix();
        }
    }

    public void checkPlayers() {
        playersList.clear();

        List<EntityPlayer> allPlayers = mc.theWorld.playerEntities;

        for (EntityPlayer curPlayer : allPlayers) {
            List<String> details = getPlayerDetails(curPlayer);
            if(details.size() > 1) {
                String[] detailsArray = new String[details.size()];
                detailsArray = details.toArray(detailsArray);
                playersList.add(detailsArray);
            }
        }
    }

    public List<String> getPlayerDetails(EntityPlayer pl) {
        List<String> ret = new ArrayList<String>();
        if (pl == mc.thePlayer)
            return ret;
        ret.add(pl.getDisplayNameString());
        if(PermCommand.permList.contains(pl.getName().toLowerCase()))
            ret.add("§cPERMED");

        ItemStack pants = pl.getCurrentArmor(1); // 1 = pants

        if (pants == null)
            return ret;

        NBTTagCompound curPlayerPantsNbt = pants.getTagCompound();

        if (!curPlayerPantsNbt.hasKey("ExtraAttributes"))
            return ret;

        NBTTagCompound curPlayerPantsExtraAttributes = (NBTTagCompound) curPlayerPantsNbt.getTag("ExtraAttributes");

        if (!curPlayerPantsExtraAttributes.hasKey("CustomEnchants") || !curPlayerPantsExtraAttributes.hasKey("Nonce"))
            return ret;

        int curPlayerPantsNonce = curPlayerPantsExtraAttributes.getInteger("Nonce");

        if (curPlayerPantsNonce != 6 && curPlayerPantsNonce != 9)
            return ret;

        NBTTagList curItemNbtCustomEnchants = curPlayerPantsExtraAttributes.getTagList("CustomEnchants", 10);
        for (int p = 0; p < curItemNbtCustomEnchants.tagCount(); p++) {

            NBTTagCompound curEnchant = (NBTTagCompound) curItemNbtCustomEnchants.get(p);

            String curEnchantKey = "enchant";
            if (curEnchant.hasKey("Key"))
                curEnchantKey = curEnchant.getString("Key");

            for (String[] curNotableEnchant : notableEnchants)
                if (curNotableEnchant[0].equals(curEnchantKey)) {

                    String curEnchantLevel = "?";
                    if (curEnchant.hasKey("Level"))
                        curEnchantLevel = String.valueOf(curEnchant.getInteger("Level"));

                    if (curEnchantLevel.equals("1"))
                        curEnchantLevel = "I";
                    else if (curEnchantLevel.equals("2"))
                        curEnchantLevel = "II";
                    else if (curEnchantLevel.equals("3"))
                        curEnchantLevel = "III";

                    String pantsColorCode = "";
                    if (curPlayerPantsNonce == 6)
                        pantsColorCode = "§5";
                    else if (curPlayerPantsNonce == 9)
                        pantsColorCode = "§c";

                    ret.add( pantsColorCode + "§l" + curNotableEnchant[1] + " " + curEnchantLevel);

                    int playerDist = (int) Math.round(mc.thePlayer.getDistanceSqToEntity(pl));

                    String playerDistColorCode = "";
                    if (playerDist < 8)
                        playerDistColorCode = "§4";
                    else if (playerDist < 16)
                        playerDistColorCode = "§c";
                    else if (playerDist < 32)
                        playerDistColorCode = "§6";
                    else if (playerDist < 64)
                        playerDistColorCode = "§e";
                    else
                        playerDistColorCode = "§f";

                    ret.add(playerDistColorCode);
                }
        }
        return ret;
    }

    public boolean isInPit() {
        return true; // do smth here idk check for something idk what's best
    }
}
