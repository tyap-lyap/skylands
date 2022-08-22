package skylands.api;

import net.minecraft.entity.player.PlayerEntity;

public interface BalanceHandler {

	int getBalance(PlayerEntity player);

	void setBalance(PlayerEntity player, int balance);

	default void increaseBalance(PlayerEntity player, int increment) {
		int balance = this.getBalance(player);
		balance = balance + increment;
		this.setBalance(player, balance);
	}

	default void decreaseBalance(PlayerEntity player, int decrement) {
		int balance = this.getBalance(player);
		balance = balance - decrement;
		this.setBalance(player, balance);
	}
}
