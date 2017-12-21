package wilson.poker;

import java.util.List;

public class Pot {
	private double amount;
	
	public Pot() {
		this.amount = 0.0;
	}
	
	public void split(List<Player> players) {
		for (Player p : players) {
			p.addMoney(amount / players.size());
		}
	}

	public void payoutTo(Player p) {
		p.addMoney(amount);
		clear();
	}
	
	public void payoutTo(List<Player> players) {
		split(players);
		clear();
	}
	
	public void add(double amount) {
		this.amount += amount;
	}
	
	private void clear() {
		setAmount(0.0);
	}

	public double getAmount() {
		return amount;
	}

	private void setAmount(double amount) throws IllegalArgumentException {
		if (amount < 0.0)
			throw new IllegalArgumentException("Pot cannot have a negative amount of money.");
		this.amount = amount;
	}
}
