package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;



@TeleOp(name = "Test")
public class Test extends OpMode {

    private NormalizedColorSensor sensor;
    private float gain = 20;

    @Override
    public void init() {

        sensor = hardwareMap.get(NormalizedColorSensor.class, "sensor");
        sensor.setGain(gain);

    }

    @Override
    public void loop() {

        telemetry.addData("Light Detected", ((OpticalDistanceSensor) sensor).getLightDetected());
        NormalizedRGBA colors = sensor.getNormalizedColors();

        //Determining the amount of red, green, and blue
        telemetry.addData("Red", "%.3f", colors.red);
        telemetry.addData("Green", "%.3f", colors.green);
        telemetry.addData("Blue", "%.3f", colors.blue);
        telemetry.update();




    }
}
