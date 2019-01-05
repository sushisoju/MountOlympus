package tdha;

import ks.common.games.Solitaire;
import ks.common.model.BuildablePile;
import ks.common.model.Column;
import ks.common.model.Stack;


/**
 * Represents the move of a column between Piles.
 * <p>
 * There are two scenarios when constructing a <code>moveColumnMove</code>
 * object. (1) The move has not yet taken place (but is being proposed); in this
 * case, there is no Column being moved, only a number of cards. (2) The move has
 * already occured, so the Column is available, and the number of cards can easily
 * be calculated.
 * <p>
 * @author: Trang
 */
public class MoveColumnMove extends ks.common.model.Move {
	/** From Column. */
	protected Column from;

	/** To Column. */
	protected Column to;

	/** Number of cards to move. */
	protected int numCards;
	
	/** Column being moved. */
	protected Column columnBeingDragged;
/**
 * MoveCardToFoundationmove constructor.
 * <p>
 * If the move has already been made, then <code>columnBeingDragged</code> is non-null, and
 * <code>numCards == columnBeingDragged.count()</code> If the move has not been made, then
 * only the number of cards to be moved is known, and <code>columnBeingDragged</code> is null.
 */
public MoveColumnMove(Column from, Column to, Column columnBeingDragged, int numCards) {
	super(); 

	this.from = from;
	this.to = to;
	this.columnBeingDragged = columnBeingDragged;
	this.numCards = numCards;
}
/**
 * Each move should knows how to execute itself.
 * <p>
 * @param ks.game.Solitaire   the game being played.
 * @return boolean
 */
public boolean doMove (Solitaire theGame) {

	// VALIDATE:
	if (valid (theGame) == false)
		return false;

	// EXECUTE:
	if (columnBeingDragged == null) {
		from.select (numCards);
		Stack st = from.getSelected ();
		to.push (st);
	} else {
		// already have the column
		to.push (columnBeingDragged);
	}

	return true;
}
/**
 * undo of a MoveColumnMove.
 */
public boolean undo(ks.common.games.Solitaire game) {
	// VALIDATE:
	if (to.count() < numCards) return false;
	
	// We know the number of cards moved, so we select them, extract the
	// stack, and move them all back to the fromPile.
	to.select (numCards);
	Stack st = to.getSelected ();
	from.push (st);

	return true;
}
/**
 * Action for MountOlympus: Move a Column from one Column to another
 *
 * @param game ks.game.Solitaire
 */
public boolean valid (Solitaire game) {
	// VALIDATION:
	boolean validation = false;

	// If move hasn't happened yet, we must extract the desired column to move.
	Column targetColumn;
	if (columnBeingDragged == null) {
		targetColumn = new Column();
		for (int i = numCards; i>=1; i--) {
			targetColumn.add (from.peek(from.count() - i));
		}
	} else {
		targetColumn = columnBeingDragged;
	}
	
	//   moveColumnBetweenPiles(Column col, Column to) : to.empty()
	if (to.empty())
		validation = false;

	// 	  moveColumnBetweenPiles(Column col,BuildablePile to) : not col.empty() and col.bottom() == to.rank() - 1 and to.peek().faceUp() == true
	if (!to.empty() && (targetColumn.peek(0).getRank() == to.rank() - 2) && (targetColumn.peek(0).getSuit() == to.peek().getSuit())) 
		validation = true;

	return validation;
}
}
