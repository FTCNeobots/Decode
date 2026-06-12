package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "TeleRed")
public class TeleRed extends OpMode {

    DcMotor spindex;
    DcMotor intake;
    private DcMotor leftFrontDrive;
    private DcMotor rightFrontDrive;
    private DcMotor leftBackDrive;
    private DcMotor rightBackDrive;
    private DcMotorEx flywheel;
    Servo outtakeServo;
    DigitalChannel sensor;
    DigitalChannel outtakeSwitch;
    NormalizedColorSensor colorSensor;
    IMU imu;
    GoBildaPinpointDriver pinpoint;
    private double maxSpeed = 1;
    private double botHeading;
    private double turnSpeed = 1;
    int deltaSlots = 1;
    double spindexSpeed = 0.4;
    boolean previousSwitchState = false;
    SpindexStructure Spindexer;
    double timeForOuttake;
    double outtakePosIn = 0.7;
    double outtakePosOut = 0.4;
    boolean moveOuttake = false;
    double timeItTakesToOutput = 0.3;
    int outtakeState = 0;
    boolean readyForNextOuttake = true;
    private Limelight3A limelight3A;
    private double xCorrection = 0;
    private double yCorrection = 0;
    private boolean aimAssistInPosition = false;
    double RPMtoTPS = 28.0/60.0;
    double TPStoRPM = 60.0/28.0;
    double flywheelSpeedFar = 5600 * RPMtoTPS;
    double flywheelSpeedClose = 4500 * RPMtoTPS;
    double flywheelSpeed = flywheelSpeedFar;

    @Override
    public void init() {
        leftBackDrive = hardwareMap.dcMotor.get("leftBack");
        leftFrontDrive = hardwareMap.dcMotor.get("leftFront");
        rightBackDrive = hardwareMap.dcMotor.get("rightBack");
        rightFrontDrive = hardwareMap.dcMotor.get("rightFront");
        //rightFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        //rightBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        //dit hoort niet te hoeven, maar zo werkt het?
        leftBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        leftFrontDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setDirection(DcMotorSimple.Direction.REVERSE);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidfFlywheel = new PIDFCoefficients(30, 0.1, 0, 11);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfFlywheel);

        imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        imu.initialize(parameters);



        limelight3A = hardwareMap.get(Limelight3A.class, "limelight");
        limelight3A.pipelineSwitch(4); //April Tags red

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.setOffsets(-3.42, -6.77, DistanceUnit.INCH);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

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
        limelight3A.start();
        outtakeServo.setPosition(outtakePosIn);
        colorSensor.setGain(20);
        flywheel.setVelocity(flywheelSpeed);

