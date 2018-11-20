package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaException;

import java.util.Arrays;

public class Hardware {
    private final static double COUNTS_PER_MM = 800 / 217;
    private final static double COUNTS_PER_DEGREE = 531563 / 28800;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private OpenGLMatrix targetPos = new OpenGLMatrix();

    DcMotor frontLeft, frontRight, backLeft, backRight, slide, arm, elbow;
    CRServo leftGrabber, rightGrabber;
    Servo door, sampler;
    ColorSensor colorSensor;

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
        door         = hardwareMap.servo.get("door");
        sampler      = hardwareMap.servo.get("sampler");
        colorSensor  = hardwareMap.colorSensor.get("colorSensor");

        setWheelMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        slide .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm   .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elbow .setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft    .setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft     .setDirection(DcMotorSimple.Direction.REVERSE);
        elbow        .setDirection(DcMotorSimple.Direction.REVERSE);
        rightGrabber .setDirection(DcMotorSimple.Direction.REVERSE);
    }

    void setElbowPower(double power) {
        elbow.setPower(power);
    }

    void setWheelZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        frontLeft  .setZeroPowerBehavior(behavior);
        frontRight .setZeroPowerBehavior(behavior);
        backLeft   .setZeroPowerBehavior(behavior);
        backRight  .setZeroPowerBehavior(behavior);
    }

    void setWheelMode(DcMotor.RunMode mode) {
        frontLeft  .setMode(mode);
        frontRight .setMode(mode);
        backLeft   .setMode(mode);
        backRight  .setMode(mode);
        slide      .setMode(mode);
    }

    void argbTelemetry() {
        telemetry.addData("ARGB", colorSensor.alpha() + ", " + colorSensor.red()
                + ", " + colorSensor.green() + ", " + colorSensor.blue());
    }

    void hsvTelemetry() {
        telemetry.addData("HSV", Arrays.toString(getHSV()));
    }

    // Autonomous methods

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
                .translation(-fieldWidth, 0, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, 90
                )));

        redTarget.setLocation(OpenGLMatrix
                .translation(0, -fieldWidth, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, 180
                )));

        backTarget.setLocation(OpenGLMatrix
                .translation(fieldWidth, 0, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, -90
                )));

        blueTarget.setLocation(OpenGLMatrix
                .translation(0, fieldWidth, targetHeight)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, 0
                )));

        for (VuforiaTrackable navTarget : navTargets) {
            telemetry.addData(navTarget.getName(), navTarget.getLocation().formatAsTransform());
        }

        OpenGLMatrix phoneLocation = OpenGLMatrix
                .translation(-140, -140, 345)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90, 0, -90
                ));
        telemetry.addData("Phone", phoneLocation.formatAsTransform());

        for (VuforiaTrackable target : navTargets) {
            ((VuforiaTrackableDefaultListener) target.getListener())
                    .setPhoneInformation(phoneLocation, parameters.cameraDirection);
        }

        navTargets.activate();

        telemetry.addLine("Initialized Vuforia");
        telemetry.update();
    }

    void setTargetPos(OpenGLMatrix targetPos) {
        this.targetPos = targetPos;
    }

    boolean isYellow() {
        return getHSV()[0] < 27;
    }

    private float[] getHSV() {
        float[] hsv = new float[3];
        Color.RGBToHSV(colorSensor.red(), colorSensor.green(), colorSensor.blue(), hsv);
        return hsv;
    }

    void runToPos(DcMotor motor, int counts, double power,
                  int timeoutMillis, LinearOpMode opMode) {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motor.setTargetPosition(motor.getCurrentPosition() + counts);
        motor.setPower(power);
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while (opMode.opModeIsActive() && motor.isBusy() && runtime.milliseconds() < timeoutMillis) {
            opMode.idle();
        }
        motor.setPower(0);
    }

    private void move(int frontLeftCounts, int frontRightCounts,
                      int backLeftCounts, int backRightCounts,
                      double power, int timeoutMillis, LinearOpMode opMode) {
        setWheelMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setWheelMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setTargetPosition(frontLeftCounts);
        frontRight.setTargetPosition(frontRightCounts);
        backLeft.setTargetPosition(backLeftCounts);
        backRight.setTargetPosition(backRightCounts);

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while (opMode.opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy()
                || backLeft.isBusy() || backRight.isBusy())
                && runtime.milliseconds() < timeoutMillis) {

            telemetry.addData("Completion",
                    (Math.abs(frontLeft.getCurrentPosition()) + Math.abs(frontLeft.getCurrentPosition())
                            + Math.abs(backLeft.getCurrentPosition()) + Math.abs(backRight.getCurrentPosition()))
                            / (Math.abs(frontLeftCounts) + Math.abs(frontRightCounts)
                            + Math.abs(backLeftCounts) + Math.abs(backRightCounts)) + "%");
            telemetry.addLine();

            telemetry.addLine("Position, Target, Difference");
            telemetry.addData("Front Left", frontLeft.getCurrentPosition() + ", " +
                    frontLeftCounts + ", " + (frontLeft.getTargetPosition()
                    - frontLeft.getCurrentPosition()));
            telemetry.addData("Front Right", frontRight.getCurrentPosition() + ", " +
                    frontRightCounts + ", " + (frontRightCounts
                    - frontRight.getCurrentPosition()));
            telemetry.addData("Back Left", backLeft.getCurrentPosition() + ", " +
                    backLeftCounts + ", " + (backLeft.getTargetPosition()
                    - backLeft.getCurrentPosition()));
            telemetry.addData("Back Right", backRight.getCurrentPosition() + ", " +
                    backRightCounts + ", " + (backRight.getTargetPosition()
                    - backRight.getCurrentPosition()));

            telemetry.update();
        }

        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    void strafeRight(int counts, double power, int timeoutMillis, LinearOpMode opMode) {
        move(counts, -counts, -counts, counts, power, timeoutMillis, opMode);
    }

    void driveForward(double mm, double power, int timeoutMillis, LinearOpMode opMode) {
        int counts = (int) (mm * COUNTS_PER_MM);
        move(counts, counts, counts, counts, power, timeoutMillis, opMode);
    }

    void driveForwardRecorded(double mm, double power, int timeoutMillis, LinearOpMode opMode) {
        driveForward(mm, power, timeoutMillis, opMode);
        float robotRotation = Orientation.getOrientation(
                targetPos, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
        targetPos.translate((float) (mm * Math.cos(robotRotation)),
                (float) (mm * Math.sin(robotRotation)), 0);
    }

    void turnLeft(double degrees, double power, int timeoutMillis, LinearOpMode opMode) {
        int counts = (int) (degrees * COUNTS_PER_DEGREE);
        move(-counts, counts, -counts, counts, power, timeoutMillis, opMode);
    }

    void turnLeftRecorded(double degrees, double power, int timeoutMillis, LinearOpMode opMode) {
        turnLeft(degrees, power, timeoutMillis, opMode);
        targetPos.rotate(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES,
                0, 0, (float) degrees);
    }

    void adjustPosition(LinearOpMode opMode) throws InterruptedException {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while (runtime.milliseconds() < 500) {
            opMode.idle();
        }
        OpenGLMatrix location = getRobotLocation();

        if (location != null) {
            telemetry.addLine("Location found!");
            telemetry.update();
            VectorF diff = targetPos.getTranslation().subtracted(location.getTranslation());

            float locationAngle = Orientation.getOrientation(location, AxesReference.EXTRINSIC,
                    AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
            float targetAngle = Orientation.getOrientation(targetPos, AxesReference.EXTRINSIC,
                    AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

            // Go to x of target
            turnLeft(-locationAngle, .5, 1_000, opMode);
            driveForward(diff.getData()[0], .5, 5_000, opMode);

            // Go to y of target
            turnLeft(90, .5, 1_000, opMode);
            driveForward(diff.getData()[1], .5, 5_000, opMode);

            // Go to angle of target
            turnLeft(targetAngle - 90, .5, 1_000, opMode);
        }
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
