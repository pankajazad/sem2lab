
/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package rrsimulation;

//~--- JDK imports ------------------------------------------------------------

import java.io.*;

import java.lang.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author pankaj
 */
public class RRSimulation {
    public static final int    NO_OF_OPTIMIZING_ITERATIONS = 10;
    private int                numberOfProcesses;
    private int                intialQuantum;
    private int                quantumStepSize;
    private int                contextSwitchLatency;
    private ArrayList<Process> processes;
    private RRSchduler         scheduler;
    private int processGroupSize;

    public RRSimulation() {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter total no. of Processes... ");
        numberOfProcesses = in.nextInt();
        processes         = new ArrayList<Process>(numberOfProcesses);

        for (int i = 0; i < numberOfProcesses; i++) {
            processes.add(new Process(i));           
        }

        System.out.print("Enter the initial time quantum (in ms)... ");
        intialQuantum = in.nextInt();
        
        System.out.print("Enter the time quantum step size(in ms)... ");
        quantumStepSize = in.nextInt();
        
        System.out.print("Enter the context switch latency (in ms)... ");
        contextSwitchLatency = in.nextInt();
           
        System.out.print("Enter number of Processes in each group... ");
        processGroupSize = in.nextInt();        
        
        System.out.println("---- Generated Input conditions ----");

        // for (Process proc : processes)
        for (int i = 0; i < processes.size(); i++) {
            System.out.println("-------Process Id "+ i+"---------");
            processes.get(i).printDetails();
            
        }

        in.close();
    }

    public void run() {
        double prevCpuUtilization = 0.0,
               cpuUtilization     = 0.0;
        int    quantum            = intialQuantum;

        System.out.println("Initiating Simulation...");
        
        do {
            prevCpuUtilization = cpuUtilization;
            scheduler          = new RRSchduler(processes, contextSwitchLatency);
            cpuUtilization     = scheduler.runPureRR(quantum);
            System.out.println("Quantum = " + quantum + " => CPU Utilization = " + String.format("%.1f",cpuUtilization) + "\n");
            quantum += quantumStepSize;
        } while (cpuUtilization > prevCpuUtilization);
        
        if(cpuUtilization < prevCpuUtilization)
        {
            cpuUtilization = prevCpuUtilization;
            quantum -= quantumStepSize;
        }
        
        System.out.println("\nPure RR Scheduling: With CPU utilization of " + String.format("%.1f %%",cpuUtilization) + ",the optimum quantum size for this mix of cpu & i/o bursts is "+ (quantum - quantumStepSize) + " ms\n");
        
        quantum = intialQuantum;        
        prevCpuUtilization = 0.0;
        cpuUtilization     = 0.0;
        
        do {
            prevCpuUtilization = cpuUtilization;
            scheduler          = new RRSchduler(processes, processGroupSize, contextSwitchLatency);
            cpuUtilization     = scheduler.runRRwithMP(quantum);
            System.out.println("Quantum = " + quantum + " => CPU Utilization = " + String.format("%.1f",cpuUtilization) + "\n");
            quantum += quantumStepSize;
        } while (cpuUtilization > prevCpuUtilization);
        
        
        if(cpuUtilization < prevCpuUtilization)
        {
            cpuUtilization = prevCpuUtilization;
            quantum -= quantumStepSize;
        }
        
        System.out.println("RR with MP Scheduling: With CPU utilization of " + String.format("%.1f %%",cpuUtilization) + ",the optimum quantum size for this mix of cpu & i/o bursts is "+ (quantum - quantumStepSize) + " ms\n");

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.print("\nComputational Lab - Assignment 1: Optimum time for RR Scheduling for a random mix of CPU & I/O Bursts");
	System.out.print("\nBy: Pankaj Azad, Roll Number - 14M517, M.Tech 2nd Sem\n\n\n") ;

        RRSimulation sim = new RRSimulation();

        sim.run();
    }
}

