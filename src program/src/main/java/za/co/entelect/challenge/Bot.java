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
        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);
        List<Object> nextBlock = blocks.subList(0,1);

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
    private List<Object> getBlocksInFront(int lane, int block) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }

            blocks.add(laneList[i].terrain);

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


}
