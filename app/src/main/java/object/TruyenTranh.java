package object;

public class TruyenTranh {
    private String tenTruyen;
    private int LinkAnh;
    public TruyenTranh (String TenTruyen,int LinkAnhh){
        this.tenTruyen=TenTruyen;
        this.LinkAnh = LinkAnhh;

    }
    public String getTenTruyen(){
        return this.tenTruyen;
    }
    public String setTenTruyen(String tenTruyen){
        return  this.tenTruyen=tenTruyen;
    }
    public int getLinkAnh(){
        return this.LinkAnh;
    }
    public int setLinkAnh(int LinkAnhh){
        return  this.LinkAnh=LinkAnhh;
    }

}
