package world;

import bee.Drone;

import java.util.concurrent.ConcurrentLinkedDeque;

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
    /** The queue of drones waiting to mate with the queen. **/
    private final ConcurrentLinkedDeque<Drone> drones = new ConcurrentLinkedDeque<>();

    /** Status of the queen - is the queen ready to mate - only true once the queen has called {@link #summonDrone()}. **/
    private boolean queenReady = false;

    /**
     * Facilitates the dismissal of any waiting drones. Resets the queen ready status as well.
     */
    public synchronized void dismissDrone() {
        this.queenReady = false;
        notifyAll();
    }

    /**
     * Facilitates the entry of a Drone into the Queen's chamber.
     *
     * <p>Upon entry to the chamber, drones will wait until they are first in the queue and the queen is ready to mate.
     * After a drone is released from waiting and allowed to mate, the queen status is updated to not ready and the
     * first drone used in mating is removed from the chamber queue.</p>
     */
    public synchronized void enterChamber(Drone drone) {

        // Add the provided drone to the chamber
        System.out.println("*QC* " + drone + " enters chamber");
        this.drones.add(drone);

        while(!this.drones.getFirst().equals(drone) || !this.queenReady) {
            // Wait if the queen is already mating with another drone
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.queenReady = false;

        System.out.println("*QC* " + drone + " leaves chamber");
        this.drones.remove(drone);
    }

    /**
     * Expresses the presence of any drones in the queen chamber according to the queue of drones that have entered.
     */
    public synchronized boolean hasDrone() {
        // There is at least one drone if the queue is not empty
        return !this.drones.isEmpty();
    }

    /**
     * Summon a drone to mate with the queen.
     *
     * <p>QueensChamber status is first updated to reflect that the queen is ready - this allows the first drone in the
     * queue to continue through {@link #enterChamber(Drone)}. The first drone is retrieved from the queue and its
     * status is updated. All waiting drones are notified subsequently.</p>
     */
    public synchronized void summonDrone() {

        // Update status so other drones know
        this.queenReady = true;

        // Get the first drone in the queue and mate
        Drone first = this.drones.getFirst();

        first.mate();

        // Cause drone to mate with queen, update drone accordingly
        System.out.println("*QC* Queen mates with " + first);

        // Notify waiting drones
        notifyAll();
    }
}