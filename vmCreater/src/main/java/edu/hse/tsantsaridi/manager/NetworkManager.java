package edu.hse.tsantsaridi.manager;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.network.RouteTable;
import edu.hse.tsantsaridi.config.network.SubNet;
import yandex.cloud.api.operation.OperationOuterClass;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.vpc.v1.SubnetServiceGrpc;
import yandex.cloud.api.vpc.v1.SubnetServiceOuterClass;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;

import static edu.hse.tsantsaridi.App.authService;

public class NetworkManager {
    public void attachRouteTableToSubnet(RouteTable routeTableConf, SubNet subNetConf) throws InvalidProtocolBufferException, InterruptedException {
        System.out.println("[INFO] Attaching route table to private subnet...");
        SubnetServiceGrpc.SubnetServiceBlockingStub subnetService = authService.getFactory().create(SubnetServiceGrpc.SubnetServiceBlockingStub.class, SubnetServiceGrpc::newBlockingStub);

        SubnetServiceOuterClass.UpdateSubnetRequest updateSubnetRequest = SubnetServiceOuterClass.UpdateSubnetRequest.newBuilder()
                .setSubnetId(subNetConf.getId())
                .setRouteTableId(routeTableConf.getId())
                .setUpdateMask(com.google.protobuf.FieldMask.newBuilder().addPaths("route_table_id").build())
                .build();

        OperationOuterClass.Operation attachOperation = subnetService.update(updateSubnetRequest);
        OperationUtils.wait(authService.getFactory().create(OperationServiceGrpc.OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub), attachOperation, Duration.ofMinutes(5));

        System.out.printf("[INFO] Route table %s attached to subnet %s successfully\n", routeTableConf.getId(), subNetConf.getId());
    }
}
