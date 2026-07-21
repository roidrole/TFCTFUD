package roidrole.modid;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import roidrole.modid.proxy.CommonProxy;


@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Modid {

    //Proxy
    @SidedProxy(clientSide = Tags.ROOT_PACKAGE+".proxy.ClientProxy", serverSide = Tags.ROOT_PACKAGE+".proxy.ServerProxy")
    //If one proxy isn't used, point it to CommonProxy
    public static CommonProxy PROXY;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {PROXY.preInit();}

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {PROXY.init();}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {PROXY.postInit();}
}