package Snake;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCore {

	Core core;
	
	@Before
	public void setUp(){
		Decider play = new Decider();
		play.setValue(true);
		core = new Core(50, 50, 15, null, 800, play, true, DeathMode.Immortal);
	}

	@Test
	public void testApples() {
		// When we use the spawnApples function, an item appears in the apples list.
		core.spawnApples();
		Assert.assertTrue(core.getApples().size() == 1);
	}
	
	@Test
	public void testMove() {
		// When we use the move function, the tail's y coordinate
		// gets upper by 1, since it's headed to North
		core.addSnake("Test", Color.BLUE, 0);
		int y1 = core.getSnakes().get(0).getSnake().get(0).y;
		core.getSnakes().get(0).move();
		int y2 = core.getSnakes().get(0).getSnake().get(0).y;
		Assert.assertEquals(y1 - 1, y2);
	}
	
	@Test
	public void testClone(){
		Core clone = core.clone();
		Assert.assertEquals(core.getWidth(), clone.getWidth());
	}

}
