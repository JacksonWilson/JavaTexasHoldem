package wilson.poker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
	public static final double SMALL_BLIND = 10;
	public static final double BIG_BLIND = SMALL_BLIND * 2;
	private Double currentBet;
	private int buttonPlayer;
	private int currentPlayer;
	private Pot pot;
	private List<Player> players;
	private List<Card> communityCards;
	private Deck deck;

	public Game() {
		this.currentPlayer = this.buttonPlayer = 0;
		this.pot = new Pot();
		this.deck = new Deck();
		this.communityCards = new ArrayList<>();
	}
	
	public Game(List<Player> initialPlayers) {
		this();
		this.players = initialPlayers;
	}

	public Pot getPot() {
		return pot;
	}

	public List<Player> getPlayers() {
		return players;
	}
	
	private Player getNextPlayer() {
		if (++currentPlayer == players.size())
			currentPlayer = 0;
		return getCurrentPlayer();
	}
	
	private Player getCurrentPlayer() {
		return players.get(currentPlayer);
	}
	
	private Player getButtonPlayer() {
		return players.get(buttonPlayer);
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public List<Card> getCommunityCards() {
		return communityCards;
	}

	public void setCommunityCards(List<Card> communityCards) {
		this.communityCards = communityCards;
	}
	
	public Deck getDeck() {
		return deck;
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

	public String playersToString() {
		String ret = "Players: {\n";
		
		for (Player p : players) {
			ret += "\t" + (p.equals(getCurrentPlayer()) ? "> " : "")  + p.toString() + (p.equals(getButtonPlayer()) ? "(Button)" : "") + "\n";
		}
		ret += "}";

		return ret;
	}
	
	@Override
	public String toString() {
		String ret = "Community Cards: [";
		if (communityCards.size() > 0) {
			for (Card c : communityCards) {
				ret += c + ", ";
			}
			ret = ret.substring(0, ret.length() - 2);
		}
		ret += "]\nCurrent Pot|Bet: " + NumberFormat.getCurrencyInstance().format(pot.getAmount()) + "|" + NumberFormat.getCurrencyInstance().format(currentBet) + "\n" + playersToString();
		
		return ret;
	}

    private static void printHeader(String str) {
        System.out.print("--------------------------------------------------------------------------------\n|");
        int spaces = 78 - str.length();
        for (int i = 0; i < spaces / 2; i++)
            System.out.print(" ");
        System.out.print(str);
        for (int i = 0; i < spaces / 2 + spaces % 2; i++)
            System.out.print(" ");
        System.out.println("|\n--------------------------------------------------------------------------------");
    }

	private boolean doneBetting() {
		for (Player p : players) {
			if (!p.isFolded() && p.getCurrentBet() != currentBet)
				return false;
		}
		return true;
	}

	private void playBettingRound() {
		do {
			if (getNextPlayer().isFolded())
				continue;
			System.out.println(toString());

			switch (getCurrentPlayer().getBetResponse(currentBet)) {
			case CALL:
				pot.add(getCurrentPlayer().payBet(currentBet));
				break;
			case RAISE:
				pot.add(getCurrentPlayer().payBet(currentBet));
				break;
			case FOLD:
				break;
			case CHECK:
				break;
			}
			System.out.println("\n");
		} while (!doneBetting());
	}
	
	// All of the game logic is here.
	public void start() {
		printHeader("Texas Holdem");
		currentBet = BIG_BLIND;
		
		pot.add(getNextPlayer().payBet(SMALL_BLIND)); // small blind
		pot.add(getNextPlayer().payBet(BIG_BLIND)); // big blind
		
		// Deal hole cards
		for (Player p : players)
			p.setHand(deck.draw(2));

		// First betting round
		printHeader("First betting round");
		playBettingRound();

		// The Flop
		for (int i = 0; i < 3; i++) {
			communityCards.add(deck.draw());
		}
		
		// Second betting round
		printHeader("Second betting round");
		playBettingRound();
		
		// The Turn
		communityCards.add(deck.draw());
		
		// Third betting round
		printHeader("Third betting round");
		playBettingRound();
		
		// The River
		communityCards.add(deck.draw());

		
		// Fourth betting round
		printHeader("Fourth betting round");
		playBettingRound();
		
		// The Show down
		printHeader("The Showdown");
		List<Player> winners = players.stream().filter(p -> !p.isFolded()).collect(Collectors.toList());
		pot.split(winners);

		System.out.println("\nFinal winner each receives $" + pot.getAmount() / winners.size() + " from the $" + pot.getAmount() + " pot.");
		winners.stream().forEach(System.out::println);
	}
}
