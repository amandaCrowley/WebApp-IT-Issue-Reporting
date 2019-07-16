import java.util.List;

public class Category{

	private String category;
	private List<String> subCategories;

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setSubCategories(List<String> subCategories){
		this.subCategories = subCategories;
	}

	public List<String> getSubCategories(){
		return subCategories;
	}
}