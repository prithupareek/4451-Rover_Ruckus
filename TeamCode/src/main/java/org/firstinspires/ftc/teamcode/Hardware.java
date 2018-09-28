package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

public class Hardware {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    DcMotor frontLeft, frontRight, backLeft, backRight;
    private DcMotor leftSlide, rightSlide;
    private CRServo leftGrabber, rightGrabber;

    private VuforiaTrackables navTargets;
    private OpenGLMatrix lastPos;

    Hardware(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

        frontLeft    = hardwareMap.dcMotor.get("frontLeft");
        frontRight   = hardwareMap.dcMotor.get("frontRight");
        backLeft     = hardwareMap.dcMotor.get("backLeft");
        backRight    = hardwareMap.dcMotor.get("backRight");
        leftSlide    = hardwareMap.dcMotor.get("leftSlide");
        rightSlide   = hardwareMap.dcMotor.get("rightSlide");
        leftGrabber  = hardwareMap.crservo.get("leftGrabber");
        rightGrabber = hardwareMap.crservo.get("rightGrabber");

        leftSlide    .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightSlide   .setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft    .setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft     .setDirection(DcMotorSimple.Direction.REVERSE);
        rightSlide   .setDirection(DcMotorSimple.Direction.REVERSE);
        rightGrabber .setDirection(DcMotorSimple.Direction.REVERSE);
    }

    void setLinearSlidePower(float power) {
        leftSlide.setPower(power);
        rightSlide.setPower(power);
    }

    void setGrabberPower(double power) {
        leftGrabber.setPower(power);
        rightGrabber.setPower(power);
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
        VuforiaTrackable redTarget = navTargets.get(1);
        VuforiaTrackable backTarget = navTargets.get(2);
        VuforiaTrackable blueTarget = navTargets.get(3);

        frontTarget.setName("FrontWall");
        redTarget.setName("RedWall");
        backTarget.setName("BackWall");
        blueTarget.setName("BlueWall");

        OpenGLMatrix frontLocation = OpenGLMatrix
                .translation(0, -1828.8f, 146.05f)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 180, 0
                ));
        frontTarget.setLocation(frontLocation);
        telemetry.addData("Front Target", frontLocation.formatAsTransform());

        OpenGLMatrix redLocation = OpenGLMatrix
                .translation(1828.8f, 0, 146.05f)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, -90, 0
                ));
        redTarget.setLocation(redLocation);
        telemetry.addData("Red Target", redLocation.formatAsTransform());

        OpenGLMatrix backLocation = OpenGLMatrix
                .translation(0, 1828.8f, 146.05f)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 0, 0
                ));
        backTarget.setLocation(backLocation);
        telemetry.addData("Back Target", backLocation.formatAsTransform());

        OpenGLMatrix blueLocation = OpenGLMatrix
                .translation(-1828.8f, 0, 146.05f)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 90, 90, 0
                ));
        blueTarget.setLocation(blueLocation);
        telemetry.addData("Blue Target", blueLocation.formatAsTransform());

        // TODO Phone location when the robot is built
        OpenGLMatrix phoneLocation = OpenGLMatrix
                .translation(0, 0, 0)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XZX,
                        AngleUnit.DEGREES, 0, 0, 0
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

    OpenGLMatrix getRobotLocation() {
        for (VuforiaTrackable trackable : navTargets) {
            OpenGLMatrix pos = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
            if (pos != null) {
                lastPos = pos;
            }
        }
        return lastPos;
    }
}