        pinpoint.resetPosAndIMU();

    }

    @Override
    public void loop() {
        pinpoint.update();

        if (gamepad1.back) {
            pinpoint.resetPosAndIMU();
            telemetry.addData("Yaw ", "reset!");
            imu.resetYaw();
        }
        botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        if(gamepad1.a){
            AimAssist();
        }else{
            NormalDrive(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            aimAssistInPosition = false;
        }

        Intaking();
        TurnSpindex();
        Outtake();
        FlywheelControl();


        telemetry.addData("Ball count: ", Spindexer.GetBallCount());
        telemetry.addData("Delta slots: ", deltaSlots);
        telemetry.addData("Is there a ball ", BallAccordingToColorSensor());
        telemetry.addData("Outtake switch position ", outtakeSwitch.getState());
        telemetry.addData("Outtake state ", outtakeState);
        telemetry.addData("Current RPM: ", flywheel.getVelocity() * TPStoRPM);
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
            if(BallAccordingToColorSensor() && deltaSlots <= 0){
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
        if(RisingEdgeSwitch()){
            deltaSlots -= 1;
        }

        if(deltaSlots <= 0 || !outtakeSwitch.getState() || outtakeState != 0 || gamepad1.y){
            spindex.setPower(0);
        }else {
            spindex.setPower(spindexSpeed);
        }

    }

    void Outtake(){
        if((aimAssistInPosition || gamepad1.b) && outtakeState == 0 && spindex.getPower() == 0 && readyForNextOuttake){
            deltaSlots = Spindexer.ShotRequest(0);
            if(deltaSlots == 0 && outtakeState == 0){
                moveOuttake = true;
            }
        }

        switch (outtakeState){
            case 0:
                outtakeServo.setPosition(outtakePosIn);
                readyForNextOuttake = true;
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
                    readyForNextOuttake = false;
                    //the outtake is back in, the spindex can clear up slot1
                    deltaSlots = Spindexer.ShootCleanup();
                }
                break;
        }


    }

    private void NormalDrive(double _Xget, double _Yget, double _Turnget) {

        if(gamepad1.left_trigger > 0){
            maxSpeed = -1;
            turnSpeed = 1;
        }else if(gamepad1.left_bumper){
            maxSpeed = -0.25;
            turnSpeed = 2;

        }else{
            maxSpeed = -0.5;
            turnSpeed = 2;
        }

        double _X = _Xget * Math.cos(botHeading) - _Yget * Math.sin(botHeading);
        double _Y = _Xget * Math.sin(botHeading) + _Yget * Math.cos(botHeading);
        ///die negatief hoort niet te hoeven, maar helpt wel
        double _Turn = -_Turnget * turnSpeed;
        _X = _X * 1.1;



        double _LFSpeed = MathLogic.Clamp(_Y - _X + _Turn, -1, 1) * maxSpeed;
        double _LBSpeed = MathLogic.Clamp(_Y + _X + _Turn, -1, 1) * maxSpeed;
        double _RBSpeed = MathLogic.Clamp(_Y - _X - _Turn, -1, 1) * maxSpeed;
        double _RFSpeed = MathLogic.Clamp(_Y + _X - _Turn, -1, 1) * maxSpeed;

        leftFrontDrive.setPower(_LFSpeed);
        leftBackDrive.setPower(_LBSpeed);
        rightBackDrive.setPower(_RBSpeed);
        rightFrontDrive.setPower(_RFSpeed);

    }

    private void AimAssist(){
        double pX = 0.015;
        double targetYaw = -45 * 3.141592654 / 180;
        double targetX;
        double targetA;
        double feedforward = 0.05;
        double deadZone = 2;
        double positionFar = 0.5;
        double positionClose = 0.7;
        double xOffset = 0;
        maxSpeed = -1;


        LLResult llResult = limelight3A.getLatestResult();
        if(llResult != null && llResult.isValid()){
            if(llResult.getTa() < 0.5){
                targetX = 0;
                targetA = 0.34;
                flywheelSpeed = flywheelSpeedFar;

            }else{
                targetX = 0;
                targetA = 0;
                flywheelSpeed = flywheelSpeedClose;
            }

            if((llResult.getTx() - targetX) > (deadZone + xOffset)){

                xCorrection = feedforward + (llResult.getTx() - targetX) * pX;
                aimAssistInPosition = false;

            }else if((llResult.getTx() - targetX) < (-deadZone + xOffset)){

                xCorrection = -feedforward + (llResult.getTx() - targetX) * pX;
                aimAssistInPosition = false;

            }else{
                xCorrection = 0;
                yCorrection = 0;


                aimAssistInPosition = true;
            }
        }else{
            if((targetYaw) < botHeading && (3.141592654 + targetYaw) > botHeading){
                xCorrection = 0.5;

            }else{
                xCorrection = -0.5;

            }

            yCorrection = 0;

            aimAssistInPosition = false;
        }


        double _LFSpeed = MathLogic.Clamp(yCorrection - xCorrection, -1, 1) * maxSpeed;
        double _LBSpeed = MathLogic.Clamp(yCorrection - xCorrection, -1, 1) * maxSpeed;
        double _RBSpeed = MathLogic.Clamp(yCorrection + xCorrection, -1, 1) * maxSpeed;
        double _RFSpeed = MathLogic.Clamp(yCorrection + xCorrection, -1, 1) * maxSpeed;

        leftFrontDrive.setPower(_LFSpeed);
        leftBackDrive.setPower(_LBSpeed);
        rightBackDrive.setPower(_RBSpeed);
        rightFrontDrive.setPower(_RFSpeed);

    }
    void FlywheelControl(){
        if(gamepad1.dpad_left){
            flywheelSpeed = flywheelSpeedClose;
        }
        if(gamepad1.dpad_right){
            flywheelSpeed = flywheelSpeedFar;
        }
        if(gamepad1.dpad_down){
            flywheelSpeed = 0;
        }

        flywheel.setVelocity(flywheelSpeed);
    }





}
