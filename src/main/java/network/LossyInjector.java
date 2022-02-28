package network;

import java.util.Random;

public class LossyInjector implements FaultInjector {
    private double lossRate;

    public LossyInjector(double lossRate) {
        this.lossRate = lossRate;
    }

    @Override
    public boolean shouldFail() {
        Random r = new Random();
        int random = 1 + r.nextInt(10);
        if(random > this.lossRate * 10){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void injectFailure() {

    }
}
