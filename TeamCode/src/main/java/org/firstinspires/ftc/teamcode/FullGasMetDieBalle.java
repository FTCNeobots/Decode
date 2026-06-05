package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "FullGasMetDieBalle")
public class FullGasMetDieBalle extends OpMode {

    private DcMotor spindex;

    @Override
    public void init() {
        spindex = hardwareMap.dcMotor.get("spindex");
    }

    @Override
    public void loop() {

        if(gamepad1.a){
            spindex.setPower(1);
        }else if(gamepad1.b){
            spindex.setPower(-1);
        }else{
            spindex.setPower(0);
        }

    }
}
