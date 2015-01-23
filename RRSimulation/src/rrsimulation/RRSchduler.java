/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rrsimulation;

import java.util.ArrayList;

/**
 *
 * @author pankaj
 */
public class RRSchduler {
    private ArrayList<Process> processList;
    int numberOfProcesses;
    int contextSwitchLatency;
    
    public RRSchduler(ArrayList<Process> processes, int contextSwitchLatency)
    {
        processList = new ArrayList<Process>(processes.size()); 
        for(int i=0;i<processes.size();i++)
        {
            processList.add(i, new Process(processes.get(i)));
        }
        this.contextSwitchLatency = contextSwitchLatency;
    }
    
    public double run(int quantum)
    {
        int numCpuTicks = 0; // in ms
        int numCpuTicsUtilized = 0;
        Process.ProcessState state;
        
        while(!processList.isEmpty())
        {
            numberOfProcesses = processList.size();
            for(Process proc : processList)              
            {
                for(int i=0; i<quantum; i++)
                {
                    state = proc.execute();
                    numCpuTicks++;
                    
                    if(state == Process.ProcessState.EXECUTING)
                        numCpuTicsUtilized++;
                    
                    if(state == Process.ProcessState.HALTED)
                    {
                        numCpuTicsUtilized++;
                        numberOfProcesses--;
                        break;
                    }
                } // quantum for each process
                numCpuTicks += contextSwitchLatency;
            } //for each process   
            ArrayList<Process> newProcessList = new ArrayList<Process>(numberOfProcesses);
            for(Process proc : processList )
                if(proc.state()!=Process.ProcessState.HALTED)
                    newProcessList.add(proc);
            processList = newProcessList;
        }//while process list is not empty
        
        return 100.0 * numCpuTicsUtilized/numCpuTicks;
    }
}
