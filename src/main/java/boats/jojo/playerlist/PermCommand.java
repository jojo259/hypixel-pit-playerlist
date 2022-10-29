package boats.jojo.playerlist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class PermCommand extends CommandBase {

	public static List<String> permList = new ArrayList<String>();
	private Minecraft mc = Minecraft.getMinecraft();

	@Override
	public String getCommandName() {
		return "perm";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args[0].equals("add")) {
			for(int i = 1; i < args.length; i++) {
				permList.add(args[i].toLowerCase());
				mc.thePlayer.addChatMessage(new ChatComponentText("§cPermed " + args[i]));
			}
		}
		else if(args[0].equals("remove")) {
			for(int i = 1; i < args.length; i++) {
				permList.remove(args[i].toLowerCase());
				mc.thePlayer.addChatMessage(new ChatComponentText("§4Unpermed " + args[i]));
			}
		}
		else if(args[0].equals("list")) {
			for(String name : permList) {
				mc.thePlayer.addChatMessage(new ChatComponentText(name));
			}
		}
		Config.updateConfig();
	}
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"list", "remove", "add"}) : null; //(args.length == 2 ? getListOfStringsMatchingLastWord(args, getListOfPlayerUsernames()): null);
    }
	
    private String[] getListOfPlayerUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/perm <add/remove/list> <username> ";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}
}
