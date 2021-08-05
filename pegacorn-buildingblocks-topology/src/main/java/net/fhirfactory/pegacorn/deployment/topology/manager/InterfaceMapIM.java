/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.deployment.topology.manager;

import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterface;
import net.fhirfactory.pegacorn.deployment.topology.model.common.IPCInterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class InterfaceMapIM {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceMapIM.class);

    private ConcurrentHashMap<String, IPCInterface> interfaces;

    public InterfaceMapIM(){
        this.interfaces = new ConcurrentHashMap<>();
    }

    public Map<String, IPCInterface> getInterfaces() {
        return (interfaces);
    }

    public void setInterfaces(ConcurrentHashMap<String, IPCInterface> interfaces) {
        this.interfaces = interfaces;
    }

    public Set<IPCInterface> getSupportedInterfaceRealisations(String interfaceFormalName, String interfaceFormalVersion){
        Set<IPCInterface> supportedInterfaces = new HashSet<>();
        Collection<IPCInterface> existingIPCInterfaces = interfaces.values();
        for(IPCInterface currentInterface: existingIPCInterfaces){
            boolean itMatches = false;
            for(IPCInterfaceDefinition currentInterfaceDef: currentInterface.getSupportedInterfaceDefinitions()){
                boolean sameFormalName = interfaceFormalName.contentEquals(currentInterfaceDef.getInterfaceFormalName());
                boolean sameFormalVersion = interfaceFormalVersion.contentEquals(currentInterfaceDef.getInterfaceFormalVersion());
                if(sameFormalName && sameFormalVersion){
                    itMatches = true;
                    break;
                }
            }
            if(itMatches){
                supportedInterfaces.add(currentInterface);
            }
        }
        return(supportedInterfaces);
    }
}
