package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

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
    }
}
