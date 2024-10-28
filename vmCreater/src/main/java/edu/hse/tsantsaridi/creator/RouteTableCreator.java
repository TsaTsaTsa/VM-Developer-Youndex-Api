package edu.hse.tsantsaridi.creator;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.General;
import edu.hse.tsantsaridi.config.network.RouteTable;
import yandex.cloud.api.vpc.v1.*;
import yandex.cloud.api.vpc.v1.RouteTableServiceOuterClass.CreateRouteTableRequest;
import yandex.cloud.api.vpc.v1.RouteTableOuterClass.StaticRoute;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;

import static edu.hse.tsantsaridi.App.authService;

public class RouteTableCreator {
    public void createRouterTable(General generalConfig, String natInstancePublicIP, RouteTable routeTableConf) throws InterruptedException, InvalidProtocolBufferException {
        RouteTableServiceGrpc.RouteTableServiceBlockingStub routeTableService = authService.getFactory().create(RouteTableServiceGrpc.RouteTableServiceBlockingStub.class, RouteTableServiceGrpc::newBlockingStub);

        // Создание таблицы маршрутизации и добавление статического маршрута
        System.out.println("Creating route table...");
        CreateRouteTableRequest routeTableRequest = CreateRouteTableRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setName(routeTableConf.getName())
                .setNetworkId(routeTableConf.getNetworkId())
                .addStaticRoutes(StaticRoute.newBuilder()
                        .setDestinationPrefix(routeTableConf.getDestinationPrefix())
                        .setNextHopAddress(natInstancePublicIP) // Использование внутреннего IP-адреса NAT-инстанса
                        .build())
                .build();

        Operation operation = routeTableService.create(routeTableRequest);
        OperationUtils.wait(authService.getOperationService(), operation, Duration.ofMinutes(5));

        RouteTableOuterClass.RouteTable routerTable = operation.getResponse().unpack(RouteTableOuterClass.RouteTable.class);
        String routeTableId = routerTable.getId();
        routeTableConf.setId(routeTableId);

        System.out.printf("[INFO] Route table %s with ID: %s created successfully\n", routeTableConf.getName(), routeTableId);
    }
}
