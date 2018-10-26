package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Driving extends OpMode {
    private Hardware hardware;
    private double speed = .5;
    private boolean reverse = false;
    private boolean limp = false;
    private boolean prevX1 = false;
    private boolean prevX2 = false;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
        // Wheels
        // Reversing
        if (gamepad1.x && !prevX1) {
            reverse = !reverse;
        }
        prevX1 = gamepad1.x;
        telemetry.addData("reverse", reverse);

        // Change wheel speed
        if (gamepad1.dpad_up) {
            speed = 1;
        }
        if (gamepad1.dpad_left || gamepad1.dpad_right) {
            speed = .5;
        }
        if (gamepad1.dpad_down) {
            speed = .2;
        }

        // Vector components
        double x = gamepad1.left_stick_x * speed * (reverse ? -1 : 1);
        double y = -gamepad1.left_stick_y * speed * (reverse ? -1 : 1);
        double turn = gamepad1.right_stick_x * speed;

        // Normalize x, y, and turn
        if (Math.abs(x) + Math.abs(y) + Math.abs(turn) != 0) {
            double max = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            if (Math.abs(turn) > max) {
                max = Math.abs(turn);
            }

            double multiplier = max / (Math.abs(x) + Math.abs(y) + Math.abs(turn));
            x    *= multiplier;
            y    *= multiplier;
            turn *= multiplier;
        }

        hardware.setWheelsVector(x, y, turn);

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
        // Going limp
        if (gamepad2.x && !prevX2) {
            limp = !limp;
            if (!limp) {
                hardware.resetArmElbow();
            }
        }
        prevX2 = gamepad2.x;
        telemetry.addData("limp", limp);

        if (limp) {
            hardware.setArmPower(0);
            hardware.setElbowPower(0);
        } else {
            hardware.setArmPower(.5);
            hardware.setElbowPower(.5);
        }

        hardware.increaseArmPos(-gamepad2.left_stick_y * 2.5);
        hardware.increaseElbowPos(-gamepad2.right_stick_y);


        telemetry.update();
    }
}
