package tdha;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.MultiDeck;

/**
 * Move up to 9 cards from to of the deck to top of each column  
 */
public class DealNineMove extends Move 
{
	protected MultiDeck deck;
	protected Column columns[];
	protected int numCardsDealt;
	
	public DealNineMove(MultiDeck deck, Column columns[]){
		this.deck = deck;
		this.columns = columns;
	}
	
	@Override
	public boolean doMove(Solitaire game) {
		if (!valid(game)){
			return false;
		}
		
		if (deck.count() < 9){
			numCardsDealt = deck.count();
		}
		
		else {
			numCardsDealt = 9;
		}
		for (int cardNum = 0; cardNum < numCardsDealt; cardNum++){
			Card card = deck.get();
			columns[cardNum].add(card);
			
			game.updateNumberCardsLeft(-1);
			
		}
		
		return true;
	}
	
	//Cannot undo this move
	@Override
	public boolean undo(Solitaire game) {
		for (int cardNum = numCardsDealt-1; cardNum >= 0; cardNum--){
			Card c = columns[cardNum].get();
			deck.add(c);
			game.updateNumberCardsLeft(+1);
		}
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		return !deck.empty();	
	}

}
