package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous
public class RedLeftEverything extends LinearOpMode {
    Hardware hardware;

    @Override
    public void runOpMode() throws InterruptedException {
        hardware = new Hardware(hardwareMap, telemetry);
        hardware.setWheelZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        hardware.slide .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hardware.arm   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hardware.elbow .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        hardware.arm   .setTargetPosition(hardware.arm.getCurrentPosition());
        hardware.elbow .setTargetPosition(hardware.elbow.getCurrentPosition());

        hardware.arm   .setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.elbow .setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.slide .setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hardware.initVuforia();

        waitForStart();

        hardware.arm.setPower(.5);
        hardware.elbow.setPower(.5);
        hardware.sampler.setPosition(1);

        hardware.slideToPos(14000, 1, 10_000, this);
        hardware.strafeRight(-1000, .5, 2_000, this);
        hardware.slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.slide.setTargetPosition(-14000);
        hardware.slide.setPower(1);

        hardware.driveForward(300, .5, 2_000, this);
        hardware.turnLeft(80, .5, 1_000, this);
        hardware.driveForward(800, .5, 2_000, this);

        OpenGLMatrix targetPos = OpenGLMatrix.translation(0, -1450, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 0, 0, 225
                ));

        if (!hardware.toPosition(targetPos, this)) {
            requestOpModeStop();
        }

        hardware.driveForward(904, .3, 4_000, this);
        hardware.sampler.setPosition(.18);
        hardware.pause(1_000, this);
        while (opModeIsActive() && !gamepad1.x) {
            hardware.hsvTelemetry();
            telemetry.addData("isYellow", hardware.isYellow());
            telemetry.update();
        }
        if (hardware.isYellow()) {
            hardware.driveForward(-200, .3, 2_000, this);
            hardware.sampler.setPosition(1);
            hardware.driveForward(1162, .3, 5_000, this);
        } else {
            hardware.sampler.setPosition(1);
            hardware.pause(500, this);
            hardware.driveForward(431, .3, 3_000, this);
            hardware.sampler.setPosition(.18);
            hardware.pause(1_000, this);
            if (hardware.isYellow()) {
                hardware.driveForward(-200, .3, 2_000, this);
                hardware.sampler.setPosition(1);
                hardware.driveForward(731, .3, 3_000, this);
            } else {
                hardware.sampler.setPosition(1);
                hardware.pause(500, this);
                hardware.driveForward(431, .3, 3_000, this);
                hardware.sampler.setPosition(.18);
                hardware.pause(1_000, this);
                hardware.driveForward(-200, .3, 2_000, this);
                hardware.sampler.setPosition(1);
                hardware.driveForward(300, .3, 2_000, this);
            }
        }
        hardware.turnLeft(135, .3, 3_000, this);
        hardware.leftGrabber.setPower(-1);
        hardware.rightGrabber.setPower(-1);
        hardware.pause(1_000, this);
        hardware.leftGrabber.setPower(0);
        hardware.rightGrabber.setPower(0);
        hardware.driveForward(-3460, 1, 8_000, this);
    }
}
