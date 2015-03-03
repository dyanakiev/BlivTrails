package net.auscraft.BlivTrails.config;

import java.util.HashMap;

import com.darkblade12.ParticleEffect.ParticleEffect;

public class TrailDefaults
{
	//Will hold the trail defaults
	public class particleDefaultStorage
	{
		/*
		 * Defaults:
		 * type
		 * length
		 * height
		 * random.x-variation
		 * random.y-variation
		 * random.z-variation
		 * dynamic.spray-variation
		 * height.feet-location
		 * height.waist-location
		 * height.halo-location
		 */
		private int optionsInt[];
		private double optionsDouble[];
		
		public particleDefaultStorage(int type, int length, int height, int colour, double xvariation, double yvariation, double zvariation,
				double sprayvariation, double feetlocation, double waistlocation, double halolocation)
		{
			optionsInt = new int[4];
			optionsInt[0] = type;
			optionsInt[1] = length;
			optionsInt[2] = height;
			optionsInt[3] = colour;
			
			optionsDouble = new double[7];
			optionsDouble[0] = xvariation;
			optionsDouble[1] = yvariation;
			optionsDouble[2] = zvariation;
			optionsDouble[3] = sprayvariation;
			optionsDouble[4] = feetlocation;
			optionsDouble[5] = waistlocation;
			optionsDouble[6] = halolocation;
		}
		
		public int getInt(String option)
		{
			switch(option)
			{
				case "type": return optionsInt[0];
				case "length": return optionsInt[1];
				case "height": return optionsInt[2];
				case "colour": return optionsInt[3];
			}
			return 0;
		}
		
		public double getDouble(String option)
		{
			switch(option)
			{
				case "xvariation": return optionsDouble[0];
				case "yvariation": return optionsDouble[1];
				case "zvariation": return optionsDouble[2];
				case "sprayvariation": return optionsDouble[3];
				case "feetlocation": return optionsDouble[4];
				case "waistlocation": return optionsDouble[5];
				case "halolocation": return optionsDouble[6];
			}
			return 0.0;
		}
	}
	
	private HashMap<String, particleDefaultStorage> particleDefaults;
	
	public TrailDefaults(ConfigAccessor cfg)
	{
		String particleString = "NULL";
		particleDefaults = new HashMap<String, particleDefaultStorage>();
		for(ParticleEffect particle : ParticleEffect.values())
		{
			particleString = particle.toString();
			if(!trailConfigName(particleString).equals("NULL"))
			{
				particleString = trailConfigName(particleString);
					/*Bukkit.getConsoleSender().sendMessage("Loaded defaults for: " + particleString + "\n" +
							"trails." + particleString + ".options.type " + cfg.getString("trails." + particleString + ".options.type") +
							"\ntrails." + particleString + ".options.length " + cfg.getString("trails." + particleString + ".options.length") +
							"\ntrails." + particleString + ".options.height " + cfg.getString("trails." + particleString + ".options.height") +
							"\ntrails." + particleString + ".options.colour " + cfg.getString("trails." + particleString + ".options.colour") +
							"\ntrails." + particleString + ".options.defaults.random.x-variation " + cfg.getDouble("trails." + particleString + ".options.defaults.random.x-variation") +
							"\ntrails." + particleString + ".options.defaults.random.y-variation " + cfg.getDouble("trails." + particleString + ".options.defaults.random.y-variation") +
							"\ntrails." + particleString + ".options.defaults.random.z-variation " + cfg.getDouble("trails." + particleString + ".options.defaults.random.z-variation") + 
							"\ntrails." + particleString + ".options.defaults.dynamic.spray-variation " + cfg.getDouble("trails." + particleString + ".options.defaults.dynamic.spray-variation") +
							"\ntrails." + particleString + ".options.defaults.height.feet-location " + cfg.getDouble("trails." + particleString + ".options.defaults.height.feet-location") +
							"\ntrails." + particleString + ".options.defaults.height.waist-location " + cfg.getDouble("trails." + particleString + ".options.defaults.height.waist-location") +
							"\ntrails." + particleString + ".options.defaults.height.halo-location " + cfg.getDouble("trails." + particleString + ".options.defaults.height.halo-location"));
							*/
				particleDefaults.put(particleString, new particleDefaultStorage(typeStringtoInt(cfg.getString("trails." + particleString + ".options.type")), lengthStringtoInt(cfg.getString("trails." + particleString + ".options.length")),
						heightStringtoInt(cfg.getString("trails." + particleString + ".options.height")), colourStringtoInt(cfg.getString("trails." + particleString + ".options.colour")),
						cfg.getDouble("trails." + particleString + ".options.defaults.random.x-variation"), cfg.getDouble("trails." + particleString + ".options.defaults.random.y-variation"),
						cfg.getDouble("trails." + particleString + ".options.defaults.random.z-variation"), cfg.getDouble("trails." + particleString + ".options.defaults.dynamic.spray-variation"),
						cfg.getDouble("trails." + particleString + ".options.defaults.height.feet-location"), cfg.getDouble("trails." + particleString + ".options.defaults.height.waist-location"),
						cfg.getDouble("trails." + particleString + ".options.defaults.height.halo-location")));
				
			}
		}
	}
	
	public particleDefaultStorage getDefaults(String particle)
	{
		return particleDefaults.get(particle);
	}
	
