package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "ServoTest" )
public class ServoTest extends OpMode {
    private Servo outtakeServo;

    @Override
    public void init() {
        outtakeServo = hardwareMap.get(Servo.class, "outtake");
    }

    @Override
    public void loop() {


        if(gamepad1.a){
            outtakeServo.setPosition(0.4);
        }
        if(gamepad1.b){
            outtakeServo.setPosition(0.7);
        }

    }
}
