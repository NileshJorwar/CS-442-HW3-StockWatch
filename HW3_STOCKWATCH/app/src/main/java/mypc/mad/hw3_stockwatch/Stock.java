package mypc.mad.hw3_stockwatch;

public class Stock implements Comparable<Stock>
{
    public String stock_symbol;
    public String company_name;
    public double price;
    public double price_change;
    public double change_percentage;

    public Stock(String stock_symbol, String company_name, double price, double price_change, double change_percentage) {
        this.stock_symbol = stock_symbol;
        this.company_name = company_name;
        this.price = price;
        this.price_change = price_change;
        this.change_percentage = change_percentage;

    }

    public String getStock_symbol() {
        return stock_symbol;
    }

    public void setStock_symbol(String stock_symbol) {
        this.stock_symbol = stock_symbol;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice_change() {
        return price_change;
    }

    public void setPrice_change(double price_change) {
        this.price_change = price_change;
    }

    public double getChange_percentage() {
        return change_percentage;
    }

    public void setChange_percentage(double change_percentage) {
        this.change_percentage = change_percentage;
    }

    @Override
    public int compareTo(Stock o) {
        return getStock_symbol().compareTo(o.getStock_symbol());
    }
}
