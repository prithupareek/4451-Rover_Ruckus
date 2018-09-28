package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Driving extends OpMode {
    private Hardware hardware;
    private double speed = 1;
    private boolean prevX = false;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        if (gamepad1.x && !prevX) {
            speed *= -1;
        }

        // Wheels
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;

        if (Math.abs(x) + Math.abs(y) + Math.abs(turn) != 0) {
            double max = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            if (Math.abs(turn) > max) {
                max = Math.abs(turn);
            }

            double multiplier = max / (Math.abs(x) + Math.abs(y) + Math.abs(turn));
            x    *= multiplier * speed;
            y    *= multiplier * speed;
            turn *= multiplier * speed;
        }

        hardware.frontLeft  .setPower( x + y + turn);
        hardware.frontRight .setPower(-x + y - turn);
        hardware.backLeft   .setPower(-x + y + turn);
        hardware.backRight  .setPower( x + y - turn);

        hardware.setGrabberPower(-gamepad2.left_stick_y);
        hardware.setLinearSlidePower(-gamepad2.right_stick_y);


        prevX = gamepad1.x;
    }
}
