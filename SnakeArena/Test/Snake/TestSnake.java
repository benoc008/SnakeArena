package Snake;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSnake {

	Snake s;
	
	@Before
	public void setUp() {
		Decider play = new Decider();
		play.setValue(true);
		s = new Snake("Test", Color.GREEN, 0);		
	}
	
	@Test
	public void testSnakeName() {
		String name = s.getName();
		Assert.assertEquals("Test", name); 
	}
	
	@Test
	public void testSnakeColor() {
		Color c = s.getColor();
		Assert.assertEquals(Color.GREEN.toString(), c.toString()); 
	}
	
	@Test
	public void testIsSnakeAlive() {
		boolean dead = s.isDead();
		Assert.assertEquals(false, dead); 		
	}
	

}
