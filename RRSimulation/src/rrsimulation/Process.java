/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rrsimulation;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author pankaj
 */
public class Process {
    public enum ProcessState { READY, EXECUTING, IO, HALTED };
    private enum BurstType { CPU, IO };
    private ProcessState currentState;
    private int numCpuBursts, numIoBursts;
    
    private ArrayList<Integer> cpuBurstsList;
    private ArrayList<Integer> ioBurstsList;
    
    private BurstType currentBurst = BurstType.CPU;
    private int currentCpuBurstIndex = 0, currentIoBurstIndex = 0;
    private int id;
    private int totalcpuBursts = 0, totalIoBursts = 0;
    
    public Process(int id)
    {
        currentState = ProcessState.READY;
        numCpuBursts = randInt(1,10);
        numIoBursts = randInt(1,10);
        this.id = id;
        
        cpuBurstsList = new ArrayList<Integer>(numCpuBursts);
        ioBurstsList = new ArrayList<Integer>(numIoBursts);
        
        int temp = 0;
        // randomly initialize the cpu & io burst lists
        for (int i=0; i<numCpuBursts; i++)
        {
            temp = randInt(0,50);
            cpuBurstsList.add(temp);
            totalcpuBursts += temp;
        }
        
        for (int i=0; i<numIoBursts; i++)
        {
            temp = randInt(0,50);
            ioBurstsList.add(temp);
            totalIoBursts += temp;
        }
    }
    
    
    public Process(Process p)
    {
        this.currentState = ProcessState.READY;
        this.numCpuBursts = p.numCpuBursts;
        this.numIoBursts = p.numIoBursts;
        this.id = p.id;
        
        cpuBurstsList = new ArrayList<Integer>(numCpuBursts);
        ioBurstsList = new ArrayList<Integer>(numIoBursts);
        
        // randomly initialize the cpu & io burst lists
        for (int i=0; i<numCpuBursts; i++)
        {            
            cpuBurstsList.add(p.cpuBurstsList.get(i));
        }
        this.totalcpuBursts = p.totalcpuBursts;
        
        for (int i=0; i<numIoBursts; i++)
        {
            ioBurstsList.add(p.ioBurstsList.get(i));
        }
        this.totalIoBursts = p.totalIoBursts;
    }
    
        /**
        * Returns a pseudo-random number between min and max, inclusive.
        * The difference between min and max can be at most
        * <code>Integer.MAX_VALUE - 1</code>.
        *
        * @param min Minimum value
        * @param max Maximum value.  Must be greater than min.
        * @return Integer between min and max, inclusive.
        * @see java.util.Random#nextInt(int)
        */
       public static int randInt(int min, int max) {

           // NOTE: Usually this should be a field rather than a method
           // variable so that it is not re-seeded every call.
           Random rand = new Random();

           // nextInt is normally exclusive of the top value,
           // so add 1 to make it inclusive
           int randomNum = rand.nextInt((max - min) + 1) + min;

           return randomNum;
       }
    
    public double percentageCpuBursts()
    {        
        return 100.0 * totalcpuBursts/( totalcpuBursts + totalIoBursts);
    }
    
    public double percentageIoBursts()
    {        
        return 100.0 * totalIoBursts / (totalcpuBursts + totalIoBursts);
    }
    
    public ProcessState state()
    {
        return currentState;
    }
    public int id()
    {
        return id;
    }
    public ProcessState execute()
    {
        if(cpuBurstsList.isEmpty() && ioBurstsList.isEmpty())
        {
            currentState = ProcessState.HALTED;
            System.out.println("\t\tProcess " + id + " Halted ");
        }
        else
        {
            switch(currentBurst)
            {
               case CPU:
                   if(!cpuBurstsList.isEmpty())
                   {
                        int temp = cpuBurstsList.get(currentCpuBurstIndex);
                        temp--;
                        if(temp<=0)
                        {
                            cpuBurstsList.remove(currentCpuBurstIndex);
                            //currentCpuBurstIndex++;
                            if(!ioBurstsList.isEmpty())
                                currentBurst = BurstType.IO;
                        }
                        else
                        {                        
                            cpuBurstsList.remove(currentCpuBurstIndex);
                            cpuBurstsList.add(currentCpuBurstIndex,temp);
                        }
                        currentState = ProcessState.EXECUTING;
                   }    
                   break;

               case IO:
                   if(!ioBurstsList.isEmpty())
                   {
                        int temp = ioBurstsList.get(currentIoBurstIndex);
                        temp--;
                        if(temp<=0)
                        {
                            ioBurstsList.remove(currentIoBurstIndex);
                            //currentIoBurstIndex++;
                            if(!cpuBurstsList.isEmpty())
                                currentBurst = BurstType.CPU;
                        }
                        else
                        {
                            ioBurstsList.remove(currentIoBurstIndex);
                            ioBurstsList.add(currentIoBurstIndex,temp);
                        }
                        currentState = ProcessState.IO;
                   }    
                   break;
            }
        }
        return currentState;
    }
    
    public void printDetails()
    {
        int maxIndex;
        if(numCpuBursts >= numIoBursts)
            maxIndex = numCpuBursts;
        else
            maxIndex = numIoBursts;
            
        
        System.out. printf("CPU Bursts(%.1f%%) \t\tI/O Bursts(%.1f%%) \n",percentageCpuBursts(),percentageIoBursts());
        for(int i=0;i<maxIndex;i++)
        {
            System.out.println(i + "\t" +((i<numCpuBursts)?cpuBurstsList.get(i):"") + "\t\t\t" + ((i<numIoBursts)?ioBurstsList.get(i):""));
        }
    }
            
}
