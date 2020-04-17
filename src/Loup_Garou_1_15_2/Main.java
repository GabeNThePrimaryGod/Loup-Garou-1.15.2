package Loup_Garou_1_15_2;

import Loup_Garou_1_15_2.Game.GameManager;
import Loup_Garou_1_15_2.listeners.*;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import org.bukkit.plugin.java.JavaPlugin;

import Loup_Garou_1_15_2.commands.*;

public class Main extends JavaPlugin
{
	public GameManager gameManager = new GameManager(this);

	@Override
	public void onEnable()
	{
		saveDefaultConfig();

		Tools.plugin = this;
		Config.plugin = this;

		new LGGameCommand(this);
		new LGSetupCommand(this);

		new JoinListener(this);
		new LeaveListener(this);
		new InventoryListener(this);
		new BlockListener(this);

		new GUIListener(this);
	}
}

