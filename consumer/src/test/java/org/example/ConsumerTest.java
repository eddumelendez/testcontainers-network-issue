package org.example;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;

import java.util.List;

public class ConsumerTest {
    
    @Test
    public void test() {
        List<com.github.dockerjava.api.model.Network> networks = DockerClientFactory.instance().client().listNetworksCmd()
                .exec();
        System.out.println("----");
        networks.forEach(System.out::println);
        System.out.println("----");
        
        Network network = getDaprNetwork();
        try (GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379)
                .withNetwork(network);
        GenericContainer<?> redis2 = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379)
        .withNetwork(network)) {
            redis.start();
            redis2.start();
            Assert.assertTrue(redis.isRunning());
            Assert.assertTrue(redis2.isRunning());
        }
    }

    public Network getDaprNetwork() {
        Network defaultDaprNetwork = new Network() {
            @Override
            public String getId() {
                return "dapr-network";
            }

            @Override
            public void close() {

            }

            @Override
            public Statement apply(Statement base, Description description) {
                return null;
            }
        };

        List<com.github.dockerjava.api.model.Network> networks = DockerClientFactory.instance().client().listNetworksCmd()
                .withNameFilter("dapr-network").exec();
        System.out.println("----");
        networks.forEach(System.out::println);
        System.out.println("----");
        if (networks.isEmpty()) {
            Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("dapr-network")).build().getId();
            return defaultDaprNetwork;
        } else {
            return defaultDaprNetwork;
        }
    }
}
