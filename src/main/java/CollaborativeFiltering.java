import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollaborativeFiltering {
    private HashMap<String, HashMap<String, Integer>> userMapping = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> itemMapping = new HashMap<>();

    public CollaborativeFiltering(List<Review> reviews) {
        for (Review review : reviews) {
            if (!this.userMapping.containsKey(review.getUser_id())) {
                this.userMapping.put(review.getUser_id(), new HashMap<>());
            }
            this.userMapping.get(review.getUser_id()).put(review.getBusiness_id(), review.getStars());

            if (!this.itemMapping.containsKey(review.getBusiness_id())) {
                this.itemMapping.put(review.getBusiness_id(), new HashMap<>());
            }
            this.itemMapping.get(review.getBusiness_id()).put(review.getUser_id(), review.getStars());
        }
    }

    public HashMap<String, HashMap<String, Integer>> getItemMapping() {
        return itemMapping;
    }

    public HashMap<String, HashMap<String, Integer>> getUserMapping() {
        return userMapping;
    }

    public double calculateSimilary(String businessId1, String businessId2) {
        List<String> intersection = this.itemMapping.get(businessId1).keySet()
                .stream()
                .filter(key -> this.itemMapping.get(businessId2).containsKey(key))
                .collect(Collectors.toList());
        if (intersection.size() == 1)
            return 0.0;
        double dotProduct = intersection
                .stream()
                .mapToDouble(userId -> this.itemMapping.get(businessId1).get(userId)
                        * this.itemMapping.get(businessId2).get(userId))
                .sum();
        double magnitude1 = Math.sqrt(intersection
                .stream()
                .mapToDouble(userId -> Math.pow(this.itemMapping.get(businessId1).get(userId), 2))
                .sum());
        double magnitude2 = Math.sqrt(intersection
                .stream()
                .mapToDouble(userId -> Math.pow(this.itemMapping.get(businessId2).get(userId), 2))
                .sum());
        return dotProduct / (magnitude1 * magnitude2);
    }

    public double calculateRating(String userId, String businessId) {
        double numerator = 0.0;
        double denominator = 0.0;
        for (Map.Entry<String, Integer> item : this.userMapping.get(userId).entrySet()) {
            String itemId = item.getKey();
            double rating = (double) item.getValue();
            if (!itemId.equals(businessId)) {
                double similarity = this.calculateSimilary(itemId, businessId);
                numerator += rating * similarity;
                denominator += similarity;
            }
        }
        return numerator / denominator;
    }

}
