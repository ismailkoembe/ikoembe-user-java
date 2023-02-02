package rabbitmq;

public enum RoutingKeys {
    USER_CREATED("user.created"),
    USER_SIGNED_IN("user.sign.in");

    private String routingKeyString;

    RoutingKeys(String routingKeyString) {
        this.routingKeyString = routingKeyString;
    }

    public String getRoutingKeyString() {
        return routingKeyString;
    }

    @Override
    public String toString() {
        return "RoutingKeys{" +
                "routingKeyString='" + routingKeyString + '\'' +
                '}';
    }
}
