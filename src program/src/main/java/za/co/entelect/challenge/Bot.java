package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.enums.PowerUps;

import java.util.*;

import static java.lang.Math.max;
import java.security.SecureRandom;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();

    private final Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;
    private static boolean pilih;
    private final static Command FIX = new FixCommand();
    private final static Command DO_NOTHING = new DoNothingCommand();
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
        this.opponent = gameState.opponent;

        directionList.add(-1);
        directionList.add(1);
    }

    public Command run() {
        List<Lane> frontblocks = new ArrayList<>();
        frontblocks = getBlocksInFront(myCar.position.lane, myCar.position.block,gameState);
        int frontSpeedReduction;
        int leftSpeedReduction;
        int rightSpeedReduction;

        frontSpeedReduction = countSpeedReduction(frontblocks, Bot.maxSpeed);
        /*jika ada daerah lumpur, oli, atau dinding */
        if(frontblocks.contains(Terrain.MUD) || frontblocks.contains(Terrain.OIL_SPILL) || frontblocks.contains(Terrain.WALL)){
            if(PunyaPower(PowerUps.LIZARD, myCar.powerups)){
                return LIZARD;
            }

            if (frontblocks.contains(Terrain.MUD) || frontblocks.contains(Terrain.OIL_SPILL) || frontblocks.contains(Terrain.WALL)){
                int i = random.nextInt(directionList.size());
                return new ChangeLaneCommand(directionList.get(i));
            }
        }        
        if (myCar.position.lane > 1) {
            List<Lane> leftblocks = getBlocksOnLeft(myCar.position.lane, myCar.position.block, gameState);
            leftSpeedReduction = countSpeedReduction(leftBlocks, Bot.maxSpeed);
        } else {
            List<Lane> leftblocks = new ArrayList<>();
            leftSpeedReduction = 99;
        }
        if (myCar.position.lane < 4) {
            List<Lane> rightblocks = getBlocksOnRight(myCar.position.lane, myCar.position.block, gameState);
            rightSpeedReduction = countSpeedReduction(rightBlocks, Bot.maxSpeed);
        } else {
            List<Lane> rightblocks = new ArrayList<>();
            rightSpeedReduction = 99;
        }
       

        if (myCar.damage >= 2) {
            return FIX;
        }else{
            if(myCar.speed < 15){
                int speed = countSpeedReduction(blocks, 15);
                if (speed == 0){
                    if(PunyaPower(PowerUps.BOOST, myCar.powerups)){
                        if(myCar.speed == 3){
                            return BOOST;
                        }else{
                            if (myCar.speed < maxSpeed){
                                return ACCELERATE;
                            }else{
                                if(opponent.position.block > myCar.position.block){
                                    if(PunyaPower(PowerUps.EMP, myCar.powerups)){
                                        return EMP;
                                    }else if(PunyaPower(PowerUps.TWEET, myCar.powerups)){
                                        return new TweetCommand(opponent.position.lane, opponent.position.block+1);
                                    }else{
                                        return DO_NOTHING;
                                    }
                                }else{
                                    if(PunyaPower(PowerUps.OIL, myCar.powerups)){
                                        return OIL;
                                    }
                                }
                            }
                        }
                    }else{
                        if(myCar.speed < maxSpeed){
                            return ACCELERATE;
                        }else{
                            if(opponent.position.block > myCar.position.block){
                                if(PunyaPower(PowerUps.EMP, myCar.powerups)){
                                    return EMP;
                                }else if(PunyaPower(PowerUps.TWEET, myCar.powerups)){
                                    return new TweetCommand(opponent.position.lane, opponent.position.block+1);
                                }else{
                                    return DO_NOTHING;
                                }
                            }else{
                                if(PunyaPower(PowerUps.OIL, myCar.powerups)){
                                    return OIL;
                                }else{
                                    return DO_NOTHING;
                                }
                            }
                        }
                    }
                }
                else{
                    if(myCar.speed < maxSpeed){
                        if(frontSpeedReduction <= leftSpeedReduction || frontSpeedReduction <= rightSpeedReduction){
                            return ACCELERATE;
                        }else{
                            if(leftSpeedReduction < rightSpeedReduction){
                                return TURN_LEFT;
                            }else{
                                return TURN_RIGHT;
                            }
                        }
                    }else{
                        if(leftSpeedReduction > 0 && rightSpeedReduction == 0){
                            return TURN_RIGHT;
                        }else if(leftSpeedReduction ==0 && rightSpeedReduction > 0){
                            return TURN_LEFT;
                        }else if(leftSpeedReduction ==0 && rightSpeedReduction == 0){
                            Random random = new Random();
                            int x = random.nextInt(2);
                            if (x == 0){
                                return TURN_LEFT;
                            }else{
                                return TURN_RIGHT;
                            }
                        }else{
                            if(PunyaPower(PowerUps.LIZARD, myCar.powerups)){
                                return LIZARD;
                            }else{
                                if(frontSpeedReduction < leftSpeedReduction || frontSpeedReduction < rightSpeedReduction){
                                    if(opponent.position.block > myCar.position.block){
                                        if(PunyaPower(PowerUps.EMP, myCar.powerups)){
                                            return EMP;
                                        }else if(PunyaPower(PowerUps.TWEET, myCar.powerups)){
                                            return new TweetCommand(opponent.position.lane, opponent.position.block+1);
                                        }else{
                                            return DO_NOTHING;
                                        }
                                    }else{
                                        if(PunyaPower(PowerUps.OIL, myCar.powerups)){
                                            return OIL;
                                        }else{
                                            return DO_NOTHING;
                                        }
                                    }
                                }else{
                                    if(leftSpeedReduction < rightSpeedReduction){
                                        return TURN_LEFT;
                                    }else{
                                        return TURN_RIGHT;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new AccelerateCommand();
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/
    private List<Object> getBlocksInFront(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Lane> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = block - startBlock-1; i <= block - startBlock + 16; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i]);

        }
        return blocks;
    }
    private List<Object> getBlocksOnLeft(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Lane> blocks = new ArrayList<>();
        int startBlock2 = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 2);
        for (int j = block - startBlock2; j <= block - startBlock2 + 15; j++) {
            if (laneList[j] == null || laneList[j].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[j]);
        }
        return blocks;
    }

    private List<Object> getBlocksOnRight(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Lane> blocks = new ArrayList<>();
        int startBlock3 = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane);
        for (int k = block - startBlock3; k <= block - startBlock3 + 15; k++) {
            if (laneList[k] == null || laneList[k].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[k]);
        }
        return blocks;
    }


    private boolean PunyaPower(PowerUps Will_Use, PowerUps[] available){
        for(PowerUps powerUp : available){
            if(powerUp.equals(Will_Use)) {
                return true;
            }
        }
        return false;
    }
    
    private int getState(int maxSpeed) {
        int State = 0;
        switch (maxSpeed) {
            case 15:
                State = 6;
                break;
            case 9:
                State = 5;
                break;
            case 8:
                State = 4;
                break;
            case 6:
                State = 3;
                break;
            case 5:
                State = 2;
                break;
            case 3:
                State = 1;
                break;
        }
        return State;
    }
   
    
    private int countSpeedReduction(List<Lane> blocks, int maxSpeed) {
        int tmp= maxSpeed,jml=0;
        if (maxSpeed > blocks.size()) {
            tmp = blocks.size();
        }
        for (int j = 0; j < tmp; j++) {
            if (blocks.get(j).terrain == Terrain.MUD || blocks.get(j).terrain == Terrain.OIL_SPILL) {
                jml += 1;
            } else if (blocks.get(j).terrain == Terrain.WALL || blocks.get(j).isOccupiedByCyberTruck) {
                jml += 5;
            }
            if (jml > 5) {
                jml = 5;
            }
        }
        if (getState(maxSpeed) - jml == 2) {
            jml += 1;
        } else if (getState(maxSpeed) - jml < 1) {
            jml = getState(maxSpeed) - 1;
        }
        return jml;
    }


}
