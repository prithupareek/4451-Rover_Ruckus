package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Hardware;

@Autonomous
public class BlueLeftClaimPark extends LinearOpMode {
	private Hardware hardware;

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

		hardware.slideToPos(17740, 1, 10_000, this);
		hardware.strafeRight(-1000, .5, 2_000, this);
		hardware.slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		hardware.slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		hardware.slide.setTargetPosition(-17740);
		hardware.slide.setPower(1);

		hardware.driveForward(300, 1, 2_000, this);
		hardware.turnLeft(80, 1, 1_000, this);
		hardware.driveForward(800, 1, 2_000, this);

		OpenGLMatrix targetPos = OpenGLMatrix.translation(-1500, 1550, 0)
				.multiplied(Orientation.getRotationMatrix(
						AxesReference.EXTRINSIC, AxesOrder.XYZ,
						AngleUnit.DEGREES, 0, 0, 180
				));
		if (!hardware.toPosition(targetPos, this)) {
			hardware.turnLeft(30, .5, 1_000, this);
			if (!hardware.toPosition(targetPos, this)) {
				requestOpModeStop();
			}
		}

		hardware.leftGrabber.setPower(1);
		hardware.rightGrabber.setPower(1);
		hardware.pause(1_000, this);
		hardware.leftGrabber.setPower(0);
		hardware.rightGrabber.setPower(0);
		hardware.driveForward(-2100, 1, 8_000, this);
		hardware.turnLeft(-90, 1, 2_000, this);
		hardware.sampler.setPosition(.3);
		hardware.pause(500, this);
	}
}
