package io.muic.ooc.fab;


import io.muic.ooc.fab.view.Observable;
import io.muic.ooc.fab.view.Observer;
import io.muic.ooc.fab.view.SimulatorView;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import java.awt.Color;

public class Simulator extends Observable {

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.08;

    // Lists of animals in the field.
    private List<Actor> actors;
    private ArrayList<Observer> observers = new ArrayList<>();





    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Random generator
    private static final Random RANDOM = new Random();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();

        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        for (ActorType actorType: ActorType.values()){
            view.setColor(actorType.getAnimalClass(),actorType.getColor());
        }

        addObserver(view);
        view.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period
     * (4000 steps).
     */
    public void runLongSimulation() {
        simulate(4000);
    }

    /**
     * Run the simulation for the given number of steps. Stop before the given
     * number of steps if it ceases to be viable.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
             delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step. Iterate over
     * the whole field updating the state of each fox and rabbit.
     */
    public void simulateOneStep() {
        step++;

        // Provide space for newborn rabbits.
        List<Actor> newActors = new ArrayList<>();
        // Let all rabbits act.
        for (Iterator<Actor> it = actors.iterator(); it.hasNext();) {
            Actor animal= it.next();
            animal.act(newActors);

            if (!animal.isAlive()) {
                it.remove();
            }
        }

        // Provide space for newborn foxes.



        // Add the newly born foxes and rabbits to the main lists.
        actors.addAll(newActors);

        notifyAllObservers(step,field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        actors.clear();

        populate();

        // Show the starting state in the view.
        notifyAllObservers(step,field);
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate() {
        
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                //fox prob
                //rabbit prob
                double random =RANDOM.nextDouble();
                double cumulativeProbability= 0;
                for (ActorType actorType : ActorType.values()){
                    cumulativeProbability += actorType.getProbability();
                    if (random <= cumulativeProbability){
                        Location location = new Location(row, col);
                        Actor actor =ActorFactory.createActor(actorType, true, field,location);
                       actors.add(actor);
                       break;
                    }
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     *
     * @param millisec The time to pause for, in milliseconds
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }


    @Override
    protected void notifyAllObservers(int count, Field field) {
            for (Observer observer : observers) {
                observer.update(count, field);
            }
        }


    @Override
    protected void addObserver(Observer observer) {
        observers.add(observer);
    }
}
