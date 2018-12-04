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

import java.util.Arrays;

public class Hardware {
    private final static double COUNTS_PER_MM = 800 / 217;
    private final static double COUNTS_PER_DEGREE = 181800 / 8640;

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

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
    }

    void argbTelemetry() {
        NormalizedRGBA rgba = ((NormalizedColorSensor) colorSensor).getNormalizedColors();
        telemetry.addData("RGBA", "%.0f, %.0f, %.0f, %.0f",
                rgba.red * 10000f, rgba.green * 10000f, rgba.blue * 10000f, rgba.alpha * 10000f);
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

        VuforiaTrackable frontTarget = navTargets.get(2);
        VuforiaTrackable redTarget   = navTargets.get(1);
        VuforiaTrackable backTarget  = navTargets.get(3);
        VuforiaTrackable blueTarget  = navTargets.get(0);

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

    boolean isYellow() {
        return getHSV()[0] < 27;
    }

    private float[] getHSV() {
        float[] hsv = new float[3];
        NormalizedRGBA rgba = ((NormalizedColorSensor) colorSensor).getNormalizedColors();
        int red = (int) (rgba.red * 10000);
        int green = (int) (rgba.green * 10000);
        int blue = (int) (rgba.blue * 10000);
        if (red > 255) {
            red = 255;
            green *= 255 / (double) red;
            blue *= 255 / (double) red;
        }
        if (green > 255) {
            green = 255;
            red *= 255 / (double) green;
            blue *= 255 / (double) green;
        }
        if (blue > 255) {
            blue = 255;
            red *= 255 / (double) blue;
            green *= 255 / (double) blue;
        }
        Color.RGBToHSV(red, green, blue, hsv);
        return hsv;
    }

    void slideToPos(int counts, double power, int timeoutMillis, LinearOpMode opMode) {
        slide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        slide.setTargetPosition(counts);
        slide.setPower(power);
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while (opMode.opModeIsActive() && slide.isBusy()
                && runtime.milliseconds() < timeoutMillis) {
            telemetry.addData("Completion", "%.0f%%",
                    (double) slide.getCurrentPosition() / (double) counts * 100);
            telemetry.addLine();

            telemetry.addLine("Position, Target, Difference");
            telemetry.addData("Linear Slide", "%d, %d, %d",
                    slide.getCurrentPosition(), counts,
                    (slide.getTargetPosition() - slide.getCurrentPosition())
            );
        }
        slide.setPower(0);
    }

    private void move(int frontLeftCounts, int frontRightCounts,
                      int backLeftCounts, int backRightCounts,
                      double power, int timeoutMillis, LinearOpMode opMode) {
        setWheelMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setWheelMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft  .setTargetPosition(frontLeftCounts);
        frontRight .setTargetPosition(frontRightCounts);
        backLeft   .setTargetPosition(backLeftCounts);
        backRight  .setTargetPosition(backRightCounts);

        frontLeft  .setPower(power);
        frontRight .setPower(power);
        backLeft   .setPower(power);
        backRight  .setPower(power);

        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while (opMode.opModeIsActive() && runtime.milliseconds() < timeoutMillis && (
                frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy()
        )) {
            double completion = 100 *
                    (double) (Math.abs(frontLeft.getCurrentPosition())
                            + Math.abs(frontRight.getCurrentPosition())
                            + Math.abs(backLeft.getCurrentPosition())
                            + Math.abs(backRight.getCurrentPosition()))
                    /
                    (double) (Math.abs(frontLeftCounts)
                            + Math.abs(frontRightCounts)
                            + Math.abs(backLeftCounts)
                            + Math.abs(backRightCounts));

            telemetry.addData("Completion", "%.0f%%", completion);
            telemetry.addLine();

            telemetry.addLine("Position, Target, Difference");
            telemetry.addData("Front Left", "%d, %d, %d",
                    frontLeft.getCurrentPosition(), frontLeftCounts,
                    (frontLeft.getTargetPosition() - frontLeft.getCurrentPosition())
            );
            telemetry.addData("Front Right", "%d, %d, %d",
                    frontRight.getCurrentPosition(), frontRightCounts,
                    (frontRightCounts - frontRight.getCurrentPosition())
            );
            telemetry.addData("Back Left", "%d, %d, %d",
                    backLeft.getCurrentPosition(), backLeftCounts,
                    (backLeft.getTargetPosition() - backLeft.getCurrentPosition())
            );
            telemetry.addData("Back Right", "%d, %d, %d",
                    backRight.getCurrentPosition(), backRightCounts,
                    (backRight.getTargetPosition() - backRight.getCurrentPosition())
            );

            telemetry.update();
        }

        frontLeft  .setPower(0);
        frontRight .setPower(0);
        backLeft   .setPower(0);
        backRight  .setPower(0);
    }

    void strafeRight(int counts, double power, int timeoutMillis, LinearOpMode opMode) {
        move(counts, -counts, -counts, counts, power, timeoutMillis, opMode);
    }

    void driveForward(double mm, double power, int timeoutMillis, LinearOpMode opMode) {
        int counts = (int) (mm * COUNTS_PER_MM);
        move(counts, counts, counts, counts, power, timeoutMillis, opMode);
    }

    void turnLeft(double degrees, double power, int timeoutMillis, LinearOpMode opMode) {
        int counts = (int) (degrees * COUNTS_PER_DEGREE);
        move(-counts, counts, -counts, counts, power, timeoutMillis, opMode);
    }

    void pause(int ms, LinearOpMode opMode) {
        ElapsedTime runtime = new ElapsedTime();
        runtime.reset();
        while (opMode.opModeIsActive() && runtime.milliseconds() < ms) {
            telemetry.addData("Paused", "%.0fms of %dms",
                    runtime.milliseconds(), ms);
            telemetry.update();
        }
    }

    boolean toPosition(OpenGLMatrix targetPos, LinearOpMode opMode) {
        pause(500, opMode);
        OpenGLMatrix location = getRobotLocation();

        if (location == null) {
            return false;
        } else {
            telemetry.addLine("Location found!");
            telemetry.update();

            VectorF diff = targetPos.getTranslation().subtracted(location.getTranslation());

            float locationAngle = Orientation.getOrientation(location, AxesReference.EXTRINSIC,
                    AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;
            float targetAngle = Orientation.getOrientation(targetPos, AxesReference.EXTRINSIC,
                    AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

            telemetry.addData("Location Angle", locationAngle);
            telemetry.addData("Target Angle", targetAngle);
            telemetry.addData("Location", location.formatAsTransform());
            telemetry.addData("Target", targetPos.formatAsTransform());
            telemetry.update();

            // Go to x of target
            turnLeft(-locationAngle, .3, 5_000, opMode);
            driveForward(diff.getData()[0], .3, 5_000, opMode);

            // Go to y of target
            turnLeft(90, .3, 5_000, opMode);
            driveForward(diff.getData()[1], .3, 5_000, opMode);

            // Go to angle of target
            turnLeft(targetAngle - 90, .3, 5_000, opMode);

            return true;
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
