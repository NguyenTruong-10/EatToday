package eatoday.com.model;

public class Food {
    //    private String FoodId;
    private String foodName;
    private String linkVideo;
    private String describle;
    private String foodImage;
    private String ingredient;
    private String idFood;

    public Food() {
    }

    public Food(String foodName, String ingredient, String linkVideo, String describle, String foodImage,String idFood) {
        this.foodName = foodName;
        this.linkVideo = linkVideo;
        this.describle = describle;
        this.ingredient = ingredient;
        this.foodImage = foodImage;
        this.idFood = idFood;
    }
    public String getIdFood() {
        return idFood;
    }

    public void setIdFood(String idFood) {
        this.idFood = idFood;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public String getDescrible() {
        return describle;
    }

    public void setDescrible(String describle) {
        this.describle = describle;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
}