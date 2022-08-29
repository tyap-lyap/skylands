package skylands.api;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Represents player balance handler
 * probably can be used for compatibility
 * with other economy mods
 */
public interface BalanceHandler {

	double getBalance(PlayerEntity player);

	void setBalance(PlayerEntity player, double balance);

	default void increaseBalance(PlayerEntity player, double increment) {
		double balance = this.getBalance(player);
		balance = balance + increment;
		this.setBalance(player, balance);
	}

	default void decreaseBalance(PlayerEntity player, double decrement) {
		double balance = this.getBalance(player);
		balance = balance - decrement;
		this.setBalance(player, balance);
	}
}
