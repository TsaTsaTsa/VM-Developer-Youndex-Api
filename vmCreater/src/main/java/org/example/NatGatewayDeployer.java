package org.example;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.config.General;
import org.example.config.Nat;
import yandex.cloud.api.operation.OperationOuterClass;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.vpc.v1.*;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;

import static org.example.YandexCloudVMDeployer.Auth;

public class NatGatewayDeployer {
    private static ServiceFactory factory;

    public NatGatewayDeployer() {
        factory = Auth();
    }

    public String createNatGateway(General generalConfig, Nat natConfig) throws InterruptedException, InvalidProtocolBufferException {
        GatewayServiceGrpc.GatewayServiceBlockingStub gatewayService = factory.create(GatewayServiceGrpc.GatewayServiceBlockingStub.class, GatewayServiceGrpc::newBlockingStub);

        GatewayServiceOuterClass.CreateGatewayRequest request = GatewayServiceOuterClass.CreateGatewayRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setName(natConfig.getNatGatewayName())
                .setSharedEgressGatewaySpec(GatewayServiceOuterClass.SharedEgressGatewaySpec.newBuilder().build())
                .build();

        OperationOuterClass.Operation operation = gatewayService.create(request);
        OperationUtils.wait(factory.create(OperationServiceGrpc.OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub), operation, Duration.ofMinutes(5));

        // Извлечение идентификатора NAT Gateway из метаданных операции
        GatewayServiceOuterClass.CreateGatewayMetadata metadata = operation.getMetadata().unpack(GatewayServiceOuterClass.CreateGatewayMetadata.class);
        String natGatewayId = metadata.getGatewayId();
        System.out.printf("NAT Gateway with ID: %s created successfully\n", natGatewayId);

        return natGatewayId;
    }

    public String createSubnet(General generalConfig, Nat natConfig) throws InterruptedException, InvalidProtocolBufferException {
        SubnetServiceGrpc.SubnetServiceBlockingStub subnetService = factory.create(SubnetServiceGrpc.SubnetServiceBlockingStub.class, SubnetServiceGrpc::newBlockingStub);

        SubnetServiceOuterClass.CreateSubnetRequest request = SubnetServiceOuterClass.CreateSubnetRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setNetworkId(natConfig.getNetworkId())
                .setZoneId(generalConfig.getZoneId())
                .addV4CidrBlocks(natConfig.getCidrBlocks())
                .setName(natConfig.getSubnetName())
                .build();

        OperationOuterClass.Operation operation = subnetService.create(request);
        OperationUtils.wait(factory.create(OperationServiceGrpc.OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub), operation, Duration.ofMinutes(5));

        SubnetServiceOuterClass.CreateSubnetMetadata metadata = operation.getMetadata().unpack(SubnetServiceOuterClass.CreateSubnetMetadata.class);
        String subnetId = metadata.getSubnetId();
        System.out.printf("Private subnet with ID: %s created successfully\n", subnetId);

        return subnetId;
    }

    public void createRouteTable(General generalConfig, Nat natConfig, String natGatewayId) throws InterruptedException {
        RouteTableServiceGrpc.RouteTableServiceBlockingStub routeTableService = factory.create(RouteTableServiceGrpc.RouteTableServiceBlockingStub.class, RouteTableServiceGrpc::newBlockingStub);

        RouteTableServiceOuterClass.CreateRouteTableRequest request = RouteTableServiceOuterClass.CreateRouteTableRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setNetworkId(natConfig.getNetworkId())
                .setName(natConfig.getRouterName())
                .addStaticRoutes(RouteTableOuterClass.StaticRoute.newBuilder()
                        .setDestinationPrefix(natConfig.getDestinationPrefix())
                        .setGatewayId(natGatewayId)
                        .build())
                .build();

        OperationOuterClass.Operation operation = routeTableService.create(request);
        OperationUtils.wait(factory.create(OperationServiceGrpc.OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub), operation, Duration.ofMinutes(5));

        System.out.print("Route table " +  natConfig.getRouterName() + " created successfully\n");
    }
}
