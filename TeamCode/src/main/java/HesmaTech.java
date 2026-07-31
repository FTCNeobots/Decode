import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.MathLogic;


@TeleOp(name = "HesmaTech")
public class HesmaTech extends OpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;
    DcMotor coreHex;

    double armPower = 0.5;

    double highestPower = 0;
    double leftPower;
    double rightPower;
    double maxSpeed = 1;

    @Override
    public void init() {
        leftMotor = hardwareMap.get(DcMotor.class, "left");
        rightMotor = hardwareMap.get(DcMotor.class, "right");
        coreHex = hardwareMap.get(DcMotor.class, "hex");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void loop() {

        leftPower = gamepad1.left_stick_y + gamepad1.right_stick_x*3;
        rightPower = gamepad1.left_stick_y - gamepad1.right_stick_x*3;

        if(Math.abs(leftPower) < 1 && Math.abs(rightPower) < 1){
            highestPower = 1;
        }else if(Math.abs(leftPower) >= Math.abs(rightPower)){
            highestPower = Math.abs(leftPower);
        }else{
            highestPower = Math.abs(rightPower);
        }

        if(gamepad1.left_bumper){
            maxSpeed = 0.5;
        }
        if(gamepad1.right_bumper){
            maxSpeed = 1;
        }

        leftMotor.setPower(leftPower/highestPower * maxSpeed);
        rightMotor.setPower(rightPower/highestPower * maxSpeed);

        if(gamepad1.a){
            coreHex.setPower(armPower);
        }else if(gamepad1.b){
            coreHex.setPower(-armPower);
        }else{
            coreHex.setPower(0);
        }

    }



}
