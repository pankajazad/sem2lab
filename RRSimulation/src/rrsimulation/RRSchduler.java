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
    ArrayList<Process> processList;
    ArrayList<ArrayList<Process>> processGroupList;
    int numberOfProcesses;
    int contextSwitchLatency;
    int processGroupSize;
    boolean quantumOver = false;
    
    public RRSchduler(ArrayList<Process> processes, int contextSwitchLatency)
    {
        processList = new ArrayList<Process>(processes.size()); 
        for(int i=0;i<processes.size();i++)
        {
            processList.add(i, new Process(processes.get(i)));
        }
        this.contextSwitchLatency = contextSwitchLatency;
    }
    
    public RRSchduler(ArrayList<Process> processes, int processGroupSize, int contextSwitchLatency)
    {
        this.processGroupSize = processGroupSize;
        processGroupList = new ArrayList<ArrayList<Process>>();
        
        int inputListIndex = 0;
        int pgIndex = 0;
        
        do
        {
            processList = new ArrayList<Process>(processGroupSize);
            for (int j=0;j<processGroupSize; j++) 
            {
                if( inputListIndex >= processes.size() )
                    break;
                processList.add(j, new Process(processes.get(inputListIndex++)));
            }
            processGroupList.add(pgIndex++, processList);
        }while(inputListIndex < processes.size());
         
        this.contextSwitchLatency = contextSwitchLatency;
    }
    public double runPureRR(int quantum)
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
    public double runRRwithMP(int quantum)
    {
        int numCpuTicks = 0; // in ms
        int numCpuTicsUtilized = 0;
        while(!processGroupList.isEmpty())
        {
            for( ArrayList<Process> plist : processGroupList)
            {
                numberOfProcesses = plist.size();
                quantumOver = false;
                
                Thread t;
                t = new Thread("Quantum Timer") {
                    public void run() {
                        try {
                            Thread.sleep(quantum);
                        }
                        catch(Exception e) {
                            System.out.println("Exception thrown while quantum elapse");
                        }                       
                        quantumOver = true;                        
                    }
                };
                t.start();
                
                boolean allHalted=true;
                int groupQuantum = quantum;
                int groupQuantumUsed=0;
                
                do
                {

                    allHalted=true;
                    for(Process proc : plist)
                    {
                        int quantumUsed=0;
                        if(proc.state() != Process.ProcessState.HALTED)
                        {
                            quantumUsed = proc.executeWithGivenQuantum(groupQuantum);

                            groupQuantum -= quantumUsed;
                            
                            groupQuantumUsed += quantumUsed;
                            
                            allHalted=false;    
                            
                            if( groupQuantum==0 || quantumOver )
                            {
                                break;
                            }
                        }
                    } //for each process                                         
                }while( groupQuantum!=0 && !quantumOver && !allHalted);

                numCpuTicks += quantum + contextSwitchLatency;
                numCpuTicsUtilized += groupQuantumUsed;
            }
            
            ArrayList<ArrayList<Process>> NewProcessGroupList = new ArrayList<ArrayList<Process>>();
            for (ArrayList<Process> plist : processGroupList)
            {
                ArrayList<Process> newProcessList = new ArrayList<Process>();
                
                for(Process proc : plist )
                {
                    if(proc.state()!=Process.ProcessState.HALTED)
                        newProcessList.add(proc);
                }
                
                if(newProcessList.size()>0)
                    NewProcessGroupList.add(newProcessList);
            }
            
            processGroupList = NewProcessGroupList;
        }//while process list is not empty
        
        return 100.0 * numCpuTicsUtilized/numCpuTicks;
    }
}
