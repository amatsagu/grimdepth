package amatsagu.grimdepth.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CreeperDestroyLightGoal extends MoveToBlockGoal {
	private final Creeper creeper;

	public CreeperDestroyLightGoal(Creeper creeper, double speedModifier) {
		super(creeper, speedModifier,
				GrimdepthConfig.INSTANCE.creepers.lightSearchHorizontalRange,
				GrimdepthConfig.INSTANCE.creepers.lightSearchVerticalRange);
		this.creeper = creeper;
	}

	@Override
	public boolean canUse() {
		if (!GrimdepthConfig.INSTANCE.creepers.huntLightSources) {
			return false;
		}
		if (this.creeper.getTarget() != null) {
			return false;
		}
		if (this.creeper.getY() > GrimdepthConfig.INSTANCE.general.deepslateYLevel) {
			return false;
		}
		if (this.creeper.level().canSeeSky(this.creeper.blockPosition())) {
			return false;
		}
		return super.canUse();
	}

	@Override
	public boolean canContinueToUse() {
		if (this.creeper.getTarget() != null) {
			return false;
		}
		return super.canContinueToUse();
	}

	@Override
	public void stop() {
		super.stop();
		if (this.creeper.getTarget() == null) {
			this.creeper.setSwellDir(-1);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (isReachedTarget() || this.creeper.distanceToSqr(
				this.blockPos.getX() + 0.5,
				this.blockPos.getY() + 0.5,
				this.blockPos.getZ() + 0.5) <= 4.0) {
			this.creeper.setSwellDir(1);
		}
	}

	@Override
	protected boolean isValidTarget(LevelReader level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!state.is(GrimdepthTags.CREEPER_LIGHT_SOURCES)) {
			return false;
		}
		if (state.getBlock() instanceof RedstoneLampBlock) {
			return state.getValue(RedstoneLampBlock.LIT);
		}
		if (state.getBlock() instanceof CopperBulbBlock) {
			return state.getValue(CopperBulbBlock.LIT);
		}
		return true;
	}
}
