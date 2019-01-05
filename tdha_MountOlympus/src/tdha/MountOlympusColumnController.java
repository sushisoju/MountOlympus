package tdha;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import tdha.MoveColumnMove;
import ks.common.controller.SolitaireReleasedAdapter;

import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.Pile;
import tdha.MountOlympus;
import ks.common.view.CardView;
import ks.common.view.ColumnView;
import ks.common.view.Container;
import ks.common.view.Widget;


public class MountOlympusColumnController extends MouseAdapter {
	protected MountOlympus theGame;

	protected ColumnView src;

	public MountOlympusColumnController(MountOlympus theGame, ColumnView src) {
		super();
		this.theGame = theGame;
		this.src = src;
	}
	
	
	/**
	 * Try to play the faceup card directly to the foundation.
	 *
	 * @param me java.awt.event.MouseEvent
	 */
	public void mouseClicked(MouseEvent me) {
		if(me.getClickCount() > 1) {

			// Point to our underlying model element.
			Column col = (Column) src.getModelElement();

			// See if we can move this one card.
			boolean moveMade = false;
			for (int f = 0; f <16; f++) {
				Pile fp = (Pile) theGame.getModelElement ("foundation" + f);
				Move m = new MoveFoundationMove (col, fp, null);
				if (m.doMove(theGame)) {

					// Success! Add this move to our history.
					theGame.pushMove (m);

					moveMade = true;
					theGame.refreshWidgets();
					break;
				}
			}

			if (!moveMade) {
				java.awt.Toolkit.getDefaultToolkit().beep();
				return; // announce our displeasure			
			}
		}
	}
	public void mousePressed(MouseEvent me) {
		// The container manages several critical pieces of information; namely, it
		// is responsible for the draggingObject; in our case, this would be a CardView
		// Widget managing the card we are trying to drag between two piles.
		Container c = theGame.getContainer();

		/** Return if there is no card to be chosen. */
		Column column = (Column) src.getModelElement();
		if (column.count() == 0) {
			//c.releaseDraggingObject();
			return;
		}
		// Get a column of cards to move from the BuildablePileView
		// Note that this method will alter the model for BuildablePileView if the condition is met.
		ColumnView colView = src.getColumnView (me);

		// an invalid selection (either all facedown, or not in faceup region)
		if (colView == null) {
			return;
		}

		// Check conditions
		Column col = (Column) colView.getModelElement();
		if (col == null) {
			System.err.println ("BuildablePileController::mousePressed(): Unexpectedly encountered a ColumnView with no Column.");
			return; // sanity check, but should never happen.
		}

		// verify that Column has desired Klondike Properties to move
		if ((!theGame.intervalTwo(col,0,col.count())) || (!col.sameSuit())) {
			column.push (col);
			java.awt.Toolkit.getDefaultToolkit().beep();
			return; // announce our displeasure
		}

		// If we get here, then the user has indeed clicked on the top card in the PileView and
		// we are able to now move it on the screen at will. For smooth action, the bounds for the
		// cardView widget reflect the original card location on the screen.
		Widget w = c.getActiveDraggingObject();
		if (w != Container.getNothingBeingDragged()) {
			System.err.println ("BuildablePileController::mousePressed(): Unexpectedly encountered a Dragging Object during a Mouse press.");
			return;
		}

		// Tell container which object is being dragged, and where in that widget the user clicked.
		c.setActiveDraggingObject (colView, me);

		// Tell container which BuildablePileView is the source for this drag event.
		c.setDragSource (src);

		// we simply redraw our source pile to avoid flicker,
		// rather than refreshing all widgets...
		src.redraw();
	}
/*		// Get a card to move from PileView. Note: this returns a CardView.
		// Note that this method will alter the model for PileView if the condition is met.
		CardView cardView = src.getCardViewForTopCard (me);

		// an invalid selection of some sort.
		if (cardView == null) {
			c.releaseDraggingObject();
			return;
		}

		// If we get here, then the user has indeed clicked on the top card in the PileView and
		// we are able to now move it on the screen at will. For smooth action, the bounds for the
		// cardView widget reflect the original card location on the screen.
		Widget w = c.getActiveDraggingObject();
		if (w != Container.getNothingBeingDragged()) {
			System.err.println ("ColumnController::mousePressed(): Unexpectedly encountered a Dragging Object during a Mouse press.");
			return;
		}

		// Tell container which object is being dragged, and where in that widget the user clicked.
		c.setActiveDraggingObject (cardView, me);

		// Tell container which source widget initiated the drag
		c.setDragSource (src);

		// The only widget that could have changed is ourselves. If we called refresh, there
		// would be a flicker, because the dragged widget would not be redrawn. We simply
		// force the WastePile's image to be updated, but nothing is refreshed on the screen.
		// This is patently OK because the card has not yet been dragged away to reveal the
		// card beneath it.  A bit tricky and I like it!
		src.redraw();

	}*/
	
