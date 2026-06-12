package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "NewAutoBlue")
public class NewAutoBlue extends OpMode {


    private DcMotor intake;
    private DcMotor spindex;
    private DcMotor flywheel;
    private Servo outtakeServo;
    private DigitalChannel servoClosed;
    private Follower follower;
    public double servoOut = 0.4;
    public double servoIn = 0.7;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private double flywheelPower = 0.95;
    private int pathState;

    //Pedro Poses
    private final Pose startPose = new Pose(48, 9, Math.toRadians(0));
    private final Pose scorePose = new Pose(55, 15, Math.toRadians(20));
    private final Pose angle1Pose = new Pose(50, 33, Math.toRadians(180));
    private final Pose pickup1Pose = new Pose(37, 33, Math.toRadians(180)); // Highest (First Set) of Artifacts from the Spike Mark.
    private final Pose pickup2Pose = new Pose(32, 33, Math.toRadians(180));
    private final Pose pickup3Pose = new Pose(15, 33, Math.toRadians(180));
    private final Pose _angle1Pose = new Pose(50, 57, Math.toRadians(180));
    private final Pose _pickup1Pose = new Pose(37, 57, Math.toRadians(180)); // Highest (First Set) of Artifacts from the Spike Mark.
    private final Pose _pickup2Pose = new Pose(32, 57, Math.toRadians(180));
    private final Pose _pickup3Pose = new Pose(15, 57, Math.toRadians(180));
    private final Pose finalPose = new Pose(40, 26, Math.toRadians(180));

    @Override
    public void init() {

        intake = hardwareMap.dcMotor.get("intake");
        spindex = hardwareMap.dcMotor.get("spindex");
        spindex.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spindex.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        outtakeServo = hardwareMap.get(Servo.class, "outtake");

        servoClosed = hardwareMap.get(DigitalChannel.class, "switch");
        servoClosed.setMode(DigitalChannel.Mode.INPUT);

        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);



    }

    @Override
    public void start(){
        outtakeServo.setPosition(servoIn);
        setPathState(0);
        flywheel.setPower(-flywheelPower);
    }

    @Override
    public void loop() {

    }

    private Path scorePreload;
    private PathChain anglePickup1, grabPickup1, grabPickup2, grabPickup3, scorePath2, _anglePickup1, _grabPickup1, _grabPickup2, _grabPickup3, _scorePath2, finalPath;
    public void buildPaths(){
        scorePreload = new Path(new BezierLine(startPose, scorePose));
        scorePreload.setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading());

        anglePickup1 = follower.pathBuilder()
                .addPath(new BezierLine(scorePose, angle1Pose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), angle1Pose.getHeading())
                .build();

        grabPickup1 = follower.pathBuilder()
                .addPath(new BezierLine(angle1Pose, pickup1Pose))
                .setLinearHeadingInterpolation(angle1Pose.getHeading(), pickup1Pose.getHeading())
                .build();

        grabPickup2 = follower.pathBuilder()
                .addPath(new BezierLine(pickup1Pose, pickup2Pose))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), pickup2Pose.getHeading())
                .build();

        grabPickup3 = follower.pathBuilder()
                .addPath(new BezierLine(pickup2Pose, pickup3Pose))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), pickup3Pose.getHeading())
                .build();

        scorePath2 = follower.pathBuilder()
                .addPath(new BezierLine(pickup3Pose, scorePose))
                .setLinearHeadingInterpolation(pickup3Pose.getHeading(), scorePose.getHeading())
                .build();

        _anglePickup1 = follower.pathBuilder()
                .addPath(new BezierLine(scorePose, _angle1Pose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), _angle1Pose.getHeading())
                .build();

        _grabPickup1 = follower.pathBuilder()
                .addPath(new BezierLine(_angle1Pose, _pickup1Pose))
                .setLinearHeadingInterpolation(_angle1Pose.getHeading(), _pickup1Pose.getHeading())
                .build();

        _grabPickup2 = follower.pathBuilder()
                .addPath(new BezierLine(_pickup1Pose, _pickup2Pose))
                .setLinearHeadingInterpolation(_pickup1Pose.getHeading(), _pickup2Pose.getHeading())
                .build();

        _grabPickup3 = follower.pathBuilder()
                .addPath(new BezierLine(_pickup2Pose, _pickup3Pose))
                .setLinearHeadingInterpolation(_pickup2Pose.getHeading(), _pickup3Pose.getHeading())
                .build();

        _scorePath2 = follower.pathBuilder()
                .addPath(new BezierLine(_pickup3Pose, scorePose))
                .setLinearHeadingInterpolation(_pickup3Pose.getHeading(), scorePose.getHeading())
                .build();

        finalPath = follower.pathBuilder()
                .addPath(new BezierLine(scorePose, finalPose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), finalPose.getHeading())
                .build();

    }

    private void StateMachine(){
        switch (pathState){
            case 0:






        }



    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }








}