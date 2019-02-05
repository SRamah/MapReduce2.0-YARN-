package com.sansthal.hadoop.yarn;

import java.util.Collections;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

public class ApplicationMaster {

  public static void main(String[] args) throws Exception {

    String command = args[0];

    int argi = 0;

    for (argi=1; argi<args.length - 1; argi++) {
      command += " " + args[argi];
    }

    final int n = Integer.valueOf(args[argi]);

    // Initialize clients to ResourceManager and NodeManagers
    Configuration conf = new YarnConfiguration();

    AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
    rmClient.init(conf);
    rmClient.start();

    NMClient nmClient = NMClient.createNMClient();
    nmClient.init(conf);
    nmClient.start();

    // Register with ResourceManager
    rmClient.registerApplicationMaster("", 0, "");
    
    // Priority for worker containers - priorities are intra-application
    Priority priority = Records.newRecord(Priority.class);
    priority.setPriority(0);

    // Resource requirements for worker containers
    Resource capability = Records.newRecord(Resource.class);
    capability.setMemory(128);
    capability.setVirtualCores(1);

    // Make container requests to ResourceManager
    for (int i=1; i <= n; i++) {
      ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
      System.out.println("=> Container Request " + i);
      rmClient.addContainerRequest(containerAsk);
    }

    // Obtain allocated containers and launch 
    int allocatedContainers = 0;
    int comm = 0,fin= 0;
    while (allocatedContainers < n) {
      AllocateResponse response = rmClient.allocate(0);
      for (Container container : response.getAllocatedContainers()) {
        ++allocatedContainers;
        
	if(allocatedContainers!=1)
	comm =  comm + 10/n ;
	if((comm + (10/n) -1)>9 || allocatedContainers==n ) fin=9;
	else fin = comm + (10/n) -1;
	
        // Launch container by create ContainerLaunchContext
        ContainerLaunchContext ctx = 
            Records.newRecord(ContainerLaunchContext.class);
        ctx.setCommands(Collections.singletonList(
                command + " "+allocatedContainers +" "+comm+" "+fin+ 
                " >" + "/home/hduser/Downloads/hadoop-yarn-master/OutPut.txt" + 
                " 2>/home/hduser/Downloads/hadoop-yarn-master/OutPutErr.txt"
                ));
        System.out.println("=> Launching Container " + allocatedContainers);
	nmClient.startContainer(container, ctx);
	
	
	
      }
      Thread.sleep(100);
    }

    // Now work with containers
    for (int container = 0; container < n; container++) {
      rmClient.allocate(0);
    }

    // Un-register with ResourceManager
    rmClient.unregisterApplicationMaster(
        FinalApplicationStatus.SUCCEEDED, "", "");
  }

}
