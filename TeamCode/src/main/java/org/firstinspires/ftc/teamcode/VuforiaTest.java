package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;

/**
 * Created by Zach on 9/8/18.
 */

@TeleOp
public class VuforiaTest extends OpMode {
    private Hardware hardware;

    @Override
    public void init() {
        hardware = new Hardware();
        hardware.initVuforia(telemetry, hardwareMap);
    }

    @Override
    public void loop() {
        OpenGLMatrix robotPos = hardware.getRobotLocation();
        telemetry.addData("Robot", robotPos == null ? "null" : robotPos.formatAsTransform());
        telemetry.update();
    }
}
