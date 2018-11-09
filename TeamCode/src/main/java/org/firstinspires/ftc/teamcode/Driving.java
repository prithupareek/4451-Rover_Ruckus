package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Driving extends OpMode {
    private Hardware hardware;
    private double speed = .5;
    private boolean reverse, brakes, armPower, elbowPower, doorOpen;
    private boolean prevX1, prevY1, prevL2, prevR2, prevX2, prevY2;

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
        telemetry.addData("Reverse", reverse);

        // Brakes
        if (gamepad1.y && !prevY1) {
            brakes = !brakes;
            hardware.setWheelZeroPowerBehavior(
                    brakes ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT
            );
        }
        prevY1 = gamepad1.y;
        telemetry.addData("Brakes", brakes);

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
        // Power mode
        if ((gamepad2.left_stick_button && !prevL2) || (gamepad2.x && !prevX2)) {
            armPower = !armPower;
            if (armPower) {
                hardware.setArmMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } else {
                hardware.resetArm();
            }
        }
        prevL2 = gamepad2.left_stick_button;
        telemetry.addData("Arm Power", armPower);

        if (armPower) {
            hardware.setArmPower(-gamepad2.left_stick_y);
        } else {
            hardware.setArmPower(.5);
            hardware.increaseArmPos(-gamepad2.left_stick_y * 2.5);
        }

        if ((gamepad2.right_stick_button && !prevR2) || (gamepad2.x && !prevX2)) {
            elbowPower = !elbowPower;
            if (armPower) {
                hardware.setElbowMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } else {
                hardware.resetElbow();
            }
        }
        prevL2 = gamepad2.right_stick_button;
        telemetry.addData("Elbow Power", elbowPower);

        if (elbowPower) {
            hardware.setElbowPower(-gamepad2.right_stick_y * .5);
        } else {
            hardware.setElbowPower(.5);
            hardware.increaseElbowPos(-gamepad2.right_stick_y);
        }

        // Door
        if (gamepad2.y && !prevY2) {
            doorOpen = !doorOpen;
        }
        telemetry.addData("Door Open", doorOpen);
        hardware.setDoorPosition(doorOpen ? 1 : .5);
        prevY2 = gamepad2.y;

        // Sampler maintain position
        hardware.setSamplerPos(1);


        telemetry.update();
    }
}
