package site.sergeyfedorov.util.limits;

public record Limited(int limit) implements LimitNumber {
    public static Limited to(int limit) {
        return new Limited(limit);
    }
}
