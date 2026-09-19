package amatsagu.grimdepth.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

	@Redirect(
			method = "forceSendPlayerSupportBlocks",
			at = @At(
					value = "INVOKE",
					target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"
			)
	)
	private void grimdepth$demoteStandingOnAirLog(Logger logger, String message, Object arg) {
		logger.debug(message, arg);
	}
}
