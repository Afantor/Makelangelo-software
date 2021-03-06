package com.marginallyclever.makelangeloRobot.generators;

import java.io.IOException;
import java.io.Writer;

import com.marginallyclever.makelangelo.Translator;
import com.marginallyclever.makelangeloRobot.MakelangeloRobotPanel;

public class Generator_FillPage extends ImageGenerator {
	private static float angle = 0;

	MakelangeloRobotPanel robotPanel;


	@Override
	public String getName() {
		return Translator.get("FillPageName");
	}

	static public float getAngle() {
		return angle;
	}
	static public void setAngle(int value) {
		angle = value;
	}
	
	@Override
	public ImageGeneratorPanel getPanel() {
		return new Generator_FillPage_Panel(this);
	}
	
	@Override
	public boolean generate(Writer out) throws IOException {
		double majorX = Math.cos(Math.toRadians(angle));
		double majorY = Math.sin(Math.toRadians(angle));

		// Set up the conversion from image space to paper space, select the current tool, etc.
		imageStart(out);
		
		// figure out how many lines we're going to have on this image.
		float stepSize = machine.getPenDiameter();
		if (stepSize < 1) stepSize = 1;

		// from top to bottom of the margin area...
		double yBottom = machine.getPaperBottom() * machine.getPaperMargin();
		double yTop    = machine.getPaperTop()    * machine.getPaperMargin();
		double xLeft   = machine.getPaperLeft()   * machine.getPaperMargin();
		double xRight  = machine.getPaperRight()  * machine.getPaperMargin();
		double dy = yTop - yBottom;
		double dx = xRight - xLeft;
		double radius = Math.sqrt(dx*dx+dy*dy);

		int i=0;
		for(double a = -radius;a<radius;a+=stepSize) {
			double majorPX = majorX * a;
			double majorPY = majorY * a;
			double startPX = majorPX - majorY * radius;
			double startPY = majorPY + majorX * radius;
			double endPX   = majorPX + majorY * radius;
			double endPY   = majorPY - majorX * radius;

			moveTo(out,startPX,startPY,true);
			moveTo(out,startPX,startPY,false);
			if ((i % 2) == 0) {
				clipLine(out, startPX, startPY, endPX, endPY, false, false, false, false);
			} else {
				clipLine(out, endPX, endPY, startPX, startPY, false, false, false, false);
			}
			++i;
		}

		imageEnd(out);
	    
	    return true;
	}
}
