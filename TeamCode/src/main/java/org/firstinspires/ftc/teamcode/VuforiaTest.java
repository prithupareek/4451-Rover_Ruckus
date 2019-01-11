package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

/**
 * Created by Zach on 9/8/18.
 */

@Disabled
@Autonomous
public class VuforiaTest extends OpMode {
	private Hardware hardware;

	@Override
	public void init() {
		hardware = new Hardware(hardwareMap, telemetry);
		hardware.initVuforia();
	}

	@Override
	public void loop() {
		OpenGLMatrix robotPos = hardware.getRobotLocation();
		telemetry.addData("Robot", robotPos == null ? "No targets visible" : robotPos.formatAsTransform());
		telemetry.update();
	}
}
