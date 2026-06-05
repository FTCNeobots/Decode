package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "TestSpindex")
public class TestSpindex extends OpMode {

    DcMotor spindex;
    DcMotor intake;
    Servo outtakeServo;
    DigitalChannel sensor;
    DigitalChannel outtakeSwitch;
    NormalizedColorSensor colorSensor;
    int deltaSlots = 1;
    double spindexSpeed = 0.4;
    boolean previousSwitchState = false;
    SpindexStructure Spindexer;
    double timeForOuttake;
    double outtakePosIn = 0.7;
    double outtakePosOut = 0.4;
    boolean moveOuttake = false;
    double timeItTakesToOutput = 0.8;
    int outtakeState = 0;

    @Override
    public void init() {
        Spindexer = new SpindexStructure();

        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "sensor");
        spindex = hardwareMap.dcMotor.get("spindex");
        intake = hardwareMap.dcMotor.get("intake");
        sensor = hardwareMap.get(DigitalChannel.class, "spinswitch");
        outtakeSwitch = hardwareMap.get(DigitalChannel.class, "switch");
        outtakeServo = hardwareMap.get(Servo.class, "outtake");
        spindex.setDirection(DcMotorSimple.Direction.REVERSE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);
        spindex.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        spindex.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void start(){
        outtakeServo.setPosition(outtakePosIn);
        colorSensor.setGain(20);

    }

    @Override
    public void loop() {

        Intaking();
        TurnSpindex();
        Outtake();


        telemetry.addData("Ball count: ", Spindexer.GetBallCount());
        telemetry.addData("Delta slots: ", deltaSlots);
        telemetry.addData("Is there a ball ", BallAccordingToColorSensor());
        telemetry.addData("Outtake switch position ", outtakeSwitch.getState());
        telemetry.addData("Outtake state ", outtakeState);
        telemetry.addLine(Spindexer.printState());

        telemetry.update();
    }

    /**
     * only returns true on edge
     * @return bool: edge detection
     */
    boolean RisingEdgeSwitch(){
        if(sensor.getState() && !previousSwitchState){
            previousSwitchState = true;
            return true;
        }
        previousSwitchState = sensor.getState();
        return false;
    }

    /**
     * controls the intake
     */
    void Intaking(){

        if(gamepad1.right_trigger > 0 && Spindexer.GetBallCount() < 3){
            intake.setPower(1);
            if(BallAccordingToColorSensor() && deltaSlots == 0){
                deltaSlots = Spindexer.Intaking(ColorSenseIsGreen());
            }
        }else{
            intake.setPower(0);
        }


    }

    /**
     * checks whether the color sensor sees a ball
     * @return bool: true if ball
     */
    boolean BallAccordingToColorSensor(){
        if( ((DistanceSensor) colorSensor).getDistance(DistanceUnit.CM) < 2.5){
            return true;
        }else{
            return false;
        }
    }

    /**
     * checks whether the ball is green
     * @return bool: true for green
     */
    public boolean ColorSenseIsGreen(){
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        if(colors.green > colors.blue){
            return true;
        }else{
            return false;
        }

    }

    /**
     * does the logic which starts and stops the spindexer, to make it turn properly
     */
    void TurnSpindex(){
        if(deltaSlots <= 0 || !outtakeSwitch.getState() || outtakeState != 0){
            spindex.setPower(0);
        }else {
            spindex.setPower(spindexSpeed);
        }

        if(RisingEdgeSwitch()){
            deltaSlots -= 1;
        }
    }

    void Outtake(){
        if(gamepad1.a && outtakeState == 0 && spindex.getPower() == 0){
            deltaSlots = Spindexer.ShotRequest(0);
            if(deltaSlots == 0 && outtakeState == 0){
                moveOuttake = true;
            }
        }

        switch (outtakeState){
            case 0:
                outtakeServo.setPosition(outtakePosIn);
                if(moveOuttake){
                    outtakeState = 5;
                    timeForOuttake = getRuntime();
                    outtakeServo.setPosition(outtakePosOut);
                    moveOuttake = false;
                }
                break;
            case 5:
                outtakeServo.setPosition(outtakePosOut);
                if((getRuntime()-timeForOuttake) >= timeItTakesToOutput){
                    outtakeState = 10;
                }
                break;
            case 10:
                outtakeServo.setPosition(outtakePosIn);
                if(outtakeSwitch.getState()){
                    outtakeState = 0;
                    //the outtake is back in, the spindex can clear up slot1
                    deltaSlots = Spindexer.ShootCleanup();
                }
                break;
        }


    }






}
