package dv512.gm222hj;
import java.util.ArrayList;
import java.util.Random;

/*
 * File:	RandomScheduling.java
 * Course: 	21HT - Operating Systems - 1DV512
 * Author: 	Gabriele Marinosci - gm222hj
 * Date: 	December 2021
 */


// You can implement additional fields and methods in code below, but
// you are not allowed to rename or remove any of it!

// Additionally, please remember that you are not allowed to use any third-party libraries

public class RandomScheduling {

	ArrayList<ScheduledProcess> waitingQueue = new ArrayList();
	ArrayList<ScheduledProcess> completedProcesses = new ArrayList();
	ScheduledProcess runningProcess = null;
	int executionTime = 0; // keeps track of total number of ticks

	public static class ScheduledProcess {
		int processId;
		int burstTime;
		int arrivalMoment;
		
		// The total time the process has waited since its arrival
		int totalWaitingTime;
		
		// The total CPU time the process has used so far
		// (when equal to burstTime -> the process is complete!)
		int allocatedCpuTime;

		public ScheduledProcess(int processId, int burstTime, int arrivalMoment) {
			this.processId = processId;
			this.burstTime = burstTime;
			this.arrivalMoment = arrivalMoment;
		}
		
		// ... add further fields and methods, if necessary
	}
		
	// Random number generator that must be used for the simulation
	Random rng;

	// ... add further fields and methods, if necessary
		
	public RandomScheduling(long rngSeed) {
		this.rng = new Random(rngSeed);
	}
	
	public void reset() {
		waitingQueue = new ArrayList();
		completedProcesses = new ArrayList();
		runningProcess = null;
		executionTime = 0;
	}
	
	public void runNewSimulation(final boolean isPreemptive, final int timeQuantum,
	    final int numProcesses,
		final int minBurstTime, final int maxBurstTime,
		final int maxArrivalsPerTick, final double probArrival) {

		// keeps track of the number of newly created processes
		int generatedProcesses = 0;
		// counter to check if the current process has reached the timeQuantum
		int preemptiveCount = 0;
		reset();

		while (completedProcesses.size() != numProcesses) {

			for (int i = 0; i < maxArrivalsPerTick; i++) {
				if (this.rng.nextDouble() < probArrival && generatedProcesses < numProcesses) {
					waitingQueue.add(new ScheduledProcess(generatedProcesses, this.rng.nextInt(maxBurstTime - minBurstTime) + minBurstTime, executionTime));
					generatedProcesses++;
				}
			}

			if (!waitingQueue.isEmpty()) {
				if (runningProcess == null) {
					runningProcess = waitingQueue.remove(this.rng.nextInt(waitingQueue.size()));
				}
				updateWaitingTime(waitingQueue);
			}

			if (runningProcess != null) {

				if (isCompleted(runningProcess)) {
					completedProcesses.add(runningProcess);
					runningProcess = null;
				} else if (isPreemptive && preemptiveCount == timeQuantum) {
					waitingQueue.add(runningProcess);
					runningProcess = null;
					preemptiveCount = 0;
				} else {
					runningProcess.allocatedCpuTime++;
					if (isPreemptive) {
						preemptiveCount++;
					}
				}
			}

			executionTime++;
		}
	}
	
	public void printResults() {

		int sumOfWaitingTime = 0;

		System.out.printf("           %17s%17s%17s%n", "Burst Time |", "Arrival Moment |", "Waiting time |");
		System.out.println("--------------------------------------------------------------");
		for (ScheduledProcess p: this.completedProcesses) {
			System.out.printf("Process %d |%15d |%15d |%15d |%n", p.processId, p.burstTime, p.arrivalMoment, p.totalWaitingTime);
			executionTime += p.burstTime;
			sumOfWaitingTime += p.totalWaitingTime;
		}

		System.out.printf("%nExecution time: %d   Average process waiting time: %.2f", executionTime, (double)sumOfWaitingTime / this.completedProcesses.size());
	}


	public boolean isCompleted(ScheduledProcess p) {

		if (p.allocatedCpuTime == p.burstTime) {
			return true;
		}
		return false;
	}


	public void updateWaitingTime(ArrayList<ScheduledProcess> waitingQueue) {

		for (ScheduledProcess process: waitingQueue) {
			process.totalWaitingTime++;
		}
	}


	public static void main(String args[]) {

		final long rngSeed = 19990616;
		
		
		// Do not modify the code below — instead, complete the implementation
		// of other methods!
		RandomScheduling scheduler = new RandomScheduling(rngSeed);
		
		final int numSimulations = 5;
		
		final int numProcesses = 10;
		final int minBurstTime = 2;
		final int maxBurstTime = 10;
		final int maxArrivalsPerTick = 2;
		final double probArrival = 0.75;
		
		final int timeQuantum = 2;

		boolean[] preemptionOptions = {false, true};

		for (boolean isPreemptive: preemptionOptions) {

			for (int i = 0; i < numSimulations; i++) {
				System.out.println("Running " + ((isPreemptive) ? "preemptive" : "non-preemptive")
					+ " simulation #" + i);

				scheduler.runNewSimulation(
					isPreemptive, timeQuantum,
					numProcesses,
					minBurstTime, maxBurstTime,
					maxArrivalsPerTick, probArrival);

				System.out.println("Simulation results:"
					+ "\n" + "----------------------");	
				scheduler.printResults();

				System.out.println("\n");
			}
		}		
		
	}
	
}