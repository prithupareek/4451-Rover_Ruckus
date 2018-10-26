package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.*;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.*;

public class Hardware {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private DcMotor frontLeft, frontRight, backLeft, backRight, slide, arm, elbow;
    private CRServo leftGrabber, rightGrabber;

    private double armTarget, elbowTarget;

    private VuforiaTrackables navTargets;
    private OpenGLMatrix lastPos;

    Hardware(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        frontLeft    = hardwareMap.dcMotor.get("frontLeft");
        frontRight   = hardwareMap.dcMotor.get("frontRight");
        backLeft     = hardwareMap.dcMotor.get("backLeft");
        backRight    = hardwareMap.dcMotor.get("backRight");
        arm          = hardwareMap.dcMotor.get("arm");
        elbow        = hardwareMap.dcMotor.get("elbow");
        slide        = hardwareMap.dcMotor.get("slide");
        leftGrabber  = hardwareMap.crservo.get("leftGrabber");
        rightGrabber = hardwareMap.crservo.get("rightGrabber");

        frontLeft  .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft   .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight  .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide      .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm        .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elbow      .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft  .setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight .setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft   .setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight  .setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        slide      .setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        arm        .setMode(DcMotor.RunMode.RUN_TO_POSITION);
        elbow      .setMode(DcMotor.RunMode.RUN_TO_POSITION);

        armTarget = arm.getCurrentPosition();
        elbowTarget = elbow.getCurrentPosition();

        slide .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        elbow .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontRight   .setDirection(DcMotorSimple.Direction.REVERSE);
        backRight    .setDirection(DcMotorSimple.Direction.REVERSE);
        elbow        .setDirection(DcMotorSimple.Direction.REVERSE);
        rightGrabber .setDirection(DcMotorSimple.Direction.REVERSE);
    }

    void setWheelsVector(double x, double y, double turn) {
        frontLeft  .setPower( x + y - turn);
        frontRight .setPower(-x + y + turn);
        backLeft   .setPower(-x + y - turn);
        backRight  .setPower( x + y + turn);
    }

    void setLinearSlidePower(double power) {
        slide.setPower(power);
    }

    void setGrabberPower(double power) {
        leftGrabber.setPower(power);
        rightGrabber.setPower(power);
    }

    void setArmPower(double power) {
        arm.setPower(power);
    }

    void increaseArmPos(double move) {
        armTarget += move;
        if (armTarget - arm.getCurrentPosition() > 72) {
            armTarget = arm.getCurrentPosition() + 72;
        }
        if (arm.getCurrentPosition() - armTarget > 72) {
            armTarget = arm.getCurrentPosition() - 72;
        }
        if (armTarget < 0) {
            armTarget = 0;
        }
        arm.setTargetPosition((int) armTarget);
        telemetry.addData("armTarget", armTarget);
        telemetry.addData("armPos", arm.getCurrentPosition());
    }

    void setElbowPower(double power) {
        elbow.setPower(power);
    }

    void increaseElbowPos(double move) {
        elbowTarget += move;
        if (elbowTarget - elbow.getCurrentPosition() > 72) {
            elbowTarget = elbow.getCurrentPosition() + 72;
        }
        if (elbow.getCurrentPosition() - elbowTarget > 72) {
            elbowTarget = elbow.getCurrentPosition() - 72;
        }
        if (elbowTarget < 0) {
            elbowTarget = 0;
        }
        elbow.setTargetPosition((int) elbowTarget);
        telemetry.addData("elbowTarget", elbowTarget);
        telemetry.addData("elbowPos", elbow.getCurrentPosition());
    }

    void resetArmElbow() {
        armTarget = arm.getCurrentPosition();
        elbowTarget = elbow.getCurrentPosition();
        arm.setTargetPosition((int) armTarget);
        elbow.setTargetPosition((int) elbowTarget);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        elbow.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    void initVuforia() {
        telemetry.addLine("Initializing Vuforia");
        telemetry.update();

        int cameraMonitorViewId = hardwareMap.appContext.getResources()
                .getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = "AZLv+a7/////AAAAGdyzndpq4khMnz5IMjSvhiR0XbtOlL7ZfQytGj9s" +
                "4zFCFoa+IqUA1Cjv4ghfSjfRAlRguu6cVbQVM+0Rxladi3AIKhUjIL6v5ToFrK/fxrWdwAzkQfEPM1S" +
                "3ijrTSm1N8DuZ6UoqiKoVmQGzyiWhDpTQoR1zIVkj88rOhBDYwBf0CnW++pxZ0pHlQBbh/bzBjt63AN" +
                "cuI9JyHU3/JLGSBhoIm04G3UnrjVrjKfPFlX9NOwWQLOYjQ+4B1l4M8u9BdihYgmfMST0BHON+MQ7qC" +
                "5dMs/2OSZlSKSZISN/L+x606xzc2Sv5G+ULUpaUiChG7Zlv/rncu337WhZjJ1X2pQGY7gIBcSH+TUw8" +
                "1n2jYKkm";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        VuforiaLocalizer vuforiaLocalizer = ClassFactory.getInstance().createVuforia(parameters);
        navTargets = vuforiaLocalizer.loadTrackablesFromAsset("RoverRuckus");

        VuforiaTrackable frontTarget = navTargets.get(0);
        VuforiaTrackable redTarget   = navTargets.get(1);
        VuforiaTrackable backTarget  = navTargets.get(2);
        VuforiaTrackable blueTarget  = navTargets.get(3);

        frontTarget .setName("FrontWall");
        redTarget   .setName("RedWall");
        backTarget  .setName("BackWall");
        blueTarget  .setName("BlueWall");

        final float mmPerInch = 25.4f;
        final float fieldWidth = 72 * mmPerInch;
        final float targetHeight = 6 * mmPerInch;

        frontTarget.setLocation(OpenGLMatrix
                .translation(0, -fieldWidth, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, 180
                )));

        redTarget.setLocation(OpenGLMatrix
                .translation(fieldWidth, 0, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, -90
                )));

        backTarget.setLocation(OpenGLMatrix
                .translation(0, fieldWidth, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, 0
                )));

        blueTarget.setLocation(OpenGLMatrix
                .translation(-fieldWidth, 0, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, 90
                )));

        for (VuforiaTrackable navTarget : navTargets) {
            telemetry.addData(navTarget.getName(), navTarget.getLocation().formatAsTransform());
        }

        // TODO Phone location when the robot is built
        OpenGLMatrix phoneLocation = OpenGLMatrix
                .translation(0, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 0, 0, 0
                ));
        telemetry.addData("Phone", phoneLocation.formatAsTransform());

        telemetry.update();

        for (VuforiaTrackable target : navTargets) {
            ((VuforiaTrackableDefaultListener) target.getListener())
                    .setPhoneInformation(phoneLocation, parameters.cameraDirection);
        }

        navTargets.activate();

        telemetry.addLine("Initialized Vuforia");
        telemetry.update();
    }

    OpenGLMatrix getRobotLocation() {
        VuforiaTrackable visible = null;
        for (VuforiaTrackable trackable : navTargets) {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                visible = trackable;
                break;
            }
        }

        if (visible == null) {
            return null;
        }

        OpenGLMatrix pos = ((VuforiaTrackableDefaultListener) visible.getListener()).getUpdatedRobotLocation();
        if (pos != null) {
            lastPos = pos;
        }
        return lastPos;
    }
}
