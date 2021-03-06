package net.auscraft.BlivTrails.listeners;

import net.auscraft.BlivTrails.BlivTrails;
import net.auscraft.BlivTrails.PlayerConfig;
import net.auscraft.BlivTrails.TrailManager;
import net.auscraft.BlivTrails.config.FlatFile;
import net.auscraft.BlivTrails.config.TrailDefaults;
import net.auscraft.BlivTrails.runnables.TrailRunnable;
import net.auscraft.BlivTrails.util.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static net.auscraft.BlivTrails.TrailManager.loadTrail;
import static net.auscraft.BlivTrails.TrailManager.saveTrail;

/**
 * Created by OhBlihv (Chris) on 31/03/2016.
 * This file is part of the project BlivTrails
 */
public class TrailListener implements Listener
{

	private boolean displayWhenSpinning = false;
	private long joinActivationDelay = 100L;

	public TrailListener()
	{
		FlatFile cfg = FlatFile.getInstance();

		displayWhenSpinning = cfg.getSave().getBoolean("trails.misc.display-when-spinning", false);
		joinActivationDelay = cfg.getSave().getInt("trails.misc.join-activation-delay", 5) * 20L;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		if(joinActivationDelay > 0)
		{
			Bukkit.getScheduler().runTaskLater(BlivTrails.getInstance(), new Runnable()
			{

				@Override
				public void run()
				{
					loadTrail(player);
					// Wait a few seconds for the async sql read to go through
				}

			}, joinActivationDelay);
		}
		else
		{
			loadTrail(player);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		PlayerConfig playerConfig = TrailManager.getPlayerConfig(player.getUniqueId());
		if (playerConfig != null)
		{
			saveTrail(player);

			if(playerConfig.isScheduled())
			{
				Bukkit.getScheduler().cancelTask(playerConfig.getTaskId());
			}
			TrailManager.removePlayer(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// Stop the trail from working while the player isn't technically moving
		if (!displayWhenSpinning && (event.getFrom().getX() == event.getTo().getX() && event.getFrom().getY() == event.getTo().getY() && event.getFrom().getZ() == event.getTo().getZ()))
		{
			return;
		}

		UUID uuid = event.getPlayer().getUniqueId();
		PlayerConfig playerConfig = TrailManager.getPlayerConfig(uuid);
		if (playerConfig == null)
		{
			return;
		}

		if (TrailManager.hasVanishHook() && playerConfig.isVanished())
		{
			return; // If Vanished, don't do the trail.
		}

		//We set the trail-time regardless of whether it's a fresh spawn.
		//Set it before we check if we need to spawn the new trail
		playerConfig.setTrailTime(TrailManager.getTrailLength());
		if (playerConfig.isScheduled())
		{
			return;
		}

		if (!playerConfig.hasValidParticle())
		{
			TrailManager.removePlayer(playerConfig);
			return;
		}

		int speed = 1;
		TrailDefaults.ParticleDefaultStorage particleDefaults = TrailDefaults.getDefaults(playerConfig.getParticle());
		if (particleDefaults.getDisplaySpeed() > 0)
		{
			speed = particleDefaults.getDisplaySpeed();
		}

		TrailRunnable trailRunnable = new TrailRunnable(event.getPlayer(), playerConfig, TrailManager.getOption());
		//Fire a test particle to ensure we can actually display it with no errors
		try
		{
			trailRunnable.run();
		}
		catch(Exception e)
		{
			BUtil.logError("An error occurred while displaying " + event.getPlayer().getName() + "'s trail. It will be disabled.");
			e.printStackTrace();

			//Remove the PlayerConfig, since it is now invalid.
			TrailManager.removePlayer(uuid);
			return;
		}

		// public TrailRunnable(BlivTrails instance, Player player, PlayerConfig playerConfig, TrailManager listener, Random rand, double[] option)
		playerConfig.setTaskId(Bukkit.getScheduler().runTaskTimerAsynchronously(BlivTrails.getInstance(), trailRunnable, 1L, speed).getTaskId());
	}

}
