package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ColorSensorTest extends OpMode {
    private Hardware hardware;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        hardware.argbTelemetry();
        hardware.hsvTelemetry();
        telemetry.addData("Yellow", hardware.isYellow());
    }
}
