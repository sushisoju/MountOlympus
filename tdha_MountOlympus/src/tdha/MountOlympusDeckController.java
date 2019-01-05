package tdha;

import java.awt.event.MouseAdapter;


import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;

public class MountOlympusDeckController extends SolitaireReleasedAdapter {
	/** The game. */
	protected MountOlympus theGame;

	/** The Columns of interest. */
	protected Column columns[];

	/** The Deck of interest. */
	protected MultiDeck deck;

	/**
	 * KlondikeDeckController constructor comment.
	 */
	public MountOlympusDeckController(MountOlympus theGame, MultiDeck d, Column columns[]) {
		super(theGame);

		this.theGame = theGame;
		this.columns = columns;
		this.deck = d;
	}

	/**
	 * Coordinate reaction to the beginning of a Drag Event. In this case,
	 * no drag is ever achieved, and we simply deal upon the pres.
	 */
	public void mousePressed (java.awt.event.MouseEvent me) {

		// Attempting a DealFourCardMove
		Move m = new DealNineMove(deck, columns);
		if (m.doMove(theGame)) {
			theGame.pushMove (m);     // Successful Move
			theGame.refreshWidgets(); // refresh updated widgets.
		}
	}	
}
