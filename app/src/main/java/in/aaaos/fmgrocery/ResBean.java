package in.aaaos.fmgrocery;

public class ResBean {
    String id,name,slug,permalink,price,regularprice,stockstatus,src,option,type;
    boolean onsale;
    int variationid;

    public String getId() {
        return id;
    }

    public int getVariationid() {
        return variationid;
    }

    public void setVariationid(int variationid) {
        this.variationid = variationid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {

        return name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRegularprice() {
        return regularprice;
    }

    public void setRegularprice(String regularprice) {
        this.regularprice = regularprice;
    }

    public String getStockstatus() {
        return stockstatus;
    }

    public void setStockstatus(String stockstatus) {
        this.stockstatus = stockstatus;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public boolean isOnsale() {
        return onsale;
    }

    public void setOnsale(boolean onsale) {
        this.onsale = onsale;
    }
}
