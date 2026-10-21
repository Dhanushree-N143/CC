import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;
import java.util.*;
public class BasicResourceAllocation {
    public static void main(String[] args) throws Exception {
        CloudSim.init(1, Calendar.getInstance(), false);
        List<Host> hosts = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            List<Pe> pe = new ArrayList<>();
            pe.add(new Pe(0, new PeProvisionerSimple(1000)));
            hosts.add(new Host(i,
                new RamProvisionerSimple(2048),
                new BwProvisionerSimple(10000),
                1000000, pe,
                new VmSchedulerTimeShared(pe)));
        }
        DatacenterCharacteristics dc = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hosts,
            10, 3, 0.05, 0.001, 0);
        new Datacenter("Datacenter_0", dc,
            new VmAllocationPolicySimple(hosts),
            new LinkedList<Storage>(), 0);
        DatacenterBroker broker = new DatacenterBroker("Broker");
        int id = broker.getId();
        List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            vms.add(new Vm(i, id, 1000, 1, 1024, 1000,
                10000, "Xen", new CloudletSchedulerTimeShared()));
        }
        broker.submitVmList(vms);
        List<Cloudlet> tasks = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Cloudlet c = new Cloudlet(i, 40000, 1, 300, 300,
                new UtilizationModelFull(),
                new UtilizationModelFull(),
                new UtilizationModelFull());

            c.setUserId(id);
            tasks.add(c);
        }
        broker.submitCloudletList(tasks);
        CloudSim.startSimulation();
        CloudSim.stopSimulation();  
        for (Cloudlet c : broker.getCloudletReceivedList()) {
            System.out.println("Cloudlet: " + c.getCloudletId()
                + " Status: " + c.getStatus()
                + " VM: " + c.getVmId());
        }
    }
}
