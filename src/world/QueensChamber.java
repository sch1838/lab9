package world;

import bee.Bee;
import bee.Drone;
import bee.Queen;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The queen's chamber is where the mating ritual between the queen and her
 * drones is conducted.  The drones will enter the chamber in order.
 * If the queen is ready and a drone is in here, the first drone will
 * be summoned and mate with the queen.  Otherwise the drone has to wait.
 * After a drone mates they perish, which is why there is no routine
 * for exiting (like with the worker bees and the flower field).
 *
 * @author Sean Strout @ RIT CS
 * @author Samuel Henderson
 */
public class QueensChamber {
    private final ConcurrentLinkedDeque<Drone> drones = new ConcurrentLinkedDeque<>();

    private boolean queenMating = false;

    public synchronized void dismissDrone() {

    }

    public synchronized void enterChamber(Drone drone) {
        // Add the provided drone to the chamber
        System.out.println("*QC* " + drone + " enters chamber");
        this.drones.add(drone);

        if(!this.drones.getFirst().equals(drone) || this.queenMating) {
            // Wait if the queen is already mating with another drone
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("*QC* " + drone + " leaves chamber");
    }

    /**
     * Expresses the presence of any drones in the queen chamber according to the queue of drones that have entered.
     */
    public synchronized boolean hasDrone() {
        // There is at least one drone if the queue is not empty
        return !this.drones.isEmpty();
    }

    public synchronized void summonDrone() {
        // Update status so other drones know
        this.queenMating = true;

        // Get the first drone in the queue
        Drone first = this.drones.getFirst();
        this.drones.remove(first);

        // Cause drone to mate with queen, update drone accordingly
        System.out.println("*QC* Queen mates with " + first);
        first.mate();

        // Update status and notify waiting drones
        this.queenMating = false;
        notifyAll();
    }
}