	/**
	 * Coordinate reaction to the completion of a Drag Event.
	 * <p>
	 * A bit of a challenge to construct the appropriate move, because cards
	 * can be dragged both from the WastePile (as a CardView widget) and the 
	 * BuildablePileView (as a ColumnView widget).
	 * <p>
	 * @param me java.awt.event.MouseEvent
	 */
	public void mouseReleased(MouseEvent me) {
		Container c = theGame.getContainer();

		/** Return if there is no card being dragged chosen. */
		Widget w = c.getActiveDraggingObject();
		if (w == Container.getNothingBeingDragged()) {
			System.err.println ("ColumnController::mouseReleased() unexpectedly found nothing being dragged.");
			c.releaseDraggingObject();		
			return;
		}

		/** Recover the from BuildablePile OR waste Pile */
		Widget fromWidget = c.getDragSource();
		if (fromWidget == null) {
			System.err.println ("ColumnController::mouseReleased(): somehow no dragSource in container.");
			c.releaseDraggingObject();
			return;
		}

		// Determine the To Pile
		Column to = (Column) src.getModelElement();
		//Column column = (Column) fromWidget.getModelElement();



			// coming from a buildable pile [user may be trying to move multiple cards]
		// Must be a ColumnView widget being dragged.
		ColumnView columnView = (ColumnView) w;
		Column col = (Column) columnView.getModelElement();
		if (col == null) {
			System.err.println ("ColumnController::mouseReleased(): somehow ColumnView model element is null.");
			return;
		}

		if (fromWidget == src) {
			to.push (col);   // simply put right back where it came from. No move
		} else {
			/*if (col.count() == 1){
		
				Move m = new MoveColumnMove (column, to, col, 1);

				if (m.doMove (theGame)) {
					// Success
					theGame.pushMove (m);
				} else {
					fromWidget.returnWidget (w);

				}
			}*/
			Column from = (Column) fromWidget.getModelElement();
			Move m = new MoveColumnMove (from, to, col, col.count());

			if (m.doMove (theGame)) {
				// Successful move! add move to our set of moves
				theGame.pushMove (m);
			} else {
				// Invalid move. Restore to original column. NO MOVE MADE
				from.push (col);
			}
		}		
	/*else {
		// Must be from the WastePile
		CardView cardView = (CardView) w;
		Card theCard = (Card) cardView.getModelElement();
		if (theCard == null) {
			System.err.println ("BuildablePileController::mouseReleased(): somehow CardView model element is null.");
			return;
		}

		Pile wastePile = (Pile) fromWidget.getModelElement();
		Move m = new MoveWasteToPileMove (wastePile, theCard, to);
		if (m.doMove (theGame)) {
			// Successful move! add move to our set of moves
			theGame.pushMove (m); 
		} else { 
			// Invalid move. Restore to original waste pile. NO MOVE MADE
			wastePile.add (theCard);
		}
	}*/
//release the dragging object, (container will reset dragSource)
	c.releaseDraggingObject();
	
	c.repaint();
	}
	
}

		
//		/** Must be the ColumnView widget being dragged. */
//		ColumnView columnView = (ColumnView) draggingWidget;
//		Column col = (Column) columnView.getModelElement();
//		if (col == null) {
//			System.err.println ("FoundationController::mouseReleased(): somehow ColumnView model element is null.");
//			c.releaseDraggingObject();			
//			return;
//		}
//
//		// must use peek() so we don't modify col prematurely. Here is a HACK! Presumably
//		// we only want the Move object to know things about the move, but we have to put
//		// in a check to verify that Column is of size one. NO good solution that I can
//		// see right now.
//		if (col.count() != 1) {
//			fromWidget.returnWidget (draggingWidget);  // return home
//		} else {
//			Move m = new MoveColumnMove (column, to, col,col.count() );
//
//			if (m.doMove (theGame)) {
//				// Success
//				theGame.pushMove (m);
//			} else {
//				fromWidget.returnWidget (draggingWidget);
//			}
//		}
//		
//		
//		// release the dragging object, (container will reset dragSource)
//				c.releaseDraggingObject();
//				
//				c.repaint();
//	}
//}
//	
				
		


