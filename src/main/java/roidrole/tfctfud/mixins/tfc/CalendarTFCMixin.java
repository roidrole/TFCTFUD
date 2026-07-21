package roidrole.tfctfud.mixins.tfc;

import net.dries007.tfc.util.calendar.CalendarTFC;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CalendarTFC.class)
public abstract class CalendarTFCMixin {
	@Redirect(
		method = "onOverworldTick",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;)V"
		),
		remap = false
	)
	void noLoggingCalendar(Logger instance, String s){
		//NO-OP
	}
	@Redirect(
		method = "onOverworldTick",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"
		),
		remap = false
	)
	void noLoggingCalendar1(Logger instance, String s, Object o){
		//NO-OP
	}
	@Redirect(
		method = "onOverworldTick",
		at = @At(
			value = "INVOKE",
			target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"
		),
		remap = false
	)
	void noLoggingCalendar5(Logger instance, String s, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6){
		//NO-OP
	}
}
