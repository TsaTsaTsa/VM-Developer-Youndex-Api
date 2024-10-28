package edu.hse.tsantsaridi.config.network;

import org.ini4j.Profile;
import yandex.cloud.api.vpc.v1.SecurityGroupOuterClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecurityConf {
    private String name;
    private String id;
    private String networkId;
    private List<RuleConf> rules;

    public SecurityConf(Profile.Section section) {
        setName(section.get("name"));
        setRules(Arrays.asList(section.get("rules").split(",")));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RuleConf> getRules() {
        return rules;
    }

    private void setRules(List<String> rules) {
        this.rules = new ArrayList<>();
        for (String rule : rules) {
            var rule_array = rule.split(" ");
            this.rules.add(new RuleConf(rule_array[0],
                    rule_array[1],
                    Integer.parseInt(rule_array[2]),
                    Integer.parseInt(rule_array[3]),
                    rule_array[4],
                    rule_array[5]
            ));
        }
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public static class RuleConf {
        private String cidr;
        private int fromPort;
        private int toPort;
        private String protocol;
        private String description;
        private SecurityGroupOuterClass.SecurityGroupRule.Direction direction;

        public RuleConf(String direction, String description, int fromPort, int toPort, String protocol, String cidr) {
            setDirection(direction);
            setDescription(description);
            setFromPort(fromPort);
            setToPort(toPort);
            setProtocol(protocol);
            setCidr(cidr);
        }

        public String getCidr() {
            return cidr;
        }

        public void setCidr(String cidr) {
            this.cidr = cidr;
        }

        public int getFromPort() {
            return fromPort;
        }

        public void setFromPort(int fromPort) {
            this.fromPort = fromPort;
        }

        public int getToPort() {
            return toPort;
        }

        public void setToPort(int toPort) {
            this.toPort = toPort;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public SecurityGroupOuterClass.SecurityGroupRule.Direction getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            try {
                this.direction = SecurityGroupOuterClass.SecurityGroupRule.Direction.valueOf(direction);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid direction value: " + direction);
            }
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

}
