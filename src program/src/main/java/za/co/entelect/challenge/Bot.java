package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.Terrain;
import za.co.entelect.challenge.enums.PowerUps;

import java.util.*;

import static java.lang.Math.max;

public class Bot {

    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    private GameState gameState;
    private Car opponent;
    private Car myCar;
    private static boolean pilih;
    private final static Command FIX = new FixCommand();
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
        List<Lane> blocks = new ArrayList<>();
        blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);

        if (myCar.damage >= 5) {
            return FIX;
        }

        /*jika ada daerah lumpur, oli, atau dinding */
        if(blocks.contains(Terrain.MUD) || blocks.contains(Terrain.OIL_SPILL) || blocks.contains(Terrain.WALL)){
            if(PunyaPower(PowerUps.LIZARD, myCar.powerups)){
                return LIZARD;
            }

            if (nextBlock.contains(Terrain.MUD) || nextBlock.contains(Terrain.OIL_SPILL) || nextBlock.contains(Terrain.WALL)){
                int i = random.nextInt(directionList.size());
                return new ChangeLaneCommand(directionList.get(i));
            }
        }

        /*menggunakan command */
        if (myCar.speed <= 3){
            return ACCELERATE;
        }
        /*menggunakan boost */
        if(PunyaPower(PowerUps.BOOST, myCar.powerups)){
            return BOOST;
        }
        if(myCar.speed == maxSpeed){
            if(PunyaPower(PowerUps.OIL, myCar.powerups)){
                return OIL;
            }
            if(PunyaPower(PowerUps.EMP, myCar.powerups)){
                return EMP;
            }
        }

        if(PunyaPower(PowerUps.TWEET, myCar.powerups)){
            int x = opponent.position.block;
            int y = opponent.position.lane;
            return new TweetCommand(y, x +1 );
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
    
    private int getdState(int SpeedMax) {
        int state = 0;
        switch (SpeedMax) {
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
   
    
    private int countSpeedReduction(List<Lane> blocks, int SpeedMax) {
        int tmp= SpeedMax,jml=0;
        if (SpeedMax > blocks.size()) {
            tmp = blocks.size();
        }
        for (int j = 0; j < tmp; l++) {
            if (blocks.get(j).terrain == Terrain.MUD || blocks.get(j).terrain == Terrain.OIL_SPILL) {
                jml += 1;
            } else if (blocks.get(j).terrain == Terrain.WALL || blocks.get(j).isOccupiedByCyberTruck) {
                jml += 5;
            }
            if (jml > 5) {
                jml = 5;
            }
        }
        if (getState(SpeedMax) - jml == 2) {
            jml += 1;
        } else if (getState(SpeedMax) - jml < 1) {
            jml = getState(SpeedMax) - 1;
        }
        return jml;
    }


}
