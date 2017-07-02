package wilson.poker;

import java.util.Scanner;

public class Player {
	private String name;
	private double money;
	private Card[] hand;
	private double currentBet;
	private boolean folded;

	public Player(String name, double money) {
		this.name = name;
		this.money = money;
		this.currentBet = 0.0;
		this.folded = false;
	}
	
	public Player(String name) {
		this(name, 0.0);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasMoney(double amount) {
		return money >= amount;
	}

	public void addMoney(double amount) {
		money += amount;
	}
	
	public double pay(double amount) {
		double ret = money > amount ? amount : money;
		if (money < amount)
			folded = true;
		money -= ret;
		return ret;
	}

	public double payBet(double currentBet) {
		double ret = currentBet - this.currentBet;
		this.currentBet = currentBet;
		return pay(ret);
	}

	public Card[] getHand() {
		return hand;
	}
	
	public void clearHand() {
		hand = null;
	}

	public void setHand(Card[] hand) {
		this.hand = hand;
	}

	public void setHand(Card c1, Card c2) {
		setHand(new Card[]{ c1, c2 });
	}

	public double getCurrentBet() {
		return currentBet;
	}

	public void resetForNextHand() {
		if (money > 0.0) {
			this.folded = false;
			this.currentBet = 0.0;
		}
	}
	
	@Override
	public String toString() {
		return name + " ($" + money + "|"+ currentBet + "):" + " [" + hand[0] + ", " + hand[1] + "] " + (folded ? "X" : "");
	}
	
	public enum BetResponse {
		CHECK,
		CALL,
		RAISE,
		FOLD
	}

	public boolean isFolded() {
		return folded;
	}

	public BetResponse getBetResponse(Double currentBet) {
		Scanner scanner = Driver.scanner;
		String input = null;
		double bet;
		BetResponse responseType = null;
		
		boolean validInput = false;
		do {
			System.out.print("[1]" + (this.currentBet == currentBet ? "Check" : "Call") + ", [2]Raise, or [3]Fold: ");

			if (scanner.hasNextLine())
				input = scanner.nextLine();
			if (!input.isEmpty()) {
				switch (input.charAt(0)) {
				case '1':
					if (this.currentBet == currentBet) {
						System.out.print("Are you sure you want to check? (Y/N): ");
						if (scanner.hasNextLine())
							input = scanner.nextLine();
						if (!input.isEmpty() && input.toUpperCase().charAt(0) == 'Y') {
							responseType = BetResponse.CHECK;
							validInput = true;
						}
					} else {
						System.out.print("Call for $" + (currentBet - this.currentBet) + "? (Y/N): ");
						if (scanner.hasNextLine())
							input = scanner.nextLine();
						if (!input.isEmpty() && input.toUpperCase().charAt(0) == 'Y') {
							responseType = BetResponse.CALL;
							validInput = true;
						}
					}
					break;
				case '2':
					do {
						System.out.print("Enter raise amount (or press [Enter] to change bet type): ");
						if (scanner.hasNextLine())
							input = scanner.nextLine();
						if (input.isEmpty()) {
							break;
						} else {
							try {
								bet = Double.parseDouble(input);
								if (bet > currentBet.doubleValue()) {
									System.out.print("Are you sure you want to raise $" + bet +  " for $" + (currentBet - this.currentBet + bet) + "? (Y/N): ");
									if (scanner.hasNextLine())
										input = scanner.nextLine();
									if (!input.isEmpty() && input.toUpperCase().charAt(0) == 'Y') {
										responseType = BetResponse.RAISE;
										currentBet += bet;
										validInput = true;
									}
								}
								else
									System.out.println("Enter a value greater than the current bet ($" + currentBet + ").");
							} catch (NumberFormatException e) {
								System.out.println("Bet ammount must be a number.");
							}
						}

					} while (!validInput);
					break;
				case '3':
					System.out.print("Are you sure you want to fold? (Y/N): ");
					if (scanner.hasNextLine())
						input = scanner.nextLine();
					if (!input.isEmpty() && input.toUpperCase().charAt(0) == 'Y') {
						responseType = BetResponse.FOLD;
						this.folded = true;
						validInput = true;
					}
					break;
				default:
				}
			}
		} while (!validInput);
		return responseType;
	}
	
	enum HandType {
		HighCard,
		Pair,
		TwoPair,
		ThreeOfKind,
		Stright,
		Flush,
		FullHouse,
		FourOfKind,
		StrightFlush,
		RoyalFlush
	}
	
	
	/*
	Evaluate best hand, set it to 'bestHand'
	return HandType
	
	// Evaluation Method //
	1. Does any single player have a straight flush? If yes, he is the winner.
		Do multiple players have a straight flush? If yes, the winner is the one with the highest card.
			If multiple people share the highest card (obviously in a different suit) they split the pot.
			(Note: Royal flush is excluded because it's just a special straight flush that no one else can beat.)
	2. Does any single player have 4 of a kind? If yes, he is the winner.
		Do multiple players have 4 of a kind? If yes, the one with the highest 'set of 4' is the winner.
			If multiple players have the highest set of 4 (which is not achievable with a standard poker deck, but is with a double deck or community cards),
			the one with the highest kicker (highest card not in the set of 4) is the winner. If this card is the same, they split the pot.
	3. Does any single player have a full house? If yes, he is the winner.
		Do multiple players have full houses? If yes, then keeping in mind that a full house is a 3-set and a 2-set,
			the player with the highest 3-set wins the pot. If multiple players share the highest 3-set
			(which isn't possible without community cards like in hold 'em, or a double deck) then the player with the highest 2-set is the winner.
			If the 2-set and 3-set is the same, those players split the pot.
	4. Does any single player have a flush? If yes, he is the winner.
		Do multiple players have a flush? If yes, the player with a flush with the highest unique card is the winner.
			This hand is similar to 'high card' resolution, where each card is effectively a kicker.
			Note that a flush requires the same suit, not just color. While the colors used on the suit are red and black, two each, there's nothing to that connection.
			A club is no more similar to a spade than it is to a heart - only suit matters.
			The colors are red and black for historical purposes and so the same deck can be played for other games where that might matter.
	5. Does any single player have a straight? If yes, he wins the pot.
		Do multiple players have straights? If so, the player with the highest straight wins.
			(a-2-3-4-5 is the lowest straight, while 10-j-q-k-a is the highest straight.) If multiple players share the highest straight, they split the pot.
	6. Does any single player have a 3 of a kind? If yes, he wins the pot.
		Do multiple players have 3 of a kind? If yes, the player with the highest 3-set wins the pot.
			If multiple players have the highest 3-set, the player with the highest kicker wins the pot.
			If multiple players tie for highest 3-set and highest kicker, the player with the next highest kicker wins the pot.
			If the players tie for the highest 3-set, highest kicker, and highest second kicker, the players split the pot.
	7. Does any single player have 2-pair? If yes, he wins the pot.
		Do multiple players have 2-pair? If yes, the player with the highest pair wins the pot.
			If multiple players tie for the highest pair, the player with the second highest pair wins the pot.
			If multiple players tie for both pairs, the player with the highest kicker wins the pot. 
			If multiple players tie for both pairs and the kicker, the players split the pot.
	8. Does any single player have a pair? If yes, he wins the pot.
		Do multiple players have a pair? If yes, the player with the highest pair win.
			If multiple players have the highest pair, the player with the highest kicker wins.
			Compare second and third kickers as expected to resolve conflicts, or split if all three kickers tie.
	9. At this point, all cards are kickers, so compare the first, second, third, fourth, and if necessary, fifth highest cards in order
		until a winner is resolved, or split the pot if the hands are identical.
	 */
	public static HandType evaluateHand(Card[] handCards, Card[] communityCards, Card[] bestHand) {
		return HandType.HighCard;
	}
}
