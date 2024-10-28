package edu.hse.tsantsaridi.creator;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.General;
import edu.hse.tsantsaridi.config.network.Network;
import edu.hse.tsantsaridi.config.network.SubNet;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.vpc.v1.SubnetServiceGrpc;
import yandex.cloud.api.vpc.v1.SubnetServiceOuterClass;
import yandex.cloud.sdk.utils.OperationUtils;
import yandex.cloud.api.vpc.v1.NetworkServiceOuterClass.CreateNetworkRequest;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.vpc.v1.NetworkServiceGrpc;
import yandex.cloud.api.vpc.v1.NetworkServiceGrpc.NetworkServiceBlockingStub;
import yandex.cloud.api.vpc.v1.NetworkServiceOuterClass;
import java.time.Duration;

import static edu.hse.tsantsaridi.App.authService;

public class NetworkCreator {
    public void createNetwork(General generalConfig, Network networkConfig) throws InterruptedException, InvalidProtocolBufferException {
        NetworkServiceBlockingStub networkService = authService.getFactory().create(NetworkServiceGrpc.NetworkServiceBlockingStub.class, NetworkServiceGrpc::newBlockingStub);

        CreateNetworkRequest networkRequest = CreateNetworkRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setName(networkConfig.getName())
                .build();

        Operation operation = networkService.create(networkRequest);
        OperationUtils.wait(authService.getOperationService(), operation, Duration.ofMinutes(5));

        NetworkServiceOuterClass.CreateNetworkMetadata metadata = operation.getMetadata().unpack(NetworkServiceOuterClass.CreateNetworkMetadata.class);
        String networkId = metadata.getNetworkId();
        networkConfig.setId(networkId);

        System.out.printf("[INFO] Network %s with ID: %s created successfully\n", networkConfig.getName(), networkId);
    }

    public void createSubnet(General generalConfig, SubNet subNetConfig) throws InterruptedException, InvalidProtocolBufferException {
        SubnetServiceGrpc.SubnetServiceBlockingStub subnetService = authService.getFactory().create(SubnetServiceGrpc.SubnetServiceBlockingStub.class, SubnetServiceGrpc::newBlockingStub);

        SubnetServiceOuterClass.CreateSubnetRequest request = SubnetServiceOuterClass.CreateSubnetRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setName(subNetConfig.getName())
                .setNetworkId(subNetConfig.getNetworkId())
                .setZoneId(generalConfig.getZoneId())
                .addV4CidrBlocks(subNetConfig.getCidrBlocks())
                .build();

        Operation operation = subnetService.create(request);
        OperationUtils.wait(authService.getFactory().create(OperationServiceGrpc.OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub), operation, Duration.ofMinutes(5));

        SubnetServiceOuterClass.CreateSubnetMetadata metadata = operation.getMetadata().unpack(SubnetServiceOuterClass.CreateSubnetMetadata.class);
        String subnetId = metadata.getSubnetId();
        subNetConfig.setId(subnetId);
        System.out.printf("[INFO] Subnet %s with ID: %s created successfully\n", subNetConfig.getName() , subnetId);
    }
}

