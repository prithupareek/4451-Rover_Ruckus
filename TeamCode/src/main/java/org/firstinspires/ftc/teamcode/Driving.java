package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Driving extends OpMode {
    private Hardware hardware;
    private double speed = .8;
    private boolean prevA = false;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap, telemetry);
    }

    @Override
    public void start() {
        hardware.arm.setPower(.5);
        hardware.elbow.setPower(.5);
    }

    @Override
    public void loop() {
        if (gamepad1.a && !prevA) {
            speed *= -1;
        }

        // Wheels
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double turn = gamepad1.right_stick_x;

        // Normalize x, y, and turn
        if (Math.abs(x) + Math.abs(y) + Math.abs(turn) != 0) {
            double max = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            if (Math.abs(turn) > max) {
                max = Math.abs(turn);
            }

            double multiplier = max / (Math.abs(x) + Math.abs(y) + Math.abs(turn));
            x    *= multiplier * speed;
            y    *= multiplier * speed;
            turn *= multiplier * Math.abs(speed);
        }

        hardware.frontLeft  .setPower( x + y + turn);
        hardware.frontRight .setPower(-x + y - turn);
        hardware.backLeft   .setPower(-x + y + turn);
        hardware.backRight  .setPower( x + y - turn);

        // Grabber
        if (gamepad2.left_trigger != 0) {
            hardware.setGrabberPower(gamepad2.left_trigger);
        } else {
            hardware.setGrabberPower(-gamepad2.right_trigger);
        }

        // Linear slide
        if (gamepad2.dpad_up) {
            hardware.setLinearSlidePower(1);
        } else if (gamepad2.dpad_down) {
            hardware.setLinearSlidePower(-1);
        } else {
            hardware.setLinearSlidePower(0);
        }

        // Arm and elbow
        hardware.increaseArmPos(-gamepad2.left_stick_y / 2);
        hardware.increaseElbowPos(-gamepad2.right_stick_y);


        prevA = gamepad1.a;
        telemetry.update();
    }
}
