package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by Zach on 9/8/18.
 */

@TeleOp
public class VuforiaTest extends OpMode {
    private VuforiaTrackable relicTemplate;

    @Override
    public void init() {
        telemetry.addLine("Initializing Vuforia");
        telemetry.update();
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = "AZLv+a7/////AAAAGdyzndpq4khMnz5IMjSvhiR0XbtOlL7ZfQytGj9s" +
                "4zFCFoa+IqUA1Cjv4ghfSjfRAlRguu6cVbQVM+0Rxladi3AIKhUjIL6v5ToFrK/fxrWdwAzkQfEPM1S" +
                "3ijrTSm1N8DuZ6UoqiKoVmQGzyiWhDpTQoR1zIVkj88rOhBDYwBf0CnW++pxZ0pHlQBbh/bzBjt63AN" +
                "cuI9JyHU3/JLGSBhoIm04G3UnrjVrjKfPFlX9NOwWQLOYjQ+4B1l4M8u9BdihYgmfMST0BHON+MQ7qC" +
                "5dMs/2OSZlSKSZISN/L+x606xzc2Sv5G+ULUpaUiChG7Zlv/rncu337WhZjJ1X2pQGY7gIBcSH+TUw8" +
                "1n2jYKkm";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        VuforiaLocalizer vuforiaLocalizer = ClassFactory.createVuforiaLocalizer(parameters);
        VuforiaTrackables navTargets = vuforiaLocalizer.loadTrackablesFromAsset("Rover-Ruckus");

        VuforiaTrackable frontWall = navTargets.get(0);
        VuforiaTrackable redWall = navTargets.get(1);
        VuforiaTrackable backWall = navTargets.get(2);
        VuforiaTrackable blueWall = navTargets.get(3);

        frontWall.setName("FrontWall");
        redWall.setName("RedWall");
        backWall.setName("BackWall");
        blueWall.setName("BlueWall");

        telemetry.addLine("Initialized Vuforia");
        telemetry.update();
    }

    @Override
    public void loop() {
    }
}
