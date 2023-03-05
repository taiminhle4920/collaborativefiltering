import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Main {

    public static List<Review> processFile(String fileName) throws IOException {
        Reviews reviews;
        ObjectMapper mapper = new ObjectMapper();
        reviews = mapper.readValue(new File(fileName), Reviews.class);
        return reviews.getReviews();
    }

    public static double randomPrediction(CollaborativeFiltering cf, List<Review> reviews) {
        Random rand = new Random();
        Review randReview = reviews.get(rand.nextInt(reviews.size()));
        double predictRating = cf.calculateRating(randReview.getUser_id(), randReview.getBusiness_id());
        double actualRating = randReview.getStars();
        System.out.print(randReview);
        System.out.println("predicted_rating: " + predictRating);
        System.out.println(cf.getUserMapping().get(randReview.getUser_id()));
        return Math.abs(predictRating - actualRating);
    }

    public static double testSet(CollaborativeFiltering cf, List<Review> reviews){
        HashMap<Integer, Integer>reviewID = new HashMap<>();
        double size = Math.ceil(reviews.size()* 0.2);
        Random rand = new Random();
        double avgFalse = 0;
        int num = 0;
        for(int i = 0; i <  size; i++){
            do {
                num = rand.nextInt(reviews.size());
            } while(reviewID.containsKey(num));
            reviewID.put(num, 1);
            Review randReview = reviews.get(num);
            double predictRating = cf.calculateRating(randReview.getUser_id(), randReview.getBusiness_id());
            double actualRating = randReview.getStars();
            avgFalse += Math.abs(predictRating - actualRating);
        }
        return avgFalse / size;
    }
    public static void main(String[] args) throws IOException {
        List<Review> reviews = processFile("./input/yelp_academic_dataset_review.json");
        CollaborativeFiltering cf = new CollaborativeFiltering(reviews);
        System.out.println("Average false is plus or minus: " + testSet(cf, reviews));
    }
}
