package org.firstinspires.ftc.teamcode;

import org.opencv.ml.EM;

public class SpindexStructure {

    /**
     * enum which defines the current occupance of the slot
     */
    enum slotState{
        EMPTY,
        GREEN,
        PURPLE
    }

    private slotState slot1 = slotState.EMPTY;
    private slotState slot2 = slotState.EMPTY;
    private slotState slot3 = slotState.EMPTY;


    /**
     * counts how many slots are currently occupied by a ball
     * @return the amount of balls in the spindex
     */
    int GetBallCount(){
        int _counter = 0;

        if(slot1 != slotState.EMPTY){
            _counter += 1;
        }
        if(slot2 != slotState.EMPTY){
            _counter += 1;
        }
        if(slot3 != slotState.EMPTY){
            _counter += 1;
        }
        return _counter;

    }

    /**
     * when a ball is detected in slot1 (the intake slot), it saves that, including the color
     * @param _isBallGreen is a bool
     * @return int: amount of slots to turn, 0 means full spindex
     */
    int Intaking(boolean _isBallGreen){

        //slot 1 is the bottom slot
        if(_isBallGreen){
            slot1 = slotState.GREEN;
        }else{
            slot1 = slotState.PURPLE;
        }

        //looks for a new empty slot to move to, and rotates the slot values accordingly
        if(slot3 == slotState.EMPTY){
            slot3 = slot2;
            slot2 = slot1;
            slot1 = slotState.EMPTY;

            return 1;
        }else if(slot2 == slotState.EMPTY){
            slot2 = slot3;
            slot3 = slot1;
            slot1 = slotState.EMPTY;

            return 2;
        }else{
            return 0;
        }

    }


    /**
     * computes to what position the spindex needs to turn to be able to shoot the deisred ball
     * @param _colorRequested 0 for not important, 1 for purple, 2 for green,
     * @return int: amount of slots to turn, 0 means full spindex, -1 means empty spindex/no ball of that color
     */
    int ShotRequest(int _colorRequested){
        switch (_colorRequested){
            //case for no desired color
            case 0:
                if(slot3 != slotState.EMPTY){
                    return 0;
                }else if(slot2 != slotState.EMPTY){
                    slotState _tempslot1 = slot1;
                    slot1 = slot3;
                    slot3 = slot2;
                    slot2 = _tempslot1;

                    return 1;
                }else if(slot1 != slotState.EMPTY){
                    slotState _tempslot1 = slot1;
                    slot1 = slot2;
                    slot2 = slot3;
                    slot3 = _tempslot1;

                    return 2;
                }else {
                    //spindex is empty
                    return -1;
                }
            //case for purple
            case 1:
                if(slot3 != slotState.PURPLE){
                    return 0;
                }else if(slot2 != slotState.PURPLE){
                    slotState _tempslot1 = slot1;
                    slot1 = slot3;
                    slot3 = slot2;
                    slot2 = _tempslot1;

                    return 1;
                }else if(slot1 != slotState.PURPLE){
                    slotState _tempslot1 = slot1;
                    slot1 = slot2;
                    slot2 = slot3;
                    slot3 = _tempslot1;

                    return 2;
                }else {
                    //spindex has no purple balls
                    return -1;
                }
            //case for green
            case 2:
                if(slot3 != slotState.GREEN){
                    return 0;
                }else if(slot2 != slotState.GREEN){
                    slotState _tempslot1 = slot1;
                    slot1 = slot3;
                    slot3 = slot2;
                    slot2 = _tempslot1;

                    return 1;
                }else if(slot1 != slotState.GREEN){
                    slotState _tempslot1 = slot1;
                    slot1 = slot2;
                    slot2 = slot3;
                    slot3 = _tempslot1;

                    return 2;
                }else {
                    //spindex has no green balls
                    return -1;
                }
        }
        return 0;
    }


    /**
     * moves the spindex 1 time to make sure slot1 is always empty
     * @return
     */
    int ShootCleanup(){
        slot3 = slot2;
        slot2 = slot1;
        slot1 = slotState.EMPTY;

        return 1;
    }

    String printState(){
        return slot1.toString() + " , " + slot2.toString() + " , " + slot3.toString();
    }











}
