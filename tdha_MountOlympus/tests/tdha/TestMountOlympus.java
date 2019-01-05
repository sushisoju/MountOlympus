package tdha;

import java.awt.event.MouseEvent;


import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Deck;
import ks.common.model.MultiDeck;
import ks.common.view.ColumnView;
import ks.launcher.Main;
import ks.tests.KSTestCase;
import ks.tests.model.ModelFactory;

public class TestMountOlympus extends KSTestCase {

	MountOlympus mo;
	
	// window for game.
	GameWindow gw;
	int overlap;
	
	protected void setUp() {
		mo = new MountOlympus();
		
		// Because solitaire variations are expected to run within a container, we need to 
		// do this, even though the Container will never be made visible. Note that here
		// we select the "random seed" by which the deck will be shuffled. We use the 
		// special constant Deck.OrderBySuit (-2) which orders the deck from Ace of clubs
		// right to King of spades.
		gw = Main.generateWindow(mo, MultiDeck.OrderByRank); 
		overlap = gw.getGameContainer().getCardImages().getOverlap();
	}
	
	// clean up properly
	protected void tearDown() {
		gw.setVisible(false);
		gw.dispose();
	}
	
	//Initialize
	public void testT() {
		ModelFactory.init(mo.Columns[0], "5H 4S 3C");
		
		assertEquals ("3C", mo.Columns[0].peek().toString());
		
		// press a bit offset into the widget.
		MouseEvent dcl = createDoubleClicked (mo, mo.columnView[0], 0, 3*overlap);
		mo.columnView[0].getMouseManager().handleMouseEvent(dcl);
				
		assertEquals ("4S", mo.Columns[0].peek().toString());
	}

	
	//Test the deal nine move
	
	public void testDealNineMove(){
		//MountOlympus mo = new MountOlympus();
		//moWindow gw = Main.generateWindow(mo, MultiDeck.OrderBySuit);
	
	    Card topCardDeck = mo.deck.peek();
	    DealNineMove dnm = new DealNineMove(mo.deck, mo.Columns);
	    
	    assertTrue(dnm.valid(mo));
	    
	    dnm.doMove(mo);

	    assertEquals(70, mo.getNumLeft().getValue());
	    assertEquals(70, mo.deck.count());
	    assertEquals(topCardDeck, mo.Columns[0].peek());
	    dnm.undo(mo);
	    assertEquals(new Card(Card.JACK, Card.HEARTS), mo.deck.peek());
	    mo.deck.removeAll();
	    assertFalse (dnm.valid(mo));
		assertFalse (dnm.doMove(mo));
	    
	}
	

	//Test Foundation Controller
	public void testFoundationController() {

		// first create a mouse event
		MouseEvent pr = createPressed (mo, mo.deckView, 0, 0);
		for (int i = 0; i < 4; i++) {
			mo.deckView.getMouseManager().handleMouseEvent(pr);
		}
		
		// top card of column 5 is now a three. Click to place it home
		
		MouseEvent dbl = createDoubleClicked(mo, mo.columnView[5], 0, 4 *overlap);
		mo.columnView[5].getMouseManager().handleMouseEvent(dbl);
		
		assertEquals (2, mo.aceFoundations[0].count());


		pr = createPressed (mo, mo.columnView[4], 0, 4* overlap);
		mo.columnView[4].getMouseManager().handleMouseEvent(dbl);
		MouseEvent rl = createReleased (mo, mo.aceFoundationView[3], 0, 0);
		mo.aceFoundationView[3].getMouseManager().handleMouseEvent(rl);
		assertEquals (2, mo.aceFoundations[3].count());
		assertEquals(new Card(Card.SIX, Card.CLUBS), mo.Columns[4].peek());
		
		pr = createPressed (mo, mo.columnView[1], 0, 4* overlap);
		mo.columnView[1].getMouseManager().handleMouseEvent(pr);
		MouseEvent rel = createReleased (mo, mo.twoFoundationView[0], 0, 0);
		mo.twoFoundationView[0].getMouseManager().handleMouseEvent(rel);
		assertEquals (2, mo.twoFoundations[0].count());
		assertEquals(new Card(Card.FOUR, Card.HEARTS), mo.twoFoundations[0].peek());
		
		
		mo.undoMove();
		mo.undoMove();
		assertEquals (1, mo.aceFoundations[3].count());
		
		
		
	

	}

