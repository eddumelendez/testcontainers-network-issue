package org.example;

import com.github.dockerjava.api.model.Network;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

public class ProducerTest {

    @Test
    public void test() {
        List<Network> networks = DockerClientFactory.instance().client().listNetworksCmd()
                .exec();
        System.out.println("----");
        networks.forEach(System.out::println);
        System.out.println("----");

        org.testcontainers.containers.Network network = getDaprNetwork();
        try (GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379)
                .withNetwork(network);
             GenericContainer<?> redis2 = new GenericContainer<>("redis:7-alpine")
                     .withExposedPorts(6379)
                     .withNetwork(network)) {
            redis.start();
            redis2.start();
        }
    }

    public org.testcontainers.containers.Network getDaprNetwork() {
        org.testcontainers.containers.Network defaultDaprNetwork = new org.testcontainers.containers.Network() {
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
            org.testcontainers.containers.Network.builder().createNetworkCmdModifier(cmd -> cmd.withName("dapr-network")).build().getId();
            return defaultDaprNetwork;
        } else {
            return defaultDaprNetwork;
        }
    }

}
