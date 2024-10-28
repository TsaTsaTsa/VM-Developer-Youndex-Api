package edu.hse.tsantsaridi.creator;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.General;
import edu.hse.tsantsaridi.config.network.SecurityConf;
import yandex.cloud.api.vpc.v1.SecurityGroupOuterClass;
import yandex.cloud.api.vpc.v1.SecurityGroupServiceGrpc;
import yandex.cloud.api.vpc.v1.SecurityGroupServiceOuterClass;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;

import static edu.hse.tsantsaridi.App.authService;

public class SecurityGroupCreator {
    private final SecurityGroupServiceGrpc.SecurityGroupServiceBlockingStub securityGroupService;
    private static final int FROM_PORT = 0;
    private static final int TO_PORT = 65535;


    public SecurityGroupCreator() {
        securityGroupService = authService.getFactory().create(SecurityGroupServiceGrpc.SecurityGroupServiceBlockingStub.class, SecurityGroupServiceGrpc::newBlockingStub);
    }

    public void createSecurityGroup(General generalConfig, SecurityConf securityConf) throws InterruptedException, InvalidProtocolBufferException {
        SecurityGroupServiceOuterClass.CreateSecurityGroupRequest securityGroupRequest = SecurityGroupServiceOuterClass.CreateSecurityGroupRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setName(securityConf.getName())
                .setNetworkId(securityConf.getNetworkId())
                .build();

        Operation operation = securityGroupService.create(securityGroupRequest);
        OperationUtils.wait(authService.getOperationService(), operation, Duration.ofMinutes(5));

        SecurityGroupOuterClass.SecurityGroup securityGroup = operation.getResponse().unpack(SecurityGroupOuterClass.SecurityGroup.class);
        String securityGroupId = securityGroup.getId();
        securityConf.setId(securityGroupId);

        for(var ruleConf : securityConf.getRules()) {
            addSecurityGroupRule(securityGroupId, ruleConf);
        }

        System.out.printf("[INFO] Security group %s with ID: %s created successfully\n", securityConf.getName(), securityGroupId);
    }

    private void addSecurityGroupRule(String securityGroupId, SecurityConf.RuleConf ruleConf) throws InterruptedException {
        SecurityGroupServiceOuterClass.SecurityGroupRuleSpec.Builder ruleBuilder = SecurityGroupServiceOuterClass.SecurityGroupRuleSpec.newBuilder()
                .setDescription(ruleConf.getDescription())
                .setDirection(ruleConf.getDirection())
                .setCidrBlocks(SecurityGroupOuterClass.CidrBlocks.newBuilder().addV4CidrBlocks(ruleConf.getCidr()))
                .setProtocolName(ruleConf.getProtocol());

        if (ruleConf.getFromPort() > 0 && ruleConf.getToPort() > 0) {
            ruleBuilder.setPorts(SecurityGroupOuterClass.PortRange.newBuilder().setFromPort(ruleConf.getFromPort()).setToPort(ruleConf.getToPort()).build());
        } else {
            ruleBuilder.setPorts(SecurityGroupOuterClass.PortRange.newBuilder().setFromPort(FROM_PORT).setToPort(TO_PORT).build());
        }

        SecurityGroupServiceOuterClass.UpdateSecurityGroupRulesRequest.Builder updateRulesRequestBuilder =
                SecurityGroupServiceOuterClass.UpdateSecurityGroupRulesRequest.newBuilder()
                        .setSecurityGroupId(securityGroupId)
                        .addAdditionRuleSpecs(ruleBuilder.build());

        SecurityGroupServiceOuterClass.UpdateSecurityGroupRulesRequest updateRulesRequest = updateRulesRequestBuilder.build();

        Operation addRuleOperation = securityGroupService.updateRules(updateRulesRequest);
        OperationUtils.wait(authService.getOperationService(), addRuleOperation, Duration.ofMinutes(5));
        System.out.printf("[INFO] Security group rule %s added successfully\n", ruleConf.getDescription());
    }

}
