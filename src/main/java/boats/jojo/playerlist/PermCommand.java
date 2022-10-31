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
		int doAddCommandFrom = -1; // bc i dont want to organize this into functions

		if (args[0].equals("add")) {
			doAddCommandFrom = 1;
		}
		else if (args[0].equals("remove")) {
			for(int i = 1; i < args.length; i++) {
				int curUsernameIndex = permList.indexOf(args[i]);

				if (curUsernameIndex == -1) {
					mc.thePlayer.addChatMessage(new ChatComponentText("§6Player " + args[i] + " is not on perm list."));
					continue;
				}
				
				permList.remove(curUsernameIndex);
				mc.thePlayer.addChatMessage(new ChatComponentText("§4Unpermed " + args[i] + "."));
			}
		}
		else if (args[0].equals("list")) {
			for(String name : permList) {
				mc.thePlayer.addChatMessage(new ChatComponentText(name));
			}
		}
		else {
			doAddCommandFrom = 0;
		}

		if (doAddCommandFrom != -1) {
			for(int i = doAddCommandFrom; i < args.length; i++) {
				permList.add(args[i].toLowerCase());
				mc.thePlayer.addChatMessage(new ChatComponentText("§4Permed " + args[i] + "."));
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
		return "/perm <add/remove/list> <username>";
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
