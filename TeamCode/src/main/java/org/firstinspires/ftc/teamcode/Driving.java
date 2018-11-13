package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Driving extends OpMode {
    private static final int ARM_UP_POS = 1300;
    private Hardware hardware;
    private double speed = .5;
    private boolean reverse, brakes, armPower, elbowPower, doorOpen, armUp, armToggling;
    private boolean prevX1, prevY1, prevL2, prevR2, prevX2, prevY2;
    private double armTarget, elbowTarget;

    @Override
    public void init() {
        hardware = new Hardware(hardwareMap, telemetry);

        hardware.setWheelMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hardware.arm   .setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.elbow .setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hardware.slide .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hardware.arm   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hardware.elbow .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        armTarget = hardware.arm.getCurrentPosition();
        elbowTarget = hardware.elbow.getCurrentPosition();
    }

    @Override
    public void loop() {
        // Wheels
        // Reversing
        if (gamepad1.x && !prevX1) {
            reverse = !reverse;
        }

        // Brakes
        if (gamepad1.y && !prevY1) {
            brakes = !brakes;
            hardware.setWheelZeroPowerBehavior(
                    brakes ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT
            );
        }

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
            x *= multiplier;
            y *= multiplier;
            turn *= multiplier;
        }

        hardware.frontLeft.setPower(x + y + turn);
        hardware.frontRight.setPower(-x + y - turn);
        hardware.backLeft.setPower(-x + y + turn);
        hardware.backRight.setPower(x + y - turn);

        // Grabber
        if (gamepad2.left_trigger != 0) {
            hardware.leftGrabber.setPower(gamepad2.left_trigger);
            hardware.rightGrabber.setPower(gamepad2.left_trigger);
        } else {
            hardware.leftGrabber.setPower(-gamepad2.right_trigger);
            hardware.rightGrabber.setPower(-gamepad2.right_trigger);
        }

        // Linear slide
        if (gamepad2.dpad_up) {
            hardware.slide.setPower(1);
        } else if (gamepad2.dpad_down) {
            hardware.slide.setPower(-1);
        } else {
            hardware.slide.setPower(0);
        }

        // Arm and elbow
        // Power mode
        if (!armToggling) {
            if (gamepad2.left_stick_button && !prevL2) {
                armPower = !armPower;
                if (armPower) {
                    hardware.arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                } else {
                    armTarget = hardware.arm.getCurrentPosition();
                    hardware.arm.setTargetPosition((int) armTarget);
                    hardware.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
            }

            if (armPower) {
                hardware.arm.setPower(-gamepad2.left_stick_y * .75);
            } else {
                hardware.arm.setPower(.5);
                increaseArmPos(-gamepad2.left_stick_y * 2.5);
            }
        }

        if (gamepad2.right_stick_button && !prevR2) {
            elbowPower = !elbowPower;
            if (elbowPower) {
                hardware.elbow.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            } else {
                elbowTarget = hardware.elbow.getCurrentPosition();
                hardware.elbow.setTargetPosition((int) elbowTarget);
                hardware.elbow.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
        }

        if (elbowPower) {
            hardware.setElbowPower(-gamepad2.right_stick_y * .75);
        } else {
            hardware.setElbowPower(.5);
            increaseElbowPos(-gamepad2.right_stick_y);
        }

        // Toggle arm position
        if (gamepad2.x && !prevX2) {
            armUp = !armUp;
            armToggling = true;
            armPower = false;
            if (armUp) {
                armTarget = ARM_UP_POS;
                hardware.arm.setTargetPosition(ARM_UP_POS);
            } else {
                armTarget = 0;
                hardware.arm.setTargetPosition(0);
            }
            hardware.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            hardware.arm.setPower(.2);
        }

        if (armToggling && Math.abs(hardware.arm.getCurrentPosition() - armTarget) < 10) {
            armToggling = false;
        }

        // Door
        if (gamepad2.y && !prevY2) {
            doorOpen = !doorOpen;
        }
        hardware.door.setPosition(doorOpen ? 1 : .5);

        // Sampler maintain position
        hardware.sampler.setPosition(1);

        // Update prevs
        prevX1 = gamepad1.x;
        prevY1 = gamepad1.y;
        prevX2 = gamepad2.x;
        prevY2 = gamepad2.y;
        prevL2 = gamepad2.left_stick_button;
        prevR2 = gamepad2.right_stick_button;

        // Telemetry
        telemetry.addData("Slide Pos", hardware.slide.getCurrentPosition());

        telemetry.addData("Reverse", reverse);
        telemetry.addData("Brakes", brakes);
        telemetry.addData("Arm Power", armPower);
        telemetry.addData("Elbow Power", elbowPower);
        telemetry.addData("Door Open", doorOpen);
        telemetry.addData("Arm Toggling", armToggling);

        telemetry.addData("Arm Target", armTarget);
        telemetry.addData("Arm Pos", hardware.arm.getCurrentPosition());
        telemetry.addData("Arm Diff", hardware.arm.getCurrentPosition() - armTarget);

        telemetry.addData("Elbow Target", elbowTarget);
        telemetry.addData("Elbow Pos", hardware.elbow.getCurrentPosition());
        telemetry.addData("Elbow Diff", hardware.elbow.getCurrentPosition() - elbowTarget);

        telemetry.update();
    }

    private void increaseArmPos(double move) {
        armTarget += move * 10;
        if (armTarget - hardware.arm.getCurrentPosition() > 280) {
            armTarget = hardware.arm.getCurrentPosition() + 280;
        }
        if (hardware.arm.getCurrentPosition() - armTarget > 280) {
            armTarget = hardware.arm.getCurrentPosition() - 280;
        }
        hardware.arm.setTargetPosition((int) armTarget);
    }

    private void increaseElbowPos(double move) {
        elbowTarget += move * 3;
        if (elbowTarget - hardware.elbow.getCurrentPosition() > 72) {
            elbowTarget = hardware.elbow.getCurrentPosition() + 72;
        }
        if (hardware.elbow.getCurrentPosition() - elbowTarget > 72) {
            elbowTarget = hardware.elbow.getCurrentPosition() - 72;
        }
        hardware.elbow.setTargetPosition((int) elbowTarget);
    }
}
