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
public class BlueRightEverything extends LinearOpMode {
    private Hardware hardware;

    @Override
    public void runOpMode() throws InterruptedException {
        hardware = new Hardware(hardwareMap, telemetry);
        hardware.setWheelZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        hardware.slide .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hardware.arm   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hardware.elbow .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        hardware.arm.setTargetPosition(hardware.arm.getCurrentPosition());
        hardware.elbow.setTargetPosition(hardware.elbow.getCurrentPosition());

        hardware.arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.elbow.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        hardware.slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        hardware.initVuforia();

        waitForStart();

        hardware.arm.setPower(.5);
        hardware.elbow.setPower(.5);
        hardware.sampler.setPosition(.8);

        hardware.runToPos(
                hardware.slide, 14000, 1, 10_000, this
        );

        hardware.strafeRight(-6000, .5, 2_000, this);
        hardware.driveForward(500, .5, 2_000, this);

        hardware.setTargetPos(
                OpenGLMatrix.translation(-500, 500, 0)
                        .multiplied(Orientation.getRotationMatrix(
                                AxesReference.EXTRINSIC, AxesOrder.XYZ,
                                AngleUnit.DEGREES, 0, 0, 135
                        ))
        );
    }
}
