package me.ohblihv.BlivTrails.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BUtil
{

	public enum FireworkType
	{

		CRATE_SPAWN(Type.BALL, Color.ORANGE, Color.WHITE, Color.YELLOW),
		OPEN_CRATE(Type.BALL, Color.GRAY, Color.WHITE, Color.BLACK),
		PVP_DEATH(Type.BALL, Color.RED, Color.ORANGE, Color.RED);

		@Getter
		private Type type;
		@Getter
		private Color colour1, colour2, colour3;

		FireworkType(Type type, Color colour1, Color colour2, Color colour3)
		{
			this.type = type;
			this.colour1 = colour1;
			this.colour2 = colour2;
			this.colour3 = colour3;
		}

	}

	private static boolean useConsoleColours = false;

	private static final String PLUGIN_NAME = "BlivTrails";
	private static final String prefix = "&f[&b" + PLUGIN_NAME + "&f]&r ";
	private static final String playerPrefix = "\u00A78[\u00A7e" + PLUGIN_NAME + "\u00A78]\u00A7r ";
	private static final java.util.logging.Logger log = Bukkit.getLogger();
	private static final Random rand = new Random();

	// ------------------------------------------------------------------------------------------------------
	// Miscellaneous
	// ------------------------------------------------------------------------------------------------------

	public static void createFirework(Location location, boolean instantExplosion, FireworkType fireworkType)
	{
		if(fireworkType == null)
		{
			fireworkType = FireworkType.CRATE_SPAWN;
		}

		FireworkEffect effect = FireworkEffect.builder().flicker(rand.nextBoolean())
				.withColor(fireworkType.getColour1(), fireworkType.getColour2()).withFade(fireworkType.getColour3())
				.with(fireworkType.getType()).trail(rand.nextBoolean()).build();

		if(instantExplosion)
		{
			location.setY(location.getY() + 2.0D);
			//CustomEntityFirework.spawn(location, effect);
		}
		else
		{
			Firework firework = location.getWorld().spawn(location, Firework.class);
			FireworkMeta meta = firework.getFireworkMeta();
			meta.addEffect(effect);
			firework.setFireworkMeta(meta);
			firework.setVelocity(new Vector(0.00, 0.05, 0.00));
		}
	}
	
	public static String getNMSVersion()
	{
		final String packageName = Bukkit.getServer().getClass().getPackage().getName();
		
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

	public static Material parseMaterial(String materialString)
	{
		Material toReturn = Material.getMaterial(materialString);
		if(toReturn != null)
		{
			return toReturn;
		}

		BUtil.logError("Invalid material: " + materialString);
		return Material.POTATO_ITEM;
	}

	public static String getLargestUnitAgo(long comparedMillis)
	{
		LocalDateTime   currentTime = LocalDateTime.now(Clock.tickSeconds(ZoneId.systemDefault())),
						comparedTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(comparedMillis), ZoneId.systemDefault()),
						runningTime = LocalDateTime.from(comparedTime);

		long    days, hours, minutes, seconds,
				largestValue = 0;
		ChronoUnit largestUnit = null; //Default to the lowest unit

		//Calculate the unit, then add it on to total time
		days = runningTime.until(currentTime, ChronoUnit.DAYS);
		if(days > 0)
		{
			largestUnit = ChronoUnit.DAYS; //No Checking required
			largestValue = days;
		}
		runningTime = runningTime.plusDays(days);

		if(largestUnit == null)
		{
			hours = runningTime.until(currentTime, ChronoUnit.HOURS);
			if(hours > 0)
			{
				largestUnit = ChronoUnit.HOURS;
				largestValue = hours;
			}
			runningTime = runningTime.plusHours(hours);
		}

		if(largestUnit == null)
		{
			minutes = runningTime.until(currentTime, ChronoUnit.MINUTES);
			if(minutes > 0)
			{
				largestUnit = ChronoUnit.MINUTES;
				largestValue = minutes;
			}
			runningTime = runningTime.plusMinutes(minutes);
		}

		if(largestUnit == null)
		{
			seconds = runningTime.until(currentTime, ChronoUnit.SECONDS);
			if(seconds > 0)
			{
				largestUnit = ChronoUnit.SECONDS;
				largestValue = seconds;
			}
			//runningTime = runningTime.plusSeconds(seconds);
		}

		String unitString = BUtil.capitaliseFirst(largestUnit.name().toLowerCase());
		if(unitString.endsWith("s") && largestValue == 1)
		{
			//Remove the trailing 's' if not required
			unitString = unitString.substring(0, unitString.length() - 1);
		}

		return largestValue + " " + unitString;
	}
	
	// ------------------------------------------------------------------------------------------------------
	// String Translation
	// ------------------------------------------------------------------------------------------------------

	public static String capitaliseFirst(String string)
	{
		return Character.toTitleCase(string.charAt(0)) + string.substring(1, string.length());
	}
	
	public static List<String> translateVariable(List<String> lines, String variable, String content)
	{
		if(lines == null) { return null; }

		if(!lines.isEmpty())
		{
			return lines.stream().map(line -> line.replace(variable, content)).collect(Collectors.toList());
		}
		return lines;
	}
	
	public static String stripColours(String toFix)
	{
		return Pattern.compile("[&|§](.)").matcher(toFix).replaceAll("");
	}

	public static String translateConsoleColours(String toFix)
	{
		if(!useConsoleColours)
		{
			return Pattern.compile("(?i)(&|Â§)([a-f0-9k-r])").matcher(toFix).replaceAll("");
		}
		toFix = Pattern.compile("(?i)(&|Â§)([a])").matcher(toFix).replaceAll("\u001B[32m\u001B[1m"); // Light Green
		toFix = Pattern.compile("(?i)(&|Â§)([b])").matcher(toFix).replaceAll("\u001B[36m"); // Aqua
		toFix = Pattern.compile("(?i)(&|Â§)([c])").matcher(toFix).replaceAll("\u001B[31m"); // Red
		toFix = Pattern.compile("(?i)(&|Â§)([d])").matcher(toFix).replaceAll("\u001B[35m\u001B[1m"); // Pink
		toFix = Pattern.compile("(?i)(&|Â§)([e])").matcher(toFix).replaceAll("\u001B[33m\u001B[1m"); // Yellow
		toFix = Pattern.compile("(?i)(&|Â§)([f])").matcher(toFix).replaceAll("\u001B[0m"); // White
		toFix = Pattern.compile("(?i)(&|Â§)([0])").matcher(toFix).replaceAll("\u001B[30m"); // Black
		toFix = Pattern.compile("(?i)(&|Â§)([1])").matcher(toFix).replaceAll("\u001B[34m"); // Dark Blue
		toFix = Pattern.compile("(?i)(&|Â§)([2])").matcher(toFix).replaceAll("\u001B[32m"); // Dark Green
		toFix = Pattern.compile("(?i)(&|Â§)([3])").matcher(toFix).replaceAll("\u001B[34m\u001B[1m"); // Light Blue
		toFix = Pattern.compile("(?i)(&|Â§)([4])").matcher(toFix).replaceAll("\u001B[31m"); // Dark Red
		toFix = Pattern.compile("(?i)(&|Â§)([5])").matcher(toFix).replaceAll("\u001B[35m"); // Purple
		toFix = Pattern.compile("(?i)(&|Â§)([6])").matcher(toFix).replaceAll("\u001B[33m"); // Gold
		toFix = Pattern.compile("(?i)(&|Â§)([7])").matcher(toFix).replaceAll("\u001B[37m"); // Light Grey
		toFix = Pattern.compile("(?i)(&|Â§)([8])").matcher(toFix).replaceAll("\u001B[30m\u001B[1m"); // Dark Grey
		toFix = Pattern.compile("(?i)(&|Â§)([9])").matcher(toFix).replaceAll("\u001B[34m"); // Dark Aqua
		toFix = Pattern.compile("(?i)(&|Â§)([r])").matcher(toFix).replaceAll("\u001B[0m");
		toFix += "\u001B[0m"; // Stop colour from overflowing to the next line with a reset code

		return toFix;
	}

	private static final Pattern colourPattern = Pattern.compile("(?i)&([0-9A-Fa-f-l-oL-OrR])");

	public static String translateColours(String toFix)
	{
		// Convert every single colour code and formatting code, excluding
		// 'magic' (&k), capitals and lowercase are converted.
		return colourPattern.matcher(toFix).replaceAll("\u00A7$1");
	}

	public static List<String> translateColours(List<String> lines)
	{
		if (lines == null || lines.isEmpty())
		{
			return null;
		}

		//list.add(i, colourPattern.matcher(list.get(i)).replaceAll("\u00A7$1"));
		return lines.stream().map(line -> colourPattern.matcher(line).replaceAll("\u00A7$1")).collect(Collectors.toList());
	}

	public static List<String> convertPlaceholders(List<String> lines, String[] placeholders, String[] content)
	{
		if (placeholders.length != content.length || lines == null)
		{
			if(lines != null)
			{
				BUtil.logError("Placeholder length does not match content length! Returning plain lines:\n" + lines.toString());
			}
			return lines;
		}
		lines = new ArrayList<>(lines);

		for(int lineNum = 0;lineNum < lines.size();lineNum++)
		{
			String line = lines.get(lineNum);
			for(int index = 0;index < placeholders.length;index++)
			{
				line = line.replace(placeholders[index], content[index]);
			}
			lines.set(lineNum, line);
		}
		return lines;
	}

	public static String convertPlaceholders(String line, String[] placeholders, String[] content)
	{
		if (placeholders.length != content.length)
		{
			return null;
		}

		for (int index = 0; index < placeholders.length; index++)
		{
			line = line.replace(placeholders[index], content[index]);
		}
		return line;
	}

	/**
	 * Very primitive variable search algorithm.
	 * Will only generally work with a single variable and no doubly-used characters
	 * found in both the variable and the char after the variable
	 *
	 * @param string String to find replacement in
	 * @param original Original String without the variable replacement
	 * @param variable Variable to search for
	 * @return The variable's replacement if found, else null
	 */
	public static String getVariableFromString(String string, String original, String variable)
	{
		Matcher variableMatcher = Pattern.compile(variable).matcher(original);
		if(!variableMatcher.find())
		{
			//BUtil.logInfo("Did not find variable in given string");
			return null;
		}

		int startIndex = variableMatcher.start(),
			endFoundIndex = -1;
		char charAfterVariable = original.charAt(variableMatcher.end() + 1);

		for(int charIdx = startIndex;charIdx < original.length();charIdx++)
		{
			if(string.charAt(charIdx) != charAfterVariable)
			{
				continue;
			}

			endFoundIndex = charIdx;
			break;
		}

		if(endFoundIndex != -1)
		{
			//BUtil.logInfo("Found at " + startIndex + " -> " + endFoundIndex + ": '" + string.substring(startIndex, endFoundIndex) + "'");
			return string.substring(startIndex, endFoundIndex);
		}
		else
		{
			//BUtil.logInfo("String not found");
			return null;
		}
	}

	// ------------------------------------------------------------------------------------------------------
	// Printing
	// ------------------------------------------------------------------------------------------------------

	public static void printSuccess(CommandSender sender, String message)
	{
		sender.sendMessage(playerPrefix + ChatColor.GREEN + translateColours(message));
	}

	public static void printPlain(CommandSender sender, String message)
	{
		sender.sendMessage(playerPrefix + translateColours(message));
	}

	public static void printInfo(CommandSender sender, String message)
	{
		sender.sendMessage(playerPrefix + ChatColor.YELLOW + translateColours(message));
	}

	public static void printError(CommandSender sender, String message)
	{
		sender.sendMessage(playerPrefix + ChatColor.RED + translateColours(message));
	}

	public static void printSelf(String message)
	{
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer("OhBlihv");
		if(offlinePlayer != null && offlinePlayer.isOnline())
		{
			offlinePlayer.getPlayer().sendMessage(translateColours(message));
		}
	}

	public static void printToOfflinePlayer(String playerName, String message)
	{
		if(playerName == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(playerName), message);
	}

	public static void printToOfflinePlayer(UUID uuid, String message)
	{
		if(uuid == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(uuid), message);
	}

	private static void printToOfflinePlayer(OfflinePlayer offlinePlayer, String message)
	{
		if(offlinePlayer != null && offlinePlayer.isOnline())
		{
			offlinePlayer.getPlayer().sendMessage(message);
		}
	}

	public static void printToOfflinePlayer(String playerName, List<String> message)
	{
		if(playerName == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(playerName), message);
	}

	public static void printToOfflinePlayer(UUID uuid, List<String> message)
	{
		if(uuid == null)
		{
			return;
		}

		printToOfflinePlayer(Bukkit.getOfflinePlayer(uuid), message);
	}

	private static void printToOfflinePlayer(OfflinePlayer offlinePlayer, List<String> message)
	{
		if(offlinePlayer != null && offlinePlayer.isOnline())
		{
			Player player = offlinePlayer.getPlayer();
			for(String line : message)
			{
				player.sendMessage(line);
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------
	// Broadcasting
	// ------------------------------------------------------------------------------------------------------

	public void broadcastPlain(String message)
	{
		Bukkit.broadcastMessage(message);
	}

	// ------------------------------------------------------------------------------------------------------
	// Logging
	// ------------------------------------------------------------------------------------------------------

	public static void logSuccess(String message)
	{
		log.log(Level.INFO, translateConsoleColours(prefix + "&2SUCCESS: &a" + message));
	}

	public static void logPlain(String message)
	{
		log.log(Level.INFO, translateConsoleColours(prefix + message));
	}

	public static void logInfo(String message)
	{
		log.log(Level.INFO, translateConsoleColours(prefix + "&9INFO: &b" + message));
		//System.out.println(translateConsoleColours(prefix + "&9INFO: &b" + message));
	}

	public static void logError(String message)
	{
		log.log(Level.WARNING, translateConsoleColours(prefix + "&4ERROR: &c" + message));
	}

	public static void logSevere(String message)
	{
		log.log(Level.SEVERE, translateConsoleColours(prefix + "&4SEVERE: &c" + message));
	}

	// ------------------------------------------------------------------------------------------------------
	// Miscellaneous
	// ------------------------------------------------------------------------------------------------------

	@Deprecated
	public static Collection<? extends Player> getOnlinePlayers()
	{
		try
		{
			Method getOnlinePlayers = Bukkit.class.getMethod("getOnlinePlayers");
			if(getOnlinePlayers.getReturnType() == Collection.class)
			{
				return (Collection<? extends Player>) getOnlinePlayers.invoke(null);
			}
			else
			{
				return Arrays.asList(((Player[]) getOnlinePlayers.invoke(null)));
			}
		}
		catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

	private static final BASE64Encoder base64Encoder = new BASE64Encoder();
	private static final BASE64Decoder base64Decoder = new BASE64Decoder();

	public static String compressUUID(UUID uuid)
	{
		return base64Encoder.encode(toBytes(uuid)).split("=")[0];
	}

	public static UUID deCompressUUID(String uuid)
	{
		try
		{
			return fromBytes(base64Decoder.decodeBuffer(uuid.split(":")[0].concat("==")));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Copied from UUIDUtils.class to avoid NoClassDefErrors which occur on Compilex' Spigot.
	 */

	public static byte[] toBytes(UUID uuid)
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		return byteBuffer.array();
	}

	public static UUID fromBytes(byte[] array)
	{
		if (array.length != 16)
		{
			throw new IllegalArgumentException("Illegal byte array length: " + array.length);
		}

		ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		long mostSignificant = byteBuffer.getLong();
		long leastSignificant = byteBuffer.getLong();

		return new UUID(mostSignificant, leastSignificant);
	}

}