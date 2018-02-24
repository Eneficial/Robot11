//""""Pseudocode"""" based on my understanding of what our robot's climber will be. 
//Still got no clue what to do here
package Team4450.Robot11;

import Team4450.Lib.Util;

public class Climber {
	
	private Robot robot;
	
	public Climber(Robot robot) 
	{
		Util.consoleLog();
		this.robot = robot;
	}
	
	public void armExtend() {
		//Deploys the arms that other robots will be on
	}
	
	public void armRaise() {
		//Raises the arms that are holding up allliance partner's robots
	}
	
	public void selfRaise() {
		//Raises self (and alliance partners) using the lift bar
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
