package Week5;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;


class Philosophers implements Runnable
{
    private static final int NB_PHILOSOPHERS = 5;
    private static final Semaphore[] forks = new Semaphore[NB_PHILOSOPHERS];
    private static Random rand = new Random();
    public List<Philosophers> whoAteAlready = new ArrayList<Philosophers>();

    private int id;

    public Philosophers(int id) {
        this.id = id;
        forks[id] = new Semaphore(1);
    }

    private void nap() {			//Make philosopher thread sleep for a while (e.g. to simulate thinking or eating for a short period)
        try {
            Thread.sleep(rand.nextInt(10) * 1000);
        } catch (InterruptedException e) {
            System.err.println("One philosopher thread died :(");
            System.exit(1);
        };
    }

    private void think() {
        this.nap();
    }

    private void eat() {
        if(whoAteAlready.contains(this)==true)
            return;

        else{
            System.out.println(this + " is hungry.");

            // Try to take fork on the left

            if(forks[id+1].tryAcquire()==false){
                forks[id].release();
            }

            System.out.println(this + " Holding left fork");

            try {
                Thread.sleep(6000);				//Philosopher is pausing before going for the other fork
            } catch (InterruptedException e) {
                System.err.println("One philosopher thread died :(");
                System.exit(1);
            };

            // Try to take fork on the right
            try{
                forks[(id + 1) % NB_PHILOSOPHERS].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(this + " Holding right fork");

            System.out.println(this + " is eating.");
            this.nap();			//Philosopher is eating for a while

            forks[(id + 1) % NB_PHILOSOPHERS].release();
            forks[id].release();

            System.out.println(this + " has finished eating.");
            whoAteAlready.add(this);
        }
    }

    public String toString() {
        return "[Philosopher " + id + "]";
    }

    public void run() {
        System.out.println(this + " enters the dining room.");
        for (int i = 0; i < 5 ; i++) {
            think();
            eat();
        }
    }

    public static void main(String [] args) {
        Thread philosophers[] = new Thread[NB_PHILOSOPHERS];
        System.out.println("Start");

        for (int i = 0 ; i < NB_PHILOSOPHERS ; i++) {
            philosophers[i] = new Thread(new Philosophers(i));
        }

        for (int i = 0 ; i < NB_PHILOSOPHERS ; i++) {
            philosophers[i].start();
        }

        try {
            for (int i = 0 ; i < NB_PHILOSOPHERS ; i++)
                philosophers[i].join();
        }
        catch (InterruptedException e) {
            System.err.println("One philosopher thread died :(");
            System.exit(1);
        }
        System.out.println("End");
    }
}
