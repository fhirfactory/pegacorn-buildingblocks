package net.fhirfactory.pegacorn.deployment.properties.codebased.jgroups;

import net.fhirfactory.pegacorn.deployment.topology.model.endpoints.edge.InitialHostSpecification;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.JChannel;
import org.jgroups.conf.ProtocolStackConfigurator;
import org.jgroups.protocols.*;
import org.jgroups.protocols.kubernetes.KUBE_PING;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JGroupsStacks {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsStacks.class);

    public static Logger getLogger() {
        return LOG;
    }

    public ProtocolStack getInterZoneProtocolStack(String bindAddress, String bindPort, List<InitialHostSpecification> gossipRouterSet) throws Exception {

        getLogger().debug(".getInterZoneProtocolStack(): Entry, bindAddress->{}, bindPort->{}, gossipRouterSet->{}", bindAddress, bindPort, gossipRouterSet);

        getLogger().trace(".getInterZoneProtocolStack(): Initialising a new stack (ProtocolStack)");
        ProtocolStack interZoneStack = new ProtocolStack();
        getLogger().trace(".getInterZoneProtocolStack(): New stack (ProtocolStack) initialised");

        getLogger().trace(".getInterZoneProtocolStack(): Resolving inetAddress (InetAddress) from provided bindAddress (String)");
        InetAddress inetAddress = InetAddress.getByName(bindAddress);
        getLogger().trace(".getInterZoneProtocolStack(): inetAddress (InetAddress) resolved, value->{}", inetAddress);

        getLogger().trace(".getInterZoneProtocolStack(): Converting port number from bindPort (String) to portNumber (Integer)");
        int portNumber = Integer.parseInt(bindPort);
        getLogger().trace(".getInterZoneProtocolStack(): portNumber (Integer) converted, value ->{}", portNumber);

        getLogger().trace(".getInterZoneProtocolStack(): Creating the TCPGOSSIP Protocol Instance!");
        TCPGOSSIP gossipProtocol = new TCPGOSSIP();
        getLogger().trace(".getInterZoneProtocolStack(): Creating the TCPGOSSIP Protocol Instance: Add Initial Hosts");
        List<InetSocketAddress> initialHostList = new ArrayList<>();
        for(InitialHostSpecification currentHostSpec: gossipRouterSet){
            getLogger().trace(".getInterZoneProtocolStack(): in loop");
            int currentInitialHostPort = Integer.parseInt(currentHostSpec.getPort());
            getLogger().trace(".getInterZoneProtocolStack(): resolved Port");
            InetAddress hostName = InetAddress.getByName(currentHostSpec.getHostName());
            getLogger().trace(".getInterZoneProtocolStack(): resolved HostName");
            InetSocketAddress currentHost = new InetSocketAddress(hostName, currentInitialHostPort);
            initialHostList.add(currentHost);
        }
        getLogger().trace(".getInterZoneProtocolStack(): Creating the TCPGOSSIP Protocol Instance: Add Initial Hosts");
        gossipProtocol.setInitialHosts(initialHostList);
        getLogger().trace(".getInterZoneProtocolStack(): TCPGOSSIP Protocol Instance created");

        getLogger().trace(".getInterZoneProtocolStack(): Creating the NAKACK2 Protocol Instance!");
        NAKACK2 nakack2Protocol = new NAKACK2();
        nakack2Protocol.setUseMcastXmit(false);
        nakack2Protocol.setDiscardDeliveredMsgs(true);
        getLogger().trace(".getInterZoneProtocolStack(): NAKACK2 Protocol Instance created");

        getLogger().trace(".getInterZoneProtocolStack(): Creating the STABLE Protocol Instance!");
        STABLE stableProtocol = new STABLE();
        stableProtocol.setDesiredAverageGossip(50000);
        getLogger().trace(".getInterZoneProtocolStack(): STABLE Protocol Instance created");

        getLogger().trace(".getInterZoneProtocolStack(): Adding the TCP Protocol to the stack!");
        interZoneStack.addProtocol(new TCP()
                        .setValue("bind_addr", inetAddress )
                        .setValue("bind_port", portNumber)
                        .setValue("recv_buf_size", 5000000 )
                        .setValue("send_buf_size", 1000000)
                        .setValue("max_bundle_size", 64000)
                        .setValue("enable_diagnostics", true)
                        .setValue("thread_naming_pattern", "cl"));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the RED Protocol to the stack!");
        interZoneStack.addProtocol(new RED());
        getLogger().trace(".getInterZoneProtocolStack(): Adding the TCPGOSSIP Protocol to the stack!");
        interZoneStack.addProtocol(gossipProtocol);
        getLogger().trace(".getInterZoneProtocolStack(): Adding the MERGE3 Protocol to the stack!");
        interZoneStack.addProtocol(new MERGE3()
                        .setValue("min_interval", 10000)
                        .setValue("max_interval", 30000));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the FD_SOCK Protocol to the stack!");
        interZoneStack.addProtocol(new FD_SOCK());
        getLogger().trace(".getInterZoneProtocolStack(): Adding the FD_ALL Protocol to the stack!");
        interZoneStack.addProtocol(new FD_ALL()
                        .setValue("timeout", 12000)
                        .setValue("interval", 3000));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the VERIFY_SUSPECT Protocol to the stack!");
        interZoneStack.addProtocol(new VERIFY_SUSPECT()
                        .setValue("timeout", 1500));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the BARRIER Protocol to the stack!");
        interZoneStack.addProtocol(new BARRIER());
        getLogger().trace(".getInterZoneProtocolStack(): Adding the NAKACK2 Protocol to the stack!");
        interZoneStack.addProtocol(nakack2Protocol);
        getLogger().trace(".getInterZoneProtocolStack(): Adding the UNICAST3 Protocol to the stack!");
        interZoneStack.addProtocol(new UNICAST3());
        getLogger().trace(".getInterZoneProtocolStack(): Adding the STABLE Protocol to the stack!");
        interZoneStack.addProtocol(stableProtocol);
        getLogger().trace(".getInterZoneProtocolStack(): Adding the GMS Protocol to the stack!");
        interZoneStack.addProtocol(new GMS()
                        .setValue("print_local_addr", true)
                        .setValue("join_timeout", 5000));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the UFC Protocol to the stack!");
        interZoneStack.addProtocol(new UFC()
                        .setValue("max_credits", 2000000)
                        .setValue("min_threshold", 0.4));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the MFC Protocol to the stack!");
        interZoneStack.addProtocol(new MFC()
                        .setValue("max_credits", 2000000)
                        .setValue("min_threshold", 0.4));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the FRAG2 Protocol to the stack!");
        interZoneStack.addProtocol(new FRAG2()
                    .setValue("frag_size", 60000));
        getLogger().trace(".getInterZoneProtocolStack(): Adding the STATE_TRANSFER Protocol to the stack!");
        interZoneStack.addProtocol(new STATE_TRANSFER());
        getLogger().debug(".getInterZoneProtocolStack(): Exit, interZoneStack->{}", interZoneStack);
        return(interZoneStack);
    }

    public ProtocolStack getIntraZoneProtocolStack(String bindAddress, String bindPort, String nameSpace, String labels) throws Exception {
        getLogger().debug(".getIntraZoneProtocolStack(): Entry, bindAddress->{}, bindPort->{}, nameSpace->{}, labels->{}", bindAddress, bindPort, nameSpace, labels);

        getLogger().trace(".getIntraZoneProtocolStack(): Initialising a new stack (ProtocolStack)");
        ProtocolStack intraZoneStack = new ProtocolStack();
        getLogger().trace(".getIntraZoneProtocolStack(): New stack (ProtocolStack) initialised");

        getLogger().trace(".getIntraZoneProtocolStack(): Resolving inetAddress (InetAddress) from provided bindAddress (String)");
        InetAddress inetAddress = InetAddress.getByName(bindAddress);
        getLogger().trace(".getIntraZoneProtocolStack(): inetAddress (InetAddress) resolved, value->{}", inetAddress);

        getLogger().trace(".getIntraZoneProtocolStack(): Converting port number from bindPort (String) to portNumber (Integer)");
        int portNumber = Integer.parseInt(bindPort);
        getLogger().trace(".getIntraZoneProtocolStack(): portNumber (Integer) converted, value ->{}", portNumber);

        getLogger().trace(".getIntraZoneProtocolStack(): Adding the TCP Protocol to the stack!");

        NAKACK2 nakack2Protocol = new NAKACK2();
        nakack2Protocol.setUseMcastXmit(false);
        nakack2Protocol.setDiscardDeliveredMsgs(true);

        STABLE stableProtocol = new STABLE();
        stableProtocol.setDesiredAverageGossip(50000);

        intraZoneStack
                .addProtocol(new TCP()
                        .setValue("bind_addr", inetAddress )
                        .setValue("bind_port", portNumber)
                        .setValue("recv_buf_size", 5000000 )
                        .setValue("send_buf_size", 1000000)
                        .setValue("max_bundle_size", 64000)
                        .setValue("enable_diagnostics", true)
                        .setValue("thread_naming_pattern", "cl"))
                .addProtocol(new RED())
                .addProtocol(new org.jgroups.protocols.kubernetes.KUBE_PING()
                        .setValue("namespace",nameSpace )
                        .setValue("labels", labels))
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL3()
                        .setValue("timeout", 12000)
                        .setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT()
                        .setValue("timeout", 1500))
                .addProtocol(new BARRIER())
                .addProtocol(nakack2Protocol)
                .addProtocol(new UNICAST3())
                .addProtocol(stableProtocol)
                .addProtocol(new GMS()
                        .setValue("print_local_addr", true)
                        .setValue("join_timeout", 5000))
                .addProtocol(new UFC()
                        .setValue("max_credits", 2000000)
                        .setValue("min_threshold", 0.4))
                .addProtocol(new MFC()
                        .setValue("max_credits", 2000000)
                        .setValue("min_threshold", 0.4))
                .addProtocol(new STATE_TRANSFER());
        return(intraZoneStack);
    }

}