	public void testDeckController() {

		// Click on deck twice
		MouseEvent pr = createPressed (mo, mo.deckView, 0, 0);
		mo.deckView.getMouseManager().handleMouseEvent(pr);	
		MouseEvent pr2 = createPressed (mo, mo.deckView, 0, 0);
		
		mo.deckView.getMouseManager().handleMouseEvent(pr2);	
		assertEquals (new Card(Card.NINE, Card.DIAMONDS), mo.Columns[0].peek());
		mo.undoMove();
		assertEquals (mo.deck.count(),70);
		mo.undoMove();
		assertEquals (mo.deck.count(),79);
		
	}
	
	public void testColumnController() {
		
		//first deal out nine cards
		MouseEvent deal = createPressed (mo, mo.deckView, 0, 0);
		mo.deckView.getMouseManager().handleMouseEvent(deal);	
		
		// create a mouse event
		MouseEvent pr = createPressed (mo, mo.columnView[8], 0, overlap);
		mo.columnView[8].getMouseManager().handleMouseEvent(pr);

		// drop on the first column
		MouseEvent rel = createReleased (mo, mo.columnView[0], 0, 0);
		mo.columnView[0].getMouseManager().handleMouseEvent(rel);

		assertEquals (3, mo.Columns[0].count());
		
		assertEquals (mo.Columns[0].peek(), new Card(Card.NINE, Card.HEARTS));
		assertEquals (mo.Columns[8].peek(), new Card(Card.JACK, Card.SPADES));
		
		/*Test move unit + move to empty column*/
		
		//Gradually move cards to another column:
		
		pr = createPressed (mo, mo.columnView[7], 0, overlap);
		mo.columnView[7].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[8], 0, 0);
		mo.columnView[8].getMouseManager().handleMouseEvent(rel);
		
		pr = createPressed (mo, mo.columnView[6], 0, overlap);
		mo.columnView[6].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[7], 0, 0);
		mo.columnView[7].getMouseManager().handleMouseEvent(rel);

		pr = createPressed (mo, mo.columnView[5], 0, overlap);
		mo.columnView[5].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[6], 0, 0);
		mo.columnView[6].getMouseManager().handleMouseEvent(rel);

		pr = createPressed (mo, mo.columnView[4], 0, overlap);
		mo.columnView[4].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[5], 0, 0);
		mo.columnView[5].getMouseManager().handleMouseEvent(rel);

		pr = createPressed (mo, mo.columnView[3], 0, overlap);
		mo.columnView[3].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[4], 0, 0);
		mo.columnView[4].getMouseManager().handleMouseEvent(rel);

		pr = createPressed (mo, mo.columnView[2], 0, overlap);
		mo.columnView[2].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[3], 0, 0);
		mo.columnView[3].getMouseManager().handleMouseEvent(rel);

		pr = createPressed (mo, mo.columnView[1], 0, overlap);
		mo.columnView[1].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[2], 0, 0);
		mo.columnView[2].getMouseManager().handleMouseEvent(rel);
		
		assertEquals(1, mo.Columns[1].count());
		
		//Move unit of Jack Heart and Nine Heart:
		pr = createPressed (mo, mo.columnView[0], 0, overlap);
		mo.columnView[0].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[1], 0, 0);
		mo.columnView[1].getMouseManager().handleMouseEvent(rel);
		
		assertEquals(3, mo.Columns[1].count());
		
		//MoveColumnMove mcmm = new MoveColumnMove(mo.Columns[8], mo.Columns[0], null, 2);
		//assertTrue(mcmm.doMove(mo));
		//Empty the last column
		pr = createPressed (mo, mo.columnView[8], 0, 0);
		mo.columnView[8].getMouseManager().handleMouseEvent(pr);
		rel = createReleased (mo, mo.columnView[0], 0, 0);
		mo.columnView[0].getMouseManager().handleMouseEvent(rel);
		
		//Show that the move to the last column is invalid
		MoveColumnMove mcm = new MoveColumnMove(mo.Columns[7], mo.Columns[8], null, 1);
		assertFalse(mcm.doMove(mo));
//		pr = createPressed (mo, mo.columnView[7], 0, 0);
//		mo.columnView[7].getMouseManager().handleMouseEvent(pr);
//		rel = createReleased (mo, mo.columnView[8], 0, 0);
//		mo.columnView[8].getMouseManager().handleMouseEvent(rel);
		
		
		// undo 
		//assertTrue (mo.undoMove());
		
		
		assertEquals (3, mo.Columns[0].count());
		assertTrue(mo.Columns[8].empty());
		
		// undo 
		assertTrue (mo.undoMove());
		assertEquals (mo.Columns[8].peek(), new Card(Card.NINE, Card.SPADES));

		
	}

	

}
