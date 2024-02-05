package object;
import java.util.List;

import object.TruyenTranh;
public class Category {
    private String categoryName;
    private List<TruyenTranh> truyenTranhList;

    public Category(String categoryName, List<TruyenTranh> truyenTranhList) {
        this.categoryName = categoryName;
        this.truyenTranhList = truyenTranhList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<TruyenTranh> getTruyenTranhList() {
        return truyenTranhList;
    }

    public void setTruyenTranhList(List<TruyenTranh> truyenTranhList) {
        this.truyenTranhList = truyenTranhList;
    }
}
