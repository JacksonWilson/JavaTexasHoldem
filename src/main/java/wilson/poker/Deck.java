package wilson.poker;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import wilson.poker.Card.Rank;
import wilson.poker.Card.Suit;

public class Deck {
	private Card[] cards;
	private int size;
	
	public Deck() {
		this.cards = new Card[52];
		this.size = cards.length;
		
		int index = 0;
		for (Rank r : Rank.values()) {
			for (Suit s : Suit.values()) {
				cards[index++] = new Card(r, s);
			}
		}
		
		suffle();
	}
	
	public void suffle() {
		Random rnd = ThreadLocalRandom.current();
	    for (int i = cards.length - 1; i > 0; i--)
	    {
	      int randIndex = rnd.nextInt(i + 1);
	      Card tempCard = cards[randIndex];
	      cards[randIndex] = cards[i];
	      cards[i] = tempCard;
	    }
	    
	    size = cards.length;
	}
	
	public Card draw() {
		if (size == 0) {
			suffle();
		}
		return this.cards[--size];
	}
	
	public Card[] draw(int numCards) {
		Card[] ret = new Card[2];
		for (int i = 0; i < numCards; i++)
			ret[i] = draw();
		return ret;
	}
}
