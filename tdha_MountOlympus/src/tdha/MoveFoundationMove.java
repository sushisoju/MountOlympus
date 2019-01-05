package tdha;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;

/**
 * Represents the move of a card to the Foundation from a Column
 * @author: Trang
 */

public class MoveFoundationMove extends Move{

	protected Column column;
	protected Pile foundation;
	protected Card cardBeingDragged;

	public MoveFoundationMove(Column from, Pile to, Card card){
		this.column = from;
		this.foundation = to;
		this.cardBeingDragged = card;
	}

	@Override
	public boolean doMove(Solitaire game) {
		if (!valid(game)){
			return false;
		}
		if (cardBeingDragged == null)
			foundation.add (column.get());
		else
			foundation.add(cardBeingDragged);
		game.updateScore(+1);

		return true;
	}


	@Override
	public boolean undo(Solitaire game) {
		column.add(foundation.get());
		game.updateScore(-1);
		return true;
	}

	@Override
	public boolean valid(Solitaire game) {
		Card topCard = foundation.peek();
		boolean validation = false;
		// If draggingCard is null, then no action has yet taken place.
		if (cardBeingDragged == null) {
			cardBeingDragged = column.peek();
			// moveWasteToFoundation(buildablePile,pile) : not foundation.empty() and not buildablePile.empty() and 
			if (!column.empty() && (cardBeingDragged.getRank() == foundation.rank() + 2) && (cardBeingDragged.getSuit() == foundation.suit())){
				validation = true;
				cardBeingDragged = column.get();
			}
				

		} else {
			
		return ((topCard.getRank() == cardBeingDragged.getRank() -2) && (topCard.getSuit() == cardBeingDragged.getSuit()));	
		}
		
		return validation;
	}
}
		
//		// If draggingCard is null, then no action has yet taken place.
//		if (cardBeingDragged == null) {
//			
//			cardBeingDragged = column.get();
//			// moveWasteToFoundation(buildablePile,pile) : not foundation.empty() and not buildablePile.empty() and 
//			//if (!column.empty() && (column.rank() == foundation.rank() + 2) && (column.suit() == foundation.suit()))
//				//validation = true;
//
//		
//		} else {
//			// the action must have taken place, so act on the card.
//
//			// moveWasteToFoundation(waste,pile) : not foundation.empty() and not waste.empty() and 
//			if ((cardBeingDragged.getRank() == foundation.rank() + 2) && (cardBeingDragged.getSuit() == foundation.suit()))
//				validation = true;
//
//				// moveWasteToFoundation(waste,pile) : foundation.empty() and card.rank() == ACE
//			
//		}
//
//		return validation;
//	}
//	}
