package cn.yapeteam.agent;

import com.sun.tools.attach.*;

import java.io.File;
import java.io.IOException;

/**
 * @author yuxiangll
 * @since 2024/7/26 下午4:00
 * IntelliJ IDEA
 */
public class AgentInjector {


    public static void main(String[] args) {

        VirtualMachine.list().forEach(AgentInjector::attachAgent);


    }

    private static void attachAgent(VirtualMachineDescriptor vmd) {
        try {
            if (vmd.displayName().contains("net.minecraft.client.main.Main")){
                File file = new File("./build/agent.jar");

                VirtualMachine vm = VirtualMachine.attach(vmd.id());
                vm.loadAgent(file.getAbsolutePath());
                vm.detach();



            }
        } catch (AttachNotSupportedException | IOException | AgentLoadException | AgentInitializationException e) {
            throw new RuntimeException(e);
        }
    }





}
