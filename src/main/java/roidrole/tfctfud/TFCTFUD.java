package roidrole.tfctfud;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(
	modid = Tags.MOD_ID,
	name = Tags.MOD_NAME,
	version = Tags.VERSION,
	dependencies = "required-after:mixinbooter;required:terrafirmacraft;required-after:configanytime"
)
public class TFCTFUD {
	public static Logger LOGGER;

	@Mod.EventHandler
	public void onConstruction(FMLPreInitializationEvent event){
		LOGGER = event.getModLog();
	}
}