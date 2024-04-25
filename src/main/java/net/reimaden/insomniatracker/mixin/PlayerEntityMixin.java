package net.reimaden.insomniatracker.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.reimaden.insomniatracker.InsomniaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    @Shadow public abstract void sendMessage(Text message, boolean overlay);
    @Shadow public abstract int getSleepTimer();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("UnreachableCode")
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/util/Identifier;)V",
                    ordinal = 4,
                    shift = At.Shift.AFTER
            )
    )
    private void insomniatracker$notifyPlayerTired(CallbackInfo ci) {
        //noinspection DataFlowIssue
        int timeSinceRest = ((ServerPlayerEntity) (Object) this)
                .getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
        // Send this message only once while not in bed
        if (timeSinceRest == InsomniaTracker.getTicksUntilPhantoms()) {
            this.sendMessage(Text.translatable("insomnia.tired"), true);
        }
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;sleepTimer:I",
                    ordinal = 2
            )
    )
    private void insomniatracker$notifyPlayerRestedInBed(CallbackInfo ci) {
        // Send this message only once while in bed
        if (this.getSleepTimer() == 99) {
            this.sendMessage(Text.translatable("insomnia.rested"), true);
        }
    }

    @Inject(
            method = "wakeUp(ZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;updateSleepingPlayers()V",
                    shift = At.Shift.AFTER
            )
    )
    private void insomniatracker$notifyPlayerRestedOnWakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo ci) {
        // Send this message if the bed is left before the above message is sent
        if (this.getSleepTimer() < 100) {
            this.sendMessage(Text.translatable("insomnia.rested"), true);
        }
    }
}
