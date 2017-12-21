package wilson.poker;

public class Card {
	enum Rank {
		TWO(2),
		THREE(3),
		FOUR(4),
		FIVE(5),
		SIX(6),
		SEVEN(7),
		EIGHT(8),
		NINE(9),
		TEN(10),
		JACK(11, "J"),
		QUEEN(12, "Q"),
		KING(13, "K"),
		ACE(14, "A");
		
		private int value;
		private String initial;
		
		Rank(int value) {
			this(value, Integer.toString(value));
		}

		Rank(int value, String initial) {
			this.value = value;
			this.initial = initial;
		}
		
		public int getValue() {
			return value;
		}
		
		public String getInitial() {
			return initial;
		}
	}
	
	enum Suit {
		HEARTS("H"),
		DIAMONDS("D"),
		SPADES("S"),
		CLUBS("C");
		
		private String inital;
		
		Suit(String inital) {
			this.inital = inital;
		}
		
		public String getInitial() {
			return inital;
		}
	}
	
	private Rank rank;
	private Suit suit;
	
	public Card(Rank rank, Suit suit) {
		this.setRank(rank);
		this.setSuit(suit);
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public void setSuit(Suit suit) {
		this.suit = suit;
	}
	
	@Override
	public String toString() {
		return rank.getInitial() + suit.getInitial();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Card
			&& ((Card)obj).rank.equals(this.rank)
			&& ((Card)obj).suit.equals(this.suit);
	}
}
