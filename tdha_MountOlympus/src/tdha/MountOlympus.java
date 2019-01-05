package tdha;


import java.awt.Dimension;

import ks.client.gamefactory.GameWindow;
import ks.client.gamefactory.skin.SkinCatalog;
import ks.common.controller.SolitaireMouseMotionAdapter;
import ks.common.controller.SolitaireReleasedAdapter;
//import ks.common.controller.SolitaireMouseMotionAdapter;
//import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.games.Solitaire;
import ks.common.games.SolitaireUndoAdapter;
//import ks.common.games.SolitaireUndoAdapter;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;
import ks.common.view.CardImages;
//import ks.common.view.CardImages;
import ks.common.view.ColumnView;
import ks.common.view.DeckView;
import ks.common.view.IntegerView;
import ks.common.view.PileView;
import ks.launcher.Main;
import ks.common.model.Card;
//import ks.common.model.Card;
import ks.common.model.Column;

public class MountOlympus extends Solitaire {
	
	MultiDeck deck;
	Pile aceFoundations[] = new Pile[8];
	Pile twoFoundations[] = new Pile[8];
	Column Columns[] = new Column[9];
	
	DeckView deckView;
	PileView aceFoundationView[] = new PileView[8];
	PileView twoFoundationView[] = new PileView[8];
	ColumnView columnView[] = new ColumnView[9];
	IntegerView numLeftView;
	IntegerView scoreView;
	
	
	@Override
	public String getName() {
		return "tdha - MountOlympus";
	}

	@Override
	public boolean hasWon() {
		return (this.getScore().getValue() == 88);
	}

	@Override
	public void initialize() {
		// initialize model
		initializeModel(getSeed());
		initializeView();
		initializeControllers();

	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension (890, 620);
	}
	
	public boolean intervalTwo(Column column, int start, int end) {
		int numCards = column.count();
		if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
			throw new IllegalArgumentException ("Stack::descending(start,end) received invalid [start:" + start + ", end:" + end + ") values.");
		
		int prevRank = column.peek(start).getRank();
		
		for (int i = start+1; i < end; i++) {
			// Any rank out of order, return FALSE
			if (column.peek(i).getRank() != prevRank-2) {
				return false;
			}
			prevRank = prevRank-2;
		}
		return true;
	}
	protected void initializeControllers() {
		// Initialize Controllers for DeckView
		deckView.setMouseAdapter(new MountOlympusDeckController (this, deck, Columns));
		deckView.setMouseMotionAdapter (new SolitaireMouseMotionAdapter(this));
		deckView.setUndoAdapter (new SolitaireUndoAdapter(this));
		
		for (int i = 0; i <= 7; i++){
			aceFoundationView[i].setMouseAdapter(new MountOlympusFoundationController (this, aceFoundationView[i]));
			aceFoundationView[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter(this));
			aceFoundationView[i].setUndoAdapter (new SolitaireUndoAdapter(this));
			twoFoundationView[i].setMouseAdapter(new MountOlympusFoundationController (this, twoFoundationView[i]));
			twoFoundationView[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter(this));
			twoFoundationView[i].setUndoAdapter (new SolitaireUndoAdapter(this));
		}
		
		for (int i = 0; i <= 8; i++){
			columnView[i].setMouseAdapter(new MountOlympusColumnController (this,columnView[i]));
			columnView[i].setMouseMotionAdapter (new SolitaireMouseMotionAdapter(this));
			columnView[i].setUndoAdapter (new SolitaireUndoAdapter(this));
		}
		
		// Ensure that any releases (and movement) are handled by the non-interactive widgets
		numLeftView.setMouseMotionAdapter (new SolitaireMouseMotionAdapter(this));
		numLeftView.setMouseAdapter (new SolitaireReleasedAdapter(this));
		numLeftView.setUndoAdapter (new SolitaireUndoAdapter(this));

		// same for scoreView
		scoreView.setMouseMotionAdapter (new SolitaireMouseMotionAdapter(this));
		scoreView.setMouseAdapter (new SolitaireReleasedAdapter(this));
		scoreView.setUndoAdapter (new SolitaireUndoAdapter(this));
		
	}
	
	
	protected void initializeModel (int seed) {
		deck = new MultiDeck(2);
		deck.create(seed);
		model.addElement (deck);   // add to our model (as defined within our superclass).
		
		Pile temp = new Pile();
		
		aceFoundations = new Pile[8];
		//Make Ace cards to put on the ace foundation pile
		Card ch = new Card (1, Card.HEARTS);
		Card cd = new Card (1, Card.DIAMONDS);
		Card cc = new Card (1, Card.CLUBS);
		Card cs = new Card (1, Card.SPADES);
		
		Pile tempPile = new Pile();
		tempPile.add(cs);
		tempPile.add(cc);
		tempPile.add(cd);
		tempPile.add(ch);
		tempPile.add(cs);
		tempPile.add(cc);
		tempPile.add(cd);
		tempPile.add(ch);
		
		//Add Ace foundations to the model
		for (int i=0; i<=7; i++) {	
		    aceFoundations[i] = new Pile("foundation" + i);
			aceFoundations[i].add(tempPile.get());
			model.addElement(aceFoundations[i]);
		}
		
		//Make Two cards to put to the two foundation pile.
		Card th = new Card (2, Card.HEARTS);
		Card td = new Card (2, Card.DIAMONDS);
		Card tc = new Card (2, Card.CLUBS);
		Card ts = new Card (2, Card.SPADES);

		tempPile.add(ts);
		tempPile.add(tc);
		tempPile.add(td);
		tempPile.add(th);
		tempPile.add(ts);
		tempPile.add(tc);
		tempPile.add(td);
		tempPile.add(th);
		
		//Add Two foundations to the model
		twoFoundations = new Pile[8];
		for (int j=0; j<=7; j++){
			
			twoFoundations[j] = new Pile("foundation" + (j+8));
			twoFoundations[j].add(tempPile.get());
			model.addElement(twoFoundations[j]);
		}
		
		//Remove all Ace's and Two's from the Deck
		for (int l=0; l<104; l++){
			Card card = deck.get();
			if ((!(card.getRank() == 1)) && (!(card.getRank() == 2))){
				temp.add(card);
			}
		}
		for (int m=0; m<88; m++){
			deck.add(temp.get());
		}

		
		//Add the Columns to the model
		Columns = new Column[9];
		for (int k=0; k<=8; k++){
			Card c = deck.get();
			Columns[k] = new Column();
			Columns[k].add(c);
			model.addElement(Columns[k]);
		}

		updateScore(0);
		updateNumberCardsLeft (79);
	} 


