package tdha;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.Pile;
import ks.common.view.CardView;
import ks.common.view.ColumnView;
import ks.common.view.Container;
import ks.common.view.PileView;
import ks.common.view.Widget;

public class MountOlympusFoundationController extends MouseAdapter {
	/** The Mount Olympus Game. */
	protected MountOlympus theGame;

	/** The specific Foundation pileView being controlled. */
	protected PileView src;
	/**
	 * MountOlympusFoundationController constructor comment.
	 */
	public MountOlympusFoundationController(MountOlympus theGame, PileView foundation) {
		super();

		this.theGame = theGame;
		this.src = foundation;
	}
	/**
	 * Coordinate reaction to the completion of a Drag Event.
	 * <p>
	 * A bit of a challenge to construct the appropriate move, because cards
	 * can be dragged both from the WastePile (as a CardView object) and the 
	 * BuildablePileView (as a ColumnView).
	 * @param me java.awt.event.MouseEvent
	 */
	public void mouseReleased(MouseEvent me) {
		Container c = theGame.getContainer();

		/** Return if there is no card being dragged chosen. */
		Widget draggingWidget = c.getActiveDraggingObject();
		if (draggingWidget == Container.getNothingBeingDragged()) {
			System.err.println ("FoundationController::mouseReleased() unexpectedly found nothing being dragged.");
			c.releaseDraggingObject();		
			return;
		}

		/** Recover the from BuildablePile OR waste Pile */
		Widget fromWidget = c.getDragSource();
		if (fromWidget == null) {
			System.err.println ("FoundationController::mouseReleased(): somehow no dragSource in container.");
			c.releaseDraggingObject();
			return;
		}

		// Determine the To Pile
		Pile foundation = (Pile) src.getModelElement();
		//Column column = (Column) fromWidget.getModelElement();

			// coming from a buildable pile [user may be trying to move multiple cards]
		Column fromColumn = (Column) fromWidget.getModelElement();

		/** Must be the ColumnView widget being dragged. */
		ColumnView columnView = (ColumnView) draggingWidget;
		Column col = (Column) columnView.getModelElement();
		if (col == null) {
			System.err.println ("FoundationController::mouseReleased(): somehow ColumnView model element is null.");
			c.releaseDraggingObject();			
			return;
		}

		// must use peek() so we don't modify col prematurely. Here is a HACK! Presumably
		// we only want the Move object to know things about the move, but we have to put
		// in a check to verify that Column is of size one. NO good solution that I can
		// see right now.
		if (col.count() != 1) {
			fromWidget.returnWidget (draggingWidget);  // return home
		} else {
			Move m = new MoveFoundationMove (fromColumn, foundation, col.peek());

			if (m.doMove (theGame)) {
				// Success
				theGame.pushMove (m);
			} else {
				fromWidget.returnWidget (draggingWidget);
			}
		}

//		/** Must be the CardView widget being dragged. */
//		CardView cardView = (CardView) draggingWidget;
//		Card theCard = (Card) cardView.getModelElement();
//		if (theCard == null) {
//			System.err.println ("FoundationController::mouseReleased(): somehow CardView model element is null.");
//			c.releaseDraggingObject();
//			return;
//		}
//
//		// must use peek() so we don't modify col prematurely
//		Move m = new MoveFoundationMove (column, foundation, theCard);
//		if (m.doMove (theGame)) {
//			// Success
//			theGame.pushMove (m);
//		} else {
//			fromWidget.returnWidget (draggingWidget);
//		}
	
		
		// release the dragging object, (this will reset dragSource)
		c.releaseDraggingObject();
		
		// finally repaint
		c.repaint();
	}
}
