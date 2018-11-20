package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Spin extends LinearOpMode {
    Hardware hardware;

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

        waitForStart();

        hardware.turnLeft(360, .5, 2_000, this);
    }
}