	protected void initializeView() {
		CardImages ci = getCardImages();
		deckView = new DeckView (deck);
		deckView.setBounds (10,10, ci.getWidth(), ci.getHeight());
		container.addWidget (deckView);

		// create Foundation PileViews, one after the other 
		for (int pileNum = 0; pileNum <=7; pileNum++) {
			aceFoundationView[pileNum] = new PileView (aceFoundations[pileNum]);
			aceFoundationView[pileNum].setBounds(100 + 9*pileNum + (pileNum)*ci.getWidth(), 10, ci.getWidth(), ci.getHeight());
			container.addWidget (aceFoundationView[pileNum]);
			
		}
		
		for (int pileNum = 0; pileNum <=7; pileNum++) {
			twoFoundationView[pileNum] = new PileView (twoFoundations[pileNum]);
			//aceFoundationView[pileNum].setBounds (10*pileNum + (pileNum)*ci.getWidth(), ci.getHeight() + 50, ci.getWidth(), 13*ci.getHeight());
			twoFoundationView[pileNum].setBounds(100 + 9*pileNum + (pileNum)*ci.getWidth(), 20 + ci.getHeight(), ci.getWidth(), ci.getHeight());
			container.addWidget (twoFoundationView[pileNum]);
					
		}
		
		
		//create the first half of the Columns
		for (int columnNum = 0; columnNum <=4; columnNum++) {
			columnView[columnNum] = new ColumnView (Columns[columnNum]);
			columnView[columnNum].setBounds((columnNum+1)*ci.getWidth() - 26 + 12*columnNum, 30 - 15*columnNum  + 3*ci.getHeight(), ci.getWidth(), 10*ci.getHeight());
			container.addWidget (columnView[columnNum]);
			
		}
		
		//create the second half of the Columns
		for (int columnNum = 5; columnNum <=8; columnNum++) {
			columnView[columnNum] = new ColumnView (Columns[columnNum]);
			columnView[columnNum].setBounds((columnNum+1)*ci.getWidth() - 26 + 12*columnNum, 15*(columnNum-6)  + 3*ci.getHeight(), ci.getWidth(), 10*ci.getHeight());
			container.addWidget (columnView[columnNum]);
			
		}
		
		//create Score View
		scoreView = new IntegerView (getScore());
		scoreView.setBounds (239+7*ci.getWidth(), 30, 100, 60);
		container.addWidget (scoreView);
		

		//Create Number of cards left View
		numLeftView = new IntegerView (getNumLeft());
		numLeftView.setFontSize (18);
		numLeftView.setBounds(10, ci.getHeight() + 10, ci.getWidth()*3/4,ci.getHeight()/2);
		container.addWidget (numLeftView);
		
	}

	
	
	/** Code to launch solitaire variation. */
	public static void main (String []args) {
		// Seed is to ensure we get the same initial cards every time.
		// Here the seed is to "order by suit."
		GameWindow gw = Main.generateWindow(new MountOlympus(), MultiDeck.OrderByRank);
		//gw.setSkin(SkinCatalog.PSYCHADELIC);

	}
}	

	