	public int typeStringtoInt(String typeString)
	{
		int type = 1;
		try
		{
			switch(typeString)
			{
				case "random": type = 2; break;
				case "dynamic": type = 3; break;
			}
			return type;
		}
		catch(NullPointerException e)
		{
			return 1;
		}
	}
	
	public int lengthStringtoInt(String lengthString)
	{
		int length = 1;
		try
		{
			switch(lengthString)
			{
				case "medium": length = 2; break;
				case "long": length = 3; break;
			}
			return length;
		}
		catch(NullPointerException e)
		{
			return 1;
		}
	}
	
	public int heightStringtoInt(String heightString)
	{
		int height = 0;
		try
		{
			switch(heightString)
			{
				case "waist": height = 1; break;
				case "halo": height = 2; break;
			}
			return height;
		}
		catch(NullPointerException e)
		{
			return 0;
		}
		
	}
	
	public int colourStringtoInt(String colourString)
	{
		int colour = 0;
		try
		{
			switch(colourString)
			{
				case "red": colour = 1; break;
				case "dark green": colour = 2; break;
				case "brown": colour = 3; break;
				case "dark blue": colour = 4; break;
				case "purple": colour = 5; break;
				case "cyan": colour = 6; break;
				case "light grey": case "light gray": colour = 7; break;
				case "grey": case "gray": colour = 8; break;
				case "pink": colour = 9; break;
				case "lime": colour = 10; break;
				case "yellow": colour = 11; break;
				case "light blue": colour = 12; break;
				case "magenta": colour = 13; break;
				case "orange": colour = 14; break;
				case "black": colour = 15;  break;
				case "random": colour = 16; break;
			}
		}
		catch(NullPointerException e)
		{
			//Null
		}
		return colour;
	}
	
	private String trailConfigName(String particleString) //TODO:
	{
		switch(particleString)
		{
			case "BARRIER": particleString = "barrier"; break;
			case "CLOUD": particleString = "cloud"; break;
			case "CRIT": particleString = "criticals"; break;
			case "CRIT_MAGIC": particleString = "criticals-magic"; break;
			case "DRIP_LAVA": particleString = "drip-lava"; break;
			case "DRIP_WATER": particleString = "drip-water"; break;
			case "ENCHANTMENT_TABLE": particleString = "enchant"; break;
			case "EXPLOSION_NORMAL": particleString = "explosion-smoke"; break;
			case "FIREWORKS_SPARK": particleString = "firework"; break;
			case "FLAME": particleString = "flame"; break;
			case "HEART": particleString = "hearts"; break;
			case "LAVA": particleString = "lava"; break;
			case "NOTE": particleString = "note"; break;
			case "PORTAL": particleString = "portal"; break;
			case "REDSTONE": particleString = "redstone"; break;
			case "SLIME": particleString = "slime"; break;
			case "SMOKE_LARGE": particleString = "smoke"; break;
			case "SNOW_SHOVEL": particleString = "snow-shovel"; break;
			case "SNOWBALL": particleString = "snow-ball"; break;
			case "SPELL": particleString = "spell"; break;
			case "SPELL_INSTANT": particleString = "spell-instant"; break;
			case "SPELL_MOB": particleString = "spell-mob"; break;
			case "SPELL_WITCH": particleString = "spell-witch"; break;
			case "VILLAGER_ANGRY": particleString = "angry-villager"; break;
			case "VILLAGER_HAPPY": particleString = "happy-villager"; break;
			case "TOWN_AURA": particleString = "town-aura"; break;
			case "WATER_DROP": particleString = "water-drop"; break;
			case "WATER_SPLASH": particleString = "water-splash"; break;
			default: particleString = "NULL"; break;
		}
		return particleString;
	}
	
	/*public String trailConfigNameLCase(String particleString)
	{
		switch(particleString)
		{
			case "barrier": particleString = "barrier"; break;
			case "cloud": particleString = "cloud"; break;
			case "crit": particleString = "criticals"; break;
			case "magicCrit": particleString = "criticals-magic"; break;
			case "dripLava": particleString = "drip-lava"; break;
			case "dripWater": particleString = "drip-water"; break;
			case "enchantmenttable": particleString = "enchant"; break;
			case "explode": particleString = "explosion-smoke"; break;
			case "fireworksSpark": particleString = "firework"; break;
			case "flame": particleString = "flame"; break;
			case "heart": particleString = "hearts"; break;
			case "lava": particleString = "lava"; break;
			case "note": particleString = "note"; break;
			case "portal": particleString = "portal"; break;
			case "redstone": particleString = "redstone"; break;
			case "slime": particleString = "slime"; break;
			case "largeSmoke": particleString = "smoke"; break;
			case "snowshovel": particleString = "snow-shovel"; break;
			case "snowballpoof": particleString = "snow-ball"; break;
			case "spell": particleString = "spell"; break;
			case "instantSpell": particleString = "spell-instant"; break;
			case "mobSpell": particleString = "spell-mob"; break;
			case "witchMagic": particleString = "spell-witch"; break;
			case "angryVillager": particleString = "angry-villager"; break;
			case "happyVillager": particleString = "happy-villager"; break;
			case "townaura": particleString = "town-aura"; break;
			case "droplet": particleString = "water-drop"; break;
			case "splash": particleString = "water-splash"; break;
			default: particleString = "NULL"; break;
		}
		return particleString;
	}*/
}